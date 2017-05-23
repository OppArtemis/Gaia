package com.google.android.gms.samples.vision.ocrreader;

import android.provider.BaseColumns;

/**
 * Created by jf2lin on 2017-05-22.
 */

public final class IngredientsNameMapContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private IngredientsNameMapContract() {}

    /* Inner class that defines the table contents */
    public static class NameEntry implements BaseColumns {
        public static final String TABLE_NAME = "NAMEMAP";
        public static final String COLUMN_NAME_INGREDIENTSAFETY_PK = "INGREDIENTSAFETY_PK";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + NameEntry.TABLE_NAME + " (" +
                    NameEntry._ID                     + " INTEGER PRIMARY KEY," +
                    NameEntry.COLUMN_NAME_INGREDIENTSAFETY_PK         + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NameEntry.TABLE_NAME;
}
