package com.gmail.maloef.rememberme.persistence;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = RememberMeProvider.AUTHORITY, database = RememberMeDatabase.class)
public class RememberMeProvider {

    public static final String AUTHORITY = "com.gmail.maloef.rememberme";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String VOCABULARY_BOXES = "vocabularyBoxes";
        String COMPARTMENTS = "compartments";
        String WORDS = "words";
        String LANGUAGES = "languages";
    }

    @TableEndpoint(table = RememberMeDatabase.VOCABULARY_BOX)
    public static class VocabularyBox {

        /**
         * Finds all vocabulary boxes.
         * <p/>
         * Example: ...rememberme/vocabularyBoxes
         */
        @ContentUri(
                path = Path.VOCABULARY_BOXES,
                type = "vnd.android.cursor.dir/vocabularyBox",
                defaultSort = VocabularyBoxColumns.NAME)
        public static final Uri VOCABULARY_BOXES = BASE_CONTENT_URI.buildUpon().appendPath(Path.VOCABULARY_BOXES).build();

        /**
         * Finds a vocabulary box by _id.
         * <p/>
         * Example: ...rememberme/vocabularyBoxes/123
         */
        @InexactContentUri(
                path = Path.VOCABULARY_BOXES + "/#",
                name = "VOCABULARY_BOX",
                type = "vnd.android.cursor.item/vocabularyBox",
                whereColumn = VocabularyBoxColumns._ID,
                pathSegment = 1)
        public static Uri findById(int id) {
            return VOCABULARY_BOXES.buildUpon().appendPath(String.valueOf(id)).build();
        }

//        /**
//         * Finds a vocabulary box by name.
//         * <p/>
//         * Example: ...rememberme/vocabularyBoxes/English
//         */
//        @InexactContentUri(
//                path = Path.VOCABULARY_BOXES + "/#",
//                name = "VOCABULARY_BOX_BY_NAME",
//                type = "vnd.android.cursor.item/vocabularyBox",
//                whereColumn = VocabularyBoxColumns.NAME,
//                pathSegment = 1)
//        public static Uri findByName(String name) {
//            return VOCABULARY_BOXES.buildUpon().appendPath(name).build();
//        }
    }

    @TableEndpoint(table = RememberMeDatabase.COMPARTMENT)
    public static class Compartment {

        /**
         * Finds all compartments (in all boxes).
         *
         * Example: ...rememberme/compartments
         */
        @ContentUri(
                path = Path.COMPARTMENTS,
                type = "vnd.android.cursor.dir/compartment",
                defaultSort = CompartmentColumns.NUMBER)
        public static final Uri COMPARTMENTS = BASE_CONTENT_URI.buildUpon().appendPath(Path.COMPARTMENTS).build();

        /**
         * Finds a compartment by id.
         * <p/>
         * Example: ...rememberme/compartments/123
         */
        @InexactContentUri(
                path = Path.COMPARTMENTS + "/#",
                name = "COMPARTMENT",
                type = "vnd.android.cursor.item/compartment",
                whereColumn = CompartmentColumns._ID,
                pathSegment = 1)
        public static Uri findById(int id) {
            return COMPARTMENTS.buildUpon().appendPath(String.valueOf(id)).build();
        }

        /**
         * Finds the compartments of one vocabulary box by the box' database _id.
         * <p/>
         * Example: ...rememberme/vocabularyBoxes/123/compartments
         */
        @InexactContentUri(
                path = Path.VOCABULARY_BOXES + "/#/" + Path.COMPARTMENTS,
                name = "COMPARTMENTS_BY_VOCABULARY_BOX",
                type = "vnd.android.cursor.dir/compartments",
                whereColumn = CompartmentColumns.VOCABULARY_BOX,
                pathSegment = 0,
                defaultSort = CompartmentColumns.NUMBER)
        public static final Uri findCompartments(int vocabularyBoxId) {
            return VocabularyBox.findById(vocabularyBoxId).buildUpon().appendPath(Path.COMPARTMENTS).build();
        }
    }

    @TableEndpoint(table = RememberMeDatabase.WORD)
    public static class Word {

        /**
         * Finds all words (in all compartments and all boxes).
         * <p/>
         * Example: ...rememberme/words
         */
        @ContentUri(
                path = Path.WORDS,
                type = "vnd.android.cursor.dir/word",
                defaultSort = WordColumns._ID)
        public static final Uri WORDS = BASE_CONTENT_URI.buildUpon().appendPath(Path.WORDS).build();

        /**
         * Finds a word by id.
         * <p/>
         * Example: ...rememberme/words/123
         */
        @InexactContentUri(
                path = Path.WORDS + "/#",
                name = "WORD",
                type = "vnd.android.cursor.item/word",
                whereColumn = WordColumns._ID,
                pathSegment = 1)
        public static Uri findById(int id) {
            return WORDS.buildUpon().appendPath(String.valueOf(id)).build();
        }

        /**
         * Finds all words in one compartment by the compartment's database id.
         * <p/>
         * Example: ...rememberme/compartments/123/words
         */
        @InexactContentUri(
                path = Path.COMPARTMENTS + "/#/" + Path.WORDS,
                name = "WORDS_BY_COMPARTMENT",
                type = "vnd.android.cursor.dir/words",
                whereColumn = WordColumns.COMPARTMENT,
                pathSegment = 1,
                defaultSort = WordColumns._ID)
        public static final Uri findWords(int compartmentId) {
            return Compartment.findById(compartmentId).buildUpon().appendPath(Path.WORDS).build();
        }
    }

    @TableEndpoint(table = RememberMeDatabase.LANGUAGE)
    public static class Language {

        /**
         * Finds all languages.
         * <p/>
         * Example: ...rememberme/languages
         */
        @ContentUri(
                path = Path.LANGUAGES,
                type = "vnd.android.cursor.dir/language",
                defaultSort = LanguageColumns.NAME)
        public static final Uri LANGUAGES = BASE_CONTENT_URI.buildUpon().appendPath(Path.LANGUAGES).build();

        /**
         * Finds all languages with language names defined by the last path segment.
         * <p/>
         * Example: ...rememberme/languages/en
         */
        @InexactContentUri(
                path = Path.LANGUAGES + "/#",
                name = "LANGUAGES_BY_NAME_CODE",
                type = "vnd.android.cursor.dir/language",
                whereColumn = LanguageColumns.NAME_CODE,
                pathSegment = 1)
        public static Uri findByNameCode(String nameCode) {
            return LANGUAGES.buildUpon().appendPath(nameCode).build();
        }
    }
}
