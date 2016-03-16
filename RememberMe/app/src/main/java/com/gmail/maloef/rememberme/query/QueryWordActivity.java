package com.gmail.maloef.rememberme.query;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gmail.maloef.rememberme.AddWordFragment;
import com.gmail.maloef.rememberme.DrawerActivity;
import com.gmail.maloef.rememberme.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class QueryWordActivity extends DrawerActivity {

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigationView) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @BindString(R.string.compartment) String compartmentString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_word);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initDrawerToggle(drawerLayout, toolbar);
        toolbar.setTitle(compartmentString + "...");

        if (savedInstanceState != null) {
            // ToDo 16.03.16: does this work?
            return;
        }

        // ToDo 16.03.16
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                logInfo("navigationItem selected: " + item);
                return true;
            }
        });

        AddWordFragment fragment = new AddWordFragment();
//        fragment.setArguments(getIntent().getExtras());

        // add the fragment to the FrameLayout query_word_content defined in activity_query_word.xml
        getFragmentManager().beginTransaction().add(R.id.query_word_content, fragment).commit();
    }
}
