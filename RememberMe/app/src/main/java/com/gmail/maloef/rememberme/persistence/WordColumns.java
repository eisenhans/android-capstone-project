package com.gmail.maloef.rememberme.persistence;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface WordColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT)
    String NATIVE_LANGUAGE = "nativeLanguage";

    @DataType(TEXT)
    String FOREIGN_LANGUAGE = "foreignLanguage";

    @DataType(INTEGER) @References(table = VocabularyBoxDatabase.COMPARTMENT, column = CompartmentColumns._ID)
    String COMPARTMENT = "compartment";

    @DataType(INTEGER) @NotNull
    String CREATION_DATE = "creationDate";

    @DataType(INTEGER)
    String UPDATE_DATE = "updateDate";

    @DataType(INTEGER)
    String LAST_REPEAT_DATE = "lastRepeatDate";

}
