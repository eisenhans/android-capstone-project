package com.gmail.maloef.rememberme.util.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.gmail.maloef.rememberme.R;

/*
 * Implementation note: this class could also extend DialogFragment, as described here:
 *
 * http://developer.android.com/guide/topics/ui/dialogs.html
 *
 * But this doesn't make things easier, and the dialog isn't properly rebuilt after screen rotation either. The current approach favors
 * composition over inheritance, which is a good thing.
 */
public class ConfirmDialog {

    public interface OkCallback {
        void onOk();
    }

    private AlertDialog alertDialog;

    public ConfirmDialog(Activity activity, CharSequence title, CharSequence message, final OkCallback okCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                okCallback.onOk();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        alertDialog = builder.create();
    }

    public void show() {
        alertDialog.show();
    }
}
