package com.gmail.maloef.rememberme.di;

import com.gmail.maloef.rememberme.word.AddWordFragment;
import com.gmail.maloef.rememberme.word.EditWordFragment;
import com.gmail.maloef.rememberme.MainActivity;
import com.gmail.maloef.rememberme.memorize.MemorizeActivity;
import com.gmail.maloef.rememberme.memorize.MemorizeFragment;
import com.gmail.maloef.rememberme.word.QueryWordFragment;
import com.gmail.maloef.rememberme.word.ShowWordFragment;
import com.gmail.maloef.rememberme.word.WordActivity;
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
