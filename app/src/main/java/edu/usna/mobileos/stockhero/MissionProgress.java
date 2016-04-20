package edu.usna.mobileos.stockhero;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by root on 4/20/16.
 */
public class MissionProgress implements Parcelable {
    private DateTime date;
    private int money;
    private int day;

    public MissionProgress(DateTime date, int day, int money){
        this.date = date;
        this.day = day;
        this.money = money;
    }

    public MissionProgress(Parcel p){
        date = (DateTime) p.readSerializable();
        day = p.readInt();
        money = p.readInt();
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
        dest.writeInt(money);
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
    public int getMoney(){return money;}



}
