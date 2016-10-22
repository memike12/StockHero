package edu.usna.mobileos.stockhero;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

/**
 * This Activity pulls in the applicable stock data and displays it in a list view
 */
public class StockListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, EndMissionFragment.EndMissionListener {
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
        db = db.getsInstance(this);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        try {
            mp = b.getParcelable("MissionProgress");
        } catch (NullPointerException npe) {
            DateTime date = db.generateDate();
            mp = new MissionProgress(date, 0, 10000);
        }
        date = mp.dateToString();

        ActionBar ab = getSupportActionBar();
        int day = mp.getDay() + 1;
        ab.setTitle("Day " + day);
        float money = mp.getMoney();
        String formattedMoney = String.format("%.02f", money);
        ab.setSubtitle("$" + formattedMoney);

        if (mp.getDay() >= 4) {
            nextDay.setText("End Week");
        }
        ListView stockListView = (ListView) findViewById(R.id.stockListView);

        //The di object gives me the stocks that we're using
        DowIndex di = new DowIndex();
        List stockList = Arrays.asList(di.getDow(date));

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stockList);
        stockListView.setAdapter(adapter);
        stockListView.setOnItemClickListener(this);

    }


    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        String stock = ((TextView) view).getText().toString();
        Intent intent = new Intent(this, StockHistoryActivity.class);

        intent.putExtra("MissionProgress", mp);
        intent.putExtra("stock", stock);
        request_Code = 100;
        startActivityForResult(intent, request_Code);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check to see which activity is returning the result
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Intent intent = getIntent();
                Bundle b = intent.getExtras();
                mp = b.getParcelable("MissionProgress");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == nextDay) {
            //if not at the end of the week.
            if (mp.getDay() <= 3) {
                mp.nextDay(this);
                Intent intent = new Intent(getBaseContext(), StockListActivity.class);
                intent.putExtra("MissionProgress", mp);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                mp.liquidate(this);
                finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newGame:
                mp.liquidate(this);
                Intent intent = new Intent(getBaseContext(), StockListActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.endWeek:
//                EndMissionFragment dialog = new EndMissionFragment();
//                mp.liquidate(this);
//                Bundle args = new Bundle();
//                args.putFloat("Earnings", mp.getMoney());
//                dialog.setArguments(args);
//                dialog.show(getFragmentManager(), "EndMissionFragment");

                mp.liquidate(this);
                intent = new Intent(getBaseContext(), EndMissionActivity.class);
                intent.putExtra("MissionProgress", mp);
                startActivity(intent);
                finish();
                return true;
//            case R.id.viewProfile:
//                intent = new Intent(getBaseContext(),ProfileActivity.class);
//                startActivity(intent);
//                mp.liquidate(this);
//                finish();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(android.app.DialogFragment dialog) {

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
//        mp.liquidate(this);
        finish();
    }
}
