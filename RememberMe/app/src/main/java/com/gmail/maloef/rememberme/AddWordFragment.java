package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddWordFragment extends Fragment {

    @Bind(R.id.foreign_word_textview) TextView foreignWordTextView;
    @Bind(R.id.native_word_textview) TextView nativeWordTextView;

    @Inject VocabularyBoxService boxService;
    @Inject GoogleTranslateService translateService;

    VocabularyBox selectedBox;
    String foreignWord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_word, container, false);

        if (savedInstanceState != null) {
            // ToDo 04.03.2016: test this
            return rootView;
        }

        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this, rootView);

        selectedBox = boxService.getSelectedBox();

        foreignWordTextView.setText("foreign");
        nativeWordTextView.setText("native");

        return rootView;
    }
}
