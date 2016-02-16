package com.gmail.maloef.rememberme.persistence;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface VocabularyBoxColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT) @Unique
    String NAME = "name";

    @DataType(TEXT)
    String NATIVE_LANGUAGE = "nativeLanguage";

    @DataType(TEXT)
    String FOREIGN_LANGUAGE = "foreignLanguage";

    @DataType(INTEGER)
    String TRANSLATION_DIRECTION = "translationDirection";
}
