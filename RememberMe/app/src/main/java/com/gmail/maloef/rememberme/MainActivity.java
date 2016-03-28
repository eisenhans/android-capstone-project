package com.gmail.maloef.rememberme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.maloef.rememberme.domain.BoxOverview;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.CompartmentRepository;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.gmail.maloef.rememberme.service.LanguageUpdateService;
import com.gmail.maloef.rememberme.util.DateUtils;
import com.gmail.maloef.rememberme.util.StringUtils;
import com.gmail.maloef.rememberme.util.dialog.InputProcessor;
import com.gmail.maloef.rememberme.util.dialog.InputValidator;
import com.gmail.maloef.rememberme.util.dialog.ValidatingInputDialog;

import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AbstractRememberMeActivity {

    @Bind(R.id.mainFragmentLayout) LinearLayout mainFragmentLayout;

    @Bind(R.id.vocabularyBoxSpinner) Spinner vocabularyBoxSpinner;
    @Bind(R.id.foreignLanguageSpinner) Spinner foreignLanguageSpinner;
    @Bind(R.id.nativeLanguageSpinner) Spinner nativeLanguageSpinner;
    @Bind(R.id.translationDirectionSpinner) Spinner translationDirectionSpinner;

    @Bind(R.id.vocabularyBoxOverviewTable) TableLayout vocabularyBoxOverviewTable;
    @Bind(R.id.overviewTableRow1) TableRow overviewTableRow1;
    @Bind(R.id.overviewTableRow2) TableRow overviewTableRow2;
    @Bind(R.id.overviewTableRow3) TableRow overviewTableRow3;
    @Bind(R.id.overviewTableRow4) TableRow overviewTableRow4;
    @Bind(R.id.overviewTableRow5) TableRow overviewTableRow5;

    @Bind(R.id.overviewWords1) TextView overviewWords1TextView;
    @Bind(R.id.overviewWords2) TextView overviewWords2TextView;
    @Bind(R.id.overviewWords3) TextView overviewWords3TextView;
    @Bind(R.id.overviewWords4) TextView overviewWords4TextView;
    @Bind(R.id.overviewWords5) TextView overviewWords5TextView;

    @Bind(R.id.overviewNotRepeated1) TextView overviewNotRepeated1TextView;
    @Bind(R.id.overviewNotRepeated2) TextView overviewNotRepeated2TextView;
    @Bind(R.id.overviewNotRepeated3) TextView overviewNotRepeated3TextView;
    @Bind(R.id.overviewNotRepeated4) TextView overviewNotRepeated4TextView;
    @Bind(R.id.overviewNotRepeated5) TextView overviewNotRepeated5TextView;

    @Bind(R.id.memorizeButton) Button memorizeButton;

    @BindColor(R.color.colorTableRowDark) int colorTableRowDark;

    @BindString(R.string.rename_box) String renameBoxString;
    @BindString(R.string.enter_new_name_for_box) String enterNewNameForBoxString;
    @BindString(android.R.string.ok) String okString;
    @BindString(R.string.box_exists) String boxExistsString;

    @BindString(R.string.create_new_box) String createNewBoxString;
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

    @Inject VocabularyBoxRepository boxRepository;
    @Inject CompartmentRepository compartmentRepository;
    @Inject WordRepository wordRepository;
    @Inject LanguageRepository languageRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RememberMeApplication.injector().inject(this);

        initToolbar(false, R.string.vocabulary_box);

        codeLanguagePairs = languageRepository.getLanguages("en");
        languageCodes = new String[codeLanguagePairs.length];
        languageNames = new String[codeLanguagePairs.length];

        for (int i = 0; i < codeLanguagePairs.length; i++) {
            languageCodes[i] = codeLanguagePairs[i].first;
            languageNames[i] = codeLanguagePairs[i].second;
        }

        if (!boxRepository.isOneBoxSaved()) {
            boxRepository.createDefaultBox();
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
                    boxRepository.updateTranslationDirection(selectedBox.id, position);
                    logInfo("updated translation direction for box " + selectedBox.name + ": " + position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

//        createTestData();

        addRowListeners();

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
    public void onResume() {
        super.onResume();

        // move all words from the 'virtual' compartment 0 to compartment 1
        wordRepository.moveAll(selectedBox.id, 0, 1);

        updateOverviewTable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void updateLanguageSpinners() {
        if (languageCount > 0 && languageCount == languageRepository.countLanguages("en")) {
            // languages are up to date
            return;
        }
        LanguageSettingsManager languageSettingsManager = new LanguageSettingsManager(this, boxRepository, languageRepository);
        languageSettingsManager.configureForeignLanguageSpinner(foreignLanguageSpinner);
        languageSettingsManager.configureNativeLanguageSpinner(nativeLanguageSpinner);

        languageCount = languageRepository.countLanguages("en");
    }

    private void addRowListeners() {
        addRowListener(overviewTableRow1, 1);
        addRowListener(overviewTableRow2, 2);
        addRowListener(overviewTableRow3, 3);
        addRowListener(overviewTableRow4, 4);
        addRowListener(overviewTableRow5, 5);
    }

    private void addRowListener(TableRow row, final int compartment) {
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInfo("clicked: compartment " + compartment);
                int wordsInCompartment = wordRepository.countWords(selectedBox.id, compartment);
                if (wordsInCompartment == 0) {
                    return;
                }

                int translationDirection = selectedBox.translationDirection;
                if (translationDirection == VocabularyBox.TRANSLATION_DIRECTION_MIXED) {
                    double random = Math.random();
                    translationDirection = (random >= 0.5) ?
                            VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE : VocabularyBox.TRANSLATION_DIRECTION_NATIVE_TO_FOREIGN;
                }

                Intent intent = new Intent(MainActivity.this, WordActivity.class)
                        .setAction(RememberMeIntent.ACTION_QUERY)
                        .putExtra(RememberMeIntent.EXTRA_BOX_ID, selectedBox.id)
                        .putExtra(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, translationDirection)
                        .putExtra(RememberMeIntent.EXTRA_COMPARTMENT, compartment)
                        .putExtra(RememberMeIntent.EXTRA_WORDS_IN_COMPARTMENT, wordsInCompartment);
                startActivity(intent);
            }
        });
    }

    private void updateOverviewTable() {
        BoxOverview boxOverview = compartmentRepository.getBoxOverview(selectedBox.id);
        overviewWords1TextView.setText(String.valueOf(boxOverview.getWordCount(1)));
        overviewWords2TextView.setText(String.valueOf(boxOverview.getWordCount(2)));
        overviewWords3TextView.setText(String.valueOf(boxOverview.getWordCount(3)));
        overviewWords4TextView.setText(String.valueOf(boxOverview.getWordCount(4)));
        overviewWords5TextView.setText(String.valueOf(boxOverview.getWordCount(5)));

        overviewNotRepeated1TextView.setText(calculateDaysSinceRepeat(boxOverview, 1));
        overviewNotRepeated2TextView.setText(calculateDaysSinceRepeat(boxOverview, 2));
        overviewNotRepeated3TextView.setText(calculateDaysSinceRepeat(boxOverview, 3));
        overviewNotRepeated4TextView.setText(calculateDaysSinceRepeat(boxOverview, 4));
        overviewNotRepeated5TextView.setText(calculateDaysSinceRepeat(boxOverview, 5));

        memorizeButton.setEnabled(boxOverview.getWordCount(1) > 0);
    }

    private String calculateDaysSinceRepeat(BoxOverview boxOverview, int compartment) {
        long repeatDate = boxOverview.getEarliestLastRepeatDate(compartment);
        if (repeatDate == 0) {
            return "-";
        }
        Date now = new Date();
        long days = DateUtils.getDaysBetweenMidnight(repeatDate, now.getTime());
        logInfo("days since repeat for compartment " + compartment + ": repeat date was " + new Date(repeatDate) + ", now is " + now +
                " days between these two dates = " + days);
        if (days == 0) {
            return getString(R.string.today);
        }
        if (days == 1) {
            return getString(R.string.yesterday);
        }
        return getString(R.string.i_days_ago, days);
    }

    void updateBoxSpinner() {
        boxNames = boxRepository.getBoxNames();
        selectedBox = boxRepository.getSelectedBox();

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
        selectedBox = boxRepository.selectBoxByName(boxName);

        int foreignLanguagePos = languagePosition(selectedBox.foreignLanguage);
        foreignLanguageSpinner.setSelection(foreignLanguagePos);

        int nativeLanguagePos = languagePosition(selectedBox.nativeLanguage);
        nativeLanguageSpinner.setSelection(nativeLanguagePos);

        translationDirectionSpinner.setSelection(selectedBox.translationDirection);

        updateOverviewTable();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private InputValidator createNewBoxNameInputValidator() {
        return new InputValidator() {
            @Override
            public boolean isValid(String input) {
                return StringUtils.isNotBlank(input) && !boxRepository.isBoxSaved(input);
            }
        };
    }

    @OnClick(R.id.memorizeButton)
    public void memorizeWordsFromCompartment1(View parentView) {
        Intent intent = new Intent(MainActivity.this, MemorizeActivity.class)
                .putExtra(RememberMeIntent.EXTRA_BOX_ID, selectedBox.id);
        startActivity(intent);
    }

    @OnClick(R.id.renameBoxButton)
    public void showRenameBoxDialog(final View parentView) {
        InputProcessor inputProcessor = new InputProcessor() {
            @Override
            public void process(String newBoxName) {
                boxRepository.updateBoxName(selectedBox.id, newBoxName);
                logInfo("updated box name: " + newBoxName);
                updateBoxSpinner();
            }
        };
        InputValidator inputValidator = createNewBoxNameInputValidator();
        CharSequence message = Html.fromHtml(enterNewNameForBoxString + " <i>" + selectedBox.name + "</i>" + ":");
        ValidatingInputDialog dialog = new ValidatingInputDialog(this, renameBoxString, message, inputValidator, inputProcessor);
        dialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem deleteBoxItem = menu.getItem(1);
        int boxes = boxRepository.countBoxes();
        deleteBoxItem.setEnabled(boxes >= 2);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_new_box) {
            showCreateNewBoxDialog();
            return true;
        }
        if (item.getItemId() == R.id.action_delete_current_box) {
            int wordsInSelectedBox = wordRepository.countWords(selectedBox.id);
            if (wordsInSelectedBox > 0) {
                showDeleteSelectedBoxDialog();
            } else {
                deleteSelectedBox();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showCreateNewBoxDialog() {
        InputProcessor inputProcessor = new InputProcessor() {
            @Override
            public void process(String newBoxName) {
                createNewBox(newBoxName);
            }
        };
        InputValidator inputValidator = createNewBoxNameInputValidator();
        ValidatingInputDialog dialog = new ValidatingInputDialog(this, createNewBoxString, enterNameForNewBoxString, inputValidator, inputProcessor);
        dialog.show();
    }

    public void createNewBox(String boxName) {
        boxRepository.createBox(boxName, null, selectedBox.nativeLanguage, selectedBox.translationDirection, true);
        logInfo("created new box: " + boxName);
        updateBoxSpinner();
        updateOverviewTable();
    }

    public void showDeleteSelectedBoxDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String rawTitle = getString(R.string.delete_box_s_and_all_its_words, selectedBox.name);
        CharSequence title = Html.fromHtml(rawTitle);
        logInfo("raw title for dialog: " + rawTitle + ", real title: " + title);
        builder.setTitle(title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteSelectedBox();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteSelectedBox() {
        String boxName = selectedBox.name;
        boxRepository.deleteSelectedBox();
        updateBoxSpinner();
        updateOverviewTable();
        CharSequence message = Html.fromHtml(getString(R.string.deleted_box_s, boxName));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    void createTestData() {
        int boxId = selectedBox.id;

        if (wordRepository.countWords(boxId, 1) > 0) {
            return;
        }

        wordRepository.createWord(boxId, "porcupine", "Stachelschwein");

        int ointmentId = wordRepository.createWord(boxId, "ointment", "Salbe");
        wordRepository.updateRepeatDate(ointmentId);

        int biasId = wordRepository.createWord(boxId, "bias", "Tendenz, Neigung");
        wordRepository.updateRepeatDate(biasId);

        wordRepository.createWord(boxId, 2, "emissary", "Abgesandter");
    }
}
