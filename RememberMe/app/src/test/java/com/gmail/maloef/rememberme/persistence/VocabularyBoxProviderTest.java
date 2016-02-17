package com.gmail.maloef.rememberme.persistence;

import android.content.ContentProvider;
import android.content.ContentValues;

import com.gmail.maloef.rememberme.AbstractRobolectricTest;
import com.gmail.maloef.rememberme.domain.Compartment;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;
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
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
                contentProvider.query(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));
        assertFalse(boxCursor.moveToFirst());
        boxCursor.close();

        insertVocabularyBox("defaultBox", "English", "German", VocabularyBox.TRANSLATION_DIRECTION_MIXED);
        boxCursor = new VocabularyBoxCursor(
                contentProvider.query(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));
        assertTrue(boxCursor.moveToFirst());
        VocabularyBox box = boxCursor.peek();
        assertEquals("defaultBox", box.name);
        assertEquals("English", box.foreignLanguage);
        assertEquals("German", box.nativeLanguage);
        assertEquals(VocabularyBox.TRANSLATION_DIRECTION_MIXED, box.translationDirection);

        assertFalse(boxCursor.moveToNext());
        boxCursor.close();
    }

    @Test
    public void testCompartment() {
        CompartmentCursor compartmentCursor = new CompartmentCursor(
                contentProvider.query(VocabularyBoxProvider.Compartment.COMPARTMENTS, null, null, null, null));
        assertFalse(compartmentCursor.moveToFirst());
        compartmentCursor.close();

        insertCompartment(1, 2);

        compartmentCursor = new CompartmentCursor(
                contentProvider.query(VocabularyBoxProvider.Compartment.COMPARTMENTS, null, null, null, null));
        assertTrue(compartmentCursor.moveToFirst());
        Compartment compartment = compartmentCursor.peek();
        assertEquals(1, compartment.vocabularyBox);
        assertEquals(2, compartment.number);
        compartmentCursor.close();
    }

    @Test
    public void testWord() {
        WordCursor wordCursor = new WordCursor(contentProvider.query(VocabularyBoxProvider.Word.WORDS, null, null, null, null));
        assertFalse(wordCursor.moveToFirst());
        wordCursor.close();

        insertWord(1, "porcupine", "Stachelschwein");

        wordCursor = new WordCursor(contentProvider.query(VocabularyBoxProvider.Word.WORDS, null, null, null, null));
        assertTrue(wordCursor.moveToFirst());
        Word word = wordCursor.peek();
        assertEquals("porcupine", word.foreignWord);
        assertEquals("Stachelschwein", word.nativeWord);

        long creationDate = word.creationDate;
        long now = new Date().getTime();
        logInfo("word was created " + (now - creationDate) + " ms ago");
        assertTrue(creationDate < now);
        assertTrue(creationDate + 1000 > now);

        wordCursor.close();
    }

    @Test
    public void testCombined() {
        insertVocabularyBox("defaultBox", "English", "German", VocabularyBox.TRANSLATION_DIRECTION_MIXED);
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
                contentProvider.query(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));
        assertTrue(boxCursor.moveToFirst());
        int vocabularyBoxId = boxCursor.peek()._id;
        boxCursor.close();

        insertCompartment(vocabularyBoxId, 1);
        CompartmentCursor compartmentCursor = new CompartmentCursor(
                contentProvider.query(VocabularyBoxProvider.Compartment.COMPARTMENTS, null, null, null, null));
        assertTrue(compartmentCursor.moveToFirst());
        int compartmentId = compartmentCursor.peek()._id;
        compartmentCursor.close();

        insertWord(compartmentId, "porcupine", "Stachelschwein");
        WordCursor wordCursor = new WordCursor(contentProvider.query(VocabularyBoxProvider.Word.WORDS, null, null, null, null));
        assertTrue(wordCursor.moveToFirst());
        int wordId = wordCursor.peek()._id;
        wordCursor.close();

        wordCursor = new WordCursor(contentProvider.query(VocabularyBoxProvider.Word.findById(wordId), null, null, null, null));
        assertTrue(wordCursor.moveToFirst());
        assertEquals(compartmentId, wordCursor.peek().compartment);
        wordCursor.close();

        compartmentCursor = new CompartmentCursor(
                contentProvider.query(VocabularyBoxProvider.Compartment.findById(compartmentId), null, null, null, null));
        assertTrue(compartmentCursor.moveToFirst());
        assertEquals(vocabularyBoxId, compartmentCursor.peek().vocabularyBox);
        compartmentCursor.close();
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
