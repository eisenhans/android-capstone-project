package com.gmail.maloef.rememberme.persistence;

import android.app.Application;

import com.gmail.maloef.rememberme.domain.BoxOverview;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class CompartmentRepositoryTest extends AbstractPersistenceTest {

    Application application;
    VocabularyBoxRepository boxService;
    CompartmentRepository compartmentService;
    WordRepository wordService;

    int boxId;

    @Before
    public void before() throws Exception {
        super.before();
        application = RuntimeEnvironment.application;
        boxService = new VocabularyBoxRepository(application);
        compartmentService = new CompartmentRepository(application);
        wordService = new WordRepository(application);

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

        int ointmentId = wordService.createWord(boxId, "ointment", "Salbe");
        wordService.updateRepeatDate(ointmentId);

        int biasId = wordService.createWord(boxId, "bias", "Tendenz, Neigung");
        wordService.updateRepeatDate(biasId);
        Long timestamp = new Date().getTime();

        wordService.createWord(boxId, 2, "emissary", "Abgesandter");

        BoxOverview boxOverview = compartmentService.getBoxOverview(boxId);

        assertEquals(3, boxOverview.getWordCount(1));
        assertAlmostEqual(timestamp, boxOverview.getEarliestLastRepeatDate(1), 100);

        assertEquals(1, boxOverview.getWordCount(2));
        assertEquals(0, boxOverview.getEarliestLastRepeatDate(2));

        for (int compartment = 3; compartment <= 5; compartment++) {
            assertCompartmentEmpty(boxOverview, compartment);
        }
    }

    void assertCompartmentEmpty(BoxOverview boxOverview, int compartment) {
        assertEquals(0, boxOverview.getWordCount(compartment));
        assertEquals(0, boxOverview.getEarliestLastRepeatDate(compartment));
    }

    void assertAlmostEqual(long first, long second, long deltaAllowed) {
        if (Math.abs(first - second) > deltaAllowed) {
            throw new AssertionError("difference between values should at most " + deltaAllowed + ", but first is " + first + ", second is " + second);
        }
    }

}
