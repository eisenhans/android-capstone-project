package com.gmail.maloef.rememberme;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.WordRepository;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QueryWordFragment extends AbstractWordFragment {

//    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;

    @Bind(R.id.query_textview) TextView queryTextView;
    @Bind(R.id.answer_edittext) EditText answerEditText;

    String queryWord;
    String answerWord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_query_word, container, false);

        if (savedInstanceState != null) {
            // ToDo 14.03.16: can this information be used somehow?
            logInfo("savedInstanceState exists: " + savedInstanceState + ", keys: " + savedInstanceState.keySet());
        }

        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this, rootView);

        configureEditTextBehavior(answerEditText);

        Bundle args = getArguments();
        logInfo("args: " + args + ", keys: " + args.keySet());

        int boxId = getArguments().getInt(RememberMeIntent.EXTRA_BOX_ID, -1);
        int compartment = getArguments().getInt(RememberMeIntent.EXTRA_COMPARTMENT, -1);

        Pair<String, String> word = wordRepository.getNextWord(boxId, compartment);
        // ToDo 17.03.16: if word is null?

        int translationDirection = getArguments().getInt(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, -1);
        if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE) {
            queryWord = word.first;
            answerWord = word.second;
        } else {
            queryWord = word.second;
            answerWord = word.first;
        }
        queryTextView.setText(queryWord);

        return rootView;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
