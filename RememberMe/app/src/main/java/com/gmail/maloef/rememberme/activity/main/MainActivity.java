package com.gmail.maloef.rememberme.activity.main;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.maloef.rememberme.LanguageLoader;
import com.gmail.maloef.rememberme.LanguageSettingsManager;
import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.RememberMeApplication;
import com.gmail.maloef.rememberme.RememberMeIntent;
import com.gmail.maloef.rememberme.activity.AbstractRememberMeActivity;
import com.gmail.maloef.rememberme.activity.memorize.MemorizeActivity;
import com.gmail.maloef.rememberme.activity.memorize.MemorizeFragment;
import com.gmail.maloef.rememberme.activity.word.AddWordFragment;
import com.gmail.maloef.rememberme.activity.word.EditWordFragment;
import com.gmail.maloef.rememberme.activity.word.QueryWordFragment;
import com.gmail.maloef.rememberme.activity.word.ShowWordFragment;
import com.gmail.maloef.rememberme.activity.word.WordActivity;
import com.gmail.maloef.rememberme.activity.wordlist.WordListActivity;
import com.gmail.maloef.rememberme.activity.wordlist.WordListFragment;
import com.gmail.maloef.rememberme.domain.BoxOverview;
import com.gmail.maloef.rememberme.domain.Language;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;
import com.gmail.maloef.rememberme.util.DateUtils;
import com.gmail.maloef.rememberme.util.StringUtils;
import com.gmail.maloef.rememberme.util.dialog.ConfirmDialog;
import com.gmail.maloef.rememberme.util.dialog.InputProcessor;
import com.gmail.maloef.rememberme.util.dialog.InputValidator;
import com.gmail.maloef.rememberme.util.dialog.ValidatingInputDialog;
import com.gmail.maloef.rememberme.widget.UpdateWidgetIntentService;

