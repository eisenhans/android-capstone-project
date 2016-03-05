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
    @BindString(android.R.string.ok) String okString;

    @Bind(R.id.foreign_word_textview) TextView foreignWordTextView;
    @Bind(R.id.native_word_textview) TextView nativeWordTextView;

    VocabularyBox selectedBox;
    String foreignWord;
    String nativeWord;
    boolean anotherTranslationNeeded;

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
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Translation> onCreateLoader(int id, Bundle args) {
        return new TranslateLoader(getActivity(), translateService, foreignWord, selectedBox.foreignLanguage, selectedBox.nativeLanguage);
    }

    @Override
    public void onLoadFinished(Loader<Translation> translateLoader, Translation translation) {
        logInfo("translation returned by loader: " + translation);

        anotherTranslationNeeded = false;
        if (translation.detectedSourceLanguage != null) {
            showConfirmLanguageSettingsDialog();
        }
        if (!anotherTranslationNeeded) {
            nativeWord = translation.translatedText;
            nativeWordTextView.setText(nativeWord);
        }
    }

    private void showConfirmLanguageSettingsDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(confirmLanguageSettingsTitle);
        alertDialogBuilder.setMessage(confirmLanguageSettingsMessage);

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_language_settings, null);

//        Spinner foreignLanguageSpinner = (Spinner) dialogView.findViewById(R.id.foreignLanguageSpinner);
//        Spinner nativeLanguageSpinner = (Spinner) dialogView.findViewById(R.id.nativeLanguageSpinner);
//
//        LanguageSettingsManager languageSettingsManager = new LanguageSettingsManager(getActivity(), boxService);

        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setPositiveButton(okString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logInfo("ok button clicked: dialog = " + dialog + ", which = " + which);
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
//
//        final EditText editText = new EditText(this);
//        alertDialogBuilder.setView(editText);
//
//        String ok = okString;
//        alertDialogBuilder.setPositiveButton(ok, null);
//        alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
//
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//
//        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                okButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String newBoxName = editText.getText().toString();
//                            if (newBoxName == null || newBoxName.isEmpty()) {
//                                return;
//                            }
//                            if (newBoxName.equals(selectedBox.name)) {
//                                // user entered the same name again - just ignore this
//                                alertDialog.dismiss();
//                                return;
//                            }
//                            if (boxService.isBoxSaved(newBoxName)) {
//                                // user entered a name that already exists - just keep the dialog open
//                                Toast.makeText(getApplicationContext(), boxExistsString, Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            boxService.updateBoxName(selectedBox._id, newBoxName);
//                            logInfo("updated box name: " + newBoxName);
//                            updateBoxSpinner();
//                            alertDialog.dismiss();
//                        }
//                    }
//                );
//            }
//        });
//
//        alertDialog.show();
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
