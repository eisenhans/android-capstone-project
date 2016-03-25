package com.gmail.maloef.rememberme;


import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@FragmentWithArgs
public class MemorizeFragment extends AbstractRememberMeFragment {

    private static final String OFFSET_KEY = "offsetKey";

    @Inject WordRepository wordRepository;

    @Bind(R.id.memorize_table_container) FrameLayout memorizeTableContainer;
    @Bind(R.id.memorize_back_button) ImageButton backButton;
    @Bind(R.id.memorize_forward_button) ImageButton forwardButton;
    @Bind(R.id.memorize_footer_textview) TextView statusTextView;

    @Arg int boxId;
    int offset = 1;

    @Override
    public void onSaveInstanceState(Bundle args) {
        args.putInt(OFFSET_KEY, offset);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RememberMeApplication.injector().inject(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(OFFSET_KEY)) {
            offset = savedInstanceState.getInt(OFFSET_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_memorize, container, false);

        ButterKnife.bind(this, rootView);

        update();

        return rootView;
    }

    private void update() {
        int wordsInCompartment1 = wordRepository.countWords(boxId, 1);
        List<Pair<String, String>> words = wordRepository.getWords(boxId, 1, offset, 5);

        statusTextView.setText(createStatusString(wordsInCompartment1, words.size()));

        TableLayout table = createTable(words);
        memorizeTableContainer.removeAllViews();
        memorizeTableContainer.addView(table);
    }

    private String createStatusString(int wordsInCompartment1, int wordsFound) {
        String status = "";
        if (wordsFound == 1) {
            status = wordsInCompartment1 + "/" + wordsInCompartment1;
        } else if (wordsFound > 1) {
            int start = offset;
            int end = start - 1 + wordsFound;
            status = start + "-" + end + "/" + wordsInCompartment1;
        }
        logInfo("status of memorize fragment: " + status);
        return status;
    }

    private TableLayout createTable(List<Pair<String, String>> words) {
        TableLayout table = new TableLayout(getActivity());
        table.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        table.setStretchAllColumns(true);

        for (int i = 0; i < words.size(); i++) {
            TableRow row = new TableRow(getActivity());
            row.setPadding(24, 24, 24, 24);

            if (i % 2 == 0) {
                row.setBackgroundColor(getResources().getColor(R.color.colorTableRowDark));
            }
            row.addView(createTableCell(words.get(i).first));
            row.addView(createTableCell(words.get(i).second));

            table.addView(row);
        }
        logInfo("created table from " + words.size() + " words: " + table);
        return table;
    }

    private TextView createTableCell(String text) {
        TextView textView = new TextView(getActivity());
        textView.setTextAppearance(getActivity(), R.style.AppTheme_WordTableCell);
        textView.setText(text);

        return textView;
    }

    @OnClick(R.id.memorize_back_button)
    public void showPreviousWords(View view) {
        logInfo("showing previous words");
        offset -= 5;
        update();
    }

    @OnClick(R.id.memorize_forward_button)
    public void showNextWords(View view) {
        logInfo("showing next words");
        offset += 5;
        update();
    }
}
