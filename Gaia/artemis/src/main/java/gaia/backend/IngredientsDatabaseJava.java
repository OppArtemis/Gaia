package gaia.backend;

/**
 * Created by jf2lin on 2017-04-15.
 */

import java.sql.*;
import java.util.*;
import java.lang.*;

public class IngredientsDatabaseJava {
    static String dbProtocol = "org.sqlite.JDBC";
    String dbName = "jdbc:sqlite:gaia_foodsafety.db";
    static String tableNameIngredientSafety = "INGREDIENTSAFETY";
    static String tableNameNameMap = "NAMEMAP";
    Connection c = null;

    public static void main( String[] args )
    {
        System.out.println("IngredientsDatabaseJava main() tester function.");

        IngredientsDatabaseJava testDb = new IngredientsDatabaseJava();
        testDb.resetDatabaseAndUpdate();
    }

    public void updateDbName(String newDbName) {
        dbName = newDbName;
    }

    public IngredientsDatabaseJava()
    {
       // openDbConnection();
    }

    public void openDbConnection()
    {
        // create a connection to the target db
        try {
            Class.forName(dbProtocol);
            c = DriverManager.getConnection(dbName);
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void closeDbConnection()
    {
        try {
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void refreshDb()
    {
        // drop all existing tables before the refresh
        createTables();
        clearTables();
    }

    public void createTables()
    {
        String sqlStr = "DROP TABLE IF EXISTS '" + tableNameIngredientSafety + "'";
        sqlExecuteUpdate(sqlStr);

        sqlStr = "DROP TABLE IF EXISTS '" + tableNameNameMap + "'";
        sqlExecuteUpdate(sqlStr);

        sqlStr = "CREATE TABLE IF NOT EXISTS " + tableNameIngredientSafety +
                "(_ID                       INT PRIMARY KEY     ," +
                " URL                       TEXT                , " +
                " SAFE                      INT                 , " +
                " DETAILS                   TEXT, " +
                " LASTUPDATE                TEXT)";
        sqlExecuteUpdate(sqlStr);

        sqlStr = "CREATE TABLE IF NOT EXISTS " + tableNameNameMap +
                "(_ID                       TEXT PRIMARY KEY   ," +
                " INGREDIENTSAFETY_PK       INT                )";
        sqlExecuteUpdate(sqlStr);

        System.out.println("Tables created successfully");
    }

    public void clearTables()
    {
        String sqlStr = "DELETE FROM " + tableNameIngredientSafety;
        sqlExecuteUpdate(sqlStr);

        sqlStr = "DELETE FROM " + tableNameNameMap;
        sqlExecuteUpdate(sqlStr);
    }

    public int findLargestPkid()
    {
        int lastPkId = 0;
        try {
            String sqlStr = "SELECT * FROM " + tableNameIngredientSafety + ";";
            ResultSet rs = sqlExecuteQuery(sqlStr);
            while ( rs.next() ) {
                lastPkId = rs.getInt("_ID");
            }

//            String sqlStr = "SELECT * FROM " + tableNameIngredientSafety + ";";
//            Statement stmt = c.prepareStatement(sqlStr, ResultSet.TYPE_SCROLL_SENSITIVE);
//            ResultSet rs = sqlExecuteQueryPrepared(stmt);
//            rs.last();
//            lastPkId = rs.getInt("ID");

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

            String sqlStr = "INSERT INTO " + tableNameIngredientSafety + " (_ID,URL,SAFE,DETAILS,LASTUPDATE) " +
                    "VALUES (" + newPkid + ", '" + pIngredients.get(i).getURL() + "', " +
                    pIngredients.get(i).getSafeInt() + ", '" + Ingredients.convertSafeDetailListEnumToString(pIngredients.get(i).getDetails()) + "', '" + nowTime + "');";
            sqlExecuteUpdate(sqlStr);

            for (int j = 0; j < commonNames.size(); j++)
            {
                System.out.println("  Adding " + commonNames.get(j) + " into " + tableNameNameMap + ".");

                sqlStr = "INSERT INTO " + tableNameNameMap + " (_ID,INGREDIENTSAFETY_PK) " +
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

    public List<Ingredients> selectIngredients(List<String> pIngredientNames)
    {
        List<Ingredients> retrievedIngredients = new ArrayList<>(pIngredientNames.size());

        for (int i = 0; i < pIngredientNames.size(); i++)
        {
            retrievedIngredients.add(selectIngredients(pIngredientNames.get(i)));
        }

        return retrievedIngredients;
    }

    public Ingredients selectIngredients(String pIngredientName)
    {
        Ingredients retrievedIngredients = null;
        try {
            // unsanitized form
//            String sqlStr = "SELECT * FROM " + tableNameNameMap + " WHERE NAMEMAP_PK='" + pIngredientName + "';";
//            ResultSet rs = sqlExecuteQuery(sqlStr);

            // sanitized form
            String sqlStr = "SELECT * FROM " + tableNameNameMap + " WHERE _ID = ?;";
            PreparedStatement preparedStatement = c.prepareStatement(sqlStr);
            preparedStatement.setString(1, pIngredientName);
            ResultSet rs = preparedStatement.executeQuery();

            retrievedIngredients = new Ingredients();
            List<String> retrievedString = new ArrayList<>();

            retrievedIngredients.setInputName(pIngredientName);

            if (!rs.isBeforeFirst() ) {
//                retrievedIngredients.setInputComments("No entry found.");
                retrievedIngredients.setSafe(false); // default set the unfound entries to unsafe

                List<Ingredients.healthConditionsEnum> tempArray = new ArrayList<>();
                tempArray.add(Ingredients.healthConditionsEnum.EntryNotFound);
                retrievedIngredients.setDetails(tempArray);

                retrievedIngredients.setURL("");
            }
            else {
//                retrievedIngredients.setInputComments("Entry found.");
                rs.next();
                int pkId = rs.getInt("INGREDIENTSAFETY_PK");

                // pull out all the common names
                String sqlStr2 = "SELECT * FROM " + tableNameNameMap + " WHERE INGREDIENTSAFETY_PK ='" + pkId + "';";
                ResultSet rs2 = sqlExecuteQuery(sqlStr2);

                while (rs2.next()) {
                    retrievedString.add(rs2.getString("_ID"));
                }

                // pull out the corresponding entry in tableNameIngredientSafety
                String sqlStr3 = "SELECT * FROM " + tableNameIngredientSafety + " WHERE _id ='" + pkId + "';";
                ResultSet rs3 = sqlExecuteQuery(sqlStr3);

                if (rs3.getInt("SAFE") == 0)
                    retrievedIngredients.setSafe(false);
                else
                    retrievedIngredients.setSafe(true);

                List<Ingredients.healthConditionsEnum> tempArray = Ingredients.convertStringToSafeDetailList(rs3.getString("DETAILS"));
                retrievedIngredients.setDetails(tempArray);

//                retrievedIngredients.setDetails(rs3.getString("DETAILS"));
                retrievedIngredients.setURL(rs3.getString("URL"));
            }

            retrievedIngredients.setCommonNames(retrievedString);
            rs.close();


        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

        return retrievedIngredients;
    }


    public void sqlExecuteUpdate(String sqlStr)
    {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(sqlStr);
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
    
    public ResultSet sqlExecuteQuery(String sqlStr)
    {
        Statement stmt = null;
        ResultSet rs = null; 
        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery(sqlStr);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        
        return rs; 
    }

    public ResultSet sqlExecuteQueryPrepared(Statement stmt)
    {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return rs;
    }

    public void resetDatabaseAndUpdate()
    {
        // initalize the db
        openDbConnection();
        refreshDb();
        closeDbConnection();

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

        // insert into the db
        openDbConnection();
        insertIngredients(addIngredients);
        closeDbConnection();

        // poll database for data
        System.out.println("Polling database...");
        List<String> loadString = new ArrayList<>();
        loadString.add("Egg");
        loadString.add("Milk");
        loadString.add("MSG");

        openDbConnection();
        List<Ingredients> loadIngredients = selectIngredients(loadString);
        closeDbConnection();

        for (int i = 0; i < loadIngredients.size(); i++) {
            System.out.println("" + loadIngredients.get(i).getInputName() + " is " +
                    loadIngredients.get(i).getSafeStr() + " (" + loadIngredients.get(i).getSafeDetailsString() + ")");
        }
    }
}
