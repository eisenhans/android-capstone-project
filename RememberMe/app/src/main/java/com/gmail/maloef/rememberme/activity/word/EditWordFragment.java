package com.gmail.maloef.rememberme.activity.word;

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

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeApplication;
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

    public static final String TAG = "editWordFragmentTag";

    public interface Callback {
        void editWordDone(int wordId);
    }

    private Callback callback;

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;

    @Bind(R.id.top_word_edittext) EditText topWordEditText;
    @Bind(R.id.bottom_word_edittext) EditText bottomWordEditText;

    @Bind (R.id.cancelButton) Button cancelButton;
    @Bind (R.id.saveButton) Button saveButton;

    @Arg int wordId;
    @Arg int translationDirection;

    String topWord;
    String bottomWord;

    VocabularyBox selectedBox;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (Callback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RememberMeApplication.injector().inject(this);
        selectedBox = boxRepository.getSelectedBox();

        Word word = wordRepository.findWord(wordId);
        if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE) {
            topWord = word.foreignWord;
            bottomWord = word.nativeWord;
        } else {
            topWord = word.nativeWord;
            bottomWord = word.foreignWord;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_word, container, false);

        if (savedInstanceState != null) {
            // ToDo 14.03.16: can this information be used somehow?
            logInfo("savedInstanceState exists: " + savedInstanceState + ", keys: " + savedInstanceState.keySet());
        }
        ButterKnife.bind(this, rootView);

        configureEditTextBehavior(topWordEditText);
        configureEditTextBehavior(bottomWordEditText);

        topWordEditText.setText(topWord);
        bottomWordEditText.setText(bottomWord);

        saveButton.setEnabled(false);

        TextWatcher textWatcher = createTextWatcher();
        topWordEditText.addTextChangedListener(textWatcher);
        bottomWordEditText.addTextChangedListener(textWatcher);

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
        String topNow = topWordEditText.getText().toString();
        String bottomNow = bottomWordEditText.getText().toString();

        saveButton.setEnabled(!topNow.isEmpty() && !bottomNow.isEmpty() &&
                !(topNow.equals(topWord) && bottomNow.equals(bottomWord)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.cancelButton)
    public void cancelEditWord(View view) {
        logInfo("cancelling edit word");
        callback.editWordDone(wordId);
    }

    @OnClick(R.id.saveButton)
    public void saveWord(View view) {
        logInfo("saving edited word");
        String foreignWord;
        String nativeWord;
        if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE) {
            foreignWord = topWordEditText.getText().toString();
            nativeWord = bottomWordEditText.getText().toString();
        } else {
            nativeWord = topWordEditText.getText().toString();
            foreignWord = bottomWordEditText.getText().toString();
        }
        wordRepository.updateWord(wordId, foreignWord, nativeWord);
        hideKeyboard();
        Toast.makeText(getActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
        callback.editWordDone(wordId);
    }
}
