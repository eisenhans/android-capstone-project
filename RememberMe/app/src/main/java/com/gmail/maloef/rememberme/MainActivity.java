package com.gmail.maloef.rememberme;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxColumns;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxProvider;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        drawer.setDrawerListener(drawerToggle);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        VocabularyBoxService boxService = new VocabularyBoxService(this);
        if (!boxService.isOneBoxSaved()) {
            boxService.createDefaultBox();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    void insertVocabularyBox() {
        ContentValues values = new ContentValues();
        long now = new Date().getTime();
        values.put(VocabularyBoxColumns.NAME, "defaultBox" + now);
        values.put(VocabularyBoxColumns.FOREIGN_LANGUAGE, "Spanish");
        values.put(VocabularyBoxColumns.NATIVE_LANGUAGE, "German");
        values.put(VocabularyBoxColumns.TRANSLATION_DIRECTION, VocabularyBox.TRANSLATION_DIRECTION_MIXED);

        getContentResolver().insert(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, values);
        logInfo("inserted vocabularyBox");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // The action bar home/up action should open or close the drawer.
        // ToDo: which of the following two is necessary?
        if (id == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
