package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WordActivity extends DrawerActivity implements QueryWordFragment.AnswerListener, ShowWordFragment.NextWordListener {

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigationView) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;

    int translationDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initDrawerToggle(drawerLayout, toolbar);

        // ToDo 16.03.16:
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                logInfo("navigationItem selected: " + item);
                return true;
            }
        });

        if (!"text/plain".equals(getIntent().getType())) {
            // ToDo 17.03.16: handle this
            logWarn("intent type is " + getIntent().getType());
        }

        logInfo("intent: " + getIntent() + ", action: " + getIntent().getAction() + ", type: " + getIntent().getType() +
                ", extras: " + getIntent().getExtras());
        if (getIntent().getExtras() != null) {
            logInfo("extra keys: " + getIntent().getExtras().keySet());
        }

        translationDirection = getIntent().getIntExtra(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, -1);

        if (isAddAction()) {
            toolbar.setTitle(getString(R.string.add_word));
        } else {
            setCompartmentToolbarTitle();
        }

        if (savedInstanceState != null) {
            return;
        }
        if (isAddAction()) {
            addWord();
        } else {
            queryWord();
        }
    }

    private boolean isAddAction() {
        String action = getIntent().getAction();
        return Intent.ACTION_SEND.equals(action) || RememberMeIntent.ACTION_ADD.equals(action);
    }

    private void addWord() {
        String foreignWord = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        AddWordFragment fragment = AddWordFragmentBuilder.newAddWordFragment(foreignWord);

        replaceFragment(fragment);
    }

    private void queryWord() {
        int boxId = getIntent().getIntExtra(RememberMeIntent.EXTRA_BOX_ID, -1);
        int compartment = getIntent().getIntExtra(RememberMeIntent.EXTRA_COMPARTMENT, -1);

        QueryWordFragment fragment = QueryWordFragmentBuilder.newQueryWordFragment(boxId, compartment, translationDirection);
        replaceFragment(fragment);
    }

    private void showWord(Word word, String givenAnswer) {
        ShowWordFragment fragment = ShowWordFragmentBuilder.newShowWordFragment(givenAnswer, translationDirection, word);
        replaceFragment(fragment);
    }

    private void setCompartmentToolbarTitle() {
        int compartment = getIntent().getExtras().getInt(RememberMeIntent.EXTRA_COMPARTMENT, -1);
        toolbar.setTitle(getString(R.string.compartment_i, compartment));
    }

    private void replaceFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.word_content, fragment).commit();
    }

    @Override
    public void onWordEntered(Word word, String givenAnswer) {
        logInfo("user entered answer " + givenAnswer + " for current word " + word);
        showWord(word, givenAnswer);
    }

    @Override
    public void onNextWordButtonClicked(boolean moreWordsAvailable) {
        if (moreWordsAvailable) {
            logInfo("showing next word, extras: " + getIntent().getExtras());
            queryWord();
        } else {
            finish();
        }
    }
}
