package com.gmail.maloef.rememberme.persistence;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface VocabularyBoxColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement
    String ID = "id";

    @DataType(TEXT) @Unique
    String NAME = "name";

    @DataType(TEXT)
    String FOREIGN_LANGUAGE = "foreignLanguage";

    @DataType(TEXT)
    String NATIVE_LANGUAGE = "nativeLanguage";

    @DataType(INTEGER)
    String TRANSLATION_DIRECTION = "translationDirection";

    @DataType(INTEGER)
    String IS_CURRENT = "isCurrent";
}
