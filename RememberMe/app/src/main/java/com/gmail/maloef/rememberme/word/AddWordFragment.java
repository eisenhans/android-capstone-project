package com.gmail.maloef.rememberme.word;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gmail.maloef.rememberme.LanguageSettingsManager;
import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeApplication;
import com.gmail.maloef.rememberme.addword.TranslationResult;
import com.gmail.maloef.rememberme.addword.TranslationResultLoader;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;
import com.gmail.maloef.rememberme.translate.google.Translation;
import com.gmail.maloef.rememberme.util.StringUtils;
import com.gmail.maloef.rememberme.util.text.AfterTextChangedWatcher;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@FragmentWithArgs
public class AddWordFragment extends AbstractWordFragment implements LoaderManager.LoaderCallbacks<TranslationResult> {

    public interface Callback {
        void languageSettingsConfirmed(String foreignLanguage, String nativeLanguage);
        void addWordDone();
    }

    public static final String TAG = "addWordFragmentTag";

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;
    @Inject LanguageRepository languageRepository;
    @Inject GoogleTranslateService translateService;

    LanguageSettingsManager languageSettingsManager;

    @Bind(R.id.add_word_parent_layout) LinearLayout addWordParentLayout;
    @Bind(R.id.top_word_edittext) EditText foreignWordEditText;
    @Bind(R.id.bottom_word_edittext) EditText nativeWordEditText;

    @Bind (R.id.cancelButton) Button cancelButton;
    @Bind (R.id.translateButton) Button translateButton;
    @Bind (R.id.saveButton) Button saveButton;

    Spinner foreignLanguageSpinner;
    Spinner nativeLanguageSpinner;

    @Arg String foreignWord;

    String nativeWord;
    VocabularyBox selectedBox;
    int languageCount;

    private Callback callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (Callback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RememberMeApplication.injector().inject(this);
        if (!boxRepository.isOneBoxSaved()) {
            boxRepository.createDefaultBox();
        }
        selectedBox = boxRepository.getSelectedBox();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_word, container, false);

        if (savedInstanceState != null) {
            // ToDo 14.03.16: can this information be used somehow?
            logInfo("savedInstanceState exists: " + savedInstanceState + ", keys: " + savedInstanceState.keySet());
        }
        ButterKnife.bind(this, rootView);

        configureEditTextBehavior(foreignWordEditText);
        configureEditTextBehavior(nativeWordEditText);

        foreignWordEditText.addTextChangedListener(createForeignWordWatcher());
        nativeWordEditText.addTextChangedListener(createNativeWordWatcher());

        translateButton.setVisibility(View.VISIBLE);

