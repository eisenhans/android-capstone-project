package com.gmail.maloef.rememberme;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

@FragmentWithArgs
public class ShowWordFragment extends AbstractWordFragment {

    public interface NextWordListener {
        void onNextWordButtonClicked(boolean moreWordsAvailable);
    }

    private NextWordListener nextWordListener;

    @Inject WordRepository wordRepository;

    @Bind(R.id.query_textview) TextView queryTextView;
    @Bind(R.id.answer_textview) TextView answerTextView;
    @Bind(R.id.result_icon) ImageView resultIconView;
    @Bind(R.id.repeat_status_textview) TextView repeatStatusTextView;
    @Bind(R.id.nextWordButton) ImageButton nextWordButton;

    @Arg Word word;
    @Arg int wordsInCompartment;
    @Arg int translationDirection;
    @Arg String givenAnswer;

    String queryWord;
    String correctAnswer;
    int compartment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        nextWordListener = (NextWordListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RememberMeApplication.injector().inject(this);

        if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE) {
            queryWord = word.foreignWord;
            correctAnswer = word.nativeWord;
        } else {
            queryWord = word.nativeWord;
            correctAnswer = word.foreignWord;
        }

        compartment = word.compartment;
        if (correctAnswer.equals(givenAnswer)) {
            wordRepository.moveToCompartment(word.id, compartment + 1);
        } else if (compartment == 1){
            // virtual compartment
            wordRepository.moveToCompartment(word.id, 0);
        } else {
            wordRepository.moveToCompartment(word.id, 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_word, container, false);

        ButterKnife.bind(this, rootView);

        queryTextView.setText(queryWord);
        answerTextView.setText(correctAnswer);

        if (correctAnswer.equals(givenAnswer)) {
            resultIconView.setImageResource(R.drawable.ic_check_36dp);
        } else {
            resultIconView.setImageResource(R.drawable.ic_close_36dp);
        }

        final int wordsInCompartmentNow = wordRepository.countWords(word.boxId, compartment);
        final int wordCount = wordsInCompartment - wordsInCompartmentNow;
        repeatStatusTextView.setText(wordCount + "/" + wordsInCompartment);

        final boolean moreWordsAvailable = wordsInCompartmentNow > 0;
        nextWordButton.setRotation(moreWordsAvailable ? 0 : 270);

        nextWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWordListener.onNextWordButtonClicked(moreWordsAvailable);
            }
        });
        return rootView;
    }
}
