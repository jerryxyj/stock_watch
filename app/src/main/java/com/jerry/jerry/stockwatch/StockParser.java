package com.jerry.jerry.stockwatch;

/**
 * Created by jerry on 7/25/2017.
 */

public class StockParser {

    private String symbol;
    private double price;
    private double change;
    private double changePct;


    StockParser(String symbol,double price,double change,double changePct){
        this.symbol=symbol;
        this.price=price;
        this.change=change;
        this.changePct=changePct;

    }
    double getChange(){return change;}
    double getChangePct(){return  changePct;}
    double getPrice(){return price;}
    public String getSymbol(){return symbol;}

    public String toString(){
        return "Stock{"+"symbol="+symbol+'\''+
                ",price="+", change="+change+
                ",changePct="+changePct+"}";

    }
}
