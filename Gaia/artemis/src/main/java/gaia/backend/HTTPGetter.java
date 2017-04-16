package gaia.backend;

// imports for realUrl
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class HTTPGetter {
    public static void main(String[] args) {
        System.out.println("HTTPGetting main() tester function.");

        // linear testing
        //Ingredients badIngredientObj = pollFromFoodAllergiesCanada("coke");
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
    // @param stringUrl : MANDATORY string @n
    //  URL to get information from.
    //
    // @return :
    //  Elements : URL body from HTML
    public static Elements getBodyFromUrl(String stringUrl) {
        // send the string to readURL and obtain response
        readUrl readUrlObj = new readUrl();
        readUrlObj.setUrl(stringUrl);
        readUrlObj.getUrl();

        String htmlOutput = readUrlObj.m_output;

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

        Elements body = getBodyFromUrl(stringUrl);
        List<String> commonNames = new ArrayList<>();
        commonNames.add(ingredientName);
        commonNames.addAll(getCommonNamesFromBody(body));

        Ingredients ingredientObj = new Ingredients();
        ingredientObj.setCommonNames(commonNames);
        ingredientObj.setURL(stringUrl);
        ingredientObj.setSafe(false);

        System.out.println(body);

        return ingredientObj;
    }

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