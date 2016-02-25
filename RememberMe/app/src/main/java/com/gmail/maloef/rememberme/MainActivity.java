package com.gmail.maloef.rememberme;

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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private Spinner vocabularyBoxSpinner;

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

        vocabularyBoxSpinner = (Spinner) findViewById(R.id.vocabularyBoxSpinner);
        List<String> boxNames = boxService.getBoxNames();
        logInfo("vocabulary boxes: " + boxNames.size());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, boxNames);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vocabularyBoxSpinner.setAdapter(spinnerAdapter);

        VocabularyBox currentBox = boxService.getCurrentBox();

        Spinner foreignLanguageSpinner = (Spinner) findViewById(R.id.foreignLanguageSpinner);
        String[] languages = getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList(languages));
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foreignLanguageSpinner.setAdapter(languageAdapter);
        foreignLanguageSpinner.setSelection(0);

        Spinner nativeLanguageSpinner = (Spinner) findViewById(R.id.nativeLanguageSpinner);
        nativeLanguageSpinner.setAdapter(languageAdapter);
        nativeLanguageSpinner.setSelection(1);

        Spinner translationDirectionSpinner = (Spinner) findViewById(R.id.translationDirectionSpinner);
        String[] translationDirections = new String[] {
                getResources().getString(R.string.foreign_to_native),
                getResources().getString(R.string.native_to_foreign),
                getResources().getString(R.string.mixed),
        };
        ArrayAdapter<String> translationDirectionAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, translationDirections);
        translationDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        translationDirectionSpinner.setAdapter(translationDirectionAdapter);
        translationDirectionSpinner.setSelection(currentBox.translationDirection);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
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
