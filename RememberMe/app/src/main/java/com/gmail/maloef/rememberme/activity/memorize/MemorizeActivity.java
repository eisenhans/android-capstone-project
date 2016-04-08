package com.gmail.maloef.rememberme.activity.memorize;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.gmail.maloef.rememberme.activity.AbstractRememberMeActivity;
import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeIntent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemorizeActivity extends AbstractRememberMeActivity implements MemorizeFragment.Callback {

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);

        ButterKnife.bind(this);

        int boxId = getIntent().getIntExtra(RememberMeIntent.EXTRA_BOX_ID, -1);
        showMemorizeFragment(boxId);
    }

    @Override
    public void memorizeDone() {
        finish();
    }
}
