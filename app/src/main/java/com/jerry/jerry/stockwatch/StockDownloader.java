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

public class StockDownloader extends AsyncTask<String,Integer,String> {
    private static final String TAG = "AsyncLoaderTask";
    private MainActivity mainActivity;


    private final String SymbolURL = "http://finance.google.com/finance/info?client=ig&q=";


    public StockDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    protected void onPreExecute() {
        Toast.makeText(mainActivity, "Loading Data...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG,"Starting StockDownloader postExecuting: "+s);
        ArrayList<StockParser> stockParsers=parseJSON(s);
        Log.d(TAG,"Processig StockDownloader postExecuting: "+stockParsers.toString());
        mainActivity.updateStock(stockParsers);



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
            toBeReplaced = ss.substring(3, ss.length()-1);
            Log.d(TAG, "just for test what it it " + toBeReplaced);




//            Log.d(TAG, "doInStockDownloaderBackground: " + toBeReplaced);

        } catch (Exception e) {
            Log.e(TAG, "doInStockDownloaderBackground: ", e);
            return null;
        }
        Log.d(TAG, "doInStockDownloaderBackground: " + toBeReplaced);

        return toBeReplaced;
    }

    private ArrayList<StockParser> parseJSON(String s) {

        ArrayList<StockParser> stockparserList = new ArrayList<>();
        try {
            JSONArray jObjMain = new JSONArray(s);


            for (int i = 0; i < jObjMain.length(); i++) {

                JSONObject jStock = (JSONObject) jObjMain.get(i);


                    String symbol = jStock.getString("t");
                    Double price = jStock.getDouble("l_fix");
                    Double change=jStock.getDouble("c");
                    Double changePct=jStock.getDouble("cp");


                    stockparserList.add(
                            new StockParser(symbol, price,change,changePct));


            }
            Log.d(TAG, "Dealing with Json: " + stockparserList.toString());
            return stockparserList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON for StockDownloader: " + e.getMessage());
            e.printStackTrace();
            stockparserList.add((new StockParser("NoStockResponse",0,0,0)));
            return  stockparserList;
        }

    }

}
