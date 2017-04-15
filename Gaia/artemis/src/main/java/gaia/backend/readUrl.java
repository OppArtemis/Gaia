package gaia.backend;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jf2lin on 2017-03-12.
 */

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