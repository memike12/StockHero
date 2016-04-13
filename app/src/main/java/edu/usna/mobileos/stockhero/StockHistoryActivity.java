package edu.usna.mobileos.stockhero;

import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StockHistoryActivity extends AppCompatActivity{
    private CombinedChart mChart;
    float[] close = getData();
    String[] days = getTimeframe();
    private final int itemcount = close.length;

    int[] volume = {2807500, 2893600, 2671900, 2857200, 2368200};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_stock);

        TextView stockName = (TextView) findViewById(R.id.stockName);
        TextView ticker = (TextView) findViewById(R.id.ticker);
        TextView open = (TextView) findViewById(R.id.open);

        stockName.setText("3M");
        ticker.setText("MMM");
        open.setText(String.valueOf(close[close.length - 1]));

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
        //rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
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
    }

    public void goBack(View v){
        Intent intent = new Intent(getBaseContext(), StockListActivity.class);
        startActivity(intent);
    }

    private LineData generateLineData() {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++) {
            Log.i("ClosePrice", String.valueOf(close[index]));
            entries.add(new Entry( close[index], index));
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

    private String[] getTimeframe(){
        String[] dates = {"2014-04-07", "2014-04-08", "2014-04-09", "2014-04-10","2014-04-11"};
        String[] names = new String[close.length];
        for(int i = 0; i < close.length; i++){
            try {
                Date day = new SimpleDateFormat("yyyy-MM-dd").parse(dates[i]);
                Log.i("Day", day.toString());
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
