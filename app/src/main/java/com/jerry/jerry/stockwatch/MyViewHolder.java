package com.jerry.jerry.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jerry on 7/25/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    public TextView company;
    public TextView change;
    public TextView price;





    public  MyViewHolder(View view){
        super(view);
        symbol=(TextView)view.findViewById(R.id.symbol);
        company=(TextView)view.findViewById(R.id.company);
        change=(TextView)view.findViewById(R.id.change);
        price=(TextView)view.findViewById(R.id.price);




    }
}
