package com.peck.android.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.peck.android.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by mammothbane on 7/31/2014.
 */
public class DateSelector extends Fragment {
    DateTime time = DateTime.now();
    Button btTime;
    Button btDate;

    DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MMMM d YYYY");

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_date_selector, container, false);
        btTime = ((Button) v.findViewById(R.id.bt_time));
        btDate = ((Button) v.findViewById(R.id.bt_date));
        btTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        time = time.withHourOfDay(hour).withMinuteOfHour(minute);
                        update();
                    }
                }, time.getHourOfDay(), time.getMinuteOfHour(), false).show();
            }
        });

        btDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker picker, int year, int month, int day) {
                        time = time.withYear(year).withMonthOfYear(month).withDayOfMonth(day);
                    }
                }, time.getYear(), time.getMonthOfYear(), time.getDayOfMonth()).show();
            }
        });

        update();
        return v;
    }

    public void with(DateTime time) {
        this.time = time;
        if (getView() != null) update();
    }

    private void update() {
        btTime.setText(time.toString("h:mm a"));
        btDate.setText(dateFormat.withLocale(Locale.getDefault()).print(time));
    }

    public DateTime getDate() {
        return time;
    }
}
