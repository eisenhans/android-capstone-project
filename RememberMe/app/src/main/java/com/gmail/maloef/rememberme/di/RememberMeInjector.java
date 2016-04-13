package com.gmail.maloef.rememberme.di;

import com.gmail.maloef.rememberme.activity.main.MainActivity;
import com.gmail.maloef.rememberme.activity.memorize.MemorizeActivity;
import com.gmail.maloef.rememberme.activity.memorize.MemorizeFragment;
import com.gmail.maloef.rememberme.activity.word.AddWordFragment;
import com.gmail.maloef.rememberme.activity.word.EditWordFragment;
import com.gmail.maloef.rememberme.activity.word.QueryWordFragment;
import com.gmail.maloef.rememberme.activity.word.ShowWordFragment;
import com.gmail.maloef.rememberme.activity.word.WordActivity;
import com.gmail.maloef.rememberme.activity.wordlist.WordListActivity;
import com.gmail.maloef.rememberme.activity.wordlist.WordListFragment;

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
}
