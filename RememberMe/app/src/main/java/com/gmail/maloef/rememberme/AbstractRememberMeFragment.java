package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.hannesdorfmann.fragmentargs.FragmentArgs;

public abstract class AbstractRememberMeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
    }

    protected void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
