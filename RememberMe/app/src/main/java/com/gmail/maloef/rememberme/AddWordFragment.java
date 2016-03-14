package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.gmail.maloef.rememberme.service.LanguageUpdateService;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;
import com.gmail.maloef.rememberme.translate.google.TranslateLoader;
import com.gmail.maloef.rememberme.translate.google.Translation;
import com.gmail.maloef.rememberme.util.StringUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddWordFragment extends Fragment implements LoaderManager.LoaderCallbacks<Translation> {

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;
    @Inject LanguageRepository languageRepository;
    @Inject GoogleTranslateService translateService;

    LanguageSettingsManager languageSettingsManager;

    @BindString(R.string.confirm_language_settings_title) String confirmLanguageSettingsTitle;
    @BindString(R.string.confirm_language_settings_message) String confirmLanguageSettingsMessage;
    @BindString(R.string.select_language_settings_title) String selectLanguageSettingsTitle;
    @BindString(R.string.select_language_settings_message) String selectLanguageSettingsMessage;

    @BindString(R.string.no_translation_found) String noTranslationFound;
    @BindString(android.R.string.ok) String okString;

    @Bind(R.id.foreign_word_edittext) EditText foreignWordEditText;
    @Bind(R.id.native_word_edittext) EditText nativeWordEditText;

    Spinner foreignLanguageSpinner;
    Spinner nativeLanguageSpinner;

    private int languageCount;

    VocabularyBox selectedBox;
    String foreignWord;
    String nativeWord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_word, container, false);

        if (savedInstanceState != null) {
            // ToDo 14.03.16: can this information be used somehow?
            logInfo("savedInstanceState exists: " + savedInstanceState + ", keys: " + savedInstanceState.keySet());
        }

        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this, rootView);

        selectedBox = boxRepository.getSelectedBox();

        configureEditBehavior(foreignWordEditText);
        configureEditBehavior(nativeWordEditText);

        return rootView;
    }

    // this does not work via xml :-(
    private void configureEditBehavior(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setSingleLine(true);
        editText.setMaxLines(3);
        editText.setHorizontallyScrolling(false);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        foreignWord = wordToAdd();
        if (foreignWord == null) {
            logInfo("nothing to translate");
            return;
        }
        foreignWordEditText.setText(foreignWord);
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
            nativeWordEditText.setText(nativeWord);
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

        foreignLanguageSpinner = (Spinner) dialogView.findViewById(R.id.foreignLanguageSpinner);
        nativeLanguageSpinner = (Spinner) dialogView.findViewById(R.id.nativeLanguageSpinner);
        languageSettingsManager = new LanguageSettingsManager(getActivity(), boxRepository, languageRepository);

        updateLanguageSpinners(foreignLanguageUsedForTranslation);
        // ToDo 09.03.16: necessary here? DRY?
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateLanguageSpinners(foreignLanguageUsedForTranslation);
            }
        };
        IntentFilter languagesUpdatedFilter = new IntentFilter(LanguageUpdateService.LANGUAGES_UPDATED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, languagesUpdatedFilter);

        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setPositiveButton(okString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedBox = boxRepository.getSelectedBox();
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

    private void updateLanguageSpinners(String foreignLanguage) {
        if (languageCount > 0 && languageCount == languageRepository.countLanguages("en")) {
            // languages are up to date
            return;
        }
        languageSettingsManager.configureForeignLanguageSpinner(foreignLanguageSpinner, foreignLanguage);
        languageSettingsManager.configureNativeLanguageSpinner(nativeLanguageSpinner);

        languageCount = languageRepository.countLanguages("en");
    }

    @Override
    public void onLoaderReset(Loader<Translation> loader) {}

    @OnClick(R.id.cancelAddWordButton)
    public void cancelAddWord(View view) {
        logInfo("cancelling add word");
        // ToDo 14.03.16: go to mainActivity or back
    }

    @OnClick(R.id.translateAddWordButton)
    public void translateForeignWord(View view) {
        logInfo("translating foreign word");
        String foreignWord = foreignWordEditText.getText().toString();
        String message;
        if (StringUtils.isBlank(foreignWord)) {
            message = "No foreign word";
        } else {
            this.foreignWord = foreignWord;
            loadTranslation();
            message = "Re-translated text";
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        // ToDo 14.03.16: put keyboard away
    }

    @OnClick(R.id.saveAddWordButton)
    public void saveWord(View view) {
        logInfo("saving new word");
        String foreignWord = foreignWordEditText.getText().toString();
        String nativeWord = nativeWordEditText.getText().toString();
        String message;
        if (StringUtils.isBlank(foreignWord)) {
            message = "No foreign word";
        } else if (StringUtils.isBlank(nativeWord)) {
            message = "No native word";
        } else {
            wordRepository.createWord(selectedBox._id, foreignWord, nativeWord);
            message = "Word saved";
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        // ToDo 14.03.16: go to mainActivity
    }


    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
