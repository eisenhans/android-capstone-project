package com.gmail.maloef.rememberme;

import android.util.Log;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public abstract class AbstractRobolectricTest {

    @Before
    public void before() throws Exception {
        ShadowLog.stream = System.out;
    }

    public void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
