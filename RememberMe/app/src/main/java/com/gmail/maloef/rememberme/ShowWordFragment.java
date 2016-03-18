package com.gmail.maloef.rememberme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowWordFragment extends AbstractWordFragment {

    private static final String QUERY_WORD = "queryWord";
    private static final String CORRECT_ANSWER = "correctAnswer";
    private static final String GIVEN_ANSWER = "givenAnswer";

    @Bind(R.id.query_textview) TextView queryTextView;
    @Bind(R.id.answer_textview) TextView answerTextView;

    String queryWord;
    String correctAnswer;
    String givenAnswer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_word, container, false);

        ButterKnife.bind(this, rootView);

        queryTextView.setText(queryWord);
        answerTextView.setText(correctAnswer);

        return rootView;
    }

    public void updateFragment(String queryWord, String correctAnswer, String givenAnswer) {
        this.queryWord = queryWord;
        this.correctAnswer = correctAnswer;
        this.givenAnswer = givenAnswer;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
