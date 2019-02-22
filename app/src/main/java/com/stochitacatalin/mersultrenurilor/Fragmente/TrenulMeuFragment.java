package com.stochitacatalin.mersultrenurilor.Fragmente;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Ruta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TrenulMeuFragment extends Fragment {

    Ruta tren;
    Calendar calendarDate;
    StatiiTrenFragment statiiTrenFragment;
    MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trenul_meu, container, false);
        activity = MainActivity.getActivity();
        tren = activity.tren;
        calendarDate = activity.calendarDate;
        if(tren!=null&&calendarDate!=null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD", Locale.getDefault());
            ((TextView) v.findViewById(R.id.date)).setText(simpleDateFormat.format(calendarDate.getTime()));
            String tnume = "";
            if (tren.getTrenuri().size() == 1)
                tnume = tren.getTrenuri().get(0).getCategorie() + " " + tren.getTrenuri().get(0).getTren();
            else if (tren.getTrenuri().size() == 2)
                tnume = tren.getTrenuri().get(0).getCategorie() + " " + tren.getTrenuri().get(0).getTren() + "-" + tren.getTrenuri().get(1).getCategorie() + " " + tren.getTrenuri().get(1).getTren();
            ((TextView) v.findViewById(R.id.numar)).setText(tnume);
            activity.getIntent().putExtra("tren", tren);
            statiiTrenFragment = new StatiiTrenFragment();
            getChildFragmentManager().beginTransaction().replace(R.id.frametren, statiiTrenFragment, StatiiTrenFragment.class.getName()).commit();
            final ImageView fav = v.findViewById(R.id.addfav);
            if (activity.favContainsTrain(tren) != -1) {
                fav.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            } else
                fav.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlueish)));
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity.favContainsTrain(tren) == -1) {
                        activity.trenurifav.add(tren);
                        activity.saveFav();
                        fav.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    } else {
                        activity.trenurifav.remove(activity.favContainsTrain(tren));
                        activity.saveFav();
                        fav.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlueish)));
                    }
                }
            });
            final Button deletetren = v.findViewById(R.id.stergetren);
            deletetren.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletetren();

                }
            });
        }
        return v;
    }
    void deletetren(){
        cleartren();
        activity.stopService();
        activity.onBackPressed();
    }

    void cleartren(){
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(activity.getFilesDir(), "trenulmeu")));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
