package edu.usna.mobileos.stockhero;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by root on 4/20/16.
 */
public class MissionProgress implements Parcelable {
    private DateTime date;
    private HashMap shortPortfolio;
    private HashMap longPortfolio;
    private float money;
    private int day;

    public MissionProgress(DateTime date, int day, float money){
        this.date = date;
        this.day = day;
        this.money = money;
        shortPortfolio= new HashMap<>();
        longPortfolio = new HashMap<String, Integer>();
    }

    public MissionProgress(Parcel p){
        date = (DateTime) p.readSerializable();
        day = p.readInt();
        money = p.readFloat();
        longPortfolio = (HashMap)p.readSerializable();
        shortPortfolio = (HashMap)p.readSerializable();
    }

    public void nextDay(DateTime date){
        this.date = date;
        day++;
        return;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(date);
        dest.writeInt(day);
        dest.writeFloat(money);
        dest.writeSerializable(longPortfolio);
        dest.writeSerializable(shortPortfolio);
    }

    public static final Parcelable.Creator<MissionProgress> CREATOR = new Parcelable.Creator<MissionProgress>(){
        @Override
        public MissionProgress createFromParcel(Parcel parcel){
            return new MissionProgress(parcel);
        }

        @Override
        public MissionProgress[] newArray(int size){
            return new MissionProgress[size];
        }
    };

    public String dateToString(){
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        return String.valueOf(fmt.print(date));
    }

    public DateTime getDate(){return date;}
    public int getDay(){return day;}
    public float getMoney(){return money;}
    public boolean longPortfolioHasStock(String ticker){
        if(longPortfolio.containsKey(ticker)){
            return true;
        }
        else return false;
    }
    public boolean shortPortfolioHasStock(String ticker){
        if(shortPortfolio.containsKey(ticker)){
            return true;
        }
        else return false;
    }
    public void executeTrade(String stock, float price, int order, String action){
        switch (action){
            case "Buy":
                money = money - price*order;
                longPortfolio.put(stock,order);
                break;
            case "Sell":
                money = money + price*order;
                if ((Integer)longPortfolio.get(stock) == order) {
                    longPortfolio.remove(stock);
                }
                else {
                    int holdings = (Integer)longPortfolio.get(stock);
                    longPortfolio.put(stock, holdings - order);
                }
                break;
            case "Short":
                money = money - price*order;
                ArrayList<Object> arrayList = new ArrayList<>(2);
                arrayList.add(0,order);
                arrayList.add(1,price);
                shortPortfolio.put(stock,arrayList);
                break;
            case "Close":
                arrayList =(ArrayList<Object>) shortPortfolio.get(stock);
                int shortOrder = (Integer)arrayList.get(0);
                float shortPrice = (float)arrayList.get(1);
                if (shortOrder == order) {
                    money = money + shortPrice*order*2 - price*order;
                    shortPortfolio.remove(stock);
                }
                else {
                    money = money + shortPrice*order*2 - price*order;
                    arrayList.set(0, shortOrder - order);
                    shortPortfolio.put(stock, arrayList);
                }
                break;
        }
    }
    public boolean longPortfolioIsEmpty(){
        if(longPortfolio.isEmpty()){
            return true;
        }
        else return false;
    }
    public boolean shortPortfolioIsEmpty(){
        if(shortPortfolio.isEmpty()){
            return true;
        }
        else return false;
    }
    public int getLongHolding(String ticker){
        return (Integer)longPortfolio.get(ticker);
    }
    public ArrayList getShortHoldings(String ticker){
        return (ArrayList) shortPortfolio.get(ticker);
    }
    public float liquidate(Context context){
        float capital =0;
        if(!longPortfolioIsEmpty()){
            Iterator it = longPortfolio.entrySet().iterator();
            while (it.hasNext()) {
                String action = "Sell";
                HashMap.Entry pair = (HashMap.Entry)it.next();
                String stock = (String)pair.getKey();
                int order = (Integer)pair.getValue();
                DBHelper db = DBHelper.getsInstance(context);
                Cursor c = db.getPriceOnDate(stock, this.dateToString());
                float price = Float.parseFloat(c.getString(c.getColumnIndex("price")));
                c.close();
                executeTrade(stock,price,order,action);
                //it.remove();
            }
        }
        if(!shortPortfolioIsEmpty()){
            Iterator it = shortPortfolio.entrySet().iterator();
            while (it.hasNext()) {
                String action = "Close";
                HashMap.Entry pair = (HashMap.Entry)it.next();
                String stock = (String)pair.getKey();
                ArrayList<Object> arrayList = (ArrayList)pair.getValue();
                int order = (Integer)arrayList.get(0);
                DBHelper db = DBHelper.getsInstance(context);
                Cursor c = db.getPriceOnDate(stock, this.dateToString());
                float price = Float.parseFloat(c.getString(c.getColumnIndex("price")));
                c.close();
                executeTrade(stock,price,order,action);
//                it.remove();
            }
        }
    return money;
    }
}
