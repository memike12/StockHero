package edu.usna.mobileos.stockhero;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by root on 4/18/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    private String DATABASE_PATH;
    private static String DATABASE_NAME = "stocks.db";
    private static DBHelper sInstance;
    private Context myContext;
    public SQLiteDatabase myDataBase;

    public DBHelper(Context context){
        super(context, DATABASE_NAME , null, 1);
        this.myContext=context;
        DATABASE_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
    }

    public static synchronized DBHelper getsInstance(Context context){
        if (sInstance == null){
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public void createDataBase(){
        boolean dbExist = checkDataBase();
        if(dbExist){
//            Log.i("Database","exists");
            //do nothing - database already exist
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database doesn't exist yet.
            Log.e("Database","Doesn't Exist");
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open("databases/"+DATABASE_NAME);
        Log.i("Database", "Copying From Assets");
        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException{
        //Open the database
        Log.i("Database", "opening");
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
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
        SQLiteDatabase db = getReadableDatabase();
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
        SQLiteDatabase db = getReadableDatabase();
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
        SQLiteDatabase db = getReadableDatabase();
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
        SQLiteDatabase db = getReadableDatabase();
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
}
