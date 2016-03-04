package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AddWordFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_word, container, false);

        TextView foreignWordTextView = (TextView) rootView.findViewById(R.id.foreign_word_textview);
        TextView nativeWordTextView = (TextView) rootView.findViewById(R.id.native_word_textview);

        foreignWordTextView.setText("foreign");
        nativeWordTextView.setText("native");

        return rootView;
    }
}