        return rootView;
    }

    private TextWatcher createForeignWordWatcher() {
        return new AfterTextChangedWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                translateButton.setEnabled(s.length() > 0 && ! s.equals(foreignWord));
                saveButton.setEnabled(s.length() > 0 && nativeWordEditText.getText().length() > 0);
            }
        };
    }

    private TextWatcher createNativeWordWatcher() {
        return new AfterTextChangedWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                translateButton.setEnabled(true);
                saveButton.setEnabled(s.length() > 0 && foreignWordEditText.getText().length() > 0);
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();

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

    void loadTranslation() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<TranslationResult> onCreateLoader(int id, Bundle args) {
        logInfo("loading translation for word " + foreignWord + " from " + selectedBox.foreignLanguage + " to " + selectedBox.nativeLanguage);
        return new TranslationResultLoader(getActivity(), languageRepository, translateService,
                foreignWord, selectedBox.foreignLanguage, selectedBox.nativeLanguage);
    }

    @Override
    public void onLoadFinished(Loader<TranslationResult> translationResultLoader, TranslationResult translationResult) {
        Translation translation = translationResult.translation;
        logInfo("translation returned by loader: " + translation);

        if (translation.detectedSourceLanguage != null) {
            showConfirmLanguageSettingsDialog(translationResult);
        }
        if (StringUtils.isBlank(translation.translatedText)) {
        //if (StringUtils.isBlank(translation.translatedText) || translation.translatedText.equalsIgnoreCase(foreignWord)) {
            nativeWord = null;
            nativeWordEditText.setText(null);
            saveButton.setEnabled(false);
            Toast.makeText(getActivity(), getString(R.string.no_translation_found), Toast.LENGTH_SHORT).show();
        } else {
            if (nativeWord != null) {
                // there was a translation before, so we should inform the user that we translated again
                Toast.makeText(getActivity(), getString(R.string.word_translated), Toast.LENGTH_SHORT).show();
            }
            nativeWord = translation.translatedText;
            nativeWordEditText.setText(nativeWord);
            saveButton.setEnabled(true);
        }
        translateButton.setEnabled(false);
        addWordParentLayout.requestFocus();
    }

    private void showConfirmLanguageSettingsDialog(TranslationResult translationResult) {
        selectedBox.foreignLanguage = translationResult.translation.detectedSourceLanguage;
        final String foreignLanguageUsedForTranslation = selectedBox.foreignLanguage;
        final String nativeLanguageUsedForTranslation = selectedBox.nativeLanguage;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.review_language_settings));

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_language_settings, null);

        foreignLanguageSpinner = (Spinner) dialogView.findViewById(R.id.foreignLanguageSpinner);
        nativeLanguageSpinner = (Spinner) dialogView.findViewById(R.id.nativeLanguageSpinner);
        languageSettingsManager = new LanguageSettingsManager(getActivity(), boxRepository, translationResult.languages);

        configureLanguageSpinners();
        updateForeignLanguageSpinner(foreignLanguageUsedForTranslation);
        updateNativeLanguageSpinner(nativeLanguageUsedForTranslation);

        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedBox = boxRepository.getSelectedBox();
                logInfo("checking if word needs to be translated again: " +
                        "translated from " + foreignLanguageUsedForTranslation + " to " + nativeLanguageUsedForTranslation +
                        ", need translation from " + selectedBox.foreignLanguage + " to " + selectedBox.nativeLanguage);

                callback.languageSettingsConfirmed(selectedBox.foreignLanguage, selectedBox.nativeLanguage);
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

    private void configureLanguageSpinners() {
        if (languageCount > 0 && languageCount == languageRepository.countLanguages("en")) {
            // languages are up to date
            return;
        }
        languageSettingsManager.configureForeignLanguageSpinner(foreignLanguageSpinner);
        languageSettingsManager.configureNativeLanguageSpinner(nativeLanguageSpinner);

        languageCount = languageRepository.countLanguages("en");
    }

    private void updateForeignLanguageSpinner(String foreignLanguage) {
        languageSettingsManager.updateForeignLanguageSpinner(foreignLanguageSpinner, foreignLanguage);
    }

    private void updateNativeLanguageSpinner(String nativeLanguage) {
        languageSettingsManager.updateNativeLanguageSpinner(nativeLanguageSpinner, nativeLanguage);
    }

    @Override
    public void onLoaderReset(Loader<TranslationResult> loader) {}

    @OnClick(R.id.cancelButton)
    public void cancelAddWord(View view) {
        logInfo("cancelling add word");
        callback.addWordDone();
    }

    @OnClick(R.id.translateButton)
    public void translateForeignWord(View view) {
        logInfo("translating foreign word");

        hideKeyboard();
        foreignWord = foreignWordEditText.getText().toString();
        loadTranslation();
    }

    @OnClick(R.id.saveButton)
    public void saveWord(View view) {
        logInfo("saving new word");
        String foreignWord = foreignWordEditText.getText().toString();
        String nativeWord = nativeWordEditText.getText().toString();
        String message;
        boolean finish = false;
        if (StringUtils.isBlank(foreignWord)) {
            message = getString(R.string.no_foreign_word);
        } else if (StringUtils.isBlank(nativeWord)) {
            message = getString(R.string.no_native_word);
        } else if (wordRepository.doesWordExist(selectedBox.id, foreignWord, nativeWord)) {
            // ToDo 15.03.16: add parameter to strings.xml
            message = getString(R.string.word_already_exists_in_box_s, selectedBox.name);
        } else {
            wordRepository.createWord(selectedBox.id, foreignWord, nativeWord);
            message = getString(R.string.word_saved);
            finish = true;
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        if (finish) {
            callback.addWordDone();
        }
    }
}
