package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemorizeActivity extends AbstractRememberMeActivity {

    private static final String MEMORIZE_FRAGMENT_TAG = "memorizeFragmentTag";

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);

//        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this);

        initToolbar(true, R.string.memorize);

        int boxId = getIntent().getIntExtra(RememberMeIntent.EXTRA_BOX_ID, -1);

        Fragment fragment = getFragmentManager().findFragmentByTag(MEMORIZE_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = MemorizeFragmentBuilder.newMemorizeFragment(boxId);
        }
        getFragmentManager().beginTransaction().replace(R.id.memorize_content, fragment, MEMORIZE_FRAGMENT_TAG).commit();
    }
}
