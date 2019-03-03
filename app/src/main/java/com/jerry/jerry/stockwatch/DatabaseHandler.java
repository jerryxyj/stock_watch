package com.jerry.jerry.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jerry on 7/25/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


        private static final String TAG = "DatabaseHandler";

        // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 1;
        private MainActivity mainActivity;
        private  StockAdapter mAdapter;

        // DB Name
        private static final String DATABASE_NAME = "StockWatchAppDB";
        // DB Table Name
        private static final String TABLE_NAME = "StockTable";
        ///DB Columns
        private static final String SYMBOL = "StockSymbol";
        private static final String COMPANY = "CompanyName";
        private static final String  PRICE= "StockPrice";
        private static final String CHANGE = "PriceChange";
        private static final String CHANGEPCT = "PriceChangePercent";
        // Columns to add later

        // DB Table Create Code
        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        SYMBOL + " TEXT not null unique," +
                        COMPANY + " TEXT not null, " +
                        PRICE + " TEXT not null, " +
                        CHANGE + " TEXT not null, " +
                        CHANGEPCT + " INT not null)";

        private SQLiteDatabase database;



        // Singleton instance
        private static DatabaseHandler instance;

        public static DatabaseHandler getInstance(Context context) {
            if (instance == null)
                instance = new DatabaseHandler(context);
            return instance;
        }

        private DatabaseHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            database = getWritableDatabase();
            Log.d(TAG, "DatabaseHandler: C'tor DONE");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // onCreate is only called is the DB does not exist
            Log.d(TAG, "onCreate: Making New DB");
            db.execSQL(SQL_CREATE_TABLE);
        }

        public void setupDb() {
            database = getWritableDatabase();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public ArrayList<Stock> loadStocks() {

            Log.d(TAG, "loadStock: LOADING STOCK DATA FROM DB");
            ArrayList<Stock> stocks = new ArrayList<>();

            Cursor cursor = database.query(
                    TABLE_NAME,  // The table to query
                    new String[]{SYMBOL,COMPANY,PRICE,CHANGE,CHANGEPCT}, // The columns to return
                    null, // The columns for the WHERE clause
                    null, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null); // The sort order
            if (cursor != null) {
                cursor.moveToFirst();

                for (int i = 0; i < cursor.getCount(); i++) {
                    String symbol = cursor.getString(0);
                    String company = cursor.getString(1);
                    Double price = cursor.getDouble(2);
                    Double change = cursor.getDouble(3);
                    Double changePct = cursor.getDouble(4);
                    stocks.add(new Stock(symbol, price, change, changePct, company));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            Log.d(TAG, "loadCountries: DONE LOADING COUNTRY DATA FROM DB");

            return stocks;
        }

        public void addStock(Stock stock) {
            Log.d(TAG, "addStock: Adding " + stock.getSymbol());
            ContentValues values = new ContentValues();
            values.put(SYMBOL, stock.getSymbol());
            values.put(COMPANY, stock.getCompany());
            values.put(PRICE, stock.getPrice());
            values.put(CHANGE, stock.getChange());
            values.put(CHANGEPCT, stock.getChangePct());
            database.insert(TABLE_NAME,null,values);



            deleteStock(stock.getSymbol());
            long key = database.insert(TABLE_NAME, null, values);
            Log.d(TAG, "addStock: "+key);

        }

        public void updateStock(Stock stock) {
            ContentValues values = new ContentValues();
            values.put(SYMBOL, stock.getSymbol());
            values.put(COMPANY, stock.getCompany());
            values.put(PRICE, stock.getPrice());
            values.put(CHANGE, stock.getChange());
            values.put(CHANGEPCT, stock.getChangePct());

            database.update(
                    TABLE_NAME, values, SYMBOL + " = ?", new String[]{stock.getSymbol()});

            Log.d(TAG, "updateStock: ");
        }

        public void deleteStock(String symbol) {
            Log.d(TAG, "DeleteStock: Deleting Stock " + symbol );


            //could be delete code below, if above doesn't work, try below code;
            int cnt = database.delete(TABLE_NAME, SYMBOL+ " = ?", new String[]{symbol});
            // int cnt = database.delete(TABLE_NAME, "SYMBOL = ?", new String[]{symbol});

            Log.d(TAG, "deleteStock: " + cnt);
        }

//        public void dumpLog() {
//            Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
//            if (cursor != null) {
//                cursor.moveToFirst();
//
//                Log.d(TAG, "dumpLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
//                for (int i = 0; i < cursor.getCount(); i++) {
//                    String symbol = cursor.getString(0);
//                    String company = cursor.getString(1);
//                    Double price = cursor.getDouble(2);
//                    Double change = cursor.getDouble(3);
//                    Double changePct = cursor.getDouble(4);
//                    Log.d(TAG, "dumpLog: " +
//                            String.format("%s %-18s", SYMBOL + ":", symbol) +
//                            String.format("%s %-18s", COMPANY + ":", company) +
//                            String.format("%s %-18s", PRICE + ":", price) +
//                            String.format("%s %-18s", CHANGE + ":", change) +
//                            String.format("%s %-18s", CHANGEPCT + ":", changePct));
//                    cursor.moveToNext();
//                }
//                cursor.close();
//            }
//
//            Log.d(TAG, "dumpLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        }

        public void shutDown() {
            database.close();
        }
}
