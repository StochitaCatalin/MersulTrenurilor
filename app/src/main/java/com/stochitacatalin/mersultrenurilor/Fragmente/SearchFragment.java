package com.stochitacatalin.mersultrenurilor.Fragmente;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.Activitati.TrenActivity;
import com.stochitacatalin.mersultrenurilor.Adaptoare.ResultTrainAdapter;
import com.stochitacatalin.mersultrenurilor.Adaptoare.SearchStatiiAdapter;
import com.stochitacatalin.mersultrenurilor.Dialogs.TrenGresitDialog;
import com.stochitacatalin.mersultrenurilor.Dialogs.TrenLipsaDialog;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Ruta;
import com.stochitacatalin.mersultrenurilor.Statie;
import com.stochitacatalin.mersultrenurilor.TrenRuta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class SearchFragment extends Fragment {
    TextView choosedate;
    Calendar cd;
    CardView dateCard;
    MainActivity activity;
    AutoCompleteTextView autoTextOrigine,autoTextDestinatie;
    private ResultTrainAdapter adapter;
    ArrayList<Ruta> trenuri;
    CalendarFragment calendarFragment;
    FrameLayout frame;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        choosedate = v.findViewById(R.id.choosedate);
        frame = v.findViewById(R.id.framecalendar);
        dateCard = v.findViewById(R.id.dateCard);
        dateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendarFragment==null)
                    calendarFragment = new CalendarFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.framecalendar,calendarFragment,CalendarFragment.class.getName()).commit();
                frame.setVisibility(View.VISIBLE);
                autoTextDestinatie.clearFocus();
                autoTextOrigine.clearFocus();
            }
        });
        cd = Calendar.getInstance();
        cd.set(Calendar.HOUR_OF_DAY,0);
        cd.set(Calendar.MINUTE,0);
        cd.set(Calendar.SECOND,0);
        setDate();
        activity = (MainActivity) getActivity();

        autoTextOrigine = v.findViewById(R.id.autoTextOrigine);
        SearchStatiiAdapter adapterOrigine = new SearchStatiiAdapter(getContext(),activity.getmDBHelper());
        autoTextOrigine.setAdapter(adapterOrigine);
        /*autoTextOrigine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoTextOrigine.setText("");
            }
        });*/
        autoTextOrigine.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(((SearchStatiiAdapter)autoTextOrigine.getAdapter()).save()) {
                        autoTextOrigine.setText(((SearchStatiiAdapter)autoTextOrigine.getAdapter()).getSelected().getName());
                        autoTextDestinatie.requestFocus();
                        checkSelectedStatii();
                    }
                    return true;
                }
                return false;
            }
        });
        autoTextOrigine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SearchStatiiAdapter)autoTextOrigine.getAdapter()).save(position);
                autoTextDestinatie.requestFocus();
                checkSelectedStatii();
            }
        });
        autoTextDestinatie = v.findViewById(R.id.autoTextDestinatie);
        SearchStatiiAdapter adapterDestinatie = new SearchStatiiAdapter(getContext(),activity.getmDBHelper());
        autoTextDestinatie.setAdapter(adapterDestinatie);
        /*autoTextDestinatie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoTextDestinatie.setText("");
            }
        });*/
        autoTextDestinatie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(((SearchStatiiAdapter)autoTextDestinatie.getAdapter()).save()) {
                        autoTextDestinatie.setText(((SearchStatiiAdapter) autoTextDestinatie.getAdapter()).getSelected().getName());
                        autoTextDestinatie.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(autoTextDestinatie.getWindowToken(), 0);
                        checkSelectedStatii();
                    }
                    return true;
                }
                return false;
            }
        });
        autoTextDestinatie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SearchStatiiAdapter)autoTextDestinatie.getAdapter()).save(position);
                autoTextDestinatie.clearFocus();
                InputMethodManager imm =  (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoTextDestinatie.getWindowToken(), 0);
                checkSelectedStatii();
            }
        });

        ListView trainlist = v.findViewById(R.id.list);
        trenuri = new ArrayList<>();
        adapter = new ResultTrainAdapter(trenuri,getContext(),true);
        trainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getCount() - 1 == position){
                    Log.d("Dialog","REPORT");
                    new TrenLipsaDialog(getContext());
                }else {
                    Ruta tr = (Ruta) parent.getItemAtPosition(position);
                    Intent i = new Intent(getContext(), TrenActivity.class);
                    i.putExtra("tren", tr);
                    i.putExtra("date", cd);
                    startActivity(i);
                }
            }
        });
        trainlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getCount() - 1 == position)
                    return false;
                else{
                    new TrenGresitDialog(getContext(),
                            ((Ruta)parent.getItemAtPosition(position)).getTrenuri().get(0).getTren(),
                            ((SearchStatiiAdapter)autoTextOrigine.getAdapter()).getSelected().getName(),
                            ((SearchStatiiAdapter)autoTextDestinatie.getAdapter()).getSelected().getName());
                }
                return true;
            }
        });
        trainlist.setAdapter(adapter);
        return v;
    }

    public void checkSelectedStatii(){
        try {
            Statie a = ((SearchStatiiAdapter) autoTextOrigine.getAdapter()).getSelected();
            Statie b = ((SearchStatiiAdapter) autoTextDestinatie.getAdapter()).getSelected();
            if (a != null && b != null && a.getId() != b.getId()) {
                cautaTrenuri(a, b);
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){}
    }

    void cautaTrenuri(Statie from,Statie to){
        ArrayList<TrenRuta> list = activity.getmDBHelper().getTrenFromTo(from,to);
        System.out.println(list.size());
        trenuri.clear();
        for(TrenRuta tr : list){
            int orp = tr.getForaPlecare()%86400;
            int oa = cd.get(Calendar.HOUR_OF_DAY)*60*60 +
                    cd.get(Calendar.MINUTE)*60 +
                    cd.get(Calendar.SECOND);
            if(oa <= orp) {
                Calendar startcalendar = (Calendar) cd.clone();
                startcalendar.add(Calendar.DATE, -(tr.getForaPlecare() / 86400));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                System.out.println("DOS:"+simpleDateFormat.format(startcalendar.getTime()));
                if(activity.getmDBHelper().workingTrain(tr.getTren(),simpleDateFormat.format(startcalendar.getTime())))
                    trenuri.add(new Ruta(tr,null,0));
            }
        }

       /* ArrayList<Ruta> list2 = activity.getmDBHelper().getTren2FromTo(from,to);
        System.out.println("LISTA TRENURI CU LEGATURA:"+ list2.size());
        for(int i = 0;i<list2.size();i++){
            trenuri.add(list2.get(i));
          //  System.out.println(list2.get(i).getOrigine().getTren()+" : " + list2.get(i).getAsteptare() +" : " + list2.get(i).getDestinatie().getTren());
        }*/
        /*
        TODO FILTER
         */
        Collections.sort(trenuri, new Comparator<Ruta>() {
            @Override
            public int compare(Ruta o1, Ruta o2) {
                return o1.getTrenuri().get(0).getForaSosire()%86400 - o2.getTrenuri().get(0).getForaSosire()%86400;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3){
            if(resultCode == Activity.RESULT_OK){
                cd = (Calendar) data.getSerializableExtra("result");
                setDate();
            }
        }
    }

    public void setCalendar(Calendar c){
        cd = c;
        setDate();
        onBackPressed();
        //refresh
    }

    public void setDate(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy",Locale.getDefault());
        choosedate.setText(dateFormat.format(cd.getTime()));
        checkSelectedStatii();
    }
    public boolean onBackPressed(){
            if(calendarFragment!=null){
                getChildFragmentManager().beginTransaction().remove(calendarFragment).commit();
                calendarFragment = null;
                frame.setVisibility(View.GONE);
                return false;
            }
        return true;
    }
}
