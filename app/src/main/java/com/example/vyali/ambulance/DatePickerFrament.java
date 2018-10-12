package com.example.vyali.ambulance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by vyali on 15/4/18.
 */

public class DatePickerFrament extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
       ((BedAdapter.HospitalViewHolder)getArguments().getSerializable("holder")).bookingDate.setText(dayOfMonth+"/"
               +month+"/"+year);
        ((BedAdapter.HospitalViewHolder)getArguments().getSerializable("holder")).bookbed.setEnabled(true);
        ((BedAdapter.HospitalViewHolder)getArguments().getSerializable("holder")).bookbed.setBackgroundColor(
                this.getResources().getColor(R.color.colorPrimary)
        );

    }
}
