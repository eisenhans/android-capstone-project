package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;
import com.gmail.maloef.rememberme.translate.google.TranslateLoader;
import com.gmail.maloef.rememberme.translate.google.Translation;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class AddWordFragment extends Fragment implements LoaderManager.LoaderCallbacks<Translation> {

    @Inject VocabularyBoxService boxService;
    @Inject GoogleTranslateService translateService;

    @BindString(R.string.confirm_language_settings_title) String confirmLanguageSettingsTitle;
    @BindString(R.string.confirm_language_settings_message) String confirmLanguageSettingsMessage;
    @BindString(R.string.select_language_settings_title) String selectLanguageSettingsTitle;
    @BindString(R.string.select_language_settings_message) String selectLanguageSettingsMessage;

    @BindString(R.string.no_translation_found) String noTranslationFound;
    @BindString(android.R.string.ok) String okString;

    @Bind(R.id.foreign_word_textview) TextView foreignWordTextView;
    @Bind(R.id.native_word_textview) TextView nativeWordTextView;

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
        loadTranslation();
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

    void loadTranslation() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Translation> onCreateLoader(int id, Bundle args) {
        logInfo("loading translation for word " + foreignWord + " from " + selectedBox.foreignLanguage + " to " + selectedBox.nativeLanguage);
        return new TranslateLoader(getActivity(), translateService, foreignWord, selectedBox.foreignLanguage, selectedBox.nativeLanguage);
    }

    @Override
    public void onLoadFinished(Loader<Translation> translateLoader, Translation translation) {
        logInfo("translation returned by loader: " + translation);

        if (translation.detectedSourceLanguage != null) {
            showConfirmLanguageSettingsDialog(translation.detectedSourceLanguage);
        }
        nativeWord = translation.translatedText;
        if (nativeWord.equals(foreignWord)) {
            //Toast.makeText(getActivity(), noTranslationFound, Toast.LENGTH_SHORT).show();
        } else {
            nativeWordTextView.setText(nativeWord);
        }
    }

    private void showConfirmLanguageSettingsDialog(String detectedSourceLanguage) {
        if (detectedSourceLanguage != null) {
            selectedBox.foreignLanguage = detectedSourceLanguage;
        }
        final String foreignLanguageUsedForTranslation = selectedBox.foreignLanguage;
        final String nativeLanguageUsedForTranslation = selectedBox.nativeLanguage;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        if (foreignLanguageUsedForTranslation.equals(nativeLanguageUsedForTranslation)) {
            alertDialogBuilder.setTitle(selectLanguageSettingsTitle);
            alertDialogBuilder.setMessage(selectLanguageSettingsMessage);
        } else {
            alertDialogBuilder.setTitle(confirmLanguageSettingsTitle);
            alertDialogBuilder.setMessage(confirmLanguageSettingsMessage);
        }
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_language_settings, null);

        final Spinner foreignLanguageSpinner = (Spinner) dialogView.findViewById(R.id.foreignLanguageSpinner);
        Spinner nativeLanguageSpinner = (Spinner) dialogView.findViewById(R.id.nativeLanguageSpinner);

        final LanguageSettingsManager languageSettingsManager = new LanguageSettingsManager(getActivity(), boxService);
        languageSettingsManager.configureForeignLanguageSpinner(foreignLanguageSpinner, detectedSourceLanguage);
        languageSettingsManager.configureNativeLanguageSpinner(nativeLanguageSpinner);

        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setPositiveButton(okString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedBox = boxService.getSelectedBox();
                logInfo("checking if word needs to be translated again: " +
                        "translated from " + foreignLanguageUsedForTranslation + " to " + nativeLanguageUsedForTranslation +
                        ", need translation from " + selectedBox.foreignLanguage + " to " + selectedBox.nativeLanguage);

                if (foreignLanguageUsedForTranslation.equals(selectedBox.foreignLanguage) &&
                        nativeLanguageUsedForTranslation.equals(selectedBox.nativeLanguage)) {
                    logInfo("no re-translation necessary");
                    return;
                }
                logInfo("re-translation needed");
                loadTranslation();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        languageSettingsManager.setLanguageSelectionListener(new LanguageSettingsManager.LanguageSelectionListener() {
            @Override
            public void selectionChanged(boolean isSelectionOk) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isSelectionOk);
            }
        });

        alertDialog.show();
        boolean enableOkButton = !nativeLanguageUsedForTranslation.equals(foreignLanguageUsedForTranslation);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enableOkButton);
    }

    @Override
    public void onLoaderReset(Loader<Translation> loader) {}

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
