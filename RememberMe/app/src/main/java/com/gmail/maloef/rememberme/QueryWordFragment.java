package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.maloef.rememberme.persistence.WordRepository;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QueryWordFragment extends Fragment {

    @Inject WordRepository wordRepository;

    @Bind(R.id.query_textview) TextView queryTextView;
    @Bind(R.id.answer_edittext) EditText answerEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_query_word, container, false);

        if (savedInstanceState != null) {
            // ToDo 14.03.16: can this information be used somehow?
            logInfo("savedInstanceState exists: " + savedInstanceState + ", keys: " + savedInstanceState.keySet());
        }

        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this, rootView);

        Bundle args = getArguments();
        logInfo("args: " + args + ", keys: " + args.keySet());

        int compartment = getArguments().getInt(RememberMeIntent.EXTRA_COMPARTMENT, -1);

        return rootView;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
