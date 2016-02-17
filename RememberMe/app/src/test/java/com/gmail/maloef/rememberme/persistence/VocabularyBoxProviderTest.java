package com.gmail.maloef.rememberme.persistence;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.maloef.rememberme.AbstractRobolectricTest;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.generated.VocabularyBoxDatabase;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowContentResolver;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VocabularyBoxProviderTest extends AbstractRobolectricTest {

    ContentProvider contentProvider;

    @Before
    public void before() throws Exception {
        super.before();
        // The generated class VocabularyBoxDatabase has a field named instance. This has to be set to null. Otherwise
        // it is not possible to run several tests - Robolectric problem.
        resetSingleton(VocabularyBoxDatabase.class, "instance");

        if (contentProvider == null) {
            contentProvider = new com.gmail.maloef.rememberme.persistence.generated.VocabularyBoxProvider();
            contentProvider.onCreate();
            ShadowContentResolver.registerProvider(VocabularyBoxProvider.AUTHORITY, contentProvider);
            logInfo("contentProvider: " + contentProvider);
        }

        contentProvider.delete(VocabularyBoxProvider.Word.WORDS, null, null);
        contentProvider.delete(VocabularyBoxProvider.Compartment.COMPARTMENTS, null, null);
        contentProvider.delete(VocabularyBoxProvider.Word.WORDS, null, null);
    }

    private void resetSingleton(Class clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testVocabularyBox() {
        Cursor cursor = contentProvider.query(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null);
        assertFalse(cursor.moveToFirst());
        cursor.close();

        insertVocabularyBox("defaultBox", "English", "German", VocabularyBox.TRANSLATION_DIRECTION_MIXED);
        cursor = contentProvider.query(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals("defaultBox", cursor.getString(cursor.getColumnIndex(VocabularyBoxColumns.NAME)));
        assertEquals("English", cursor.getString(cursor.getColumnIndex(VocabularyBoxColumns.FOREIGN_LANGUAGE)));
        assertEquals("German", cursor.getString(cursor.getColumnIndex(VocabularyBoxColumns.NATIVE_LANGUAGE)));
        assertEquals(VocabularyBox.TRANSLATION_DIRECTION_MIXED,
                cursor.getInt(cursor.getColumnIndex(VocabularyBoxColumns.TRANSLATION_DIRECTION)));

        assertFalse(cursor.moveToNext());
        cursor.close();
    }

    @Test
    public void testCompartment() {
        Cursor cursor = contentProvider.query(VocabularyBoxProvider.Compartment.COMPARTMENTS, null, null, null, null);
        assertFalse(cursor.moveToFirst());
        cursor.close();

        insertCompartment(1, 1);

        cursor = contentProvider.query(VocabularyBoxProvider.Compartment.COMPARTMENTS, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testWord() {
        Cursor cursor = contentProvider.query(VocabularyBoxProvider.Word.WORDS, null, null, null, null);
        assertFalse(cursor.moveToFirst());
        cursor.close();

        insertWord(1, "porcupine", "Stachelschwein");

        cursor = contentProvider.query(VocabularyBoxProvider.Word.WORDS, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals("porcupine", cursor.getString(cursor.getColumnIndex(WordColumns.FOREIGN_WORD)));
        assertEquals("Stachelschwein", cursor.getString(cursor.getColumnIndex(WordColumns.NATIVE_WORD)));

        long creationDate = cursor.getLong(cursor.getColumnIndex(WordColumns.CREATION_DATE));
        long now = new Date().getTime();
        logInfo("word was created " + (now - creationDate) + " ms ago");
        assertTrue(creationDate < now);
        assertTrue(creationDate + 1000 > now);

        cursor.close();
    }

    @Test
    public void testCombined() {
        insertVocabularyBox("defaultBox", "English", "German", VocabularyBox.TRANSLATION_DIRECTION_MIXED);
        Cursor cursor = contentProvider.query(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        int vocabularyBoxId = cursor.getInt(cursor.getColumnIndex(VocabularyBoxColumns._ID));
        cursor.close();

        insertCompartment(vocabularyBoxId, 1);
        cursor = contentProvider.query(VocabularyBoxProvider.Compartment.COMPARTMENTS, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        int compartmentId = cursor.getInt(cursor.getColumnIndex(CompartmentColumns._ID));
        cursor.close();

        insertWord(compartmentId, "porcupine", "Stachelschwein");
        cursor = contentProvider.query(VocabularyBoxProvider.Word.WORDS, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        int wordId = cursor.getInt(cursor.getColumnIndex(WordColumns._ID));
        cursor.close();

        cursor = contentProvider.query(VocabularyBoxProvider.Word.findById(wordId), null, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals(compartmentId, cursor.getInt(cursor.getColumnIndex(WordColumns.COMPARTMENT)));
        cursor.close();

        cursor = contentProvider.query(VocabularyBoxProvider.Compartment.findById(compartmentId), null, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals(vocabularyBoxId, cursor.getInt(cursor.getColumnIndex(CompartmentColumns.VOCABULARY_BOX)));
        cursor.close();
    }

    private void insertVocabularyBox(String name, String foreignLanguage, String nativeLanguage, int translationDirection) {
        ContentValues values = new ContentValues();
        values.put(VocabularyBoxColumns.NAME, name);
        values.put(VocabularyBoxColumns.FOREIGN_LANGUAGE, foreignLanguage);
        values.put(VocabularyBoxColumns.NATIVE_LANGUAGE, nativeLanguage);
        values.put(VocabularyBoxColumns.TRANSLATION_DIRECTION, translationDirection);

        contentProvider.insert(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, values);
    }

    private void insertCompartment(int vocabularyBoxId, int number) {
        ContentValues values = new ContentValues();
        values.put(CompartmentColumns.VOCABULARY_BOX, vocabularyBoxId);
        values.put(CompartmentColumns.NUMBER, number);

        contentProvider.insert(VocabularyBoxProvider.Compartment.COMPARTMENTS, values);
    }

    private void insertWord(int compartmentId, String foreignWord, String nativeWord) {
        ContentValues values = new ContentValues();
        values.put(WordColumns.COMPARTMENT, compartmentId);
        values.put(WordColumns.FOREIGN_WORD, foreignWord);
        values.put(WordColumns.NATIVE_WORD, nativeWord);

        Date now = new Date();
        values.put(WordColumns.CREATION_DATE, now.getTime());

        contentProvider.insert(VocabularyBoxProvider.Word.WORDS, values);
    }
}
