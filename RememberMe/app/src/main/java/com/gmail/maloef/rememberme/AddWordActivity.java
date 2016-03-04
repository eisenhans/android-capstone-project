package com.gmail.maloef.rememberme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AddWordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        if (savedInstanceState != null) {
            return;
        }

        if (!Intent.ACTION_SEND.equals(getIntent().getAction())) {
            logWarn("intent action is " + getIntent().getAction());
        }
        if (!"text/plain".equals(getIntent().getType())) {
            logWarn("intent type is " + getIntent().getType());
        }

        AddWordFragment fragment = new AddWordFragment();
        fragment.setArguments(getIntent().getExtras());

        // add the fragment to the FrameLayout add_word_container defined in activity_add_word.xml
        getFragmentManager().beginTransaction().add(R.id.add_word_container, fragment).commit();
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
