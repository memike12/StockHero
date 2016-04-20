package edu.usna.mobileos.stockhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.List;

/**
 * This Activity pulls in the applicable stock data and displays it in a list view
 */
public class StockListActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    MissionProgress mp;
    String date;
    int request_Code;
    Button nextDay;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_list);

        nextDay = (Button) findViewById(R.id.nextDay);
        nextDay.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mp = b.getParcelable("MissionProgress");
        date = mp.dateToString();

        if(mp.getDay()>=4){
            nextDay.setText("End Week");
        }
        Toast.makeText(this, "Day " + String.valueOf(mp.getDay()), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        if(v == nextDay){
            if(mp.getDay()<=3){

                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
                DateTime date = mp.getDate();
                date = date.plusDays(1);

                db = new DBHelper(this);
                db.createDataBase();
                db.openDataBase();
                while(!db.CheckIfDateInDB(fmt.print(date))){
                    date=date.plusDays(1);
                }
                db.close();
                Log.i("Date", fmt.print(date));
                mp.nextDay(date);

                Intent intent = new Intent(getBaseContext(), StockListActivity.class);
                intent.putExtra("MissionProgress", mp);
                //intent.putExtra("Database", db);
                startActivity(intent);
                finish();
            }
            else{
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                //intent.putExtra("Database", db);
                startActivity(intent);
                finish();
            }
        }
    }
}
