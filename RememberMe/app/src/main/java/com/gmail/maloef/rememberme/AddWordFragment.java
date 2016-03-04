package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;
import com.gmail.maloef.rememberme.translate.google.TranslateLoader;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddWordFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    @Bind(R.id.foreign_word_textview) TextView foreignWordTextView;
    @Bind(R.id.native_word_textview) TextView nativeWordTextView;

    @Inject VocabularyBoxService boxService;
    @Inject GoogleTranslateService translateService;

    VocabularyBox selectedBox;
    String foreignWord;
    String nativeWord;

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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        foreignWord = wordToAdd();
        if (foreignWord == null) {
            logInfo("nothing to translate");
            return;
        }
        foreignWordTextView.setText(foreignWord);
        getLoaderManager().initLoader(0, null, this);
    }

    String wordToAdd() {
        if (!getArguments().containsKey(Intent.EXTRA_TEXT)) {
            logInfo("bundle does not contain key " + Intent.EXTRA_TEXT + ", keys are: " + getArguments().keySet());
            return null;
        }
        String sharedText = (String) getArguments().get(Intent.EXTRA_TEXT);
        if (sharedText == null) {
            logWarn("bundle contains key " + Intent.EXTRA_TEXT + ", but value is null");
            return null;
        }
        return sharedText;
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        String foreignLanguage = selectedBox.foreignLanguage;
        if (foreignLanguage.equals("undefined")) {
            // ToDo 04.03.2016: guess language
            foreignLanguage = "en";
        }
        return new TranslateLoader(getActivity(), translateService, foreignWord, foreignLanguage, selectedBox.nativeLanguage);
    }

    @Override
    public void onLoadFinished(Loader<String> translateLoader, String translation) {
        logInfo("translation returned by loader: " + translation);
        nativeWord = translation;
        nativeWordTextView.setText(nativeWord);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {}

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
