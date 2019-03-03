package com.jerry.jerry.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
implements View.OnClickListener,View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private static String MarketWatchURL = "http://www.marketwatch.com/investing/stock/";
    private List<Stock> stockList=new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout
    private  StockAdapter mAdapter;
    private int selectedPostion;
    private TextView symbol;
    private TextView company;
    private TextView change;
    private TextView price;
    String companyname;
    String symbolname;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!networkCheck()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.drawable.icon1);
            builder.setMessage("Stocks Cannot be Added Without a Network Connection");
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        };
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        mAdapter=new StockAdapter(stockList,this);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mlayoutManger=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mlayoutManger);
        symbol=(TextView)findViewById(R.id.symbol);
        company=(TextView)findViewById(R.id.company);
        change=(TextView)findViewById(R.id.change);
        price=(TextView)findViewById(R.id.price);
        DatabaseHandler.getInstance(this).setupDb();




        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });



    }
    private void doRefresh() {
        Collections.shuffle(stockList);
        List<Stock> list = stockList;

        if(networkCheck()) {
            for (int i = 0; i < list.size(); i++) {
                companyname=list.get(i).getCompany();
                updatStockLoad(list.get(i).getSymbol());
                //DatabaseHandler.getInstance(this).updateStock(stockList.get(i));

            }

            swiper.setRefreshing(false);
            Toast.makeText(this, "Stock updated", Toast.LENGTH_SHORT).show();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.drawable.icon1);
            builder.setMessage("Stocks Cannot be Added Without a Network Connection");
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    public void onClick(View v){
        Toast.makeText(this, "You want to watch this stock", Toast.LENGTH_SHORT).show();
        int pos = recyclerView.getChildLayoutPosition(v);
        String stockname=stockList.get(pos).getSymbol();
        String url = MarketWatchURL+stockname;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    public boolean onLongClick(View v){
        final int pos = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_sweep_black_24dp);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseHandler.getInstance(MainActivity.this).deleteStock(stockList.get(pos).getSymbol());
                stockList.remove(pos);
                mAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Delete Stock Symbol " +stockList.get(pos).getSymbol() + "?");


        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.add:
                if(networkCheck()) {


                    Toast.makeText(this, "You want to add Stock", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    final EditText et = new EditText(this);

                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);

                    builder.setView(et);
//                builder.setIcon(R.drawable.icon1);


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            symbolname = et.getText().toString();
                            doNameLoad(symbolname);
                            Log.d(TAG, "Send the input symbol to doasyncload: " + symbolname);


                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    builder.setMessage("Please enter a Stock Symbol:");
                    builder.setTitle("Stock Selection");

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.drawable.icon1);
                    builder.setMessage("Stocks Cannot be Added Without a Network Connection");
                    builder.setTitle("No Network Connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
       //DatabaseHandler.getInstance(this).dumpLog();
        Log.d(TAG,"reload stock from resume");
        ArrayList<Stock> list = DatabaseHandler.getInstance(this).loadStocks();
        stockList.clear();
        stockList.addAll(list);
        Log.d(TAG, "onResume: " + list);
        mAdapter.notifyDataSetChanged();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        DatabaseHandler.getInstance(this).shutDown();
        super.onDestroy();
    }

    private void doNameLoad(String s) {

        NameDownloader alt = new NameDownloader(this);
        alt.execute(s);
        Log.d(TAG,"doasyncload is under excuting: "+s);

    }

    public void updateData(final ArrayList<NameParser> nList) {
        final CharSequence[] sArray = new CharSequence[nList.size()];
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nList.size(); i++)
            sArray[i] = nList.get(i).getSymbol()+"-"+nList.get(i).getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
//        builder.setIcon(R.drawable.icon2);
        if(nList.size()==0){
//                builder.setIcon(R.drawable.icon1);
            builder.setMessage("Data for stock symbol");
            builder.setTitle("Symbol Not Found:"+symbolname);
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else if(nList.size()==1){
            sb.append(sArray[0].subSequence(0, nList.get(0).getSymbol().length()));
            companyname = nList.get(0).getName();
            doStockLoad(sb.toString());

        }
        else {

            builder.setItems(sArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    sb.append(sArray[which].subSequence(0, nList.get(which).getSymbol().length()));
                    companyname = nList.get(which).getName();

                    Log.d(TAG, "My selection symbol is: " + sb.toString());
                    doStockLoad(sb.toString());


                }

            });

            builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();


            dialog.show();
        }

        mAdapter.notifyDataSetChanged();
    }
    private void doStockLoad(String s) {
        boolean duplicate=false;
        for(int i=0; i<stockList.size();i++){
           if(s.equals(stockList.get(i).getSymbol())) {
               duplicate = true;
           }

        }
        if(duplicate){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_warning_black_24dp);
            builder.setMessage("Stock Symbol "+s+" already displayed");
            builder.setTitle("Duplicate Stock");
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else {


            StockDownloader slt = new StockDownloader(this);
            slt.execute(s);
            Log.d(TAG, "Stockload is under excuting: " + s);
        }

    }

    private void updatStockLoad(String s) {
            StockDownloader slt = new StockDownloader(this);
            slt.execute(s);
            Log.d(TAG, "Stockload is under excuting: " + s);

    }

    public void updateStock(final ArrayList<StockParser> sList) {
        if(sList.get(0).getSymbol().equals("NoStockResponse")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_warning_black_24dp);
            builder.setMessage("No Stock "+companyname+" response");
            builder.setTitle("Stock has no response");
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else {
            String symbol = sList.get(0).getSymbol();
            Double price = sList.get(0).getPrice();
            Double change = sList.get(0).getChange();
            Double changePct = sList.get(0).getChangePct();
            Stock stock = new Stock(symbol, price, change, changePct, companyname);
            DatabaseHandler.getInstance(this).addStock(stock);
            ArrayList<Stock> list = DatabaseHandler.getInstance(this).loadStocks();
            stockList.clear();
            stockList.addAll(list);
            mAdapter.notifyDataSetChanged();
            Log.d(TAG, "After updated finace data: " + symbol + price);
        }
    }
    public Boolean networkCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean network;
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            network=true;

        } else {

            network=false;

        }
        return network;

    }
}
