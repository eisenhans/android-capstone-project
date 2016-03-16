package com.gmail.maloef.rememberme;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class AddWordActivity extends AppCompatActivity {

    ActionBarDrawerToggle drawerToggle;
    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @BindString(R.string.add_word) String addWordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        drawer.setDrawerListener(drawerToggle);

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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
