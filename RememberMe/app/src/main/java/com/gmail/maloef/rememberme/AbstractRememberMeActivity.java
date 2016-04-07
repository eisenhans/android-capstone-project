package com.gmail.maloef.rememberme;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.memorize.MemorizeFragment;
import com.gmail.maloef.rememberme.memorize.MemorizeFragmentBuilder;
import com.gmail.maloef.rememberme.word.AddWordFragment;
import com.gmail.maloef.rememberme.word.AddWordFragmentBuilder;
import com.gmail.maloef.rememberme.word.EditWordFragment;
import com.gmail.maloef.rememberme.word.EditWordFragmentBuilder;
import com.gmail.maloef.rememberme.word.QueryWordFragment;
import com.gmail.maloef.rememberme.word.QueryWordFragmentBuilder;
import com.gmail.maloef.rememberme.word.ShowWordFragment;
import com.gmail.maloef.rememberme.word.ShowWordFragmentBuilder;
import com.gmail.maloef.rememberme.wordlist.WordListFragment;
import com.gmail.maloef.rememberme.wordlist.WordListFragmentBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class AbstractRememberMeActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    protected void initToolbar(boolean withUpArrow, int titleResId, String... args) {
        setSupportActionBar(toolbar);
        if (withUpArrow) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setTitle(getString(titleResId, args));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // ToDo 28.03.16
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void hideKeyboard() {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    protected void showMemorizeFragment(int boxId) {
        initToolbar(true, R.string.memorize);
        Fragment memorizeFragment = getFragmentManager().findFragmentByTag(MemorizeFragment.TAG);
        if (memorizeFragment == null) {
            memorizeFragment = MemorizeFragmentBuilder.newMemorizeFragment(boxId);
        }
        getFragmentManager().beginTransaction().replace(R.id.detail_container, memorizeFragment, MemorizeFragment.TAG).commit();
    }

    protected void showQueryWordFragment(int boxId, int compartment, int translationDirection) {
        Fragment fragment = getFragmentManager().findFragmentByTag(QueryWordFragment.TAG);
        if (fragment == null) {
            fragment = QueryWordFragmentBuilder.newQueryWordFragment(boxId, compartment, translationDirection);
        }
        getFragmentManager().beginTransaction().replace(R.id.detail_container, fragment, QueryWordFragment.TAG).commit();
    }

    protected void showShowWordFragment(String givenAnswer, int translationDirection, Word word, int wordsInCompartment) {
        Fragment fragment = getFragmentManager().findFragmentByTag(ShowWordFragment.TAG);
        if (fragment == null) {
            fragment = ShowWordFragmentBuilder.newShowWordFragment(givenAnswer, translationDirection, word, wordsInCompartment);
        }
        getFragmentManager().beginTransaction().replace(R.id.detail_container, fragment, ShowWordFragment.TAG).commit();
    }

    protected void showAddWordFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag(AddWordFragment.TAG);
        if (fragment == null) {
            fragment = AddWordFragmentBuilder.newAddWordFragment(null);
        }
        getFragmentManager().beginTransaction().replace(R.id.detail_container, fragment, AddWordFragment.TAG).commit();
    }

    protected void showEditWordFragment(int translationDirection, int wordId) {
        Fragment fragment = getFragmentManager().findFragmentByTag(EditWordFragment.TAG);
        if (fragment == null) {
            fragment = EditWordFragmentBuilder.newEditWordFragment(translationDirection, wordId);
        }
        getFragmentManager().beginTransaction().replace(R.id.detail_container, fragment, EditWordFragment.TAG).commit();
    }

    protected void showWordListFragment(int boxId, int compartment) {
        Fragment fragment = getFragmentManager().findFragmentByTag(WordListFragment.TAG);
        if (fragment == null) {
            fragment = WordListFragmentBuilder.newWordListFragment(boxId, compartment);
        }
        getFragmentManager().beginTransaction().replace(R.id.detail_container, fragment, WordListFragment.TAG).commit();
    }

    protected void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    protected void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
