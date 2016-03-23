package com.gmail.maloef.rememberme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hannesdorfmann.fragmentargs.FragmentArgs;

public abstract class AbstractWordFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
    }

    // this does not work via xml :-(
    protected void configureEditTextBehavior(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setSingleLine(true);
        editText.setMaxLines(3);
        editText.setHorizontallyScrolling(false);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    protected void showKeyboard(final EditText editText) {
        Runnable showKeyboard = new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        };
        editText.postDelayed(showKeyboard, 50);

        // I googled these approaches, but they didn't work
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
