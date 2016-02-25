package com.gmail.maloef.rememberme.persistence;

import android.app.Application;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

public class VocabularyBoxServiceTest extends AbstractPersistenceTest {

    private Application application;
    private VocabularyBoxService boxService;

    @Before
    public void before() throws Exception {
        super.before();
        application = RuntimeEnvironment.application;
        boxService = new VocabularyBoxService(application);
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

}
