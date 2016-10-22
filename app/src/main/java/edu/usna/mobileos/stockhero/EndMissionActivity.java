package edu.usna.mobileos.stockhero;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;

public class EndMissionActivity extends AppCompatActivity {
    MissionProgress mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_mission);

        TextView earningsReport = (TextView) findViewById(R.id.weekEarnings);
        Chart dailyEarningsChart = (Chart) findViewById(R.id.weekChart);
        Button okButton = (Button) findViewById(R.id.continueButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mp = b.getParcelable("MissionProgress");

        float[] dailyEarnings = mp.getDailyEarnings();
        String earnings = "You posted a " + dailyEarnings[0]/dailyEarnings[4] + "% return this week.";
        earningsReport.setText(earnings);
        Log.i("End","inmissionend");
    }
}
