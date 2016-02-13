package com.gmail.maloef.rememberme;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RobolectricTest {

    @Before
    public void before() {
        ShadowLog.stream = System.out;
    }

    @Test
    public void test() {
        logInfo("test message");
        System.out.println("hi there");
    }

    static void logInfo(String message) {
        Log.i(RobolectricTest.class.getSimpleName(), message);
    }
}