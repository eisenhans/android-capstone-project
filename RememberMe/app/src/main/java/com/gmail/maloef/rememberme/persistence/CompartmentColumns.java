package com.gmail.maloef.rememberme.persistence;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;

public interface CompartmentColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement
    String _ID = "_id";

    @DataType(INTEGER) @References(table = VocabularyBoxDatabase.VOCABULARY_BOX, column = VocabularyBoxColumns._ID)
    String VOCABULARY_BOX = "vocabularyBox";

    @DataType(INTEGER)
    String NUMBER = "number";
}
