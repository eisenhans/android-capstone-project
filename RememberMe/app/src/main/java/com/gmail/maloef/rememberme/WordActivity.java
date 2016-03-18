package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WordActivity extends DrawerActivity {

    private static final String ADD_WORD = "addWord";
    private static final String QUERY_WORD = "queryWord";
    private static final String SHOW_WORD = "showWord";

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigationView) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;

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

        String action = getIntent().getAction();
        if (action.equals(Intent.ACTION_SEND) || action.equals(RememberMeIntent.ACTION_ADD)) {
            toolbar.setTitle(getString(R.string.add_word));
            addWord();
        } else if (action.equals(RememberMeIntent.ACTION_QUERY)) {
            setCompartmentToolbarTitle();
            queryWord();
        } else if (action.equals(RememberMeIntent.ACTION_SHOW)) {
            setCompartmentToolbarTitle();
            showWord();
        }
    }

    private void setCompartmentToolbarTitle() {
        toolbar.setTitle(getString(R.string.compartment_i, 999));
    }

    private void setFragment(Fragment fragment) {
        fragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.word_content, fragment).commit();
    }

    private void addWord() {
        setFragment(new AddWordFragment());
    }

    private void queryWord() {
        Bundle extras = getIntent().getExtras();
        int boxId = extras.getInt(RememberMeIntent.EXTRA_BOX_ID, -1);
        int compartment = extras.getInt(RememberMeIntent.EXTRA_COMPARTMENT, -1);
        int translationDirection = extras.getInt(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, -1);

        Pair<String, String> word = wordRepository.getNextWord(boxId, compartment);

        String queryWord;
        String answerWord;
        if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE) {
            queryWord = word.first;
            answerWord = word.second;
        } else {
            queryWord = word.second;
            answerWord = word.first;
        }
        displayQueryWordFragment(queryWord, answerWord);
    }

    public void displayQueryWordFragment(String queryWord, String answerWord) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        QueryWordFragment queryWordFragment = (QueryWordFragment) getFragmentManager().findFragmentByTag(QUERY_WORD);
        ShowWordFragment showWordFragment = (ShowWordFragment) getFragmentManager().findFragmentByTag(SHOW_WORD);

        if (showWordFragment != null && showWordFragment.isAdded()) {
            ft.hide(showWordFragment);
        }
        if (queryWordFragment == null) {
            queryWordFragment = new QueryWordFragment();
        }
        if (queryWordFragment.isAdded()) {
            ft.show(queryWordFragment);
        } else {
            ft.add(R.id.word_content, queryWordFragment, QUERY_WORD);
        }
        queryWordFragment.updateFragment(queryWord, answerWord);
        ft.commit();
    }

    private void showWord() {
        Bundle extras = getIntent().getExtras();

        String queryWord = extras.getString(RememberMeIntent.EXTRA_QUERY_WORD);
        String correctAnswer = extras.getString(RememberMeIntent.EXTRA_CORRECT_ANSWER);
        String givenAnswer = extras.getString(RememberMeIntent.EXTRA_GIVEN_ANSWER);
        displayShowWordFragment(queryWord, correctAnswer, givenAnswer);
    }

    public void displayShowWordFragment(String queryWord, String correctAnswer, String givenAnswer) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        QueryWordFragment queryWordFragment = (QueryWordFragment) getFragmentManager().findFragmentByTag(QUERY_WORD);
        ShowWordFragment showWordFragment = (ShowWordFragment) getFragmentManager().findFragmentByTag(SHOW_WORD);

        if (queryWordFragment != null && queryWordFragment.isAdded()) {
            ft.hide(queryWordFragment);
        }
        if (showWordFragment == null) {
            showWordFragment = new ShowWordFragment();
        }
        if (showWordFragment.isAdded()) {
            ft.show(showWordFragment);
        } else {
            ft.add(R.id.word_content, showWordFragment, SHOW_WORD);
        }
        showWordFragment.updateFragment(queryWord, correctAnswer, givenAnswer);
        ft.commit();
    }

}