import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AbstractRememberMeActivity implements LoaderManager.LoaderCallbacks<Language[]>,
        QueryWordFragment.AnswerListener, ShowWordFragment.ShowWordCallback, AddWordFragment.Callback, EditWordFragment.Callback,
        MemorizeFragment.Callback {

    @Inject VocabularyBoxRepository boxRepository;
    @Inject WordRepository wordRepository;
    @Inject LanguageRepository languageRepository;
    @Inject LanguageProvider languageProvider;

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
    @Bind(R.id.overviewTableRow6) TableRow overviewTableRow6;

    @Bind(R.id.overviewWords1) TextView overviewWords1TextView;
    @Bind(R.id.overviewWords2) TextView overviewWords2TextView;
    @Bind(R.id.overviewWords3) TextView overviewWords3TextView;
    @Bind(R.id.overviewWords4) TextView overviewWords4TextView;
    @Bind(R.id.overviewWords5) TextView overviewWords5TextView;
    @Bind(R.id.overviewWords6) TextView overviewWords6TextView;

    @Bind(R.id.overviewNotRepeated1) TextView overviewNotRepeated1TextView;
    @Bind(R.id.overviewNotRepeated2) TextView overviewNotRepeated2TextView;
    @Bind(R.id.overviewNotRepeated3) TextView overviewNotRepeated3TextView;
    @Bind(R.id.overviewNotRepeated4) TextView overviewNotRepeated4TextView;
    @Bind(R.id.overviewNotRepeated5) TextView overviewNotRepeated5TextView;
    @Bind(R.id.overviewNotRepeated6) TextView overviewNotRepeated6TextView;

    @Bind(R.id.memorizeButton) Button memorizeButton;

    @Bind(R.id.detail_container) @Nullable View contentDetailView;

    @BindString(R.string.rename_box) String renameBoxString;
    @BindString(R.string.enter_new_name_for_box) String enterNewNameForBoxString;
    @BindString(android.R.string.ok) String okString;
    @BindString(R.string.box_exists) String boxExistsString;

    @BindString(R.string.create_new_box) String createNewBoxString;
    @BindString(R.string.enter_name_for_new_box) String enterNameForNewBoxString;

    @BindString(R.string.foreign_to_native) String foreignToNativeString;
    @BindString(R.string.native_to_foreign) String nativeToForeignString;
    @BindString(R.string.randomString) String randomString;

    private String[] boxNames;

    private VocabularyBox selectedBox;
    private LanguageSettingsManager languageSettingsManager;
    private boolean loadFinished;

    private boolean twoPaneLayout;

    private int compartmentForQuery;
    private int wordsInCompartmentForQuery;
    private int translationDirectionForQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RememberMeApplication.injector().inject(this);

        if (!boxRepository.isOneBoxSaved()) {
            boxRepository.createDefaultBox();
        }
        selectedBox = boxRepository.getSelectedBox();
        getLoaderManager().initLoader(0, null, this);

        if (contentDetailView != null) {
            logInfo("twoPaneLayout because contentDetailView is not null");
            twoPaneLayout = true;
        } else if (isWordSharedFromOtherApp()) {
            String foreignWord = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            // user shared a word from another app -> just add this word and go back to other app, skip rest of this method
            Intent intent = new Intent(this, WordActivity.class)
                    .setAction(RememberMeIntent.ACTION_ADD)
                    .putExtra(RememberMeIntent.EXTRA_BOX_ID, selectedBox.id)
                    .putExtra(RememberMeIntent.EXTRA_FOREIGN_WORD, foreignWord);
            startActivity(intent);
            finish();
            return;
        }

        initToolbar(false, R.string.vocabulary_box);

        updateBoxSpinner();

        String[] translationDirections = new String[] { foreignToNativeString, nativeToForeignString, randomString};

        ArrayAdapter<String> translationDirectionAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, translationDirections);
        translationDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        translationDirectionSpinner.setAdapter(translationDirectionAdapter);
        translationDirectionSpinner.setSelection(selectedBox.translationDirection);

        translationDirectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != selectedBox.translationDirection) {
                    selectedBox.translationDirection = position;
                    boxRepository.updateTranslationDirection(selectedBox.id, position);
                    translationDirectionForQuery = position;
                    logInfo("updated translation direction for box " + selectedBox.name + ": " + position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        createTestData();

        addRowListeners();
        updateOverviewTable();

        if (isWordSharedFromOtherApp()) {
            // we're in twoPaneLayout, otherwise we wouldn't get here
            String foreignWord = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            showAddWordFragment(foreignWord);
        }
    }

    private boolean isWordSharedFromOtherApp() {
        String action = getIntent().getAction();
        return Intent.ACTION_SEND.equals(action);
    }

    @Override
    public void onResume() {
        super.onResume();

        clearTempCompartment();
        updateSelectedBox(selectedBox.name);
        updateOverviewTable();
    }

    @Override
    public void onStop() {
        super.onStop();

        logInfo("updating widget because user may have repeated words");
        startService(new Intent(this, UpdateWidgetIntentService.class));
    }

    private void clearTempCompartment() {
        // move all words from the 'virtual' compartment 0 to compartment 1
        wordRepository.moveAll(selectedBox.id, 0, 1);
    }

    private void updateForeignLanguageSpinner(String foreignLanguage) {
        if (loadFinished) {
            languageSettingsManager.updateForeignLanguageSpinner(foreignLanguageSpinner, foreignLanguage);
        }
    }

    private void updateNativeLanguageSpinner(String nativeLanguage) {
        if (loadFinished) {
            languageSettingsManager.updateNativeLanguageSpinner(nativeLanguageSpinner, nativeLanguage);
        }
    }

    private void addRowListeners() {
        addWordActivityRowListener(overviewTableRow1, 1);
        addWordActivityRowListener(overviewTableRow2, 2);
        addWordActivityRowListener(overviewTableRow3, 3);
        addWordActivityRowListener(overviewTableRow4, 4);
        addWordActivityRowListener(overviewTableRow5, 5);
        addWordListActivityRowListener(overviewTableRow6, 6);
    }

    private void addWordActivityRowListener(TableRow row, final int compartment) {
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInfo("clicked: compartment " + compartment);
                compartmentForQuery = compartment;
                wordsInCompartmentForQuery = wordRepository.countWords(selectedBox.id, compartment);
                if (wordsInCompartmentForQuery == 0) {
                    return;
                }

                translationDirectionForQuery = selectedBox.translationDirection;
                if (translationDirectionForQuery == VocabularyBox.TRANSLATION_DIRECTION_RANDOM) {
                    double random = Math.random();
                    translationDirectionForQuery = (random >= 0.5) ?
                            VocabularyBox.TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE : VocabularyBox.TRANSLATION_DIRECTION_NATIVE_TO_FOREIGN;
                }
                if (twoPaneLayout) {
                    clearTempCompartment();
                    updateOverviewTable();
                    showQueryWordFragment(selectedBox.id, compartment, translationDirectionForQuery);
                } else {
                    Intent intent = new Intent(MainActivity.this, WordActivity.class)
                            .setAction(RememberMeIntent.ACTION_QUERY)
                            .putExtra(RememberMeIntent.EXTRA_BOX_ID, selectedBox.id)
                            .putExtra(RememberMeIntent.EXTRA_TRANSLATION_DIRECTION, translationDirectionForQuery)
                            .putExtra(RememberMeIntent.EXTRA_COMPARTMENT, compartment)
                            .putExtra(RememberMeIntent.EXTRA_WORDS_IN_COMPARTMENT, wordsInCompartmentForQuery);
                    startActivity(intent);
                }
            }
        });
    }

    private void addWordListActivityRowListener(TableRow row, final int compartment) {
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logInfo("clicked: compartment " + compartment);
                int wordsInCompartment = wordRepository.countWords(selectedBox.id, compartment);
                if (wordsInCompartment == 0) {
                    return;
                }
                if (twoPaneLayout) {
                    initToolbar(false, R.string.words_learned);
                    clearTempCompartment();
                    updateOverviewTable();
                    showWordListFragment(selectedBox.id, compartment);
                } else {
                    Intent intent = new Intent(MainActivity.this, WordListActivity.class)
                            .setAction(RememberMeIntent.ACTION_SHOW)
                            .putExtra(RememberMeIntent.EXTRA_BOX_ID, selectedBox.id)
                            .putExtra(RememberMeIntent.EXTRA_COMPARTMENT, compartment);

                    startActivity(intent);
                }
            }
        });
    }

    void updateBoxSpinner() {
        boxNames = boxRepository.getBoxNames();

        logInfo("updating spinner, boxNames: " + Arrays.asList(boxNames));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, boxNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vocabularyBoxSpinner.setAdapter(spinnerAdapter);

        int selectedBoxPos = selectedBoxPosition();
        vocabularyBoxSpinner.setSelection(selectedBoxPos);

        vocabularyBoxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                logInfo("item selected: position=" + position + ", id=" + id + ", vocabularyBoxSpinner.getSelectedItem()=" +
                        vocabularyBoxSpinner.getSelectedItem() + ", boxName so far=" + selectedBox.name);
                String newBoxName = vocabularyBoxSpinner.getSelectedItem().toString();
                if (!newBoxName.equals(selectedBox.name)) {
                    updateSelectedBox(vocabularyBoxSpinner.getSelectedItem().toString());
                    clearState();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    void updateSelectedBox(String boxName) {
        selectedBox = boxRepository.selectBoxByName(boxName);

        // This can happen before the languages are loaded from Google Translate. Therefore we update the spinners now only if
        // loadFinished is true. They are updated again from onLoadFinished().
        updateForeignLanguageSpinner(selectedBox.foreignLanguage);
        updateNativeLanguageSpinner(selectedBox.nativeLanguage);
        translationDirectionSpinner.setSelection(selectedBox.translationDirection);

        logInfo("updated selected box: " + boxName);
    }

    private void updateOverviewTable() {
        BoxOverview boxOverview = wordRepository.getBoxOverview(selectedBox.id);
        overviewWords1TextView.setText(String.valueOf(boxOverview.getWordCount(1)));
        overviewWords2TextView.setText(String.valueOf(boxOverview.getWordCount(2)));
        overviewWords3TextView.setText(String.valueOf(boxOverview.getWordCount(3)));
        overviewWords4TextView.setText(String.valueOf(boxOverview.getWordCount(4)));
        overviewWords5TextView.setText(String.valueOf(boxOverview.getWordCount(5)));
        overviewWords6TextView.setText(String.valueOf(boxOverview.getWordCount(6)));

        overviewNotRepeated1TextView.setText(calculateDaysSinceRepeat(boxOverview, 1));
        overviewNotRepeated2TextView.setText(calculateDaysSinceRepeat(boxOverview, 2));
        overviewNotRepeated3TextView.setText(calculateDaysSinceRepeat(boxOverview, 3));
        overviewNotRepeated4TextView.setText(calculateDaysSinceRepeat(boxOverview, 4));
        overviewNotRepeated5TextView.setText(calculateDaysSinceRepeat(boxOverview, 5));
        overviewNotRepeated6TextView.setText(calculateDaysSinceRepeat(boxOverview, 6));

        overviewTableRow1.setBackgroundColor(calculateRowColor(boxOverview, 1));
        overviewTableRow2.setBackgroundColor(calculateRowColor(boxOverview, 2));
        overviewTableRow3.setBackgroundColor(calculateRowColor(boxOverview, 3));
        overviewTableRow4.setBackgroundColor(calculateRowColor(boxOverview, 4));
        overviewTableRow5.setBackgroundColor(calculateRowColor(boxOverview, 5));
        overviewTableRow6.setBackgroundColor(calculateRowColor(boxOverview, 6));

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
                ", days between these two dates: " + days);
        if (days == 0) {
            return getString(R.string.today);
        }
        if (days == 1) {
            return getString(R.string.yesterday);
        }
        return getString(R.string.i_days_ago, days);
    }

    private int calculateRowColor(BoxOverview boxOverview, int compartment) {
        if (boxOverview.getWordCount(compartment) == 0) {
            return getResources().getColor(R.color.colorTableRowDefault);
        }
        if (boxOverview.isWordDue(compartment)) {
            return getResources().getColor(R.color.colorTableRowWordsDue);
        }
        return getResources().getColor(R.color.colorTableRowNoWordsDue);
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
        if (twoPaneLayout) {
            logInfo("showing words in right pane");
            initToolbar(false, R.string.memorize);
            clearTempCompartment();
            updateOverviewTable();
            showMemorizeFragment(selectedBox.id);
        } else {
            Intent intent = new Intent(MainActivity.this, MemorizeActivity.class).putExtra(RememberMeIntent.EXTRA_BOX_ID, selectedBox.id);
            startActivity(intent);
        }
    }

    public void showRenameBoxDialog() {
        InputProcessor inputProcessor = new InputProcessor() {
            @Override
            public void process(String newBoxName) {
                boxRepository.updateBoxName(selectedBox.id, newBoxName);
                selectedBox = boxRepository.getSelectedBox();
                logInfo("updated box name: " + newBoxName);
                updateBoxSpinner();
                clearState();
            }
        };
        InputValidator inputValidator = createNewBoxNameInputValidator();
        CharSequence message = Html.fromHtml(enterNewNameForBoxString + " <i>" + selectedBox.name + "</i>" + ":");
        ValidatingInputDialog dialog = new ValidatingInputDialog(this, renameBoxString, message, inputValidator, inputProcessor);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem deleteBoxItem = menu.findItem(R.id.action_delete_current_box);
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
        if (item.getItemId() == R.id.action_rename_box) {
            showRenameBoxDialog();
            return true;
        }
        if (item.getItemId() == R.id.action_delete_current_box) {
            int wordsInSelectedBox = wordRepository.countWords(selectedBox.id);
            if (wordsInSelectedBox > 0) {
                showConfirmDeleteDialog();
            } else {
                deleteSelectedBox();
            }
            return true;
        }
        if (item.getItemId() == R.id.action_add_word) {
            if (twoPaneLayout) {
                clearTempCompartment();
                updateOverviewTable();
                showAddWordFragment(null);
            } else {
                Intent intent = new Intent(MainActivity.this, WordActivity.class)
                        .setAction(RememberMeIntent.ACTION_ADD)
                        .putExtra(RememberMeIntent.EXTRA_BOX_ID, selectedBox.id);

                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmDeleteDialog() {
        CharSequence message = Html.fromHtml(getString(R.string.delete_box_s_and_all_its_words, selectedBox.name));
        ConfirmDialog dialog = new ConfirmDialog(this, null, message, new ConfirmDialog.OkCallback() {
            @Override
            public void onOk() {
                deleteSelectedBox();
            }
        });
        dialog.show();
    }

    public void showCreateNewBoxDialog() {
        InputProcessor inputProcessor = new InputProcessor() {
            @Override
            public void process(String newBoxName) {
                createNewBox(newBoxName);
            }
        };
        InputValidator inputValidator = createNewBoxNameInputValidator();
        ValidatingInputDialog dialog = new ValidatingInputDialog(this, createNewBoxString, enterNameForNewBoxString, inputValidator,
                inputProcessor);
        dialog.show();
    }

    public void createNewBox(String boxName) {
        boxRepository.createBox(boxName, null, selectedBox.nativeLanguage, selectedBox.translationDirection, true);
        selectedBox = boxRepository.getSelectedBox();
        logInfo("created new box: " + boxName);
        updateBoxSpinner();
        clearState();
    }

    public void deleteSelectedBox() {
        String boxName = selectedBox.name;
        boxRepository.deleteSelectedBox();
        selectedBox = boxRepository.getSelectedBox();
        updateBoxSpinner();
        clearState();
        CharSequence message = Html.fromHtml(getString(R.string.deleted_box_s, boxName));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    void createTestData() {
        int boxId = selectedBox.id;

        if (wordRepository.countWords(boxId) > 0) {
            return;
        }

        wordRepository.createWord(boxId, "porcupine", "Stachelschwein");

        int ointmentId = wordRepository.createWord(boxId, "ointment", "Salbe");
        wordRepository.updateRepeatDate(ointmentId);

        int biasId = wordRepository.createWord(boxId, "bias", "Tendenz, Neigung");
        wordRepository.updateRepeatDate(biasId);

        wordRepository.createWord(boxId, 6, "emissary", "Abgesandter");
    }

    @Override
    public Loader<Language[]> onCreateLoader(int id, Bundle args) {
        return new LanguageLoader(this, languageRepository, languageProvider);
    }

    @Override
    public void onLoadFinished(Loader<Language[]> languageLoader, Language[] languages) {
        languageSettingsManager = new LanguageSettingsManager(this, boxRepository, languages);

        languageSettingsManager.configureForeignLanguageSpinner(foreignLanguageSpinner);
        languageSettingsManager.configureNativeLanguageSpinner(nativeLanguageSpinner);
        loadFinished = true;

        updateForeignLanguageSpinner(selectedBox.foreignLanguage);
        updateNativeLanguageSpinner(selectedBox.nativeLanguage);
    }

    @Override
    public void onLoaderReset(Loader<Language[]> languageLoader) {}

    @Override
    public void onWordEntered(Word word, String givenAnswer) {
        showShowWordFragment(givenAnswer, translationDirectionForQuery, word, wordsInCompartmentForQuery);
    }

    @Override
    public void showNextWord(boolean moreWordsAvailable) {
        if (moreWordsAvailable) {
            logInfo("showing next word, extras: " + getIntent().getExtras());
            showQueryWordFragment(selectedBox.id, compartmentForQuery, translationDirectionForQuery);
        } else {
            clearState();
        }
    }

    private void clearState() {
        initToolbar(R.string.vocabulary_box);
        clearTempCompartment();
        updateOverviewTable();
        hideKeyboard();
        if (twoPaneLayout) {
            removeFragments(MemorizeFragment.TAG, WordListFragment.TAG, QueryWordFragment.TAG, ShowWordFragment.TAG, AddWordFragment.TAG,
                    EditWordFragment.TAG);
        }
    }

    private void removeFragments(String... tags) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (String tag : tags) {
            Fragment fragment = getFragmentManager().findFragmentByTag(tag);
            if (fragment != null && fragment.isVisible()) {
                logInfo("removing fragment " + tag);
                fragmentTransaction.remove(fragment);
            }
        }
        fragmentTransaction.commit();
    }

    @Override
    public void editShownWord(int wordId) {
        showEditWordFragment(translationDirectionForQuery, wordId);
    }

    @Override
    public void updateOverview() {
        updateOverviewTable();
    }

    @Override
    public void languageSettingsConfirmed(String foreignLanguage, String nativeLanguage) {
        updateForeignLanguageSpinner(foreignLanguage);
        updateNativeLanguageSpinner(nativeLanguage);
    }

    @Override
    public void addWordDone() {
        if (isWordSharedFromOtherApp()) {
            finish();
        } else {
            // user added word via menu item
            clearState();
        }
    }

    @Override
    public void editWordDone(int wordId) {
        Word word = wordRepository.findWord(wordId);
        showShowWordFragment(null, translationDirectionForQuery, word, wordsInCompartmentForQuery);
    }

    @Override
    public void memorizeDone() {
        clearState();
    }
}
