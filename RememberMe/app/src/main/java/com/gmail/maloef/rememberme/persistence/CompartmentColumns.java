package com.gmail.maloef.rememberme.persistence;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;

public interface CompartmentColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement
    String ID = "id";

    @DataType(INTEGER) @NotNull @References(table = RememberMeDatabase.VOCABULARY_BOX, column = VocabularyBoxColumns.ID)
    String VOCABULARY_BOX = "vocabularyBox";

    @DataType(INTEGER)
    String NUMBER = "number";
}
