package com.gmail.maloef.rememberme;

import android.os.Bundle;

import butterknife.ButterKnife;

public class WordListActivity extends AbstractRememberMeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_list);
        ButterKnife.bind(this);
        RememberMeApplication.injector().inject(this);

        initToolbar(true, R.string.words_learned);
    }
}
