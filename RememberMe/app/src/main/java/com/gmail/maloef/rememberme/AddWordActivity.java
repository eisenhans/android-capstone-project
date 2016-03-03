package com.gmail.maloef.rememberme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;
import com.gmail.maloef.rememberme.translate.google.TranslateLoader;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class AddWordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    @Inject
    GoogleTranslateService translateService;

    String foreignWord;
    String foreignLanguage = "en";
    String nativeLanguage = "de";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RememberMeApplication.injector().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        foreignWord = wordToAdd();
        logInfo("foreign word: " + foreignWord);
        if (foreignWord == null) {
            return;
        }
        Loader<String> translateLoader = getSupportLoaderManager().initLoader(0, null, this);
        logInfo("created loader for foreign word " + foreignWord + ": " + translateLoader.getId());
    }

    String wordToAdd() {
        Intent intent = getIntent();
        logInfo("received intent " + intent);
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                logInfo("received intent with extras " + intent.getExtras());
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    for (String key : extras.keySet()) {
                        logInfo("key: " + key + ", value: " + extras.get(key));
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    logInfo("received intent with clip data " + intent.getClipData());
                }

            }
        }
        return "porcupine";
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        TranslateLoader translateLoader = new TranslateLoader(this, foreignWord, foreignLanguage, nativeLanguage, translateService);

        logInfo("created translateLoader " + translateLoader);

        return translateLoader;
    }

    @Override
    public void onLoadFinished(Loader<String> translateLoader, String translation) {
        logInfo("translation returned by loader: " + translation);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
