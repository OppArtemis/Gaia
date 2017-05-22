package gaia.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jf2lin on 2017-05-21.
 */

public class IngredientsDatabaseAndroid extends SQLiteOpenHelper {
    static String dbName = "gaia_foodsafety.db";
    static String tableNameIngredientSafety = "INGREDIENTSAFETY";
    static String tableNameNameMap = "NAMEMAP";

    public IngredientsDatabaseAndroid(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        createTables();
        populateTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        dropTables();
        onCreate(db);
    }

    public void dropTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlStr = "DROP TABLE IF EXISTS '" + tableNameIngredientSafety + "'";
        db.execSQL(sqlStr);

        sqlStr = "DROP TABLE IF EXISTS '" + tableNameNameMap + "'";
        db.execSQL(sqlStr);

        System.out.println("Tables dropped successfully");
    }

    public void createTables() {
        String sqlStr = "CREATE TABLE IF NOT EXISTS " + tableNameIngredientSafety +
                "(INGREDIENTSAFETY_PK       INT PRIMARY KEY     NOT NULL," +
                " URL                       TEXT                NOT NULL, " +
                " SAFE                      INT                 NOT NULL, " +
                " DETAILS                   TEXT, " +
                " LASTUPDATE                TEXT)";
        db.execSQL(sqlStr);

        sqlStr = "CREATE TABLE IF NOT EXISTS " + tableNameNameMap +
                "(NAMEMAP_PK                TEXT PRIMARY KEY   NOT NULL," +
                " INGREDIENTSAFETY_PK       INT                NOT NULL)";
        db.execSQL(sqlStr);

        System.out.println("Tables created successfully");
    }

    public void clearTables(SQLiteDatabase db)
    {
        String sqlStr = "DELETE FROM " + tableNameIngredientSafety;
        db.execSQL(sqlStr);

        sqlStr = "DELETE FROM " + tableNameNameMap;
        db.execSQL(sqlStr);
    }

    public int findLargestPkid()
    {
        int lastPkId = 0;
        try {
            String sqlStr = "SELECT * FROM " + tableNameIngredientSafety + ";";
            ResultSet rs = sqlExecuteQuery(sqlStr);
            while ( rs.next() ) {
                lastPkId = rs.getInt("INGREDIENTSAFETY_PK");
            }

            rs.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return lastPkId;
    }

    public void insertIngredients(List<Ingredients> pIngredients)
    {
        Statement stmt = null;
        String nowTime = "1970/01/01";

        for (int i = 0; i < pIngredients.size(); i++)
        {
            int newPkid = findLargestPkid() + 1;
            List<String> commonNames = pIngredients.get(i).getCommonNames();

            System.out.println("Adding " + commonNames.get(0) + " into " + tableNameIngredientSafety + "...");

            String sqlStr = "INSERT INTO " + tableNameIngredientSafety + " (INGREDIENTSAFETY_PK,URL,SAFE,DETAILS,LASTUPDATE) " +
                    "VALUES (" + newPkid + ", '" + pIngredients.get(i).getURL() + "', " +
                    pIngredients.get(i).getSafeInt() + ", '" + Ingredients.convertSafeDetailListEnumToString(pIngredients.get(i).getDetails()) + "', '" + nowTime + "');";
            sqlExecuteUpdate(sqlStr);

            for (int j = 0; j < commonNames.size(); j++)
            {
                System.out.println("  Adding " + commonNames.get(j) + " into " + tableNameNameMap + ".");

                sqlStr = "INSERT INTO " + tableNameNameMap + " (NAMEMAP_PK,INGREDIENTSAFETY_PK) " +
                        "VALUES ('" + commonNames.get(j) + "', " + newPkid + ");";

                try {
                    sqlExecuteUpdate(sqlStr);
                }
                catch ( Exception e ) {
                    System.out.println("  Error adding " + commonNames.get(j) + " into " + tableNameNameMap + ": " + e.toString());
                }
            }
        }

        System.out.println("Records created successfully");
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

        insertIngredients(db, addIngredients);
    }
}
