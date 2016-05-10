package edu.usna.mobileos.stockhero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.profileTitle);

        TextView totalEarnings = (TextView) findViewById(R.id.totalEarnings);
        TextView totalAllotted = (TextView) findViewById(R.id.totalAlloted);
        TextView percentReturn = (TextView) findViewById(R.id.totalReturn);
        TextView totalWeeks = (TextView) findViewById(R.id.totalWeeks);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float totEarnings = prefs.getFloat("TotalEarnings",0);
        float totAllotted = prefs.getFloat("TotalAllotment",0);
        Float perReturn = (totEarnings/totAllotted - 1)*100;
        if (perReturn.isNaN()){
            perReturn = 0.0f;
        }
        int weeks = prefs.getInt("TotalWeeks", 0);

        String formattedEarnings = String.format("%.02f", totEarnings);
        String formattedAllotment = String.format("%.02f", totAllotted);
        String formattedReturn = String.format("%.02f", perReturn);

        totalEarnings.setText("$ "+formattedEarnings);
        totalAllotted.setText("$ "+formattedAllotment);
        percentReturn.setText(formattedReturn+"%");
        totalWeeks.setText(String.valueOf(weeks));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle item selection
        switch(item.getItemId()){
            case R.id.newGame:
                Intent intent = new Intent(getBaseContext(),StockListActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.resetProfile:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("TotalEarnings", 0);
                editor.putFloat("TotalAllotment", 0);
                editor.putInt("TotalWeeks", 0);
                editor.commit();

                TextView totalEarnings = (TextView) findViewById(R.id.totalEarnings);
                TextView totalAllotted = (TextView) findViewById(R.id.totalAlloted);
                TextView percentReturn = (TextView) findViewById(R.id.totalReturn);
                TextView totalWeeks = (TextView) findViewById(R.id.totalWeeks);

                float totEarnings = prefs.getFloat("TotalEarnings",0);
                float totAllotted = prefs.getFloat("TotalAllotment",0);
                Float perReturn = (totEarnings/totAllotted - 1)*100;
                if (perReturn.isNaN()){
                    perReturn = 0.0f;
                }
                int weeks = prefs.getInt("TotalWeeks", 0);

                String formattedEarnings = String.format("%.02f", totEarnings);
                String formattedAllotment = String.format("%.02f", totAllotted);
                String formattedReturn = String.format("%.02f", perReturn);

                totalEarnings.setText("$ "+formattedEarnings);
                totalAllotted.setText("$ "+formattedAllotment);
                percentReturn.setText(formattedReturn+"%");
                totalWeeks.setText(String.valueOf(weeks));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

