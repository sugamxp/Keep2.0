//Not Used

package com.example.sugam.keep20;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader extends android.support.v4.content.AsyncTaskLoader {

private String queryString;

    public ImageLoader(Context context, String queryString){
        super(context);
        this.queryString = queryString;
    }
    @Override
    public String loadInBackground() {
        String jsonString = null;
        try {
            URL url = new URL(queryString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            if (builder.length() == 0) {
                return null;
            }
            jsonString = builder.toString();


        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonString;
    }
}
