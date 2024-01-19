package com.sandun.efoodsaverservice.model;

import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateDialog {
    FragmentManager fragmentManager;

    public DateDialog(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a Date")
                .setSelection(today)
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String formattedDate = sdf.format(selection);
            editText.setText(formattedDate);
        });

        datePicker.show(fragmentManager, datePicker.toString());
    }
}
