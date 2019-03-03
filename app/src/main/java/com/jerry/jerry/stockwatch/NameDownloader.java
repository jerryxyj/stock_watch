package com.jerry.jerry.stockwatch;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jerry on 7/25/2017.
 */

public class NameDownloader extends AsyncTask<String,Integer,String> {
    private static final String TAG = "AsyncLoaderTask";
    private MainActivity mainActivity;


    private final String SymbolURL = "http://d.yimg.com/aq/autoc?region=US&lang=en-US&query=";


    public NameDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    protected void onPreExecute() {
        Toast.makeText(mainActivity, "Loading Data...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG,"Starting postExecuting: "+s);
        ArrayList<NameParser>nameparserList=parseJSON(s);
        Log.d(TAG,"Processing postExecuting: "+nameparserList.toString());
        mainActivity.updateData(nameparserList);



    }

    @Override
    protected String doInBackground(String... params) {
         String urlToUse = SymbolURL+params[0];
         Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        String ss;
        String toBeReplaced;
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');

            }
            ss=sb.toString();

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        return ss;
    }

    private ArrayList<NameParser> parseJSON(String s) {

        ArrayList<NameParser> nameparserList = new ArrayList<>();
        try {
            JSONObject jObj = new JSONObject(s);
            JSONObject subObj = jObj.getJSONObject("ResultSet");
            JSONArray jObjMain = subObj.getJSONArray("Result");


            for (int i = 0; i < jObjMain.length(); i++) {

                JSONObject jStock = (JSONObject) jObjMain.get(i);
                if(jStock.getString("type").equals("S")&&!jStock.getString("symbol").contains(".")) {

                    String symbol = jStock.getString("symbol");
                    String name = jStock.getString("name");
                    String type = jStock.getString("type");

                    nameparserList.add(
                            new NameParser(symbol, name, type));
                }

            }
            Log.d(TAG, "Dealing with Json: " + nameparserList.toString());
            return nameparserList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
