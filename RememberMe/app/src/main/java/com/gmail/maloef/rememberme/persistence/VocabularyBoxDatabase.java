package com.gmail.maloef.rememberme.persistence;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = VocabularyBoxDatabase.VERSION)
public class VocabularyBoxDatabase {

    static final int VERSION = 1;

    @Table(VocabularyBoxColumns.class)
    static final String VOCABULARY_BOX = "vocabularyBox";

    @Table(CompartmentColumns.class)
    static final String COMPARTMENT = "compartment";

    @Table(WordColumns.class)
    static final String WORD = "word";
}
