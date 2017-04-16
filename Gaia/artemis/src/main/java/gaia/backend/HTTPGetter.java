package gaia.backend;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

// Required for parsing HTML
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Utilities
import java.util.*;

public class HTTPGetter {
    public static void main(String[] args) {
        System.out.println("HTTPGetting main() tester function.");

        // linear testing
        Ingredients notFoundIngredientObj = pollFromFoodAllergiesCanada("coke");
        Ingredients eggIngredientObj = pollFromFoodAllergiesCanada("egg");
        Ingredients milkIngredientObj = pollFromFoodAllergiesCanada("milk");
        //pollURL("Monosodium glutamate");
        //pollURL("lalala");

        // set up a list of URL

        // set up a list of ingredients to search for


    }

    //
    //
    // @param htmlBody : MANDATORY Elements @n
    //  Object containing HTML body.
    //
    // @return :
    //  List<String> : list of common names for ingredient.
    public static List<String> getCommonNamesFromBody(Elements htmlBody) {
        // This select has two parts:
        //
        // First part "*:contains(...)"
        // is based on a "search" for string "Other names for",
        // The * means that it can be any tag type (h, a, li, div)
        //
        // Second part "+ ul" is based on finding the next "sibling"
        // node of type "ul".
        //
        // Since HTML has a tree structure, then we wish to get the lowest node
        // hence getting the last node. And the children nodes will contain the
        // list of names.
        Elements childNodes = htmlBody.select("*:contains(Other names for) + ul").last().children();

        // Store information in list
        List<String> otherNames = new ArrayList<>();

        for (int i = 0; i < childNodes.size(); i++) {
            otherNames.add(childNodes.get(i).text());
        }

        return otherNames;
    }

    //
    //
    // @param htmlOutput : MANDATORY string @n
    //  HTML in string format.
    //
    // @return :
    //  Elements : URL body from HTML
    public static Elements getBodyFromHtml(String htmlOutput) {
        // Use Jsoup to get the body field of HTML
        Document doc = Jsoup.parse(htmlOutput);
        Elements body = doc.select("body");

        return body;
    }

    //
    //
    // @param ingredientName : MANDATORY string @n
    //  Ingredient.
    //
    // @return :
    //  List<String> : list of common names for ingredient.
    public static Ingredients pollFromFoodAllergiesCanada(String ingredientName) {
        // clean the string before passing it to the database
        ingredientName = ingredientName.trim();

        String stringUrl = "http://foodallergycanada.ca/about-allergies/food-allergens/";
        stringUrl += ingredientName;

        // Instantiate Ingredients object
        Ingredients ingredientObj = new Ingredients();
        ingredientObj.setURL(stringUrl);

        // Store in a list the ingredient common names
        List<String> commonNames = new ArrayList<>();
        commonNames.add(ingredientName);

        // send the string to readURL and obtain response
        readUrl readUrlObj = new readUrl();
        readUrlObj.setUrl(stringUrl);
        readUrlObj.getUrl();

        String htmlOutput = readUrlObj.m_output;

        // If the page is not found, then the ingredient has no known allergies concerns.
        if (htmlOutput.equals("Page not found")) {
            ingredientObj.setSafe(true);
        }
        else {
            Elements body = getBodyFromHtml(htmlOutput);
            commonNames.addAll(getCommonNamesFromBody(body));

            ingredientObj.setSafe(false);
        }

        ingredientObj.setCommonNames(commonNames);

        return ingredientObj;
    }

    //
    //
    public static void pollURL(String searchString) {
        // clean the string before passing it to the database
        searchString = ParseRawInfo.sanitizeString(searchString);
        String stringUrl = "http://webprod.hc-sc.gc.ca/nhpid-bdipsn/ingredsReq.do?srchRchTxt=" + searchString + "&srchRchRole=-1&mthd=Search&lang=eng";

        // send the string to readURL and obtain response
        readUrl readUrlObj = new readUrl();
        readUrlObj.setUrl(stringUrl);
        readUrlObj.getUrl();

        String patternToMatch = ".*Your search found\n.*[0-9]*.*\n.*Ingredients.*";
        Pattern p = Pattern.compile(patternToMatch, Pattern.CASE_INSENSITIVE);
        String output = readUrlObj.m_output;

        Matcher m = p.matcher(output);

        String foundIngredient = "Ingredient NOT found: " + searchString;
        if (m.find()) {
            foundIngredient = "Ingredient found: " + searchString;
        }

        System.out.println(foundIngredient);
    }
}