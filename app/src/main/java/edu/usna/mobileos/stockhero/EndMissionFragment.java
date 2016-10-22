package edu.usna.mobileos.stockhero;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;


/**
 *
 */
public class EndMissionFragment extends DialogFragment implements DialogInterface.OnClickListener{
//    private OnFragmentInteractionListener mListener;
    Activity mActivity;
    EndMissionListener mCallback;

    public interface EndMissionListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        Log.i("end", "4");
        Bundle bundle = getArguments();
//        mp = bundle.getParcelable("MissionProgress");
        float earnings = bundle.getFloat("Earnings");
        Log.i("end", "5");
        Log.i("end", String.valueOf(earnings));
        String formattedEarnings = String.format("%.02f", earnings);
        Log.i("end", "6");
        View layout = inflater.inflate(R.layout.fragment_end_mission, null);

        builder.setView(layout)
                .setTitle("Week Ended")
                .setMessage("You posted "+formattedEarnings+"% earnings this week.")
                .setPositiveButton("OK", this)
                .setCancelable(false);

        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_end_mission, container, false);
    }

    public void onClick(DialogInterface dialog, int id) {
        String buttonName = "";
        switch (id) {
            case Dialog.BUTTON_POSITIVE:
//                mp.liquidate(stockListActivity.this);
//                Intent intent = new Intent(mActivity,MainActivity.class);
//                startActivity(intent);
                mCallback.onDialogPositiveClick(this);
                break;
        }
        Log.d("PEPIN", "You clicked the " + buttonName + " button");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (EndMissionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


}
