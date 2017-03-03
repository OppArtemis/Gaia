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
        Pattern p = Pattern.compile(patternToMatch,Pattern.CASE_INSENSITIVE);
        String output = readUrlObj.m_output;

        Matcher m = p.matcher(output);

        String foundIngredient = "Ingredient NOT found: " + searchString;
        if (m.find()) {
            foundIngredient = "Ingredient found: " + searchString;
        }

        System.out.println(foundIngredient);
    }
}

class readUrl {
    public String m_input = "";
    public String m_output = "lala";

    protected void setUrl(String inputStr) {
        m_input = inputStr;
    }

    protected void getUrl() {
        URL url;
        try {
            url = new URL(m_input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection urlConnection;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream in;
        String output;

        try {
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            output = readStream(in);
        } finally {
            urlConnection.disconnect();
        }

        m_output = output;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}