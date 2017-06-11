package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jf2lin on 2017-06-04.
 */

public class GetDataFromSQL extends AsyncTask<String, String, String> {

    private Context mContext;
    private List<String> loadString = new ArrayList<>();
    public String responseString = "noDataYet";

    public GetDataFromSQL(Context context) {
        mContext = context;

        // testing
        loadString = new ArrayList<>();
        loadString.add("Egg");
        loadString.add("Milk");
        loadString.add("MSG");
    }

    public GetDataFromSQL(Context context, List<String> newLoadString) {
        mContext = context;
        loadString = newLoadString;
    }

    @Override
    protected String doInBackground(String... f_url) {
        try {
            IngredientsDatabaseAndroid dbConn = new IngredientsDatabaseAndroid(mContext);
            responseString = dbConn.selectAndStringResponse(loadString);
        } catch (Exception e) {
            Log.d("GAIA", e.toString());
        }

        return null;
    }

}