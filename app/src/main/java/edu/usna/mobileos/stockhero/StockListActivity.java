package edu.usna.mobileos.stockhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * This Activity pulls in the applicable stock data and displays it in a list view
 */


public class StockListActivity extends Activity implements AdapterView.OnItemClickListener{
    String date;
    int request_Code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_list);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
        ListView stockListView = (ListView) findViewById(R.id.stockListView);

        //The di object gives me the stocks that we're using
        DowIndex di = new DowIndex();
        List stockList = Arrays.asList(di.getDow(date));

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stockList);
        stockListView.setAdapter(adapter);
        stockListView.setOnItemClickListener(this);
    }


    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        String stock = ((TextView)view).getText().toString();
        Intent intent = new Intent(this, StockHistoryActivity.class);

        Bundle extras = new Bundle();
        extras.putString("stock", stock);
        extras.putString("date", date);
        intent.putExtras(extras);
        request_Code = 100;
        startActivityForResult(intent, request_Code);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check to see which activity is returning the result
        if (requestCode == 100) {
            // check result code
            if (resultCode == RESULT_OK) {
                // get data set with setData
                String choice = data.getExtras().getString("choice");
                Toast.makeText(StockListActivity.this, choice, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
