package edu.usna.mobileos.stockhero;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m164488 on 4/12/2016.
 */


public class StockListActivity {

    public ListView stockListView;
    public ArrayAdapter<String> adapter;
    public List<String> rssItemList = new ArrayList<String>();

    stockListView = (ListView) findViewById(R.id.stockListView);
    adapter = new RssItemAdapter(this, android.R.layout.simple_list_item_1, rssItemList);
    newsListView.setAdapter(adapter);
    newsListView.setOnItemClickListener(this);

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        // actions to execute to do when an item is clicked

        RssItem RssItemClicked = adapter.getItem(pos);
        Toast.makeText(getBaseContext(), RssItemClicked.getPubDate(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(), RssItemClicked.getLink(), Toast.LENGTH_SHORT).show();

    }
}
