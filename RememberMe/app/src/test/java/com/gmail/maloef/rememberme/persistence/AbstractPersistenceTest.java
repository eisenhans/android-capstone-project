package com.gmail.maloef.rememberme.persistence;

import android.content.ContentProvider;

import com.gmail.maloef.rememberme.AbstractRobolectricTest;

import org.junit.Before;
import org.robolectric.shadows.ShadowContentResolver;

import java.lang.reflect.Field;

public abstract class AbstractPersistenceTest extends AbstractRobolectricTest {

    ContentProvider contentProvider;

    @Before
    public void before() throws Exception {
        super.before();
        // The generated class VocabularyBoxDatabase has a field named instance. This has to be set to null. Otherwise
        // it is not possible to run several tests - Robolectric problem.
        resetSingleton(com.gmail.maloef.rememberme.persistence.generated.VocabularyBoxDatabase.class, "instance");

        if (contentProvider == null) {
            contentProvider = new com.gmail.maloef.rememberme.persistence.generated.VocabularyBoxProvider();
            contentProvider.onCreate();
            ShadowContentResolver.registerProvider(VocabularyBoxProvider.AUTHORITY, contentProvider);
            logInfo("contentProvider: " + contentProvider);
        }

        contentProvider.delete(VocabularyBoxProvider.Word.WORDS, null, null);
        contentProvider.delete(VocabularyBoxProvider.Compartment.COMPARTMENTS, null, null);
        contentProvider.delete(VocabularyBoxProvider.Word.WORDS, null, null);
    }

    private void resetSingleton(Class clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }
}
