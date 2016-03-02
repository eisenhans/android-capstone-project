package com.gmail.maloef.rememberme.di;

import com.gmail.maloef.rememberme.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RememberMeModule.class)
public interface RememberMeInjector {

    void inject(MainActivity mainActivity);
}
