package com.gmail.maloef.rememberme;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemorizeActivity extends DrawerActivity {

    private static final String MEMORIZE_FRAGMENT_TAG = "memorizeFragmentTag";

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigationView) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);

        RememberMeApplication.injector().inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initDrawerToggle(drawerLayout, toolbar);
        toolbar.setTitle(getString(R.string.memorize));

        // ToDo 16.03.16:
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                logInfo("navigationItem selected: " + item);
                return true;
            }
        });

        int boxId = getIntent().getIntExtra(RememberMeIntent.EXTRA_BOX_ID, -1);

        Fragment fragment = getFragmentManager().findFragmentByTag(MEMORIZE_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = MemorizeFragmentBuilder.newMemorizeFragment(boxId);
        }
        getFragmentManager().beginTransaction().replace(R.id.memorize_content, fragment, MEMORIZE_FRAGMENT_TAG).commit();
    }
}
