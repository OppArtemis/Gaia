package gaia.backend;

/**
 * Created by jf2lin on 2017-04-15.
 */

import java.sql.*;
import java.util.*;
import java.lang.*;

public class IngredientsDatabase {
    static String dbProtocol = "org.sqlite.JDBC";
    static String dbName = "jdbc:sqlite:test.db";
    static String tableNameIngredientSafety = "INGREDIENTSAFETY";
    static String tableNameNameMap = "NAMEMAP";
    Connection c = null;

    public static void main( String args[] )
    {
        System.out.println("IngredientsDatabase main() tester function.");

        IngredientsDatabase testDb = new IngredientsDatabase();

        // initalize the db
        testDb.openDbConnection();
        testDb.refreshDb();
        testDb.closeDbConnection();

//        // populate with some data
//        testDb.openDbConnection();
//        testDb.insertTesting(1);
//        testDb.insertTesting(2);
//        testDb.insertTesting(3);
//        int largestPkId = testDb.findLargestPkid();
//        testDb.insertTesting(largestPkId+1);
//        testDb.closeDbConnection();

        // populate with actual data
        

        testDb.openDbConnection();
        testDb.insertIngredients();
        testDb.closeDbConnection();

        // loading actual data
        List<java.lang.String> loadString = new ArrayList<>();
        loadString.add("egg");
        loadString.add("milk");
        loadString.add("MSG");

        testDb.openDbConnection();
        testDb.selectIngredients();
        testDb.closeDbConnection();
    }

    public IngredientsDatabase()
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
        String sqlStr = "CREATE TABLE IF NOT EXISTS " + tableNameIngredientSafety +
                "(INGREDIENTSAFETY_PK       INT PRIMARY KEY     NOT NULL," +
                " URL                       TEXT                NOT NULL, " +
                " SAFE                      INT                 NOT NULL, " +
                " DETAILS                   TEXT, " +
                " LASTUPDATE                TEXT)";
        sqlExecuteUpdate(sqlStr);

        sqlStr = "CREATE TABLE IF NOT EXISTS " + tableNameNameMap +
                "(NAMEMAP_PK                TEXT PRIMARY KEY   NOT NULL," +
                " INGREDIENTSAFETY_PK       INT                NOT NULL)";
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
                lastPkId = rs.getInt("ID");
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

            String sqlStr = "INSERT INTO " + tableNameIngredientSafety + " (INGREDIENTSAFETY_PK,URL,SAFE,DETAILS,LASTUPDATE) " +
                    "VALUES (" + newPkid + ", '" + pIngredients.get(i).getURL() + "', " +
                    pIngredients.get(i).getSafe() + ", '" + pIngredients.get(i).getDetails() + "', '" + nowTime + "');";
            sqlExecuteUpdate(sqlStr);

            List<String> commonNames = pIngredients.get(i).getCommonNames();
            for (int j = 0; j < commonNames.size(); j++)
            {
                sqlStr = "INSERT INTO " + tableNameNameMap + " (NAMEMAP_PK,INGREDIENTSAFETY_PK) " +
                        "VALUES ('" + commonNames.get(j) + "', " + newPkid + ");";
                sqlExecuteUpdate(sqlStr);
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
            String sqlStr = "SELECT * FROM " + tableNameNameMap + " WHERE NAMEMAP_PK='" + pIngredientName + "';";
            ResultSet rs = sqlExecuteQuery(sqlStr);
            if (!rs.isLast())
            {
                int pkId = rs.getInt("ID");
                retrievedIngredients = new Ingredients();

                // pull out all the common names
                List<String> retrievedString = new ArrayList<>();
                String sqlStr2 = "SELECT * FROM " + tableNameNameMap + " WHERE INGREDIENTSAFETY_PK='" + pkId + "';";
                ResultSet rs2 = sqlExecuteQuery(sqlStr);

                while ( rs2.next() ) {
                    retrievedString.add(rs.getString("NAMEMAP_PK"));
                }
                retrievedIngredients.setCommonNames(retrievedString);

                // pull out the corresponding entry in tableNameIngredientSafety
                sqlStr2 = "SELECT * FROM " + tableNameIngredientSafety + " WHERE INGREDIENTSAFETY_PK='" + pkId + "';";
                rs2 = sqlExecuteQuery(sqlStr);

                if (rs2.getInt("SAFE") == 0)
                    retrievedIngredients.setSafe(false);
                else
                    retrievedIngredients.setSafe(true);

                retrievedIngredients.setDetails(rs2.getString("DETAILS"));
                retrievedIngredients.setURL(rs2.getString("URL"));
            }

            rs.close();


        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return retrievedIngredients;
    }

    public void insertTesting(int pkId)
    {
        String sqlStr = "INSERT INTO " + tableNameIngredientSafety + " (INGREDIENTSAFETY_PK,URL,SAFE,DETAILS,LASTUPDATE) " +
                "VALUES (" + pkId + ", 'http://fda.ca/eggs/', 0, 'Egg', 'Jan 2, 1970');";
        sqlExecuteUpdate(sqlStr);

        sqlStr = "INSERT INTO " + tableNameNameMap + " (NAMEMAP_PK,INGREDIENTSAFETY_PK) " +
                "VALUES ('Egg" + pkId + "', " + pkId + ");";
        sqlExecuteUpdate(sqlStr);

        sqlStr = "INSERT INTO " + tableNameNameMap + " (NAMEMAP_PK,INGREDIENTSAFETY_PK) " +
                "VALUES ('Albumin" + pkId + "', " + pkId + ");";
        sqlExecuteUpdate(sqlStr);

        System.out.println("Records created successfully");
    }

//    public void sqlExecuteUpdatePrepared(Statement stmt)
//    {
//        try {
//            stmt = c.createStatement();
//            stmt.executeUpdate(sqlStr);
//            stmt.close();
//        } catch ( Exception e ) {
//            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//            System.exit(0);
//        }
//    }

    public void sqlExecuteUpdate(String sqlStr)
    {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(sqlStr);
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
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
}
