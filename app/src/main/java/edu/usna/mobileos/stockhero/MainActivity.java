package edu.usna.mobileos.stockhero;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button genData;
    DBHelper db;
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.content_main);

        genData= (Button) findViewById(R.id.genData);
        genData.setOnClickListener(this);
    }

    private DateTime generateDate(){
        int year = randBetween(2001, 2014);
        int month = randBetween(1, 12);
        int day = randBetween(1, new DateTime(year, month, 1, 0, 0).dayOfMonth().getMaximumValue());
        DateTime dt = new DateTime(year, month, day,0,0);
        return dt;
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    @Override
    public void onClick(View v) {
        if(v==genData) {
            Log.i("Clicked", "Lets Go!");
            db = new DBHelper(this);
            db.createDataBase();
            db.openDataBase();

            DateTime date = generateDate();
            Log.i("Date", "Tried " + fmt.print(date));
            while (!db.CheckIfDateInDB(fmt.print(date))){
                date = generateDate();
                Log.i("Date", "Tried " + fmt.print(date));
            }
            Log.i("Date", fmt.print(date));
            db.close();
            MissionProgress mp = new MissionProgress(date, 0, 10000);
            Intent intent = new Intent(getBaseContext(), StockListActivity.class);
            intent.putExtra("MissionProgress", mp);
            //intent.putExtra("Database", db);
            startActivity(intent);
            finish();
        }
    }
}
