package edu.usna.mobileos.stockhero;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    public ListView stockListView;
    public ArrayAdapter<String> adapter;
    public List<String> stockItemList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.content_main);

        Button genData = (Button) findViewById(R.id.genData);

        genData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime date = generateDate();
                Intent intent = new Intent(getBaseContext(), StockHistoryActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

    }

    private void generateData(){



    }

    private DateTime generateDate(){
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        int year = randBetween(2001, 2014);// Here you can set Range of years you need
        int month = randBetween(1, 12);
//        Log.d("year", String.valueOf(year));
//        Log.d("month", String.valueOf(month));
//        Log.d("max", String.valueOf(new DateTime(year, month, 1, 0, 0).dayOfMonth().getMaximumValue()));
        int day = randBetween(1, new DateTime(year, month, 1, 0, 0).dayOfMonth().getMaximumValue());
//        Log.d("day", String.valueOf(day));
        DateTime dt = new DateTime(year, month, day,0,0);
        System.out.println(fmt.print(dt));
        return dt;
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

}
