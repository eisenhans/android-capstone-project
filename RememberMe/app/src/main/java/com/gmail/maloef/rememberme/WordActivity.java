package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WordActivity extends DrawerActivity {

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigationView) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initDrawerToggle(drawerLayout, toolbar);

        if (savedInstanceState != null) {
            // ToDo 16.03.16: does this work?
            return;
        }

        // ToDo 16.03.16:
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                logInfo("navigationItem selected: " + item);
                return true;
            }
        });

        if (!"text/plain".equals(getIntent().getType())) {
            // ToDo 17.03.16: handle this
            logWarn("intent type is " + getIntent().getType());
        }

        logInfo("intent: " + getIntent() + ", action: " + getIntent().getAction() + ", type: " + getIntent().getType() +
                ", extras: " + getIntent().getExtras());
        if (getIntent().getExtras() != null) {
            logInfo("extra keys: " + getIntent().getExtras().keySet());
        }

        String action = getIntent().getAction();
        if (action.equals(Intent.ACTION_SEND) || action.equals(RememberMeIntent.ACTION_ADD)) {
            toolbar.setTitle(getString(R.string.add_word));
            setFragment(new AddWordFragment());
        } else if (action.equals(RememberMeIntent.ACTION_QUERY)) {
            setCompartmentFragment(new QueryWordFragment());
        } else if (action.equals(RememberMeIntent.ACTION_SHOW)) {
//            setCompartmentFragment(new QueryWordFragment());
        }
    }

    private void setCompartmentFragment(Fragment fragment) {
        int compartment = getIntent().getIntExtra(RememberMeIntent.EXTRA_COMPARTMENT, -1);
        logInfo("compartment: " + compartment);
        if (compartment != -1) {
            toolbar.setTitle(getString(R.string.compartment_i, compartment));
        }
        setFragment(fragment);
    }

    private void setFragment(Fragment fragment) {
        fragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.word_content, fragment).commit();
    }
}
