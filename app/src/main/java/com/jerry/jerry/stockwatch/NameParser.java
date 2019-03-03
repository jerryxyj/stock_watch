package com.jerry.jerry.stockwatch;


import java.io.Serializable;

/**
 * Created by jerry on 7/25/2017.
 */

public class NameParser implements Serializable {
    private String symbol;
    private String name;
    private String type;

    NameParser(String symbol,String name,String type){
        this.symbol=symbol;
        this.name=name;
        this.type=type;
    }


    String getName(){return name;}
    String getType(){return type;}

    public String getSymbol(){return symbol;}

    public String toString(){
        return symbol+name+type;

    }


}
