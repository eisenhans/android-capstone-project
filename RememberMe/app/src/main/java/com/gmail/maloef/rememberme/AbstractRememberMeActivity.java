package com.gmail.maloef.rememberme;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import butterknife.Bind;

public abstract class AbstractRememberMeActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    protected void initToolbar(boolean withBackArrow, int titleResId, String... args) {
        setSupportActionBar(toolbar);
        if (withBackArrow) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setTitle(getString(titleResId, args));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    protected void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
