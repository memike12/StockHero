package edu.usna.mobileos.stockhero;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;

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
        shortPortfolio= new HashMap<String, Integer>();
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
    public void executeTrade(String stock, float price, int order, String action){
        switch (action){
            case "Buy":
                money = money - price*order;
                longPortfolio.put(stock,order);
                break;
            case "Sell":
                money = money + price*order;
                if (longPortfolio.get(stock) == order) {
                    longPortfolio.remove(stock);
                }
                else {
                    int holdings = (Integer)longPortfolio.get(stock);
                    longPortfolio.put(stock, holdings - order);
                }
                break;
            case "Short":
                money = money - price*order;
                shortPortfolio.put(stock,order);
                break;
            case "Close":
                money = money + price*order;
                if (shortPortfolio.get(stock) == order) {
                    shortPortfolio.remove(stock);
                }
                else {
                    int holdings = (Integer)shortPortfolio.get(stock);
                    shortPortfolio.put(stock, holdings - order);
                }
                break;
        }
    }
}
