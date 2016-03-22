package com.gmail.maloef.rememberme;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

@FragmentWithArgs
public class QueryWordFragment extends AbstractWordFragment {

    public interface AnswerListener {
        void onWordEntered(Word word, String givenAnswer, int wordsLeft);
    }

    private AnswerListener answerListener;

    @Inject WordRepository wordRepository;

    @Bind(R.id.query_textview) TextView queryTextView;
    @Bind(R.id.answer_edittext) EditText answerEditText;

    @Arg int compartment;
    @Arg int boxId;
    @Arg int translationDirection;
    @Arg long startTime;

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

        word = wordRepository.getNextWord(boxId, compartment, startTime);
        logInfo("looked up next word for box " + boxId + ", compartment " + compartment + ", startTime " + new Date(startTime) + ": " +  word);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_query_word, container, false);

        ButterKnife.bind(this, rootView);
        configureEditTextBehavior(answerEditText);

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
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    String givenAnswer = answerEditText.getText().toString().trim();
                    int wordsLeft = wordRepository.countWords(boxId, compartment, startTime) - 1; // don't count the current word
                    logInfo("user entered word " + givenAnswer + " for word " + word + ", words left: " + wordsLeft);
                    answerListener.onWordEntered(word, givenAnswer, wordsLeft);

                    return true;
                }
                return false;
            }
        };
    }

    private boolean isFalse(String givenAnswer) {
        String correctAnswer = translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE ?
                word.nativeWord : word.foreignWord;
        return !givenAnswer.equals(correctAnswer);
    }
}
