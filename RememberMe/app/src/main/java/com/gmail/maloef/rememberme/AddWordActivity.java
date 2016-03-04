package com.gmail.maloef.rememberme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;
import com.gmail.maloef.rememberme.translate.google.TranslateLoader;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class AddWordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    @Inject
    VocabularyBoxService boxService;

    @Inject
    GoogleTranslateService translateService;

    VocabularyBox selectedBox;

    String foreignWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        if (savedInstanceState != null) {
            return;
        }

        ButterKnife.bind(this);
        RememberMeApplication.injector().inject(this);

        selectedBox = boxService.getSelectedBox();

        AddWordFragment fragment = new AddWordFragment();
        fragment.setArguments(getIntent().getExtras());

        // add the fragment to the FrameLayout add_word_container defined in activity_add_word.xml
        getFragmentManager().beginTransaction().add(R.id.add_word_container, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        foreignWord = wordToAdd();
        if (foreignWord == null) {
            logInfo("nothing to translate");
            return;
        }
        getSupportLoaderManager().initLoader(0, null, this);
    }

    String wordToAdd() {
        Intent intent = getIntent();
        if (!Intent.ACTION_SEND.equals(intent.getAction())) {
            logInfo("intent action is " + intent.getAction() + " - will be ignored");
            return null;
        }
        if (!"text/plain".equals(intent.getType())) {
            logInfo("intent type is " + intent.getType() + " - will be ignored");
            return null;
        }
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText == null) {
            logInfo("intent has no extra for key " + Intent.EXTRA_TEXT + ", extras exist only for these keys: " + intent.getExtras().keySet());
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
        return new TranslateLoader(this, translateService, foreignWord, foreignLanguage, selectedBox.nativeLanguage);
    }

    @Override
    public void onLoadFinished(Loader<String> translateLoader, String translation) {
        logInfo("translation returned by loader: " + translation);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {}

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
