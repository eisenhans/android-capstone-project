package com.gmail.maloef.rememberme.persistence;

import android.app.Application;
import android.util.Pair;

import com.gmail.maloef.rememberme.RememberMeConstants;
import com.gmail.maloef.rememberme.domain.BoxOverview;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WordRepositoryTest extends AbstractPersistenceTest {

    Application application;
    VocabularyBoxRepository boxRepository;
    WordRepository wordRepository;

    int boxId;

    @Before
    public void before() throws Exception {
        super.before();
        application = RuntimeEnvironment.application;
        boxRepository = new VocabularyBoxRepository(application);
        wordRepository = new WordRepository(application);

        boxId = boxRepository.createDefaultBox();
    }

    @Test
    public void createWord() {
        String foreignWord = "porcupine";
        String nativeWord = "Stachelschwein";
        assertFalse(wordRepository.doesWordExist(boxId, foreignWord, nativeWord));

        long start = System.currentTimeMillis();
        int wordId = wordRepository.createWord(boxId, foreignWord, nativeWord);
        long stop = System.currentTimeMillis();
        assertAlmostEqual(start, stop, 100);
    }

    @Test
    public void doesWordExist() {
        String foreignWord = "porcupine";
        String nativeWord = "Stachelschwein";
        assertFalse(wordRepository.doesWordExist(boxId, foreignWord, nativeWord));

        wordRepository.createWord(boxId, foreignWord, nativeWord);

        assertTrue(wordRepository.doesWordExist(boxId, foreignWord, nativeWord));
        assertFalse(wordRepository.doesWordExist(boxId, foreignWord, ""));
        assertFalse(wordRepository.doesWordExist(boxId, "", nativeWord));
        assertFalse(wordRepository.doesWordExist(boxId + 1, foreignWord, nativeWord));
    }

    @Test
    public void getNextWord() {
        int[] wordIds = new int[5];
        for (int i = 0; i < 5; i++) {
            wordIds[i] = wordRepository.createWord(boxId, "foreign" + i, "native" + i);
        }
        assertEquals(5, wordRepository.countWords(boxId, 1));

        long beforeUpdate = new Date().getTime() - 1;
        wordRepository.updateRepeatDate(wordIds[0]);
        wordRepository.updateRepeatDate(wordIds[2]);
        assertEquals(5, wordRepository.countWords(boxId, 1));
        assertEquals(3, wordRepository.countWords(boxId, 1, beforeUpdate));

        Word first = wordRepository.getNextWord(boxId, 1);
        assertEquals("foreign1", first.foreignWord);

        Word firstAfterUpdate = wordRepository.getNextWord(boxId, 1, beforeUpdate);
        assertEquals("foreign1", firstAfterUpdate.foreignWord);
    }

    @Test
    public void getNextWordInCorrectOrder() {
        int[] wordIds = new int[5];
        for (int i = 0; i < 5; i++) {
            wordIds[i] = wordRepository.createWord(boxId, "foreign" + i, "native" + i);
        }
        wordRepository.updateRepeatDate(wordIds[3]);
        wordRepository.updateRepeatDate(wordIds[1]);

        assertNextWord(wordIds[0]);
        assertNextWord(wordIds[2]);
        assertNextWord(wordIds[4]);
        assertNextWord(wordIds[3]);
        assertNextWord(wordIds[1]);
    }

    void assertNextWord(int expectedWordId) {
        assertEquals(expectedWordId, wordRepository.getNextWord(boxId, 1).id);
        wordRepository.moveToCompartment(expectedWordId, 2);
    }

    @Test
    public void countWords() {
        assertTrue(0 < Long.MAX_VALUE);

        int wordId = wordRepository.createWord(boxId, "foreign", "native");
        assertEquals(1, wordRepository.countWords(boxId, 1));

        Word word = wordRepository.findWord(wordId);
        assertEquals(0, word.lastRepeatDate);

        long before = new Date().getTime();
        wordRepository.updateRepeatDate(wordId);
        long after = new Date().getTime();
        word = wordRepository.findWord(wordId);
        logInfo("repeatDate=" + word.lastRepeatDate + " (" + new Date(word.lastRepeatDate) + ")");
        logInfo("before=" + before + ", update=" + word.lastRepeatDate + ", after=" + after);

        assertTrue(before <= word.lastRepeatDate);
        assertTrue(after >= word.lastRepeatDate);

        assertEquals(1, wordRepository.countWords(boxId, 1));
        assertEquals(0, wordRepository.countWords(boxId, 1, before - 1));
        assertEquals(1, wordRepository.countWords(boxId, 1, after));
        assertEquals(1, wordRepository.countWords(boxId, 1, Long.MAX_VALUE));
    }

    @Test
    public void getWords() {
        for (int i = 1; i <= 8; i++) {
            wordRepository.createWord(boxId, "foreign" + i, "native" + i);
        }
        List<Pair<String, String>> firstFive = wordRepository.getWords(boxId, 1, 1, 5);
        assertEquals(5, firstFive.size());
        assertEquals("foreign1", firstFive.get(0).first);
        assertEquals("native1", firstFive.get(0).second);
        assertEquals("foreign5", firstFive.get(4).first);
        assertEquals("native5", firstFive.get(4).second);

        List<Pair<String, String>> fifthToEighth = wordRepository.getWords(boxId, 1, 6, 5);
        assertEquals(3, fifthToEighth.size());
        assertEquals("foreign6", fifthToEighth.get(0).first);
        assertEquals("native6", fifthToEighth.get(0).second);
        assertEquals("foreign8", fifthToEighth.get(2).first);
        assertEquals("native8", fifthToEighth.get(2).second);

        assertTrue(wordRepository.getWords(boxId, 1, 9, 5).isEmpty());
        assertTrue(wordRepository.getWords(boxId, 1, 10, 5).isEmpty());
    }

    @Test
    public void moveAll() {
        for (int i = 1; i <= 5; i++) {
            wordRepository.createWord(boxId, "foreign" + i, "native" + i);
        }
        wordRepository.moveAll(boxId, 1, 0);
        assertEquals(0, wordRepository.countWords(boxId, 1));
        assertEquals(5, wordRepository.countWords(boxId, 0));

        wordRepository.moveAll(boxId, 0, 1);
        assertEquals(0, wordRepository.countWords(boxId, 0));
        assertEquals(5, wordRepository.countWords(boxId, 1));
    }

    @Test
    public void delete() {
        int wordId = wordRepository.createWord(boxId, "foreign", "native");
        assertEquals(1, wordRepository.countWords(boxId));

        assertTrue(wordRepository.deleteWord(wordId));
        assertEquals(0, wordRepository.countWords(boxId));

        assertFalse(wordRepository.deleteWord(wordId));
    }

    @Test
    public void update() {
        int wordId = wordRepository.createWord(boxId, "foreign", "native");
        Word word = wordRepository.findWord(wordId);
        assertEquals(0, word.updateDate);

        long timestamp = new Date().getTime();
        wordRepository.updateWord(wordId, "foreignNew", "nativeNew");
        assertEquals(1, wordRepository.countWords(boxId));

        word = wordRepository.findWord(wordId);
        assertEquals("foreignNew", word.foreignWord);
        assertEquals("nativeNew", word.nativeWord);
        assertTrue(word.updateDate >= timestamp);
    }

    @Test
    public void getBoxOverviewBoxEmpty() {
        BoxOverview boxOverview = wordRepository.getBoxOverview(boxId);
        for (int compartment = 1; compartment <= 5; compartment++) {
            assertCompartmentEmpty(boxOverview, compartment);
        }
    }

    @Test
    public void getBoxOverview() {
        wordRepository.createWord(boxId, "porcupine", "Stachelschwein");

        int ointmentId = wordRepository.createWord(boxId, "ointment", "Salbe");
        wordRepository.updateRepeatDate(ointmentId);

        int biasId = wordRepository.createWord(boxId, "bias", "Tendenz, Neigung");
        wordRepository.updateRepeatDate(biasId);
        Long timestamp = new Date().getTime();

        wordRepository.createWord(boxId, 2, "emissary", "Abgesandter");

        BoxOverview boxOverview = wordRepository.getBoxOverview(boxId);

        assertEquals(3, boxOverview.getWordCount(1));
        assertAlmostEqual(timestamp, boxOverview.getEarliestLastRepeatDate(1), 100);

        assertEquals(1, boxOverview.getWordCount(2));
        assertEquals(0, boxOverview.getEarliestLastRepeatDate(2));

        for (int compartment = 3; compartment <= 5; compartment++) {
            assertCompartmentEmpty(boxOverview, compartment);
        }
    }

    @Test
    public void countWordsDue() {
        wordRepository.createWord(boxId, "foreign1", "native1");

        // doesn't count because word is in final compartment
        wordRepository.createWord(boxId, RememberMeConstants.NUMBER_OF_COMPARTMENTS, "foreign2", "native2");

        int otherBoxId = createAnotherBox();
        wordRepository.createWord(otherBoxId, "foreign", "native");

        assertEquals(2, wordRepository.countWordsDue());
    }

    private int createAnotherBox() {
        return boxRepository.createBox("otherBox", "it", "de", VocabularyBox.TRANSLATION_DIRECTION_RANDOM, false);
    }

    void assertCompartmentEmpty(BoxOverview boxOverview, int compartment) {
        assertEquals(0, boxOverview.getWordCount(compartment));
        assertEquals(0, boxOverview.getEarliestLastRepeatDate(compartment));
    }
}
