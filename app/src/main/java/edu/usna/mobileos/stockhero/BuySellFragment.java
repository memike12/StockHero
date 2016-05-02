package edu.usna.mobileos.stockhero;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;



public class BuySellFragment extends DialogFragment implements DialogInterface.OnClickListener {
    EditText editText;
    OnStockSelectedListener mCallback;
    String stock;
    float price;
    String action;
    int hint;

    public interface OnStockSelectedListener{
        void onStockSelected(String stock, float price, int order, String action);
        void onDialogDismissListener(int pos);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnStockSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        Bundle bundle = getArguments();
        price = bundle.getFloat("price");
        stock = bundle.getString("ticker");
        action = bundle.getString("action");
        hint = bundle.getInt("hint");
        Log.i("Hint", String.valueOf(hint));
        View layout = inflater.inflate(R.layout.buy_sell_dialog, null);
        editText = (EditText) layout.findViewById(R.id.numPicker);
        if(hint > 0) {
            editText.setText(String.valueOf(hint));
        }
        editText.setSelection(editText.getText().length());
        builder.setView(layout)
            .setTitle(action+" "+stock)
            .setMessage("Price: "+String.valueOf(price))
            .setPositiveButton(action, this)
            .setNegativeButton("Cancel", this);

        return builder.create();
    }

    public void onClick(DialogInterface dialog, int id) {
        if(id == Dialog.BUTTON_POSITIVE){
            //Log.i("purchased", editText.getText().toString());
            mCallback.onStockSelected(stock, price,Integer.valueOf(editText.getText().toString()),action);
            mCallback.onDialogDismissListener(10);
        }
    }
}
