package com.gmail.maloef.rememberme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class AddWordActivity extends DrawerActivity {

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @BindString(R.string.add_word) String addWordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initDrawerToggle(drawerLayout, toolbar);

        if (savedInstanceState != null) {
            return;
        }

        if (!Intent.ACTION_SEND.equals(getIntent().getAction())) {
            logWarn("intent action is " + getIntent().getAction());
        }
        if (!"text/plain".equals(getIntent().getType())) {
            logWarn("intent type is " + getIntent().getType());
        }

        toolbar.setTitle(addWordString);

        AddWordFragment fragment = new AddWordFragment();
        fragment.setArguments(getIntent().getExtras());

        // add the fragment to the FrameLayout add_word_container defined in activity_add_word.xml
        getFragmentManager().beginTransaction().add(R.id.add_word_container, fragment).commit();
    }
}
