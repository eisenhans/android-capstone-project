package com.gmail.maloef.rememberme.memorize;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.gmail.maloef.rememberme.AbstractRememberMeActivity;
import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeIntent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemorizeActivity extends AbstractRememberMeActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);

        ButterKnife.bind(this);

        initToolbar(true, R.string.memorize);

        int boxId = getIntent().getIntExtra(RememberMeIntent.EXTRA_BOX_ID, -1);
        showMemorizeFragment(boxId);
    }

    // ToDo 06.04.16: same method as in MainActivity - merge?
    private void showMemorizeFragment(int boxId) {
        Fragment memorizeFragment = getFragmentManager().findFragmentByTag(MemorizeFragment.TAG);
        if (memorizeFragment == null) {
            memorizeFragment = MemorizeFragmentBuilder.newMemorizeFragment(boxId);
        }
        getFragmentManager().beginTransaction().replace(R.id.detail_container, memorizeFragment, MemorizeFragment.TAG).commit();
    }
}
