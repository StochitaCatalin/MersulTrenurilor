package com.stochitacatalin.mersultrenurilor.Fragmente;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    MainActivity activity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = MainActivity.getActivity();
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        Button save = v.findViewById(R.id.save);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        final CalendarView c = v.findViewById(R.id.calendarView);
        final TabLayout tabLayout = v.findViewById(R.id.tabs);
        c.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cs = new GregorianCalendar( year, month, dayOfMonth );
                calendar.setTime(cs.getTime());
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY,tabLayout.getSelectedTabPosition());
                Fragment fragment = activity.getActiveFragment();
                if(fragment instanceof SearchFragment){
                    ((SearchFragment)fragment).setCalendar(calendar);
                }else if(fragment instanceof TrenFragment){
                    ((TrenFragment)fragment).setCalendar(calendar);
                }
                DateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
                System.out.println((dateFormat.format(calendar.getTime())));
            }
        });
        return v;
    }
}
