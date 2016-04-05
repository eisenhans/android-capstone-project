package com.gmail.maloef.rememberme;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.gmail.maloef.rememberme.domain.Language;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageSettingsManager {

    interface LanguageSelectionListener {
        void selectionChanged(boolean isSelectionOk);
    }

    private Context context;
    private VocabularyBoxRepository boxService;

    private String[] languageCodes;
    private String[] languageNames;

    private LanguageSelectionListener languageSelectionListener;

    public LanguageSettingsManager(Context context, VocabularyBoxRepository boxService, Language[] languages) {
        this.context = context;
        this.boxService = boxService;

        languageCodes = new String[languages.length];
        languageNames = new String[languages.length];
        for (int i = 0; i < languages.length; i++) {
            languageCodes[i] = languages[i].code;
            languageNames[i] = languages[i].name;
        }
    }

    public void configureForeignLanguageSpinner(final Spinner spinner) {
        List<String> languagesPlusDetect = new ArrayList<>();

        String detectedLabel = context.getString(R.string.will_be_detected);
        languagesPlusDetect.add(detectedLabel);
        languagesPlusDetect.addAll(Arrays.asList(languageNames));

        spinner.setAdapter(createLanguageAdapter(languagesPlusDetect));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selectedItemPos = spinner.getSelectedItemPosition();
                String selectedIso;
                if (selectedItemPos == 0) {
                    selectedIso = null;
                } else {
                    selectedIso = languageCodes[selectedItemPos - 1];
                }
                if (selectedIso == null && getSelectedBox().foreignLanguage == null) {
                    return;
                }
                if (selectedIso != null && selectedIso.equals(getSelectedBox().foreignLanguage)) {
                    return;
                }
                getSelectedBox().foreignLanguage = selectedIso;
                boxService.updateForeignLanguage(getSelectedBox().id, selectedIso);
                informListeners();
                logInfo("updated foreign language for box " + getSelectedBox().name + ": " + selectedIso);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    public void updateForeignLanguageSpinner(Spinner foreignLanguageSpinner, String foreignLanguage) {
        int languagePos = (foreignLanguage == null ? 0 : languagePosition(foreignLanguage) + 1);
        foreignLanguageSpinner.setSelection(languagePos);
        boxService.updateForeignLanguage(getSelectedBox().id, foreignLanguage);
    }

    public void configureNativeLanguageSpinner(final Spinner spinner) {
        spinner.setAdapter(createLanguageAdapter(Arrays.asList(languageNames)));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selectedItemPos = spinner.getSelectedItemPosition();
                String selectedIso = languageCodes[selectedItemPos];
                if (selectedIso.equals(getSelectedBox().nativeLanguage)) {
                    return;
                }
                getSelectedBox().nativeLanguage = selectedIso;
                boxService.updateNativeLanguage(getSelectedBox().id, selectedIso);
                informListeners();
                logInfo("updated native language for box " + getSelectedBox().name + ": " + selectedIso);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    public void updateNativeLanguageSpinner(Spinner nativeLanguageSpinner, String nativeLanguage) {
        int languagePos = languagePosition(nativeLanguage);
        nativeLanguageSpinner.setSelection(languagePos);
        boxService.updateNativeLanguage(getSelectedBox().id, nativeLanguage);
    }

    public void setLanguageSelectionListener(LanguageSelectionListener languageSelectionListener) {
        this.languageSelectionListener = languageSelectionListener;
    }

    private void informListeners() {
        if (languageSelectionListener == null) {
            return;
        }
        String foreignLanguage = getSelectedBox().foreignLanguage;
        if (foreignLanguage == null) {
            languageSelectionListener.selectionChanged(false);
            return;
        }
        String nativeLanguage = getSelectedBox().nativeLanguage;
        languageSelectionListener.selectionChanged(!nativeLanguage.equals(foreignLanguage));
    }

    // ToDo 07.03.16: cache this
    private VocabularyBox getSelectedBox() {
        return boxService.getSelectedBox();
    }

    private SpinnerAdapter createLanguageAdapter(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    // ToDo 05.03.2016: simplify this?
    private int languagePosition(String isoCode) {
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(isoCode)) {
                return i;
            }
        }
        throw new IllegalArgumentException("unknown iso code: " + isoCode);
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
