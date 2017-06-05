package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jf2lin on 2017-06-04.
 */

public class GetDataFromSQL extends AsyncTask<String, String, String> {

    private Context mContext;

    public GetDataFromSQL(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            IngredientsDatabaseAndroid dbConn = new IngredientsDatabaseAndroid(mContext);
            dbConn.testLoadEntries();
            int ha = 1;

        } catch (Exception e) {
            int lala = 0;
            lala = 1;
        }

        return null;
    }

}