package com.gmail.maloef.rememberme.persistence;

import android.app.Application;

import com.gmail.maloef.rememberme.domain.BoxOverview;
import com.gmail.maloef.rememberme.service.CompartmentService;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;
import com.gmail.maloef.rememberme.service.WordService;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CompartmentServiceTest extends AbstractPersistenceTest {

    Application application;
    VocabularyBoxService boxService;
    CompartmentService compartmentService;
    WordService wordService;

    int boxId;

    @Before
    public void before() throws Exception {
        super.before();
        application = RuntimeEnvironment.application;
        boxService = new VocabularyBoxService(application);
        compartmentService = new CompartmentService(application);
        wordService = new WordService(application);

        boxId = boxService.createDefaultBox();
    }

    @Test
    public void testGetBoxOverviewBoxEmpty() {
        BoxOverview boxOverview = compartmentService.getBoxOverview(boxId);
        for (int compartment = 1; compartment <= 5; compartment++) {
            assertCompartmentEmpty(boxOverview, compartment);
        }
    }

    @Test
    public void testGetBoxOverview() {
        wordService.createWord(boxId, "porcupine", "Stachelschwein");
        wordService.createWord(boxId, "ointment", "Salbe");

        int biasId = wordService.createWord(boxId, "bias", "Tendenz, Neigung");
        wordService.updateRepeatDate(biasId);
        Long now = new Date().getTime();

        wordService.createWord(boxId, 2, "emissary", "Abgesandter");
        BoxOverview boxOverview = compartmentService.getBoxOverview(boxId);

        assertEquals(3, boxOverview.getWordCount(1));
        assertEquals(now, boxOverview.getEarliestLastRepeatDate(1));

        assertEquals(1, boxOverview.getWordCount(2));
        assertNull(boxOverview.getEarliestLastRepeatDate(2));

        for (int compartment = 3; compartment <= 5; compartment++) {
            assertCompartmentEmpty(boxOverview, compartment);
        }
    }

    void assertCompartmentEmpty(BoxOverview boxOverview, int compartment) {
        assertEquals(0, boxOverview.getWordCount(compartment));
        assertNull(boxOverview.getEarliestLastRepeatDate(compartment));
    }

}
