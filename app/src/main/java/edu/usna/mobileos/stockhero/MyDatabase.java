package edu.usna.mobileos.stockhero;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;


import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by root on 4/18/16.
 */
public class MyDatabase extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "stocks.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getHistory(SQLiteDatabase db, String ticker, String Date) {

//        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"*"};
        String sqlTables = "price";
        String sel = "symbol='"+ticker+"'";
        qb.setTables(sqlTables);

        Cursor c = qb.query(db, sqlSelect, sel, null,
                null, null, null);
        c.moveToFirst();
        return c;

    }

    public Cursor getStockInfo(SQLiteDatabase db, String ticker){
//        SQLiteDatabase db = getWritableDatabase();
//        Log.i("Database",db.getPath());
//
//        //////This prints out how many tables there are
//        Cursor cr= db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type = 'table'", null);
//        int count = cr.getCount();
//        Log.i("Number of Tables",String.valueOf(count));


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"*"};
        String sqlTables = "symbol";
        String sel = "symbol='"+ticker+"'";
        qb.setTables(sqlTables);

        Cursor c = qb.query(db, sqlSelect, sel, null,
                null, null, null);
        c.moveToFirst();
        return c;
    }

}
