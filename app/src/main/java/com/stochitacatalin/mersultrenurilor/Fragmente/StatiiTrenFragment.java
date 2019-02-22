package com.stochitacatalin.mersultrenurilor.Fragmente;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.Adaptoare.StatieTrenAdapter;
import com.stochitacatalin.mersultrenurilor.Dialogs.StatieGresitaDialog;
import com.stochitacatalin.mersultrenurilor.Dialogs.StatieLipsaDialog;
import com.stochitacatalin.mersultrenurilor.ElementTrasa;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Ruta;
import com.stochitacatalin.mersultrenurilor.TrenTrasa;
import com.stochitacatalin.mersultrenurilor.Utils;

import java.util.Objects;

public class StatiiTrenFragment extends Fragment {
    MainActivity activity;
    StatieTrenAdapter adapter;
    Ruta tren;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statii_tren, container, false);
        tren = (Ruta) Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).getSerializable("tren");
        ListView list = v.findViewById(R.id.list);
        activity = MainActivity.getActivity();
        TrenTrasa tt = null;
        int ch = -1;
        String asteptare = null,trennr = null;
        if(tren.getTrenuri().size() == 1)
            tt = activity.getmDBHelper().getTrenTrasa(tren.getTrenuri().get(0));
        else if(tren.getTrenuri().size() == 2) {
            TrenTrasa a = activity.getmDBHelper().getTrenTrasa(tren.getTrenuri().get(0));
            for(int i = a.getEtrasa().size() -1 ;i>=0;i--) {
                if (a.getEtrasa().get(i).getStatie().getId() == tren.getTrenuri().get(0).getTstatie().getId())
                    break;
                a.getEtrasa().remove(i);
            }
            ch = a.getEtrasa().size();
            TrenTrasa b = activity.getmDBHelper().getTrenTrasa(tren.getTrenuri().get(1));
            boolean bf = false;
            for(int i = b.getEtrasa().size() - 1;i>=0;i--){
                if(bf)
                    b.getEtrasa().remove(i);
                else if(b.getEtrasa().get(i).getStatie().getId() == tren.getTrenuri().get(0).getTstatie().getId())
                    bf = true;
            }
            for(ElementTrasa e : b.getEtrasa())
                a.getEtrasa().add(e);
            tt = a;
            asteptare = Utils.toTime(tren.getAsteptare().get(0),false);
            trennr = tren.getTrenuri().get(1).getCategorie() + " " + tren.getTrenuri().get(1).getTren();
        }
        assert tt != null;
        adapter = new StatieTrenAdapter(tt,getContext(),ch,"Următorul tren : " + trennr,"Timp Așteptare : "+asteptare);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getCount() - 1 == position){
                    new StatieLipsaDialog(getContext());
                }
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getCount() - 1 == position)
                    return false;
                else{
                    new StatieGresitaDialog(getContext(),
                            tren.getTrenuri().get(0).getTren(),
                            ((ElementTrasa)parent.getItemAtPosition(position)));
                    return true;
                }
            }
        });
        list.setAdapter(adapter);
        return v;
    }
}
