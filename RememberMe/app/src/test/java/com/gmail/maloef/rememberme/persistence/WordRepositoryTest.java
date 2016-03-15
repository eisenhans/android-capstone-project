package com.gmail.maloef.rememberme.persistence;

import android.app.Application;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WordRepositoryTest extends AbstractPersistenceTest {

    Application application;
    VocabularyBoxRepository boxService;
    WordRepository wordService;

    int boxId;

    @Before
    public void before() throws Exception {
        super.before();
        application = RuntimeEnvironment.application;
        boxService = new VocabularyBoxRepository(application);
        wordService = new WordRepository(application);

        boxId = boxService.createDefaultBox();
    }

    @Test
    public void doesWordExist() {
        String foreignWord = "porcupine";
        String nativeWord = "Stachelschwein";
        assertFalse(wordService.doesWordExist(boxId, foreignWord, nativeWord));

        wordService.createWord(boxId, foreignWord, nativeWord);

        assertTrue(wordService.doesWordExist(boxId, foreignWord, nativeWord));
        assertFalse(wordService.doesWordExist(boxId, foreignWord, ""));
        assertFalse(wordService.doesWordExist(boxId, "", nativeWord));
        assertFalse(wordService.doesWordExist(boxId + 1, foreignWord, nativeWord));
    }
}
