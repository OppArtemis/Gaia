package artemis.gaia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.lang.Thread;

import android.os.AsyncTask;
import java.io.BufferedInputStream;

import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.net.URL;

import android.text.method.*;

public class HelloWorld extends AppCompatActivity {
    String m_logHeader = "Android : ";
    EditText m_editTextSearch;
    TextView m_textViewReport;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        Log.d(m_logHeader, "The onCreate() event");

        // initialize the on-screen UI objects
        m_editTextSearch = (EditText) findViewById(R.id.txt_searchString);
        m_textViewReport = (TextView) findViewById(R.id.txt_view);
    }

    public void pollURL(View view) {
        // check edittextsearch for the contents and respond to user
        String searchString = m_editTextSearch.getText().toString();
        Toast.makeText(HelloWorld.this, "Searching for: " + searchString, Toast.LENGTH_SHORT).show();

        // clean the string before passing it to the database
        searchString = stringProcessing.sanitizeString(searchString);

        // send the string to readURL and obtain response
        readUrl obj = new readUrl();
        obj.setInput(searchString);
        obj.execute();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException  e) {
            throw new RuntimeException(e);
        }

        //String patternToMatch = ".*DOCTYPE.*";
        String patternToMatch = ".*Your search found\n.*[0-9]*.*\n.*Ingredients.*";
        Pattern p = Pattern.compile(patternToMatch,Pattern.CASE_INSENSITIVE);
        String output = obj.m_output;

        Matcher m = p.matcher(output);

        String foundIngredient = "Ingredient NOT found: " + searchString;
        if (m.find()) {
            foundIngredient = "Ingredient found:" + searchString;
        }

        Toast.makeText(HelloWorld.this, foundIngredient, Toast.LENGTH_SHORT).show();

        //connectInternet object = new connectInternet(stringUrl);

        m_textViewReport.setMovementMethod(ScrollingMovementMethod.getInstance());
        m_textViewReport.setText(foundIngredient + "\n URL Content:" + output);
        m_textViewReport.requestFocus();
//        findViewById(R.id.txt_verify).set;
    }
}

class stringProcessing {
    public static String sanitizeString(String inputString) {
        return inputString.trim();
    }
}

class readUrl extends AsyncTask<Void,Void,Void> {
    public String m_input = "";
    public String m_output = "lala";

    protected void onPreExecute() {
        //display progress dialog.

    }

    protected void setInput(String inputStr) {
        m_input = inputStr;
    }

    protected Void doInBackground(Void... params) {
        String stringUrl = "http://webprod.hc-sc.gc.ca/nhpid-bdipsn/ingredsReq.do?srchRchTxt=" + m_input + "&srchRchRole=-1&mthd=Search&lang=eng";
        //String stringUrl = "http://www.google.ca";

        URL url;
        try {
            url = new URL(stringUrl);
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



        //System.out.println(output);
        m_output = output;
        return null;
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



    protected void onPostExecute(Void result) {
        // dismiss progress dialog and update ui
    }
}

