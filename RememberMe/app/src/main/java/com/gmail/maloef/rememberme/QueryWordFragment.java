package com.gmail.maloef.rememberme;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

    int boxId;
    int translationDirection;
    int compartment;

    String queryWord;
    String answerWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RememberMeApplication.injector().inject(this);

        logInfo("args: " + getArguments() + ", keys: " + getArguments().keySet());

        boxId = getArguments().getInt(RememberMeIntent.EXTRA_BOX_ID, -1);
        compartment = getArguments().getInt(RememberMeIntent.EXTRA_COMPARTMENT, -1);

        Pair<String, String> word = wordRepository.getNextWord(boxId, compartment);
        // ToDo 17.03.16: if word is null?

        translationDirection = getArguments().getInt(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, -1);
        if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE) {
            queryWord = word.first;
            answerWord = word.second;
        } else {
            queryWord = word.second;
            answerWord = word.first;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_query_word, container, false);

        if (savedInstanceState != null) {
            // ToDo 14.03.16: can this information be used somehow?
            logInfo("savedInstanceState exists: " + savedInstanceState + ", keys: " + savedInstanceState.keySet());
        }
        ButterKnife.bind(this, rootView);
        configureEditTextBehavior(answerEditText);
        queryTextView.setText(queryWord);

        answerEditText.setOnEditorActionListener(createDoneListener());
        return rootView;
    }

    private TextView.OnEditorActionListener createDoneListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logInfo("done");
                    WordActivity wordActivity = (WordActivity) getActivity();
//                    Bundle args = new Bundle();
//                    args.putString(RememberMeIntent.QUERY_WORD, queryWord);
                    logInfo("queryWord: " + queryWord + ", correct answer: " + answerWord + ", given answer: " + answerEditText.getText().toString());
                    wordActivity.displayShowWordFragment(queryWord, answerWord, answerEditText.getText().toString());

//                    Intent intent = new Intent(getActivity(), WordActivity.class)
//                            .setAction(RememberMeIntent.ACTION_SHOW)
//                            .putExtra(RememberMeIntent.EXTRA_BOX_ID, boxId)
//                            .putExtra(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, translationDirection)
//                            .putExtra(RememberMeIntent.EXTRA_COMPARTMENT, compartment);
//                    startActivity(intent);
                    return true;
                }
                return false;
            }
        };
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
