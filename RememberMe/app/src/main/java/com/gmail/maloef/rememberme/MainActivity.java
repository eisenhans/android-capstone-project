package com.gmail.maloef.rememberme;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.maloef.rememberme.domain.BoxOverview;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.CompartmentService;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;
import com.gmail.maloef.rememberme.service.WordService;
import com.gmail.maloef.rememberme.util.DateUtils;

import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle drawerToggle;
    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Bind(R.id.vocabularyBoxSpinner) Spinner vocabularyBoxSpinner;
    @Bind(R.id.foreignLanguageSpinner) Spinner foreignLanguageSpinner;
    @Bind(R.id.nativeLanguageSpinner) Spinner nativeLanguageSpinner;
    @Bind(R.id.translationDirectionSpinner) Spinner translationDirectionSpinner;

    @Bind(R.id.vocabularyBoxOverviewTable) TableLayout vocabularyBoxOverviewTable;

    @BindColor(R.color.colorTableRowDark) int colorTableRowDark;

    @BindString(R.string.rename_box) String renameBoxString;
    @BindString(R.string.enter_new_name_for_box) String enterNewNameForBoxString;
    @BindString(android.R.string.ok) String okString;
    @BindString(R.string.box_exists) String boxExistsString;

    @BindString(R.string.foreign_to_native) String foreignToNativeString;
    @BindString(R.string.native_to_foreign) String nativeToForeignString;
    @BindString(R.string.mixed) String mixedString;

    private String[] languageIsoCodes;

    private String[] languages;
    private String[] boxNames;

    private VocabularyBox selectedBox;

    @Inject VocabularyBoxService boxService;
    @Inject CompartmentService compartmentService;
    @Inject WordService wordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RememberMeApplication.injector().inject(this);
//        RememberMeApplication rememberMeApp = (RememberMeApplication) getApplication();
//        rememberMeApp.getInjector().inject(this);

        setSupportActionBar(toolbar);

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

//        boxService = new VocabularyBoxService(this);
        if (!boxService.isOneBoxSaved()) {
            boxService.createDefaultBox();
        }
        updateBoxSpinner();

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
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        nativeLanguageSpinner.setAdapter(languageAdapter);

        int nativeLanguagePos = languagePosition(selectedBox.nativeLanguage);
        nativeLanguageSpinner.setSelection(nativeLanguagePos);

        nativeLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedIso = languageIsoCodes[position];
                if (!selectedIso.equals(selectedBox.nativeLanguage)) {
                    selectedBox.nativeLanguage = selectedIso;
                    boxService.updateNativeLanguage(selectedBox._id, selectedIso);
                    logInfo("updated native language for box " + selectedBox.name + ": " + selectedIso);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        String[] translationDirections = new String[] { foreignToNativeString, nativeToForeignString, mixedString };

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
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

//        wordService = new WordService(this);
        createTestData();

//        compartmentService = new CompartmentService(this);
        BoxOverview boxOverview = compartmentService.getBoxOverview(selectedBox._id);

        for (int compartment = 1; compartment <= 5; compartment++) {
            addOverviewRow(compartment, boxOverview);
        }

    }

    private void addOverviewRow(final int compartment, BoxOverview boxOverview) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 16, 0, 16);

        if (compartment % 2 == 1) {
            row.setBackgroundColor(colorTableRowDark);
//            row.setBackgroundColor(getResources().getColor(R.color.blue100));
        }

        addCompartmentCell(row, compartment);
        addWordsCell(row, compartment, boxOverview);
        addNotRepeatedSinceCell(row, compartment, boxOverview);

        vocabularyBoxOverviewTable.addView(row);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInfo("clicked: " + v + ", compartment: " + compartment);
            }
        });
    }

    private void addCompartmentCell(TableRow row, int compartment) {
        addOverviewCell(row, String.valueOf(compartment));
    }

    private void addWordsCell(TableRow row, int compartment, BoxOverview boxOverview) {
        int words = boxOverview.getWordCount(compartment);
        addOverviewCell(row, String.valueOf(words));
    }

    private void addNotRepeatedSinceCell(TableRow row, int compartment, BoxOverview boxOverview) {
        Long repeatDate = boxOverview.getEarliestLastRepeatDate(compartment);
        String notRepeatedSince = calculateNotRepeatedSinceDays(repeatDate);
        addOverviewCell(row, notRepeatedSince);
    }

    private void addOverviewCell(TableRow row, String content) {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setTextAppearance(this, R.style.AppTheme_VocabularyBoxTableCell);
        textView.setText(content);

        row.addView(textView);
    }

    String calculateNotRepeatedSinceDays(Long repeatDate) {
        if (repeatDate == null) {
            return "-";
        }
        Date now = new Date();
        long days = DateUtils.getDaysBetweenMidnight(repeatDate, now.getTime());
        return String.valueOf(days);
    }

    void updateBoxSpinner() {
        boxNames = boxService.getBoxNames();
        selectedBox = boxService.getSelectedBox();

        logInfo("updating spinner, boxNames: " + Arrays.asList(boxNames));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, boxNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vocabularyBoxSpinner.setAdapter(spinnerAdapter);

        int selectedBoxPos = selectedBoxPosition();
        vocabularyBoxSpinner.setSelection(selectedBoxPos);

        vocabularyBoxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateSelectedBox(vocabularyBoxSpinner.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    int selectedBoxPosition() {
        for (int i = 0; i < boxNames.length; i++) {
            if (boxNames[i].equals(selectedBox.name)) {
                return i;
            }
        }
        throw new IllegalStateException("cannot find selected box: boxNames = " + Arrays.asList(boxNames) +
                ", selectedBox = " + selectedBox.name);
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

    @OnClick(R.id.renameBoxButton)
    public void showRenameBoxDialog(final View parentView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(renameBoxString);
        CharSequence message = Html.fromHtml(enterNewNameForBoxString + " <i>" + selectedBox.name + "</i>" + ":");
        alertDialogBuilder.setMessage(message);

        final EditText editText = new EditText(this);
        alertDialogBuilder.setView(editText);

        String ok = okString;
        alertDialogBuilder.setPositiveButton(ok, null);
        alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    String newBoxName = editText.getText().toString();
                                                    if (newBoxName == null || newBoxName.isEmpty()) {
                                                        return;
                                                    }
                                                    if (newBoxName.equals(selectedBox.name)) {
                                                        // user entered the same name again - just ignore this
                                                        alertDialog.dismiss();
                                                        return;
                                                    }
                                                    if (boxService.isBoxSaved(newBoxName)) {
                                                        // user entered a name that already exists - just keep the dialog open
                                                        Toast.makeText(getApplicationContext(), boxExistsString, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    boxService.updateBoxName(selectedBox._id, newBoxName);
                                                    logInfo("updated box name: " + newBoxName);
                                                    updateBoxSpinner();
                                                    alertDialog.dismiss();
                                                }
                                            }
                );
            }
        });

        alertDialog.show();
    }

    @OnClick(R.id.memorizeButton)
    public void memorizeWordsFromCompartment1(View parentView) {
        logInfo("showing memorize activity");
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void createTestData() {
        int boxId = selectedBox._id;

        wordService.createWord(boxId, "porcupine", "Stachelschwein");

        int ointmentId = wordService.createWord(boxId, "ointment", "Salbe");
        wordService.updateRepeatDate(ointmentId);

        int biasId = wordService.createWord(boxId, "bias", "Tendenz, Neigung");
        wordService.updateRepeatDate(biasId);

        wordService.createWord(boxId, 2, "emissary", "Abgesandter");
    }
}
