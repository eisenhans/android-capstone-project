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
    @Bind(R.id.memorize_left_button) ImageButton leftButton;
    @Bind(R.id.memorize_right_button) ImageButton rightButton;
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

        statusTextView.setText(createStatusString(wordsInCompartment1));
        updateButtons(wordsInCompartment1);

        TableLayout table = createTable(words);
        memorizeTableContainer.removeAllViews();
        memorizeTableContainer.addView(table);
    }

    private void updateButtons(int wordsInCompartment1) {
        if (wordsInCompartment1 <= 5) {
            leftButton.setVisibility(View.INVISIBLE);
            rightButton.setRotation(270);
        } else if (offset == 1) {
            leftButton.setRotation(90);
            rightButton.setRotation(0);
        } else if (wordsInCompartment1 < offset + 5) {
            leftButton.setRotation(0);
            rightButton.setRotation(270);
        } else {
            leftButton.setRotation(0);
            rightButton.setRotation(0);
        }
    }

    private String createStatusString(int wordsInCompartment1) {
        int pages;
        if (wordsInCompartment1 % 5 == 0) {
            pages = wordsInCompartment1 / 5;
        } else {
            pages = wordsInCompartment1 / 5 + 1;
        }
        int currentPage = offset / 5 + 1;
        String status = currentPage + "/" + pages;
        logInfo("status of memorize fragment: " + status);
        return status;
    }

    private TableLayout createTable(List<Pair<String, String>> words) {
        TableLayout table = new TableLayout(getActivity());
        table.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//        table.setStretchAllColumns(true);
//        table.setShrinkAllColumns(true);

        for (int i = 0; i < words.size(); i++) {
            TableRow row = new TableRow(getActivity());
            row.setGravity(Gravity.CENTER_VERTICAL);

            if (i % 2 == 0) {
                row.setBackgroundColor(getResources().getColor(R.color.colorTableRowDark));
            }
            row.addView(createTableCell(words.get(i).second));
            row.addView(createTableCell(words.get(i).first));

            table.addView(row);
        }
        logInfo("created table from " + words.size() + " words: " + table);
        return table;
    }

    private TextView createTableCell(String text) {
        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        textView.setPadding(36, 36, 36, 36);
        textView.setTextAppearance(getActivity(), R.style.DefaultText);
        textView.setText(text);

        return textView;
    }

    @OnClick(R.id.memorize_left_button)
    public void onLeftButtonClick(View view) {
        if (leftButton.getRotation() != 0) {
            goUp();
        } else {
            logInfo("showing previous words");
            offset -= 5;
            update();
        }
    }

    @OnClick(R.id.memorize_right_button)
    public void onRightButtonClick(View view) {
        if (rightButton.getRotation() != 0) {
            goUp();
        } else {
            logInfo("showing next words");
            offset += 5;
            update();
        }
    }

    private void goUp() {
        getActivity().finish();
    }
}
