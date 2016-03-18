package com.gmail.maloef.rememberme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QueryWordFragment extends AbstractWordFragment {

    private static final String QUERY_WORD = "queryWord";
    private static final String ANSWER_WORD = "answerWord";

    @Bind(R.id.query_textview) TextView queryTextView;
    @Bind(R.id.answer_edittext) EditText answerEditText;

    String queryWord;
    String answerWord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_query_word, container, false);

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
                    hideKeyboard();
                    logInfo("done");
                    String givenAnswer = answerEditText.getText().toString();
                    logInfo("queryWord: " + queryWord + ", correct answer: " + answerWord + ", given answer: " + givenAnswer);

                    Intent intent = new Intent(getActivity(), WordActivity.class)
                            .setAction(RememberMeIntent.ACTION_SHOW)
                            .putExtra(RememberMeIntent.EXTRA_QUERY_WORD, queryWord)
                            .putExtra(RememberMeIntent.EXTRA_CORRECT_ANSWER, answerWord)
                            .putExtra(RememberMeIntent.EXTRA_GIVEN_ANSWER, givenAnswer);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        };
    }

    public void updateFragment(String queryWord, String answerWord) {
        this.queryWord = queryWord;
        this.answerWord = answerWord;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
