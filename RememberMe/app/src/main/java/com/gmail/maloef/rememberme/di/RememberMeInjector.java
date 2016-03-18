package com.gmail.maloef.rememberme.di;

import com.gmail.maloef.rememberme.AddWordFragment;
import com.gmail.maloef.rememberme.MainActivity;
import com.gmail.maloef.rememberme.WordActivity;
import com.gmail.maloef.rememberme.service.LanguageUpdateService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RememberMeModule.class)
public interface RememberMeInjector {

    void inject(MainActivity mainActivity);

    void inject(WordActivity wordActivity);
    void inject(AddWordFragment addWordFragment);
//    void inject(QueryWordFragment queryWordFragment);
//    void inject(ShowWordFragment showWordFragment);

    void inject(LanguageUpdateService languageUpdateService);
}
