package com.gmail.maloef.rememberme.persistence;

import android.content.ContentValues;
import android.net.Uri;

import com.gmail.maloef.rememberme.domain.Compartment;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RememberMeProviderTest extends AbstractPersistenceTest {

    @Test
    public void testVocabularyBox() {
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
                contentProvider.query(RememberMeProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));
        assertFalse(boxCursor.moveToFirst());
        boxCursor.close();

        insertVocabularyBox("defaultBox", "English", "German", VocabularyBox.TRANSLATION_DIRECTION_MIXED);
        boxCursor = new VocabularyBoxCursor(
                contentProvider.query(RememberMeProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));
        assertTrue(boxCursor.moveToFirst());
        VocabularyBox box = boxCursor.peek();
        assertEquals("defaultBox", box.name);
        assertEquals("English", box.foreignLanguage);
        assertEquals("German", box.nativeLanguage);
        assertEquals(VocabularyBox.TRANSLATION_DIRECTION_MIXED, box.translationDirection);
        assertFalse(box.isCurrent);

        assertFalse(boxCursor.moveToNext());
        boxCursor.close();
    }

    @Test
    public void testCompartment() {
        CompartmentCursor compartmentCursor = new CompartmentCursor(
                contentProvider.query(RememberMeProvider.Compartment.COMPARTMENTS, null, null, null, null));
        assertFalse(compartmentCursor.moveToFirst());
        compartmentCursor.close();

        insertCompartment(1, 2);

        compartmentCursor = new CompartmentCursor(
                contentProvider.query(RememberMeProvider.Compartment.COMPARTMENTS, null, null, null, null));
        assertTrue(compartmentCursor.moveToFirst());
        Compartment compartment = compartmentCursor.peek();
        assertEquals(1, compartment.vocabularyBox);
        assertEquals(2, compartment.number);
        compartmentCursor.close();
    }

    private long time = System.currentTimeMillis();

    private void logTime(String message) {
        long newTime = System.currentTimeMillis();
        logInfo(message + " (" + (newTime - time) + " ms)");
        time = newTime;
    }

    @Test
    public void testWord() {
        logTime("inside testWord");
        WordCursor wordCursor = new WordCursor(contentProvider.query(RememberMeProvider.Word.WORDS, null, null, null, null));
        assertFalse(wordCursor.moveToFirst());
        wordCursor.close();
        logTime("checked no word in db");

        int boxId = insertVocabularyBox("defaultBox", "English", "German", VocabularyBox.TRANSLATION_DIRECTION_MIXED);
        logTime("inserted voacabulary box");
        insertWord(boxId, 1, "porcupine", "Stachelschwein");
        logTime("inserted one word");

        wordCursor = new WordCursor(contentProvider.query(RememberMeProvider.Word.WORDS, null, null, null, null));
        assertTrue(wordCursor.moveToFirst());
        Word word = wordCursor.peek();
        logTime("read word");
        assertEquals("porcupine", word.foreignWord);
        assertEquals("Stachelschwein", word.nativeWord);

        long creationDate = word.creationDate;
        long now = new Date().getTime();
        logInfo("word was created " + (now - creationDate) + " ms ago");
        assertTrue(creationDate < now);
        assertTrue(creationDate + 2000 > now);

        wordCursor.close();
        logTime("end of testWord");
    }

    @Test
    public void testCombined() {
        int boxId = insertVocabularyBox("defaultBox", "English", "German", VocabularyBox.TRANSLATION_DIRECTION_MIXED);
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
                contentProvider.query(RememberMeProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));
        assertTrue(boxCursor.moveToFirst());
        assertEquals(boxId, boxCursor.peek().id);
        boxCursor.close();

        insertCompartment(boxId, 1);
        CompartmentCursor compartmentCursor = new CompartmentCursor(
                contentProvider.query(RememberMeProvider.Compartment.COMPARTMENTS, null, null, null, null));
        assertTrue(compartmentCursor.moveToFirst());
        int compartmentId = compartmentCursor.peek().id;
        compartmentCursor.close();

        insertWord(boxId, 1, "porcupine", "Stachelschwein");
        WordCursor wordCursor = new WordCursor(contentProvider.query(RememberMeProvider.Word.WORDS, null, null, null, null));
        assertTrue(wordCursor.moveToFirst());
        int wordId = wordCursor.peek().id;
        wordCursor.close();

        wordCursor = new WordCursor(contentProvider.query(RememberMeProvider.Word.findById(wordId), null, null, null, null));
        assertTrue(wordCursor.moveToFirst());
        assertEquals(compartmentId, wordCursor.peek().compartment);
        wordCursor.close();

        compartmentCursor = new CompartmentCursor(
                contentProvider.query(RememberMeProvider.Compartment.findById(compartmentId), null, null, null, null));
        assertTrue(compartmentCursor.moveToFirst());
        assertEquals(boxId, compartmentCursor.peek().vocabularyBox);
        compartmentCursor.close();
    }

    private int insertVocabularyBox(String name, String foreignLanguage, String nativeLanguage, int translationDirection) {
        ContentValues values = new ContentValues();
        values.put(VocabularyBoxColumns.NAME, name);
        values.put(VocabularyBoxColumns.FOREIGN_LANGUAGE, foreignLanguage);
        values.put(VocabularyBoxColumns.NATIVE_LANGUAGE, nativeLanguage);
        values.put(VocabularyBoxColumns.TRANSLATION_DIRECTION, translationDirection);

        Uri uri = contentProvider.insert(RememberMeProvider.VocabularyBox.VOCABULARY_BOXES, values);
        String boxIdString = uri.getLastPathSegment();
        return Integer.valueOf(boxIdString);
    }

    private void insertCompartment(int boxId, int number) {
        ContentValues values = new ContentValues();
        values.put(CompartmentColumns.VOCABULARY_BOX, boxId);
        values.put(CompartmentColumns.NUMBER, number);

        contentProvider.insert(RememberMeProvider.Compartment.COMPARTMENTS, values);
    }

    private void insertWord(int boxId, int compartment, String foreignWord, String nativeWord) {
        ContentValues values = new ContentValues();
        values.put(WordColumns.BOX_ID, boxId);
        values.put(WordColumns.COMPARTMENT, compartment);
        values.put(WordColumns.FOREIGN_WORD, foreignWord);
        values.put(WordColumns.NATIVE_WORD, nativeWord);

        Date now = new Date();
        values.put(WordColumns.CREATION_DATE, now.getTime());

        contentProvider.insert(RememberMeProvider.Word.WORDS, values);
    }
}
