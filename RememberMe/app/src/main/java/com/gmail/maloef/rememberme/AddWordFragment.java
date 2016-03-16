package com.gmail.maloef.rememberme;

import android.app.Activity;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    @BindString(R.string.confirm_language_settings_title) String confirmLanguageSettingsTitleString;
    @BindString(R.string.confirm_language_settings_message) String confirmLanguageSettingsMessageString;
    @BindString(R.string.select_language_settings_title) String selectLanguageSettingsTitleString;
    @BindString(R.string.select_language_settings_message) String selectLanguageSettingsMessageString;
    @BindString(R.string.no_translation_found) String noTranslationFoundString;
    @BindString(android.R.string.ok) String okString;
    @BindString(R.string.word_translated) String wordTranslatedString;
    @BindString(R.string.no_foreign_word) String noForeignWordString;
    @BindString(R.string.no_native_word) String noNativeWordString;
    @BindString(R.string.word_already_exists_in_box) String wordAlreadyExistsInBoxString;
    @BindString(R.string.word_saved) String wordSavedString;

    @Bind(R.id.add_word_parent_layout) LinearLayout addWordParentLayout;
    @Bind(R.id.foreign_word_edittext) EditText foreignWordEditText;
    @Bind(R.id.native_word_edittext) EditText nativeWordEditText;

    Spinner foreignLanguageSpinner;
    Spinner nativeLanguageSpinner;

    @Bind (R.id.cancelAddWordButton) Button cancelButton;
    @Bind (R.id.translateAddWordButton) Button translateButton;
    @Bind (R.id.saveAddWordButton) Button saveButton;

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

        foreignWordEditText.addTextChangedListener(createForeignWordWatcher());
        nativeWordEditText.addTextChangedListener(createNativeWordWatcher());

        return rootView;
    }

    private TextWatcher createForeignWordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                translateButton.setEnabled(s.length() > 0 && ! s.equals(foreignWord));
                saveButton.setEnabled(s.length() > 0 && nativeWordEditText.getText().length() > 0);
            }
        };
    }

    private TextWatcher createNativeWordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                translateButton.setEnabled(true);
                saveButton.setEnabled(s.length() > 0 && foreignWordEditText.getText().length() > 0);
            }
        };
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
            translateButton.setEnabled(false);
            saveButton.setEnabled(false);
            foreignWordEditText.requestFocus();
            return;
        }
        foreignWordEditText.setText(foreignWord);
        loadTranslation();
    }

    String wordToAdd() {
        if (getArguments() == null) {
            logInfo("no arguments");
            return null;
        }
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
        if (StringUtils.isBlank(translation.translatedText) || translation.translatedText.equalsIgnoreCase(foreignWord)) {
            nativeWord = null;
            saveButton.setEnabled(false);
            Toast.makeText(getActivity(), noTranslationFoundString, Toast.LENGTH_SHORT).show();
        } else {
            if (nativeWord != null) {
                // there was a translation before, so we should inform the user that we translated again
                Toast.makeText(getActivity(), wordTranslatedString, Toast.LENGTH_SHORT).show();
            }
            nativeWord = translation.translatedText;
            nativeWordEditText.setText(nativeWord);
            saveButton.setEnabled(true);
        }
        translateButton.setEnabled(false);
        addWordParentLayout.requestFocus();
    }

    private void showConfirmLanguageSettingsDialog(String detectedSourceLanguage) {
        if (detectedSourceLanguage != null) {
            selectedBox.foreignLanguage = detectedSourceLanguage;
        }
        final String foreignLanguageUsedForTranslation = selectedBox.foreignLanguage;
        final String nativeLanguageUsedForTranslation = selectedBox.nativeLanguage;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        if (foreignLanguageUsedForTranslation.equals(nativeLanguageUsedForTranslation)) {
            alertDialogBuilder.setTitle(selectLanguageSettingsTitleString);
            alertDialogBuilder.setMessage(selectLanguageSettingsMessageString);
        } else {
            alertDialogBuilder.setTitle(confirmLanguageSettingsTitleString);
            alertDialogBuilder.setMessage(confirmLanguageSettingsMessageString);
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
        getActivity().finish();
    }

    @OnClick(R.id.translateAddWordButton)
    public void translateForeignWord(View view) {
        logInfo("translating foreign word");

        hideKeyboard();
        foreignWord = foreignWordEditText.getText().toString();
        loadTranslation();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @OnClick(R.id.saveAddWordButton)
    public void saveWord(View view) {
        logInfo("saving new word");
        String foreignWord = foreignWordEditText.getText().toString();
        String nativeWord = nativeWordEditText.getText().toString();
        String message;
        boolean finish = false;
        if (StringUtils.isBlank(foreignWord)) {
            message = noForeignWordString;
        } else if (StringUtils.isBlank(nativeWord)) {
            message = noNativeWordString;
        } else if (wordRepository.doesWordExist(selectedBox.id, foreignWord, nativeWord)) {
            // ToDo 15.03.16: add parameter to strings.xml
            message = wordAlreadyExistsInBoxString + selectedBox.name;
        } else {
            wordRepository.createWord(selectedBox.id, foreignWord, nativeWord);
            message = wordSavedString;
            finish = true;
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        if (finish) {
            getActivity().finish();
        }
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
