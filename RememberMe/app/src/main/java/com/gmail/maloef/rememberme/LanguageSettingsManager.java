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
import java.util.List;

public class LanguageSettingsManager {

    public interface LanguageSelectionListener {
        void selectionChanged(boolean isSelectionOk);
    }

    private Context context;
    private VocabularyBoxRepository boxService;

    private VocabularyBox selectedBox;

    private List<String> languageCodes;
    private List<String> languageNames;

    private LanguageSelectionListener languageSelectionListener;

    public LanguageSettingsManager(Context context, VocabularyBoxRepository boxService, Language[] languages) {
        this.context = context;
        this.boxService = boxService;
        this.selectedBox = boxService.getSelectedBox();

        languageCodes = new ArrayList<>(languages.length);
        languageNames = new ArrayList<>(languages.length);
        for (int i = 0; i < languages.length; i++) {
            languageCodes.add(languages[i].code);
            languageNames.add(languages[i].name);
        }
    }

    public void configureForeignLanguageSpinner(final Spinner spinner) {
        List<String> languagesPlusDetect = new ArrayList<>();

        String detectedLabel = context.getString(R.string.will_be_detected);
        languagesPlusDetect.add(detectedLabel);
        languagesPlusDetect.addAll(languageNames);

        spinner.setAdapter(createLanguageAdapter(languagesPlusDetect));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selectedItemPos = spinner.getSelectedItemPosition();
                String selectedIso;
                if (selectedItemPos == 0) {
                    selectedIso = null;
                } else {
                    selectedIso = languageCodes.get(selectedItemPos - 1);
                }
                if (selectedIso == null && selectedBox.foreignLanguage == null) {
                    return;
                }
                if (selectedIso != null && selectedIso.equals(selectedBox.foreignLanguage)) {
                    return;
                }
                selectedBox.foreignLanguage = selectedIso;
                boxService.updateForeignLanguage(selectedBox.id, selectedIso);
                informListeners();
                logInfo("updated foreign language for box " + selectedBox.name + ": " + selectedIso);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    public void updateForeignLanguageSpinner(Spinner foreignLanguageSpinner, String foreignLanguage) {
        int languagePos = (foreignLanguage == null ? 0 : languagePosition(foreignLanguage) + 1);
        foreignLanguageSpinner.setSelection(languagePos);
        boxService.updateForeignLanguage(selectedBox.id, foreignLanguage);
    }

    public void configureNativeLanguageSpinner(final Spinner spinner) {
        spinner.setAdapter(createLanguageAdapter(languageNames));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selectedItemPos = spinner.getSelectedItemPosition();
                String selectedIso = languageCodes.get(selectedItemPos);
                if (selectedIso.equals(selectedBox.nativeLanguage)) {
                    return;
                }
                selectedBox.nativeLanguage = selectedIso;
                boxService.updateNativeLanguage(selectedBox.id, selectedIso);
                informListeners();
                logInfo("updated native language for box " + selectedBox.name + ": " + selectedIso);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    public void updateNativeLanguageSpinner(Spinner nativeLanguageSpinner, String nativeLanguage) {
        int languagePos = languagePosition(nativeLanguage);
        nativeLanguageSpinner.setSelection(languagePos);
        boxService.updateNativeLanguage(selectedBox.id, nativeLanguage);
    }

    public void setLanguageSelectionListener(LanguageSelectionListener languageSelectionListener) {
        this.languageSelectionListener = languageSelectionListener;
    }

    private void informListeners() {
        if (languageSelectionListener == null) {
            return;
        }
        String foreignLanguage = selectedBox.foreignLanguage;
        if (foreignLanguage == null) {
            languageSelectionListener.selectionChanged(false);
            return;
        }
        String nativeLanguage = selectedBox.nativeLanguage;
        languageSelectionListener.selectionChanged(!nativeLanguage.equals(foreignLanguage));
    }

    private SpinnerAdapter createLanguageAdapter(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    private int languagePosition(String isoCode) {
        int pos = languageCodes.indexOf(isoCode);
        if (pos == -1) {
            throw new IllegalArgumentException("unknown iso code: " + isoCode);
        }
        return pos;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
