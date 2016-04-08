package com.gmail.maloef.rememberme.activity.word;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeApplication;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

@FragmentWithArgs
public class QueryWordFragment extends AbstractWordFragment {

    public static final String TAG = "queryWordFragmentTag";

    public interface AnswerListener {
        void onWordEntered(Word word, String givenAnswer);
    }

    private AnswerListener answerListener;

    @Inject WordRepository wordRepository;

    @Bind(R.id.query_textview) TextView queryTextView;
    @Bind(R.id.answer_edittext) EditText answerEditText;

    @Arg int compartment;
    @Arg int boxId;
    @Arg int translationDirection;

    Word word;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        answerListener = (AnswerListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RememberMeApplication.injector().inject(this);

        word = wordRepository.getNextWord(boxId, compartment);
        logInfo("looked up next word for box " + boxId + ", compartment " + compartment + ": " + word);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_query_word, container, false);

        ButterKnife.bind(this, rootView);
        configureEditTextBehavior(answerEditText);
        answerEditText.requestFocus();
        showKeyboard(answerEditText);

        if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE) {
            queryTextView.setText(word.foreignWord);
        } else {
            queryTextView.setText(word.nativeWord);
        }
        answerEditText.setOnEditorActionListener(createDoneListener());
        return rootView;
    }

    private TextView.OnEditorActionListener createDoneListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                    String givenAnswer = answerEditText.getText().toString().trim();
                    logInfo("user entered word " + givenAnswer + " for word " + word);
                    answerListener.onWordEntered(word, givenAnswer);

                    return true;
                }
                return false;
            }
        };
    }
}
