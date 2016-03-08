package com.gmail.maloef.rememberme.persistence;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface LanguageColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement
    String ID = "id";

    @DataType(INTEGER) @NotNull
    String CODE = "code";

    @DataType(TEXT) @NotNull
    String NAME = "name";

    @DataType(TEXT) @NotNull
    String NAME_CODE = "nameCode";
}
