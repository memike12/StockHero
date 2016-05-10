package edu.usna.mobileos.stockhero;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
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

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StockHistoryActivity extends FragmentActivity implements View.OnClickListener, BuySellFragment.OnStockSelectedListener {
    private CombinedChart mChart;
    int tradingYear = 252;
    float[] price = new float[tradingYear];
    String[] days = new String[tradingYear];
    int[] volume = new int[tradingYear];
    float[] periodPrice;
    String[] periodDays;
    int[] periodVolume;
    float openPrice;
    DBHelper db;
    //String date;
    String stock;
    Button shortsell,buy,sell,close;
    Button fiveDay;
    Button thirtyDay;
    Button ninetyDay;
    Button oneYear;
    int longHeld =0, shortHeld=0;
    MissionProgress mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_stock);

        shortsell = (Button) findViewById(R.id.shortsale);
        buy = (Button) findViewById(R.id.buy);
        sell = (Button) findViewById(R.id.sell);
        close = (Button) findViewById(R.id.close);
        fiveDay = (Button) findViewById(R.id.fiveDay);
        thirtyDay = (Button) findViewById(R.id.thirtyDay);
        ninetyDay = (Button) findViewById(R.id.ninetyDay);
        oneYear = (Button) findViewById(R.id.oneYear);
        buy.setOnClickListener(this);
        shortsell.setOnClickListener(this);
        sell.setOnClickListener(this);
        close.setOnClickListener(this);
        fiveDay.setOnClickListener(this);
        thirtyDay.setOnClickListener(this);
        ninetyDay.setOnClickListener(this);
        oneYear.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mp = b.getParcelable("MissionProgress");
        stock = b.getString("stock");
        TextView stockName = (TextView) findViewById(R.id.stockName);
        TextView ticker = (TextView) findViewById(R.id.ticker);
        TextView open = (TextView) findViewById(R.id.open);

        if(mp.longPortfolioHasStock(stock)){
            longHeld = mp.getLongHolding(stock);
            sell.setVisibility(View.VISIBLE);
        }

        if(mp.shortPortfolioHasStock(stock)){
            shortHeld = (Integer)mp.getShortHoldings(stock).get(0);
            close.setVisibility(View.VISIBLE);
        }

        try {
            db = DBHelper.getsInstance(this);
//            db.createDataBase();
//            db.openDataBase();
            Log.e("Database", "Looking for " + stock);
            Cursor cs = db.getStockInfo(stock);
            stockName.setText(cs.getString(cs.getColumnIndex("company")));
            cs.close();

            /////////TODO: This needs to change to be faster
            Cursor c = db.getFullStockHistory(stock);

            while(!c.isAfterLast()) {
                if(c.getString(c.getColumnIndex("date")).equals(mp.dateToString())) {
                    for (int x = tradingYear-1; x >= 0 ; x--) {
                        price[x] = Float.parseFloat(c.getString(c.getColumnIndex("price")));
                        days[x] = c.getString(c.getColumnIndex("date"));
                        volume[x] = c.getInt(c.getColumnIndex("volume"));
                        c.moveToNext();
                    }
                    break;
                }
                else{
                    c.moveToNext();
                }
            }
            c.close();
        }
        catch (Exception e){
            e.printStackTrace();
//            db.close();
        }

        Log.i("Money Now", String.valueOf(mp.getMoney()));
        ticker.setText(stock);
        openPrice = price[price.length - 1];
        open.setText(String.valueOf(openPrice));

        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        //set chart time period to 5 days right off the bat
        makeChart(5);
    }

    //This executes the trade
    public void onStockSelected(String stock, float price, int order, String action){
        Log.i(action+ " "+ stock,String.valueOf(price*order));
        mp.executeTrade(stock,price,order,action);
    }

    //This goes back to stock list activity but finishes the current one first
    public void onDialogDismissListener(int position) {
        Intent intent = new Intent(getBaseContext(), StockListActivity.class);
        intent.putExtra("MissionProgress", mp);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.buy:
                BuySellFragment dialog = new BuySellFragment();
                Bundle args = new Bundle();
                args.putFloat("price",  openPrice);
                args.putString("ticker", stock);
                args.putString("action", "Buy");
                args.putInt("hint", longHeld);
                args.putParcelable("MissionProgress", mp);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "BuySellFragment");
                break;
            case R.id.sell:
                dialog = new BuySellFragment();
                args = new Bundle();
                args.putFloat("price",  openPrice);
                args.putString("ticker", stock);
                args.putString("action", "Sell");
                args.putInt("hint", longHeld);
                args.putParcelable("MissionProgress", mp);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "BuySellFragment");
                break;
            case R.id.shortsale:
                dialog = new BuySellFragment();
                args = new Bundle();
                args.putFloat("price",  openPrice);
                args.putString("ticker", stock);
                args.putString("action", "Short");
                args.putInt("hint", shortHeld);
                args.putParcelable("MissionProgress", mp);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "BuySellFragment");
                break;
            case R.id.close:
                dialog = new BuySellFragment();
                args = new Bundle();
                args.putFloat("price",  openPrice);
                args.putString("ticker", stock);
                args.putString("action", "Close");
                args.putInt("hint", shortHeld);
                args.putParcelable("MissionProgress", mp);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "BuySellFragment");
                break;
            case R.id.thirtyDay:
                makeChart(30);
                break;
            case R.id.ninetyDay:
                makeChart(90);
                break;
            case R.id.oneYear:
                makeChart(252);
                break;
            case R.id.fiveDay:
                makeChart(5);
                break;
        }

    }

    private void makeChart(int period){
        periodPrice = new float[period];
        periodVolume = new int[period];
        periodDays = new String[period];
        for(int x =0; x<period; x++){
            periodPrice[period-x-1] = price[tradingYear-x-1];
            periodVolume[period-x-1] = volume[tradingYear-x-1];
            periodDays[period-x-1] = days[tradingYear-x-1];
        }

        mChart.setDoubleTapToZoomEnabled(false);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setAxisMaxValue(getMaxFromData(periodPrice)*1.009f); // this replaces setStartAtZero(true)
        //Log.i("Max set", String.valueOf(getMaxFromData(periodPrice)*1.009f));
        rightAxis.setAxisMinValue(getMinFromData(periodPrice)*.95f);
        //Log.i("Min set", String.valueOf(getMinFromData(periodPrice)*.95f));
        rightAxis.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        leftAxis.setAxisMaxValue(getMaxFromData(toFloatArray(periodVolume))*15);
        leftAxis.setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.TOP);
        xAxis.setEnabled(false);
        CombinedData data = new CombinedData(periodDays);
        data.setData(generateLineData(period));
        data.setData(generateBarData(period));
        switch (period) {
            case 5:
                //Log.i("Case","5");
                mChart.animateX(500, Easing.EasingOption.EaseInCubic);
                data.setDrawValues(true);
                break;
            case 30:
                //Log.i("Case","30");
                mChart.animateX(500, Easing.EasingOption.EaseInCubic);
                data.setDrawValues(false);
                break;
            case 90:
                //Log.i("Case","90");
                mChart.animateX(700, Easing.EasingOption.EaseInCubic);
                data.setDrawValues(false);
                break;
            case 252:
                mChart.animateX(1100, Easing.EasingOption.EaseInCubic);
                data.setDrawValues(false);
                break;
            default:
                mChart.animateX(1500, Easing.EasingOption.EaseInCubic);
                data.setDrawValues(false);
                break;
        }
        mChart.setData(data);
        mChart.getLegend().setEnabled(false);
        mChart.invalidate();
    }

    private LineData generateLineData(int period) {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < period; index++) {
            //Log.i("ClosePrice", String.valueOf(price[index]));
            entries.add(new Entry( periodPrice[index], index));
        }

        LineDataSet set = new LineDataSet(entries, "Price");
        set.setColor(Color.BLUE);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.BLACK);
        set.setCircleRadius(5f);
        set.setDrawCubic(false);
        set.setDrawCircles(false);
        set.setDrawValues(true);
        set.setValueTextSize(20f);
        set.setValueTextColor(Color.BLACK);
        set.setValueFormatter(new MyValueFormatter());
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData(int period) {

        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
       // int[] volume = {2807500, 2893600, 2671900, 2857200, 2368200};
        for (int index = 0; index < period; index++)
            entries.add(new BarEntry((float) periodVolume[index], index));

        BarDataSet set = new BarDataSet(entries, "Volume");
        set.setColor(Color.rgb(36, 133, 19));
        set.setValueTextColor(Color.rgb(36, 133, 19));
        set.setValueTextSize(20f);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueFormatter(new LargeValueFormatter());
        d.addDataSet(set);
        return d;
    }

    private float getMaxFromData(float[] data){
        float max = 0;
        for(int i =0;i < data.length; i++){
            if (max<data[i])
                max = data[i];
        }
        //Log.i("Max", String.valueOf(max));
        return max;
    }

    private float getMinFromData(float[] data){
        float min = 10000000;
        for(int i =0;i < data.length; i++){
            if (min>data[i])
                min = data[i];
        }
        //Log.i("Min", String.valueOf(min));
        return min;
    }

    private String[] getTimeframe(String[] dates){
        String[] names = new String[dates.length];
        for(int i = 0; i < dates.length; i++){
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
