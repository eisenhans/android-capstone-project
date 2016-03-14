package com.gmail.maloef.rememberme.util.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.gmail.maloef.rememberme.R;

public class ValidatingInputDialog {

    private AlertDialog alertDialog;
    private InputValidator inputValidator;

    public ValidatingInputDialog(Activity activity, CharSequence title, CharSequence message, InputProcessor inputProcessor) {
        this(activity, title, message, new NotEmptyInputValidator(), inputProcessor);
    }

    public ValidatingInputDialog(Activity activity, CharSequence title, CharSequence message, InputValidator inputValidator,
                                 final InputProcessor inputProcessor) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        this.inputValidator = inputValidator;

        final EditText editText = new EditText(activity);
        alertDialogBuilder.setView(editText);

        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputProcessor.process(editText.getText().toString());
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, null);

        alertDialog = alertDialogBuilder.create();
        editText.addTextChangedListener(createTextWatcher());
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                boolean inputOk = inputValidator.isValid(input);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(inputOk);
            }
        };
    }

    public void show() {
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }
}
