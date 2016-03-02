package com.gmail.maloef.rememberme.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class RememberMeModule {

    Context context;

    public RememberMeModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }


//    @Provides @Singleton
//    public VocabularyBoxService provideVocabularyBoxService() {
//        return new VocabularyBoxService(context);
//    }
//
//    @Provides @Singleton
//    public CompartmentService provideCompartmentService() {
//        return new CompartmentService(context);
//    }
//
//    @Provides @Singleton
//    public WordService provideWordService() {
//        return new WordService(context);
//    }

}
