package com.gmail.maloef.rememberme.wordlist;

import android.app.Fragment;
import android.os.Bundle;

import com.gmail.maloef.rememberme.AbstractRememberMeActivity;
import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeApplication;
import com.gmail.maloef.rememberme.RememberMeIntent;

import butterknife.ButterKnife;

public class WordListActivity extends AbstractRememberMeActivity {

    private static final String WORD_LIST_FRAGMENT_TAG = "wordListFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RememberMeApplication.injector().inject(this);
        setContentView(R.layout.activity_word_list);
        ButterKnife.bind(this);
        initToolbar(true, R.string.words_learned);

        int boxId = getIntent().getIntExtra(RememberMeIntent.EXTRA_BOX_ID, -1);
        int compartment = getIntent().getIntExtra(RememberMeIntent.EXTRA_COMPARTMENT, -1);

        Fragment fragment = getFragmentManager().findFragmentByTag(WORD_LIST_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = WordListFragmentBuilder.newWordListFragment(boxId, compartment);
        }
        getFragmentManager().beginTransaction().replace(R.id.word_list_content, fragment, WORD_LIST_FRAGMENT_TAG).commit();
    }


}
