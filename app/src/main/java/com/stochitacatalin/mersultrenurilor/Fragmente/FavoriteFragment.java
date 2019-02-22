package com.stochitacatalin.mersultrenurilor.Fragmente;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.Activitati.TrenActivity;
import com.stochitacatalin.mersultrenurilor.Adaptoare.ResultTrainAdapter;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Ruta;

import java.util.ArrayList;
import java.util.Calendar;

public class FavoriteFragment extends Fragment {
    ResultTrainAdapter adapter;
    ArrayList<Ruta> trenuri;
    MainActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourite, container, false);
        ListView trainlist = v.findViewById(R.id.list);
        activity = (MainActivity) getActivity();
        fill();
        adapter = new ResultTrainAdapter(trenuri,getContext(),false);
        trainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ruta item = (Ruta) parent.getItemAtPosition(position);
                Intent i = new Intent(getContext(), TrenActivity.class);
                i.putExtra("tren", item);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY,0);
                c.set(Calendar.MINUTE,0);
                c.set(Calendar.SECOND,0);
                i.putExtra("date",c);
                startActivity(i);
            }
        });
        trainlist.setAdapter(adapter);
        return v;
    }

    private void fill() {
        ArrayList<Ruta> tr = activity.trenurifav;
        if(tr == null)
            trenuri = new ArrayList<>();
        else {
            trenuri = tr;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh(){
        fill();
        adapter.notifyDataSetChanged();
    }
}
