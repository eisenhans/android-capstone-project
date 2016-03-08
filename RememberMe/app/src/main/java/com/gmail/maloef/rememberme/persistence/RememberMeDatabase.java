package com.gmail.maloef.rememberme.persistence;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RememberMeDatabase.VERSION)
public class RememberMeDatabase {

    static final int VERSION = 1;

    @Table(VocabularyBoxColumns.class)
    static final String VOCABULARY_BOX = "vocabularyBox";

    @Table(CompartmentColumns.class)
    static final String COMPARTMENT = "compartment";

    @Table(WordColumns.class)
    static final String WORD = "word";

    @Table(LanguageColumns.class)
    static final String LANGUAGE = "language";
}
