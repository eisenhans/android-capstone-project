package com.gmail.maloef.rememberme.wordlist;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.gmail.maloef.rememberme.AbstractRememberMeActivity;
import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeApplication;
import com.gmail.maloef.rememberme.RememberMeIntent;
import com.gmail.maloef.rememberme.RememberMePreferences;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class WordListActivity extends AbstractRememberMeActivity {

    private static final String WORD_LIST_FRAGMENT_TAG = "wordListFragmentTag";

    @Inject RememberMePreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RememberMeApplication.injector().inject(this);
        setContentView(R.layout.activity_word_list);
        ButterKnife.bind(this);
        initToolbar(true, R.string.words_learned);

        if (!preferences.wasWordListInfoDialogShown()) {
            showWordListInfoDialog();
            preferences.setWordListInfoDialogShown();
        }

        int boxId = getIntent().getIntExtra(RememberMeIntent.EXTRA_BOX_ID, -1);
        int compartment = getIntent().getIntExtra(RememberMeIntent.EXTRA_COMPARTMENT, -1);

        Fragment fragment = getFragmentManager().findFragmentByTag(WORD_LIST_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = WordListFragmentBuilder.newWordListFragment(boxId, compartment);
        }
        getFragmentManager().beginTransaction().replace(R.id.word_list_content, fragment, WORD_LIST_FRAGMENT_TAG).commit();
    }

    private void showWordListInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.words_from_compartment_known);

        builder.setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
