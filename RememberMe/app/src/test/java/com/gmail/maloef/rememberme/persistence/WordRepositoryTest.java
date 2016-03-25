package com.gmail.maloef.rememberme.persistence;

import android.app.Application;
import android.util.Pair;

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
        assertEquals(3, wordRepository.countWords(boxId, 1, beforeUpdate));

        Word first = wordRepository.getNextWord(boxId, 1);
        assertEquals("foreign0", first.foreignWord);

        Word firstAfterUpdate = wordRepository.getNextWord(boxId, 1, beforeUpdate);
        assertEquals("foreign1", firstAfterUpdate.foreignWord);
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
        List<Pair<String, String>> firstFive = wordRepository.getWords(boxId, 1, 0, 5);
        assertEquals(5, firstFive.size());
        assertEquals("foreign1", firstFive.get(0).first);
        assertEquals("native1", firstFive.get(0).second);
        assertEquals("foreign5", firstFive.get(4).first);
        assertEquals("native5", firstFive.get(4).second);

        List<Pair<String, String>> fifthToEighth = wordRepository.getWords(boxId, 1, 5, 5);
        assertEquals(3, fifthToEighth.size());
        assertEquals("foreign6", fifthToEighth.get(0).first);
        assertEquals("native6", fifthToEighth.get(0).second);
        assertEquals("foreign8", fifthToEighth.get(2).first);
        assertEquals("native8", fifthToEighth.get(2).second);

        assertTrue(wordRepository.getWords(boxId, 1, 8, 5).isEmpty());
        assertTrue(wordRepository.getWords(boxId, 1, 10, 5).isEmpty());
    }
}
