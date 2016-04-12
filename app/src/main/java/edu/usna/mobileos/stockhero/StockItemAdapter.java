package edu.usna.mobileos.stockhero;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by m164488 on 4/12/2016.
 */
public class StockItemAdapter extends ArrayAdapter<String> {

    private Context context;

    public StockItemAdapter(Context context, int textViewResourceId,
                            List<String> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.stock_list, null);
        }

        String item = getItem(position);

        return view;
    }
}