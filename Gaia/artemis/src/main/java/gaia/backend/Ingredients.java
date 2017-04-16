package gaia.backend;

import java.util.*;

/**
 * Created by jf2lin on 2017-02-14.
 */

public class Ingredients {
    private boolean safeBoolean;
    private List<String> commonNames;
    private String safeDetails;
    private String URL;
    private String strRawFromUrl;
    private String strSanitized;
    private dataSourcesEnum dataSource;
    private healthConditionsEnum healthCondition;

    public enum dataSourcesEnum { // denotes the data sources to poll for health information
        All,
        HealthCanada,
        FoodAllergiesCanada
    }

    public enum healthConditionsEnum { // denotes the type of conditions the user are interested in seeing
        All
    }

    public boolean getSafe() {return safeBoolean;}
    public int getSafeInt() {if (safeBoolean == true) return 1; else return 0;}
    public List<String> getCommonNames() {return commonNames;};
    public String getDetails() {return safeDetails;};
    public String getURL() {return URL;};

    public void setSafe(boolean pSafe) {safeBoolean = pSafe;}
    public void setCommonNames(List<String> pCommonName) {commonNames = pCommonName;};
    public void setDetails(String pDetails) {safeDetails = pDetails;};
    public void setURL(String pURL) {URL = pURL;};

    public static void main(String[] args) {
        // generate a list of ingredients
        String ingredientSearchName = "";
        Ingredients[] ingredientsList = new Ingredients[1];

        // add "MSG" as a search term using the health canada website
        ingredientSearchName = "MSG";
        ingredientsList[0].commonNames = new ArrayList<>();
        ingredientsList[0].commonNames.add(ingredientSearchName);
        ingredientsList[0].dataSource = dataSourcesEnum.HealthCanada;
        ingredientsList[0].healthCondition = healthConditionsEnum.All;
        ingredientsList[0].fillIngredientStructure();

        for (int i = 0; i < ingredientsList.length; i++) {
            ingredientsList[i].analyze();
        }
    }

    public void analyze() {
        // todo check the database for information
        Boolean existInDatabase = analyzeDatabase();

        // if database has no information, pull the information from the source URL...
        if (existInDatabase == false) {
            Boolean existInURL = analyzeIngredientFromUrl();

            if (existInURL == true) {
                // todo insert into database

                // todo load from the database
            }
            else {
                // couldn't find anything. todo populate with blanks
            }
        }


        // process the
    }

    public Boolean analyzeDatabase() {
        // returns true if it exists in the database. return false if it does not

        // todo

        return false;
    }

    public void fillIngredientStructure() {
        // fill any missing variables before an URL poll can be performed
        if (URL == null) {
            switch (dataSource) {
                case HealthCanada:
                    URL = AnalyzeIngredientsHealthCanada.generateUrl(commonNames.get(0));
                    break;
            }
        }
    }

    public Boolean analyzeIngredientFromUrl() {
        // returns true if it exists in the database. return false if it does not

        // activate an URL request and pull website data
        readUrl urlLink = new readUrl();
        urlLink.setUrl(URL);
        urlLink.getUrl();
        String urlResponse = urlLink.m_output;

        // process the URL response

        // parse into the member variables of this class
        return false;
    }
}
