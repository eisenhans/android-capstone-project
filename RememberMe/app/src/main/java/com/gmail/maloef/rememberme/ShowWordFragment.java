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

    @Bind(R.id.query_textview) TextView queryTextView;
    @Bind(R.id.answer_textview) TextView answerTextView;

    String queryWord;
    String correctAnswer;
    String givenAnswer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RememberMeApplication.injector().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_word, container, false);

        if (savedInstanceState != null) {
            // ToDo 14.03.16: can this information be used somehow?
            logInfo("savedInstanceState exists: " + savedInstanceState + ", keys: " + savedInstanceState.keySet());
        }
        ButterKnife.bind(this, rootView);

//        String queryWord = (String) getArguments().get(RememberMeIntent.QUERY_WORD);
        queryTextView.setText(queryWord);
        answerTextView.setText(correctAnswer);

        return rootView;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
