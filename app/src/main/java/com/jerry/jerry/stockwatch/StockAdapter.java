package com.jerry.jerry.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jerry on 7/25/2017.
 */

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainActivity;


    public StockAdapter(List<Stock>stockList, MainActivity mainActivity){
        this.stockList=stockList;
        this.mainActivity=mainActivity;
    }



    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);


        return new MyViewHolder(itemView);

    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Stock stock=stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.company.setText(stock.getCompany());

        holder.price.setText(Double.toString(stock.getPrice()));
        if(stock.getChange()>0) {
            holder.change.setText("▲"+Double.toString(stock.getChange()) + "(" + Double.toString(stock.getChangePct()) + "%)");
            holder.symbol.setTextColor(Color.GREEN);
            holder.company.setTextColor(Color.GREEN);
            holder.change.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
        }
        else{
            holder.change.setText("▼"+Double.toString(stock.getChange()) + "(" + Double.toString(stock.getChangePct()) + "%)");
            holder.symbol.setTextColor(Color.RED);
            holder.company.setTextColor(Color.RED);
            holder.change.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
        }

    }
    public int getItemCount() {
        return stockList.size();
    }

    }
