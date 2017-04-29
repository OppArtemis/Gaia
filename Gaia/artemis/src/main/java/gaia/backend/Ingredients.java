package gaia.backend;

import java.util.*;

/**
 * Created by jf2lin on 2017-02-14.
 */

public class Ingredients{
    static String dbEntryDelimiter = "!";

    private boolean safeBoolean;
    private List<String> commonNames;
    private List<healthConditionsEnum> safeDetails;
    private String URL;
    private dataSourcesEnum dataSource;
    private String inputName;
    private String inputComments;

    public enum dataSourcesEnum { // denotes the data sources to poll for health information
        All,
        HealthCanada,
        FoodAllergiesCanada
    }

    public enum healthConditionsEnum { // denotes the type of conditions the user are interested in seeing
        None, EntryNotFound, Bad,
        Allergy_Eggs, Allergy_Milk, Allergy_Mustard, Allergy_Peanuts, Allergy_Seafood, Allergy_Sesame,
        Allergy_Soy, Allergy_Sulphites, Allergy_TreeNuts, Allergy_Wheat
    }

    public boolean getSafe() {return safeBoolean;}
    public int getSafeInt() {if (safeBoolean == true) return 1; else return 0;}
    public String getSafeStr() {if (safeBoolean == true) return "SAFE"; else return "NOT SAFE";}
    public List<String> getCommonNames() {return commonNames;}
    public List<healthConditionsEnum> getDetails() {return safeDetails;}
    public String getURL() {return URL;}
    public String getInputName() {return inputName;}
    public String getInputComments() {return inputComments;}

    public void setSafe(boolean pSafe) {safeBoolean = pSafe;}
    public void setCommonNames(List<String> pCommonName) {commonNames = pCommonName;};
    public void setDetails(List<healthConditionsEnum> pDetails) {safeDetails = pDetails;};
    public void setURL(String pURL) {URL = pURL;};
    public void setInputName(String pInputName) {inputName = pInputName;}
    public void setInputComments(String pInputComments) {inputComments = pInputComments;}

    public static void main(String[] args) {
        // generate a list of ingredients
        String ingredientSearchName = "";
        Ingredients[] ingredientsList = new Ingredients[1];
        List<healthConditionsEnum> newSafeDetails = new ArrayList<>();
        newSafeDetails.add(healthConditionsEnum.Bad);

        // add "MSG" as a search term using the health canada website
        ingredientSearchName = "MSG";
        ingredientsList[0].commonNames = new ArrayList<>();
        ingredientsList[0].commonNames.add(ingredientSearchName);
        ingredientsList[0].dataSource = dataSourcesEnum.HealthCanada;
        ingredientsList[0].safeDetails = newSafeDetails;
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

    public static String convertSafeDetailListEnumToString(List<Ingredients.healthConditionsEnum> safeDetailsList) {
        String safeDetailsString = safeDetailsList.get(0).name();

        for (int i = 1; i < safeDetailsList.size(); i++) {
            safeDetailsString = safeDetailsString + dbEntryDelimiter + safeDetailsList.get(i).name();
        }

        return safeDetailsString;
    }

    public static List<Ingredients.healthConditionsEnum> convertStringToSafeDetailList(String safeDetailsString) {
        List<Ingredients.healthConditionsEnum> safeDetailsList = new ArrayList<>();
        List<String> stringList = Arrays.asList(safeDetailsString.split(dbEntryDelimiter));

        for (int i = 0; i < stringList.size(); i++) {
            safeDetailsList.add(Ingredients.healthConditionsEnum.valueOf(stringList.get(i)));
        }

        return safeDetailsList;
    }

    public String getSafeDetailsString() {
        return convertSafeDetailListEnumToString(safeDetails);
    }
}
