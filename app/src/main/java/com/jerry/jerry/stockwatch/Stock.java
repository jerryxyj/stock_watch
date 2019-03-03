package com.jerry.jerry.stockwatch;

/**
 * Created by jerry on 7/25/2017.
 */

public class Stock implements Comparable<Stock> {

    private String symbol;
    private double price;
    private double change;
    private double changePct;
    private String company;

    Stock(String symbol,double price,double change,double changePct,String company){
        this.symbol=symbol;
        this.price=price;
        this.change=change;
        this.changePct=changePct;
        this.company=company;
    }
    double getChange(){return change;}
    double getChangePct(){return  changePct;}
    String getCompany(){return  company;}
    double getPrice(){return price;}
    public String getSymbol(){return symbol;}

    public String toString(){
        return "Stock{"+"symbol="+symbol+'\''+
                ",price="+", change="+change+
                ",changePct="+changePct+
                ",company="+company+'\''+
                '}';

    }


    public int compareTo(Stock o){
        return symbol.compareTo(o.symbol);
    }



}
