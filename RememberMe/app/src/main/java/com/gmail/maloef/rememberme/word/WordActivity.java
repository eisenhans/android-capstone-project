package com.gmail.maloef.rememberme.word;

import android.app.Fragment;
import android.os.Bundle;

import com.gmail.maloef.rememberme.AbstractRememberMeActivity;
import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeApplication;
import com.gmail.maloef.rememberme.RememberMeIntent;
import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class WordActivity extends AbstractRememberMeActivity implements QueryWordFragment.AnswerListener, ShowWordFragment.ShowWordCallback,
        EditWordFragment.Callback, AddWordFragment.Callback {

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;

    int wordsInCompartment;
    int translationDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this);

        logInfo("intent: " + getIntent() + ", action: " + getIntent().getAction() + ", type: " + getIntent().getType() +
                ", extras: " + getIntent().getExtras());
        if (getIntent().getExtras() != null) {
            logInfo("extra keys: " + getIntent().getExtras().keySet());
        }

        wordsInCompartment = getIntent().getIntExtra(RememberMeIntent.EXTRA_WORDS_IN_COMPARTMENT, -1);
        translationDirection = getIntent().getIntExtra(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, -1);

        if (savedInstanceState != null) {
            return;
        }
        if (isAddWord()) {
            addWord();
        } else {
            queryWord();
        }
    }

    private boolean isAddWord() {
        return getIntent().getAction().equals(RememberMeIntent.ACTION_ADD);
    }

    private void addWord() {
        initToolbar(false, R.string.add_word);

        String foreignWord = getIntent().getStringExtra(RememberMeIntent.EXTRA_FOREIGN_WORD);
        showAddWordFragment(foreignWord);
    }

    private void queryWord() {
        int compartment = getIntent().getIntExtra(RememberMeIntent.EXTRA_COMPARTMENT, -1);
        int boxId = getIntent().getIntExtra(RememberMeIntent.EXTRA_BOX_ID, -1);

        showQueryWordFragment(boxId, compartment, translationDirection);
    }

    public void showWord(int wordId) {
        Word word = wordRepository.findWord(wordId);
        showWord(word, null);
    }

    private void showWord(Word word, String givenAnswer) {
        int compartment = getIntent().getIntExtra(RememberMeIntent.EXTRA_COMPARTMENT, -1);
        initToolbar(true, R.string.compartment_i, String.valueOf(compartment));

        ShowWordFragment fragment = ShowWordFragmentBuilder.newShowWordFragment(givenAnswer, translationDirection, word, wordsInCompartment);
        replaceFragment(fragment);
    }

    @Override
    public void editShownWord(int wordId) {
        initToolbar(false, R.string.edit_word);
        showEditWordFragment(wordId);
    }

    private void showEditWordFragment(int wordId) {
        Fragment fragment = getFragmentManager().findFragmentByTag(EditWordFragment.TAG);
        if (fragment == null) {
            fragment = EditWordFragmentBuilder.newEditWordFragment(translationDirection, wordId);
        }
        getFragmentManager().beginTransaction().replace(R.id.detail_container, fragment, EditWordFragment.TAG).commit();
    }

    @Override
    public void updateOverview() {}

    // ToDo 07.04.16: remove, use superclass methods instead
    private void replaceFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.detail_container, fragment).commit();
    }

    @Override
    public void onWordEntered(Word word, String givenAnswer) {
        logInfo("user entered answer " + givenAnswer + " for current word " + word);
        showWord(word, givenAnswer);
    }

    @Override
    public void showNextWord(boolean moreWordsAvailable) {
        if (moreWordsAvailable) {
            logInfo("showing next word, extras: " + getIntent().getExtras());
            queryWord();
        } else {
            finish();
        }
    }

    @Override
    public void editWordDone(int wordId) {
        showWord(wordId);
    }

    @Override
    public void languageSettingsConfirmed(String foreignLanguage, String nativeLanguage) {}

    @Override
    public void addWordDone() {
        finish();
    }
}
