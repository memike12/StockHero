package edu.usna.mobileos.stockhero;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StockHistoryActivity extends Activity {
    private CombinedChart mChart;
    float[] price = getData();
    String[] days = {"2014-04-07", "2014-04-08", "2014-04-09", "2014-04-10","2014-04-11"};
    private final int itemcount = price.length;
    private SharedPreferences prefs;


    int[] volume = {2807500, 2893600, 2671900, 2857200, 2368200};

    DBHelper db;
    String date;
    String stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_stock);

        Intent intent = getIntent();
        stock = intent.getExtras().getString("stock");
        date = intent.getExtras().getString("date");

        TextView stockName = (TextView) findViewById(R.id.stockName);
        TextView ticker = (TextView) findViewById(R.id.ticker);
        TextView open = (TextView) findViewById(R.id.open);

        try {
            db = new DBHelper(this);
            db.createDataBase();
            db.openDataBase();
            Cursor cs = db.getStockInfo(stock);
            stockName.setText(cs.getString(cs.getColumnIndex("company")));
            Cursor c = db.getHistory(stock, date);
            for (int x = 0; x < 5; x++) {
                price[x] = Float.parseFloat(c.getString(c.getColumnIndex("price")));
                days[x] = c.getString(c.getColumnIndex("date"));
                volume[x] = c.getInt(c.getColumnIndex("volume"));
                c.moveToNext();
            }
        }
        catch (Exception e){
            Log.e("Database","Didn't Load");
            db.close();
        }

        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
        days = getTimeframe(days);

        ticker.setText(stock);
        open.setText(String.valueOf(price[price.length - 1]));

        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);


        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMaxValue(getMaxFromData(price)*1.009f); // this replaces setStartAtZero(true)
        rightAxis.setAxisMinValue(getMinFromData(price)*.98f);
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        leftAxis.setAxisMaxValue(getMaxFromData(toFloatArray(volume))*15);
        leftAxis.setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.TOP);

        CombinedData data = new CombinedData(days);

        data.setData(generateLineData());
        data.setData(generateBarData());

        mChart.setData(data);
        mChart.setDrawValueAboveBar(false);
        mChart.invalidate();
        db.close();
    }

    public void sell(View v){
        Intent intent = new Intent();
        intent.putExtra("choice", "Sell");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void buy(View v){
        Intent intent = new Intent();
        intent.putExtra("choice", "Buy");
        setResult(RESULT_OK, intent);
        finish();
    }

    private LineData generateLineData() {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++) {
            //Log.i("ClosePrice", String.valueOf(price[index]));
            entries.add(new Entry( price[index], index));
        }

        LineDataSet set = new LineDataSet(entries, "Price");
        set.setColor(Color.BLUE);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.BLACK);
        set.setCircleRadius(5f);
        set.setDrawCubic(false);
        set.setDrawValues(true);
        set.setValueTextSize(20f);
        set.setValueTextColor(Color.BLACK);
        set.setValueFormatter(new MyValueFormatter());
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        int[] volume = {2807500, 2893600, 2671900, 2857200, 2368200};
        for (int index = 0; index < itemcount; index++)
            entries.add(new BarEntry((float) volume[index], index));

        BarDataSet set = new BarDataSet(entries, "Volume");
        set.setColor(Color.rgb(36, 133, 19));
        set.setValueTextColor(Color.rgb(36, 133, 19));
        set.setValueTextSize(20f);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueFormatter(new LargeValueFormatter());
        d.addDataSet(set);
        return d;
    }

    private float[] getData(){
        return new float[]{134.40f, 134.84f, 135.84f, 133.97f, 132.39f};
    }

    private float getMaxFromData(float[] data){
        float max = 0;
        for(int i =0;i < data.length; i++){
            if (max<data[i])
                max = data[i];
        }
        return max;
    }

    private float getMinFromData(float[] data){
        float min = 10000000;
        for(int i =0;i < data.length; i++){
            if (min>data[i])
                min = data[i];
        }
        return min;
    }

    private String[] getTimeframe(String[] dates){
        String[] names = new String[price.length];
        for(int i = 0; i < price.length; i++){
            try {
                Date day = new SimpleDateFormat("yyyy-MM-dd").parse(dates[i]);
                names[i] = new SimpleDateFormat("EE").format(day);
            }
            catch (ParseException e){
                e.printStackTrace();
            }
        }
        return names;
    }

    private float[] toFloatArray(int[] data){
        float[] floatArray = new float[data.length];
        for(int i = 0 ; i < data.length; i++){
            floatArray[i]=(float) data[i];
        }
        return floatArray;
    }
    private float[] toFloatArray(double[] data){
        float[] floatArray = new float[data.length];
        for(int i = 0 ; i < data.length; i++){
            floatArray[i]=(float) data[i];
        }
        return floatArray;
    }


}
