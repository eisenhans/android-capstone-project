package com.gmail.maloef.rememberme.activity.word;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeApplication;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.gmail.maloef.rememberme.util.dialog.ConfirmDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

@FragmentWithArgs
public class ShowWordFragment extends AbstractWordFragment {

    public static final String TAG = "showWordFragmentTag";

    public interface ShowWordCallback {
        void showNextWord(boolean moreWordsAvailable);
        void editShownWord(int wordId);
        void updateOverview();
    }

    private ShowWordCallback showWordCallback;

    @Inject WordRepository wordRepository;
    @Inject Tracker tracker;

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
        showWordCallback = (ShowWordCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        RememberMeApplication.injector().inject(this);
        compartment = Math.max(word.compartment, 1);

        if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE) {
            queryWord = word.foreignWord;
            correctAnswer = word.nativeWord;
        } else {
            queryWord = word.nativeWord;
            correctAnswer = word.foreignWord;
        }

        if (givenAnswer == null || word.compartment == 0) {
            return;
        }
        int newCompartment;
        if (correctAnswer.equals(givenAnswer)) {
            newCompartment = word.compartment + 1;
        } else if (compartment == 1){
            // move word to virtual compartment
            newCompartment = 0;
        } else {
            // move word back to compartment 1
            newCompartment = 1;
        }
        wordRepository.moveToCompartment(word.id, newCompartment);
        trackRepeatWordEvent(newCompartment);

        showWordCallback.updateOverview();
    }

    private void trackRepeatWordEvent(int newCompartment) {
        Map<String, String> repeatEvent = new HitBuilders.EventBuilder()
                .setCategory("Word")
                .setAction("Repeat")
                .setLabel("newCompartment" + newCompartment)
                .build();

        tracker.send(repeatEvent);
        logInfo("sent repeatEvent to google analytics: " + repeatEvent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_word, container, false);

        ButterKnife.bind(this, rootView);

        queryTextView.setText(queryWord);
        answerTextView.setText(correctAnswer);

        if (givenAnswer == null) {
            resultIconView.setVisibility(View.INVISIBLE);
        } else if (correctAnswer.equals(givenAnswer)) {
            resultIconView.setVisibility(View.VISIBLE);
            resultIconView.setImageResource(R.drawable.ic_check_36dp);
            resultIconView.setContentDescription(getString(R.string.right));
        } else {
            resultIconView.setVisibility(View.VISIBLE);
            resultIconView.setImageResource(R.drawable.ic_close_36dp);
            resultIconView.setContentDescription(getString(R.string.wrong));
        }

        int wordsInCompartmentNow = wordRepository.countWords(word.boxId, compartment);
        int wordCount = wordsInCompartment - wordsInCompartmentNow;
        repeatStatusTextView.setText(wordCount + "/" + wordsInCompartment);
        repeatStatusTextView.setContentDescription(getString(R.string.word) + wordCount + getString(R.string.out_of) + wordsInCompartment);

        nextWordButton.setRotation(wordsInCompartmentNow > 0 ? 0 : 270);

        nextWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNextWord();
            }
        });
        return rootView;
    }

    private void requestNextWord() {
        int words = wordRepository.countWords(word.boxId, compartment);
        showWordCallback.showNextWord(words > 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_show_word, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_word) {
            logInfo("editing word");
            showWordCallback.editShownWord(word.id);
            return true;
        }
        if (item.getItemId() == R.id.action_delete_word) {
            showConfirmDeleteWordDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmDeleteWordDialog() {
        CharSequence message = Html.fromHtml(getString(R.string.delete_word_s, word.foreignWord));
        ConfirmDialog dialog = new ConfirmDialog(getActivity(), null, message, new ConfirmDialog.OkCallback() {
            @Override
            public void onOk() {
                deleteCurrentWord();
            }
        });
        dialog.show();
    }

    private void deleteCurrentWord() {
        logInfo("deleting word " + word.foreignWord);
        wordRepository.deleteWord(word.id);
        requestNextWord();
        Toast.makeText(getActivity(), getString(R.string.word_deleted), Toast.LENGTH_SHORT).show();
    }
}
