package edu.usna.mobileos.stockhero;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * This Activity pulls in the applicable stock data and displays it in a list view
 */


public class StockListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_list);
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        Log.d("date", date);

        ListView stockListView = (ListView) findViewById(R.id.stockListView);
        DowIndex di = new DowIndex();
        List stockList = Arrays.asList(di.getDow(date));

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stockList);
        stockListView.setAdapter(adapter);
        stockListView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        String stock = ((TextView)view).getText().toString();
        Intent intent = new Intent(getBaseContext(), StockHistoryActivity.class);
        intent.putExtra("stock", stock);
        startActivity(intent);
    }

}
