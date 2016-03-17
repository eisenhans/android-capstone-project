package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public abstract class AbstractWordFragment extends Fragment {

    // this does not work via xml :-(
    protected void configureEditTextBehavior(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setSingleLine(true);
        editText.setMaxLines(3);
        editText.setHorizontallyScrolling(false);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }
}
