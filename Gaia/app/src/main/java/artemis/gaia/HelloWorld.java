package artemis.gaia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.lang.Thread;

import android.os.AsyncTask;
import java.io.BufferedInputStream;

import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import java.util.concurrent.TimeUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HelloWorld extends AppCompatActivity {
    String msg = "Android : ";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        Log.d(msg, "The onCreate() event");

        readUrl obj = new readUrl();
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

        String foundIngredient = "Ingredient NOT found";
        if (m.find()) {
            foundIngredient = "Found Ingredient";
        }

        Toast.makeText(HelloWorld.this, foundIngredient, Toast.LENGTH_SHORT).show();

        //connectInternet object = new connectInternet(stringUrl);
}

    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(msg, "The onStart() event");
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(msg, "The onResume() event");
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(msg, "The onPause() event");
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(msg, "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(msg, "The onDestroy() event");
    }
}

class readUrl extends AsyncTask<Void,Void,Void>
{
    public String m_output = "lala";

    protected void onPreExecute() {
        //display progress dialog.

    }
    protected Void doInBackground(Void... params) {
        String stringUrl = "http://webprod.hc-sc.gc.ca/nhpid-bdipsn/ingredsReq.do?srchRchTxt=salt&srchRchRole=-1&mthd=Search&lang=eng";
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

