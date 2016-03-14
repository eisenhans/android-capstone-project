package com.gmail.maloef.rememberme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gmail.maloef.rememberme.domain.BoxOverview;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.CompartmentRepository;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.gmail.maloef.rememberme.service.LanguageUpdateService;
import com.gmail.maloef.rememberme.util.DateUtils;
import com.gmail.maloef.rememberme.util.dialog.InputProcessor;
import com.gmail.maloef.rememberme.util.dialog.InputValidator;
import com.gmail.maloef.rememberme.util.dialog.NotEmptyInputValidator;
import com.gmail.maloef.rememberme.util.dialog.ValidatingInputDialog;

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
    @Bind(R.id.navigationView) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.mainFragmentLayout) LinearLayout mainFragmentLayout;

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

    @BindString(R.string.create_box) String createBoxString;
    @BindString(R.string.enter_name_for_new_box) String enterNameForNewBoxString;

    @BindString(R.string.foreign_to_native) String foreignToNativeString;
    @BindString(R.string.native_to_foreign) String nativeToForeignString;
    @BindString(R.string.mixed) String mixedString;

    private Pair<String, String>[] codeLanguagePairs;
    private String[] languageCodes;
    private String[] languageNames;
    private int languageCount;

    private String[] boxNames;

    private VocabularyBox selectedBox;
    @Inject
    VocabularyBoxRepository boxService;
    @Inject
    CompartmentRepository compartmentService;
    @Inject
    WordRepository wordService;
    @Inject
    LanguageRepository languageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RememberMeApplication.injector().inject(this);

        setSupportActionBar(toolbar);

        drawerToggle = setupDrawerToggle();
        drawer.setDrawerListener(drawerToggle);

        logInfo("navigationView: " + navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                logInfo("navigationItem selected: " + item);
                if (item.getItemId() == R.id.create_new_box_item) {
                    showNewBoxNameDialog(mainFragmentLayout);
                }
                return true;
            }
        });

        codeLanguagePairs = languageService.getLanguages("en");
        languageCodes = new String[codeLanguagePairs.length];
        languageNames = new String[codeLanguagePairs.length];

        for (int i = 0; i < codeLanguagePairs.length; i++) {
            languageCodes[i] = codeLanguagePairs[i].first;
            languageNames[i] = codeLanguagePairs[i].second;
        }

        if (!boxService.isOneBoxSaved()) {
            boxService.createDefaultBox();
        }
        updateBoxSpinner();

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

        createTestData();

        BoxOverview boxOverview = compartmentService.getBoxOverview(selectedBox._id);

        for (int compartment = 1; compartment <= 5; compartment++) {
            addOverviewRow(compartment, boxOverview);
        }

        updateLanguageSpinners();
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateLanguageSpinners();
            }
        };
        IntentFilter languagesUpdatedFilter = new IntentFilter(LanguageUpdateService.LANGUAGES_UPDATED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, languagesUpdatedFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void updateLanguageSpinners() {
        if (languageCount > 0 && languageCount == languageService.countLanguages("en")) {
            // languages are up to date
            return;
        }
        LanguageSettingsManager languageSettingsManager = new LanguageSettingsManager(this, boxService, languageService);
        languageSettingsManager.configureForeignLanguageSpinner(foreignLanguageSpinner);
        languageSettingsManager.configureNativeLanguageSpinner(nativeLanguageSpinner);

        languageCount = languageService.countLanguages("en");
    }

    private void addOverviewRow(final int compartment, BoxOverview boxOverview) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 16, 0, 16);

        if (compartment % 2 == 1) {
            row.setBackgroundColor(colorTableRowDark);
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
        if (isoCode == null) {
            return 0;
        }
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(isoCode)) {
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
        InputProcessor inputProcessor = new InputProcessor() {
            @Override
            public void process(String newBoxName) {
                boxService.updateBoxName(selectedBox._id, newBoxName);
                logInfo("updated box name: " + newBoxName);
                updateBoxSpinner();
            }
        };
        InputValidator inputValidator = createNewBoxNameInputValidator();
        CharSequence message = Html.fromHtml(enterNewNameForBoxString + " <i>" + selectedBox.name + "</i>" + ":");
        ValidatingInputDialog dialog = new ValidatingInputDialog(this, renameBoxString, message, inputValidator, inputProcessor);
        dialog.show();
    }

    public void showNewBoxNameDialog(final View parentView) {
        InputProcessor inputProcessor = new InputProcessor() {
            @Override
            public void process(String newBoxName) {
                boxService.createBox(newBoxName, null, selectedBox.nativeLanguage, selectedBox.translationDirection, true);
                logInfo("created new box: " + newBoxName);
                updateBoxSpinner();
                logInfo("closing dialog and drawer");
                drawer.closeDrawer(GravityCompat.START);
            }
        };
        InputValidator inputValidator = createNewBoxNameInputValidator();
        ValidatingInputDialog dialog = new ValidatingInputDialog(this, createBoxString, enterNameForNewBoxString, inputValidator, inputProcessor);
        dialog.show();
    }

    private InputValidator createNewBoxNameInputValidator() {
        return new NotEmptyInputValidator() {
            @Override
            public boolean isValid(String input) {
                return super.isValid(input) && !boxService.isBoxSaved(input);
            }
        };
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
