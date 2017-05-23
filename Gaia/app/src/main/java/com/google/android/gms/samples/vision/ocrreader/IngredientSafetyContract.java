package com.google.android.gms.samples.vision.ocrreader;

import android.provider.BaseColumns;

/**
 * Created by jf2lin on 2017-05-22.
 */

public final class IngredientSafetyContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private IngredientSafetyContract() {}

    /* Inner class that defines the table contents */
    public static class IngredientEntry implements BaseColumns {
        public static final String TABLE_NAME = "INGREDIENTSAFETY";
        public static final String COLUMN_NAME_URL = "URL";
        public static final String COLUMN_NAME_SAFE = "SAFE";
        public static final String COLUMN_NAME_DETAILS = "DETAILS";
        public static final String COLUMN_NAME_LASTUPDATE = "LASTUPDATE";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + IngredientEntry.TABLE_NAME + " (" +
                    IngredientEntry._ID                     + " INTEGER PRIMARY KEY," +
                    IngredientEntry.COLUMN_NAME_URL         + " TEXT," +
                    IngredientEntry.COLUMN_NAME_SAFE        + " INT," +
                    IngredientEntry.COLUMN_NAME_DETAILS     + " TEXT," +
                    IngredientEntry.COLUMN_NAME_LASTUPDATE  + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME;
}
