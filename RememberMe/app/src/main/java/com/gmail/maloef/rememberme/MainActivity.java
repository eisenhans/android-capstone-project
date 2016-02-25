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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private Spinner vocabularyBoxSpinner;
    private Button renameBoxButton;
    private Spinner foreignLanguageSpinner;
    private Spinner nativeLanguageSpinner;
    private Spinner translationDirectionSpinner;

    private String[] languageIsoCodes;
    private String[] languages;
    private String[] boxNames;
    private VocabularyBox selectedBox;
    private VocabularyBoxService boxService;

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

        languageIsoCodes = getResources().getStringArray(R.array.languageIsoCodes);
        languages = getResources().getStringArray(R.array.languages);

        boxService = new VocabularyBoxService(this);
        if (!boxService.isOneBoxSaved()) {
            boxService.createDefaultBox();
        }
        boxNames = boxService.getBoxNames();
        selectedBox = boxService.getSelectedBox();

        vocabularyBoxSpinner = (Spinner) findViewById(R.id.vocabularyBoxSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, boxNames);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vocabularyBoxSpinner.setAdapter(spinnerAdapter);

        vocabularyBoxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateSelectedBox(vocabularyBoxSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //renameBoxButton = (Button) findViewById(R.id.renameBoxButton);

        foreignLanguageSpinner = (Spinner) findViewById(R.id.foreignLanguageSpinner);
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList(languages));
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foreignLanguageSpinner.setAdapter(languageAdapter);

        int foreignLanguagePos = languagePosition(selectedBox.foreignLanguage);
        foreignLanguageSpinner.setSelection(foreignLanguagePos);

        foreignLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selectedItemPos = foreignLanguageSpinner.getSelectedItemPosition();
                String selectedIso = languageIsoCodes[selectedItemPos];
                if (!selectedIso.equals(selectedBox.foreignLanguage)) {
                    selectedBox.foreignLanguage = selectedIso;
                    boxService.updateForeignLanguage(selectedBox._id, selectedIso);
                    logInfo("updated foreign language for box " + selectedBox.name + ": " + selectedIso);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        nativeLanguageSpinner = (Spinner) findViewById(R.id.nativeLanguageSpinner);
        nativeLanguageSpinner.setAdapter(languageAdapter);

        int nativeLanguagePos = languagePosition(selectedBox.nativeLanguage);
        nativeLanguageSpinner.setSelection(nativeLanguagePos);

        nativeLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                int selectedItemPos = nativeLanguageSpinner.getSelectedItemPosition();
                String selectedIso = languageIsoCodes[position];
                if (!selectedIso.equals(selectedBox.nativeLanguage)) {
                    selectedBox.nativeLanguage = selectedIso;
                    boxService.updateNativeLanguage(selectedBox._id, selectedIso);
                    logInfo("updated native language for box " + selectedBox.name + ": " + selectedIso);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        translationDirectionSpinner = (Spinner) findViewById(R.id.translationDirectionSpinner);
        String[] translationDirections = new String[] {
                getResources().getString(R.string.foreign_to_native),
                getResources().getString(R.string.native_to_foreign),
                getResources().getString(R.string.mixed),
        };
        ArrayAdapter<String> translationDirectionAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, translationDirections);
        translationDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        translationDirectionSpinner.setAdapter(translationDirectionAdapter);
        translationDirectionSpinner.setSelection(selectedBox.translationDirection);

        translationDirectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != selectedBox.translationDirection) {
                    selectedBox.translationDirection = position;
                    boxService.updateTranslationDirection(selectedBox._id, position);
                    logInfo("updated translation direction for box " + selectedBox.name + ": " + position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    void updateSelectedBox(String boxName) {
        if (boxName.equals(selectedBox.name)) {
            return;
        }
        selectedBox = boxService.selectBoxByName(boxName);

        int foreignLanguagePos = languagePosition(selectedBox.foreignLanguage);
        foreignLanguageSpinner.setSelection(foreignLanguagePos);

        int nativeLanguagePos = languagePosition(selectedBox.nativeLanguage);
        nativeLanguageSpinner.setSelection(nativeLanguagePos);

        translationDirectionSpinner.setSelection(selectedBox.translationDirection);

        logInfo("updated selected box: " + boxName);
    }

    int languagePosition(String isoCode) {
        for (int i = 0; i < languageIsoCodes.length; i++) {
            if (languageIsoCodes[i].equals(isoCode)) {
                return i;
            }
        }
        throw new IllegalArgumentException("unknown iso code: " + isoCode);
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

    public void renameBox() {

    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
