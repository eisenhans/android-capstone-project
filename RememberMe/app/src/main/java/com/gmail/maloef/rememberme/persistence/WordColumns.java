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
    String ID = "id";

    @DataType(INTEGER) @NotNull @References(table = RememberMeDatabase.VOCABULARY_BOX, column = VocabularyBoxColumns.ID)
    String BOX_ID = "boxId";

    @DataType(INTEGER) @NotNull
    String COMPARTMENT = "compartment";

    @DataType(TEXT)
    String NATIVE_WORD = "nativeWord";

    @DataType(TEXT)
    String FOREIGN_WORD = "foreignWord";

    @DataType(INTEGER) @NotNull
    String CREATION_DATE = "creationDate";

    // May be null in the database, but the field Word.updateDate is a long, so it's always January 1st, 1970. Using a Long instead
    // lead to other problems. Maybe use @NotNull and a default value here?
    @DataType(INTEGER)
    String UPDATE_DATE = "updateDate";

    @DataType(INTEGER)
    String LAST_REPEAT_DATE = "lastRepeatDate";

}
