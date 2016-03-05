package com.gmail.maloef.rememberme;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.service.VocabularyBoxService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageSettingsManager {

    private Context context;
    private VocabularyBoxService boxService;

    private String[] languageIsoCodes;
    private String[] languages;

    public LanguageSettingsManager(Context context, VocabularyBoxService boxService) {
        this.context = context;
        this.boxService = boxService;

        languageIsoCodes = context.getResources().getStringArray(R.array.languageIsoCodes);
        languages = context.getResources().getStringArray(R.array.languages);
    }

    public void configureForeignLanguageSpinner(final Spinner spinner) {
        configureForeignLanguageSpinner(spinner, null);
    }

    public void configureForeignLanguageSpinner(final Spinner spinner, String selectLanguage) {
        List<String> languagesPlusDetect = new ArrayList<String>();
        languagesPlusDetect.add("Will be detected");
        languagesPlusDetect.addAll(Arrays.asList(languages));

        spinner.setAdapter(createLanguageAdapter(languagesPlusDetect));

        int languagePos;
        if (selectLanguage == null) {
            if (getSelectedBox().foreignLanguage == null) {
                languagePos = 0;
            } else {
                languagePos = languagePosition(getSelectedBox().foreignLanguage) + 1;
            }
        } else {
            languagePos = languagePosition(selectLanguage) + 1;
            boxService.updateForeignLanguage(getSelectedBox()._id, selectLanguage);
        }
        logInfo("selected language " + selectLanguage + ", selection is position " + languagePos);
        spinner.setSelection(languagePos);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selectedItemPos = spinner.getSelectedItemPosition();
                String selectedIso;
                if (selectedItemPos == 0) {
                    selectedIso = null;
                } else {
                    selectedIso = languageIsoCodes[selectedItemPos - 1];
                }
                if (selectedIso == null) {
                    if (getSelectedBox().foreignLanguage == null) {
                        return;
                    }
                    getSelectedBox().foreignLanguage = null;
                    boxService.updateForeignLanguage(getSelectedBox()._id, null);
                    logInfo("removed foreign language setting from box " + getSelectedBox().name);
                    return;
                }
                if (!selectedIso.equals(getSelectedBox().foreignLanguage)) {
                    getSelectedBox().foreignLanguage = selectedIso;
                    boxService.updateForeignLanguage(getSelectedBox()._id, selectedIso);
                    logInfo("updated foreign language for box " + getSelectedBox().name + ": " + selectedIso);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void configureNativeLanguageSpinner(final Spinner spinner) {
        spinner.setAdapter(createLanguageAdapter(Arrays.asList(languages)));

        int languagePos = languagePosition(getSelectedBox().nativeLanguage);
        spinner.setSelection(languagePos);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selectedItemPos = spinner.getSelectedItemPosition();
                String selectedIso = languageIsoCodes[selectedItemPos];
                if (!selectedIso.equals(getSelectedBox().nativeLanguage)) {
                    getSelectedBox().nativeLanguage = selectedIso;
                    boxService.updateNativeLanguage(getSelectedBox()._id, selectedIso);
                    logInfo("updated native language for box " + getSelectedBox().name + ": " + selectedIso);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

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
        for (int i = 0; i < languageIsoCodes.length; i++) {
            if (languageIsoCodes[i].equals(isoCode)) {
                return i;
            }
        }
        throw new IllegalArgumentException("unknown iso code: " + isoCode);
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
