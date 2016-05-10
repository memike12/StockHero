package edu.usna.mobileos.stockhero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "kjQDbgti7JSNX7CW65J80ipEW";
    private static final String TWITTER_SECRET = "2jNtPnwXdtmLZLeU5imxdnh6EZPLBT7gXNUq2PeTJ6Kk9Tt1MH";

    Button genData;
    DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        JodaTimeAndroid.init(this);
        setContentView(R.layout.content_main);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.mainTitle);

        TextView missionText = (TextView) findViewById(R.id.welcomeMessage);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int weeks = prefs.getInt("TotalWeeks",0);
        if(weeks == 0){
            missionText.setText(R.string.firstMission);
        }
        else{
            double random = Math.random();
            if (random > .5){
                missionText.setText(R.string.goodMission);
            }
            else
                missionText.setText(R.string.roughMission);
        }

        genData= (Button) findViewById(R.id.genData);
        genData.setOnClickListener(this);
        db = db.getsInstance(this);
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    @Override
    public void onClick(View v) {
        if(v==genData) {
            DateTime date = db.generateDate();
            MissionProgress mp = new MissionProgress(date, 0, 10000);
            Intent intent = new Intent(getBaseContext(), StockListActivity.class);
            intent.putExtra("MissionProgress", mp);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.viewProfile:
                Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
