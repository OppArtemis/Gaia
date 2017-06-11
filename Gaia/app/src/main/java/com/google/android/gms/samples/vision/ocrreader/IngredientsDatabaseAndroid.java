package com.google.android.gms.samples.vision.ocrreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gaia.backend.HTTPGetter;
import gaia.backend.Ingredients;

/**
 * Created by jf2lin on 2017-05-21.
 */

public class IngredientsDatabaseAndroid extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gaia_foodsafety.db";

    public IngredientsDatabaseAndroid(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(IngredientSafetyContract.SQL_CREATE_ENTRIES);
//        db.execSQL(IngredientsNameMapContract.SQL_CREATE_ENTRIES);
//        System.out.println("Tables created successfully");
//
//        populateTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(IngredientSafetyContract.SQL_DELETE_ENTRIES);
//        db.execSQL(IngredientsNameMapContract.SQL_DELETE_ENTRIES);
//        System.out.println("Tables dropped successfully");
//
//        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int findLargestPkid()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                IngredientSafetyContract.IngredientEntry._ID
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                IngredientSafetyContract.IngredientEntry._ID + " DESC";

        Cursor cursor = db.query(
                IngredientSafetyContract.IngredientEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        int lastPkId = 0;
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            int itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(IngredientSafetyContract.IngredientEntry._ID));

            if (itemId > lastPkId)
                lastPkId = itemId;
        }
        cursor.close();

        return lastPkId;
    }

    public void insertIngredients(List<Ingredients> pIngredients)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String nowTime = "1970/01/01";

        for (int i = 0; i < pIngredients.size(); i++)
        {
            List<String> commonNames = pIngredients.get(i).getCommonNames();

            System.out.println("Adding " + commonNames.get(0) + " into " + IngredientSafetyContract.IngredientEntry.TABLE_NAME + "...");

            // Create a new map of values, where column names are the keys
            ContentValues valuesSafety = new ContentValues();
            valuesSafety.put(IngredientSafetyContract.IngredientEntry.COLUMN_NAME_URL, pIngredients.get(i).getURL());
            valuesSafety.put(IngredientSafetyContract.IngredientEntry.COLUMN_NAME_SAFE, pIngredients.get(i).getSafeInt());
            valuesSafety.put(IngredientSafetyContract.IngredientEntry.COLUMN_NAME_DETAILS, Ingredients.convertSafeDetailListEnumToString(pIngredients.get(i).getDetails()));
            valuesSafety.put(IngredientSafetyContract.IngredientEntry.COLUMN_NAME_LASTUPDATE, nowTime);

            // Insert the new row, returning the primary key value of the new row
            long newSafetyRowId = db.insert(IngredientSafetyContract.IngredientEntry.TABLE_NAME, null, valuesSafety);

            for (int j = 0; j < commonNames.size(); j++)
            {
                System.out.println("  Adding " + commonNames.get(j) + " into " + IngredientsNameMapContract.NameEntry.TABLE_NAME + ".");

                ContentValues valuesName = new ContentValues();
                valuesName.put(IngredientsNameMapContract.NameEntry._ID, commonNames.get(j));
                valuesName.put(IngredientsNameMapContract.NameEntry.COLUMN_NAME_INGREDIENTSAFETY_PK, newSafetyRowId);

                long newNameRowId = db.insert(IngredientsNameMapContract.NameEntry.TABLE_NAME, null, valuesName);
            }
        }

        System.out.println("Records created successfully");
    }

    public int selectPkIdFromIngredientsName(SQLiteDatabase db, String pIngredientName) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                IngredientsNameMapContract.NameEntry.COLUMN_NAME_INGREDIENTSAFETY_PK
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = IngredientsNameMapContract.NameEntry._ID + " = ?";
        String[] selectionArgs = { pIngredientName };

        Cursor cursor = db.query(
                IngredientsNameMapContract.NameEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        int pkId = -1;
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            pkId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(IngredientsNameMapContract.NameEntry.COLUMN_NAME_INGREDIENTSAFETY_PK));
        }

        return pkId;
    }

    public List<String> selectIngredientNamesFromPkId(SQLiteDatabase db, int pkId) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                IngredientsNameMapContract.NameEntry._ID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = IngredientsNameMapContract.NameEntry.COLUMN_NAME_INGREDIENTSAFETY_PK + " = ?";
        String[] selectionArgs = { Integer.toString(pkId) };

        Cursor cursor = db.query(
                IngredientsNameMapContract.NameEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        List<String> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(IngredientsNameMapContract.NameEntry._ID));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds;
    }

    public Ingredients selectIngredientSafetyFromPkId(SQLiteDatabase db, int pkId) {
        String[] projection = {
                IngredientSafetyContract.IngredientEntry.COLUMN_NAME_URL,
                IngredientSafetyContract.IngredientEntry.COLUMN_NAME_SAFE,
                IngredientSafetyContract.IngredientEntry.COLUMN_NAME_DETAILS,
                IngredientSafetyContract.IngredientEntry.COLUMN_NAME_LASTUPDATE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = IngredientSafetyContract.IngredientEntry._ID + " = ?";
        String[] selectionArgs = { Integer.toString(pkId) };

        Cursor cursor = db.query(
                IngredientSafetyContract.IngredientEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        Ingredients retrievedIngredients = new Ingredients();
        if (cursor.getCount() > 0) {
            cursor.moveToNext();

            int retrievedValue = cursor.getInt(
                    cursor.getColumnIndexOrThrow(IngredientSafetyContract.IngredientEntry.COLUMN_NAME_SAFE));
            if (retrievedValue == 0)
                retrievedIngredients.setSafe(false);
            else
                retrievedIngredients.setSafe(true);

            String retrievedString = cursor.getString(
                    cursor.getColumnIndexOrThrow(IngredientSafetyContract.IngredientEntry.COLUMN_NAME_DETAILS));
            List<Ingredients.healthConditionsEnum> tempArray = Ingredients.convertStringToSafeDetailList(retrievedString);
            retrievedIngredients.setDetails(tempArray);

            retrievedString = cursor.getString(
                    cursor.getColumnIndexOrThrow(IngredientSafetyContract.IngredientEntry.COLUMN_NAME_URL));
            retrievedIngredients.setURL(retrievedString);
        }

        return retrievedIngredients;
    }

    public Ingredients selectIngredients(String pIngredientName)
    {
        Ingredients retrievedIngredients = new Ingredients();
        List<String> retrievedString = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // does this ingredient exist in the name map table?
        int pkId = selectPkIdFromIngredientsName(db, pIngredientName);

        if (pkId > -1) { // if yes...
            // pull out all the common names from the name map table
            retrievedString = selectIngredientNamesFromPkId(db, pkId);

            // pull out the corresponding entry in tableNameIngredientSafety
            retrievedIngredients = selectIngredientSafetyFromPkId(db, pkId);
        }
        else {
            retrievedIngredients.setSafe(false); // default set the unfound entries to unsafe

            List<Ingredients.healthConditionsEnum> tempArray = new ArrayList<>();
            tempArray.add(Ingredients.healthConditionsEnum.EntryNotFound);
            retrievedIngredients.setDetails(tempArray);

            retrievedIngredients.setURL("");
        }

        retrievedIngredients.setCommonNames(retrievedString);
        retrievedIngredients.setInputName(pIngredientName);
        
        return retrievedIngredients;
    }


    public ArrayList<Ingredients> selectIngredients(List<String> pIngredientNames)
    {
        ArrayList<Ingredients> retrievedIngredients = new ArrayList<>(pIngredientNames.size());

        for (int i = 0; i < pIngredientNames.size(); i++)
        {
            Ingredients newIng = selectIngredients(pIngredientNames.get(i));
            retrievedIngredients.add(newIng);
        }

        return retrievedIngredients;
    }

    public void populateTables() {
        // loading data from websites
        System.out.println("Populating database...");
        List<Ingredients> addIngredients = new ArrayList<>();
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Egg", Ingredients.healthConditionsEnum.Allergy_Eggs));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Milk", Ingredients.healthConditionsEnum.Allergy_Milk));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Mustard", Ingredients.healthConditionsEnum.Allergy_Mustard));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Peanuts", Ingredients.healthConditionsEnum.Allergy_Peanuts));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Seafood", Ingredients.healthConditionsEnum.Allergy_Seafood));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Sesame", Ingredients.healthConditionsEnum.Allergy_Sesame));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Soy", Ingredients.healthConditionsEnum.Allergy_Soy));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Sulphites", Ingredients.healthConditionsEnum.Allergy_Sulphites));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Tree-nuts", Ingredients.healthConditionsEnum.Allergy_TreeNuts));
        addIngredients.add(HTTPGetter.pollFromFoodAllergiesCanada("Wheat", Ingredients.healthConditionsEnum.Allergy_Wheat));

        insertIngredients(addIngredients);
    }

    public String selectAndStringResponse(List<String> loadString) {
        System.out.println("Polling database...");

        String respString = "";

        try {
            ArrayList<Ingredients> loadIngredients = selectIngredients(loadString);

            for (int i = 0; i < loadIngredients.size(); i++) {
                respString = respString + ("" + loadIngredients.get(i).getInputName() + " is " +
                        loadIngredients.get(i).getSafeStr() + " (" + loadIngredients.get(i).getSafeDetailsString() + ")" + System.lineSeparator());
            }

            Log.d("Gaia", respString);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

        return respString;
    }
}
