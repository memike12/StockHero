package edu.usna.mobileos.stockhero;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is all my awesome database stuff
 */
public class DBHelper extends SQLiteOpenHelper {
    private String DATABASE_PATH;
    private static final String DATABASE_NAME = "stocks.db";
    private static final int DATABASE_VERSION = 3;
    private static final String SP_KEY_DB_VER = "db_ver";
    private static DBHelper sInstance;
    private Context myContext;
    public SQLiteDatabase myDataBase;
    SQLiteDatabase db;

    public DBHelper(Context context){
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        this.myContext=context;
        DATABASE_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
        initialize();
        db = getReadableDatabase();
    }

    public static synchronized DBHelper getsInstance(Context context){
        if (sInstance == null){
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Initializes database. Creates database if doesn't exist.
     */
    private void initialize() {
        if (databaseExists()) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(myContext);
            int dbVersion = prefs.getInt(SP_KEY_DB_VER, 1);
            if (DATABASE_VERSION != dbVersion) {
                File dbFile = myContext.getDatabasePath(DATABASE_NAME);
                if (!dbFile.delete()) {
                    Log.w("Database", "Unable to update database");
                }
            }
        }
        if (!databaseExists()) {
            createDatabase();
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean databaseExists() {
        File dbFile = myContext.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Creates database by copying it from assets directory.
     */
    private void createDatabase() {
        String parentPath = myContext.getDatabasePath(DATABASE_NAME).getParent();

        File file = new File(parentPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.w("Database", "Unable to create database directory");
                return;
            }
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = myContext.getAssets().open("databases/"+DATABASE_NAME);
            os = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(myContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SP_KEY_DB_VER, DATABASE_VERSION);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getFullStockHistory(String ticker) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"*"};
        String sqlTables = "price";
        String sel = "symbol='" + ticker + "'";
        qb.setTables(sqlTables);

        Cursor c = qb.query(db, sqlSelect, sel, null,
                null, null, null);
        c.moveToFirst();
        return c;
    }

    public Cursor getStockInfo( String ticker){
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

    public Cursor getPriceOnDate(String ticker, String date){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"*"};
        String sqlTables = "price";
        String sel = "symbol='"+ticker+"' AND date='"+date+"'";
        qb.setTables(sqlTables);

        Cursor c = qb.query(db, sqlSelect, sel, null,
                null, null, null);
        c.moveToFirst();
        return c;
    }

    public boolean CheckIfDateInDB(String date){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"*"};
        String sqlTables = "price";
        String sel = "date='"+date+"'";
        qb.setTables(sqlTables);

        Cursor c = qb.query(db, sqlSelect, sel, null,
                null, null, null);
        if(c.getCount() > 0){
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public DateTime generateDate(){
        DateTime date;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"date"};
        String sqlTables = "price";
        String orderBy = "RANDOM()";
        String limitBy = "1";
        qb.setTables(sqlTables);
        qb.setDistinct(true);

        Cursor c = qb.query(db,sqlSelect,null,null,null,null,orderBy,limitBy);
        c.moveToFirst();
        String dateString = c.getString(0);
        date = new DateTime(dateString);

        return date;
    }
}
