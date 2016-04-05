package com.gmail.maloef.rememberme.di;

import com.gmail.maloef.rememberme.AddWordFragment;
import com.gmail.maloef.rememberme.EditWordFragment;
import com.gmail.maloef.rememberme.MainActivity;
import com.gmail.maloef.rememberme.MemorizeActivity;
import com.gmail.maloef.rememberme.MemorizeFragment;
import com.gmail.maloef.rememberme.QueryWordFragment;
import com.gmail.maloef.rememberme.ShowWordFragment;
import com.gmail.maloef.rememberme.WordActivity;
import com.gmail.maloef.rememberme.service.LanguageUpdater;
import com.gmail.maloef.rememberme.wordlist.WordListActivity;
import com.gmail.maloef.rememberme.wordlist.WordListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RememberMeModule.class)
public interface RememberMeInjector {

    void inject(MainActivity mainActivity);
    void inject(WordActivity wordActivity);
    void inject(MemorizeActivity memorizeActivity);
    void inject(WordListActivity wordListActivity);

    void inject(AddWordFragment addWordFragment);
    void inject(EditWordFragment editWordFragment);
    void inject(QueryWordFragment queryWordFragment);
    void inject(ShowWordFragment showWordFragment);
    void inject(MemorizeFragment memorizeFragment);
    void inject(WordListFragment wordListFragment);

//    void inject(RememberMePreferences prefs);
    void inject(LanguageUpdater languageUpdater);
}
