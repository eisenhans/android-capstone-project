package com.gmail.maloef.rememberme.persistence;

import android.app.Application;

import com.gmail.maloef.rememberme.domain.VocabularyBox;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

public class VocabularyBoxRepositoryTest extends AbstractPersistenceTest {

    private Application application;
    private VocabularyBoxRepository boxService;

    @Before
    public void before() throws Exception {
        super.before();
        application = RuntimeEnvironment.application;
        boxService = new VocabularyBoxRepository(application);
    }

    @Test
    public void testCreateAndSelectBox() {
        boxService.createDefaultBox();
        VocabularyBox selectedBox = boxService.getSelectedBox();
        assertEquals("Default", selectedBox.name);

        boxService.createBox("box1", "es", "de", 0, false);
        selectedBox = boxService.getSelectedBox();
        assertEquals("Default", selectedBox.name);

        boxService.createBox("box2", "fr", "de", 0, true);
        selectedBox = boxService.getSelectedBox();
        assertEquals("box2", selectedBox.name);

        selectedBox = boxService.selectBoxByName("box1");
        assertEquals("box1", selectedBox.name);
    }

    @Test
    public void testGetBoxNames() {
        boxService.createBox("English", "en", "de", VocabularyBox.TRANSLATION_DIRECTION_RANDOM, false);
        boxService.createBox("Italian", "it", "de", VocabularyBox.TRANSLATION_DIRECTION_RANDOM, true);
        boxService.createBox("Hungarian", "hu", "de", VocabularyBox.TRANSLATION_DIRECTION_RANDOM, false);

        String[] boxNames = boxService.getBoxNames();
        assertEquals(3, boxNames.length);
        assertEquals("English", boxNames[0]);
        assertEquals("Hungarian", boxNames[1]);
        assertEquals("Italian", boxNames[2]);

        assertEquals("Italian", boxService.getSelectedBox().name);
        boxService.selectBoxByName("English");
        assertEquals("English", boxService.getSelectedBox().name);

        boxNames = boxService.getBoxNames();
        assertEquals(3, boxNames.length);
        assertEquals("English", boxNames[0]);
        assertEquals("Hungarian", boxNames[1]);
        assertEquals("Italian", boxNames[2]);

        VocabularyBox hungarianBox = boxService.findBoxByName("Hungarian");
        boxService.updateBoxName(hungarianBox.id, "HungarianXXL");

        boxNames = boxService.getBoxNames();
        assertEquals(3, boxNames.length);
        assertEquals("English", boxNames[0]);
        assertEquals("HungarianXXL", boxNames[1]);
        assertEquals("Italian", boxNames[2]);
    }
}
