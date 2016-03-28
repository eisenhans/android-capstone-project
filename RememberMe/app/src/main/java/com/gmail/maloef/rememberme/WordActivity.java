package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class WordActivity extends AbstractRememberMeActivity implements QueryWordFragment.AnswerListener, ShowWordFragment.NextWordListener {

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;

    int wordsInCompartment;
    int translationDirection;
    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this);

        if (!"text/plain".equals(getIntent().getType())) {
            // ToDo 17.03.16: handle this
            logWarn("intent type is " + getIntent().getType());
        }

        logInfo("intent: " + getIntent() + ", action: " + getIntent().getAction() + ", type: " + getIntent().getType() +
                ", extras: " + getIntent().getExtras());
        if (getIntent().getExtras() != null) {
            logInfo("extra keys: " + getIntent().getExtras().keySet());
        }

        wordsInCompartment = getIntent().getIntExtra(RememberMeIntent.EXTRA_WORDS_IN_COMPARTMENT, -1);
        translationDirection = getIntent().getIntExtra(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, -1);
        startTime = getIntent().getLongExtra(RememberMeIntent.EXTRA_START_TIME, Long.MAX_VALUE);

        if (isAddAction()) {
            initToolbar(false, R.string.add_word);
        } else {
            int compartment = getIntent().getExtras().getInt(RememberMeIntent.EXTRA_COMPARTMENT, -1);
           initToolbar(true, R.string.compartment_i, String.valueOf(compartment));
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

        QueryWordFragment fragment = QueryWordFragmentBuilder.newQueryWordFragment(boxId, compartment, startTime, translationDirection);
        replaceFragment(fragment);
    }

    private void showWord(Word word, String givenAnswer) {
        ShowWordFragment fragment = ShowWordFragmentBuilder.newShowWordFragment(givenAnswer, translationDirection, word, wordsInCompartment);
        replaceFragment(fragment);
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
