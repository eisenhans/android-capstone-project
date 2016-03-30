package com.gmail.maloef.rememberme;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.gmail.maloef.rememberme.util.text.AfterTextChangedWatcher;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@FragmentWithArgs
public class EditWordFragment extends AbstractWordFragment {

    public interface EditWordCallback {
        void editDone(int wordId);
    }

    private EditWordCallback editWordCallback;

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;

    @Bind(R.id.foreign_word_edittext) EditText foreignWordEditText;
    @Bind(R.id.native_word_edittext) EditText nativeWordEditText;

    @Bind (R.id.cancelButton) Button cancelButton;
    @Bind (R.id.saveButton) Button saveButton;

    @Arg int wordId;

    String foreignWord;
    String nativeWord;

    VocabularyBox selectedBox;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        editWordCallback = (EditWordCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RememberMeApplication.injector().inject(this);
        selectedBox = boxRepository.getSelectedBox();

        Word word = wordRepository.findWord(wordId);
        foreignWord = word.foreignWord;
        nativeWord = word.nativeWord;
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

        foreignWordEditText.setText(foreignWord);
        nativeWordEditText.setText(nativeWord);

        saveButton.setEnabled(false);

        TextWatcher textWatcher = createTextWatcher();
        foreignWordEditText.addTextChangedListener(textWatcher);
        nativeWordEditText.addTextChangedListener(textWatcher);

        return rootView;
    }

    private TextWatcher createTextWatcher() {
        return new AfterTextChangedWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                updateSaveButtonState();
            }
        };
    }

    private void updateSaveButtonState() {
        String foreignNow = foreignWordEditText.getText().toString();
        String nativeNow = nativeWordEditText.getText().toString();

        saveButton.setEnabled(!foreignNow.isEmpty() && !nativeNow.isEmpty() &&
                !(foreignNow.equals(foreignWord) && nativeNow.equals(nativeWord)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.cancelButton)
    public void cancelEditWord(View view) {
        logInfo("cancelling edit word");
        editWordCallback.editDone(wordId);
    }

    @OnClick(R.id.saveButton)
    public void saveWord(View view) {
        logInfo("saving edited word");
        String foreignWord = foreignWordEditText.getText().toString();
        String nativeWord = nativeWordEditText.getText().toString();

        wordRepository.updateWord(wordId, foreignWord, nativeWord);
        Toast.makeText(getActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
        editWordCallback.editDone(wordId);
    }
}
