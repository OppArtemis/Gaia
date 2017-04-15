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

public class HTTPGetter {
    public static void main(String[] args) {
        System.out.println("HTTPGetting main() tester function.");

        // linear testing
        pollURL("MSG");
        pollURL("Monosodium glutamate");
        pollURL("lalala");

        // set up a list of URL

        // set up a list of ingredients to search for


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