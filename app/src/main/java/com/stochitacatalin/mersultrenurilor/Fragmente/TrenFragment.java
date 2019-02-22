package com.stochitacatalin.mersultrenurilor.Fragmente;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.Adaptoare.CautaTrenuriAdapter;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Ruta;
import com.stochitacatalin.mersultrenurilor.TrenRuta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class TrenFragment extends Fragment {
    CardView dateCard;
    TextView choosedate;
    Calendar cd;
    MainActivity activity;
    AutoCompleteTextView autoText;
    StatiiTrenFragment statiiTrenFragment;
    QrCodeFragment qrCodeFragment;
    CalendarFragment calendarFragment;
    Fragment activeFragment;
    TrenRuta tren;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tren, container, false);
        choosedate = v.findViewById(R.id.choosedate);
        dateCard = v.findViewById(R.id.dateCard);
        dateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendarFragment==null)
                    calendarFragment = new CalendarFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.frametren,calendarFragment,CalendarFragment.class.getName()).commit();
                activeFragment = calendarFragment;
            }
        });
        cd = Calendar.getInstance();
        cd.set(Calendar.HOUR_OF_DAY,0);
        cd.set(Calendar.MINUTE,0);
        cd.set(Calendar.SECOND,0);
        setDate();
        activity = (MainActivity) getActivity();

        autoText = v.findViewById(R.id.autoTextOrigine);
        CautaTrenuriAdapter adapterOrigine = new CautaTrenuriAdapter(getContext(),activity.getmDBHelper());
        autoText.setAdapter(adapterOrigine);
        /*autoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoText.setText("");
            }
        });*/
        autoText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(((CautaTrenuriAdapter)autoText.getAdapter()).save()) {
                        autoText.setText(((CautaTrenuriAdapter) autoText.getAdapter()).getSelected().getNumar());
                        autoText.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(autoText.getWindowToken(), 0);
                        tren = activity.getmDBHelper().getTrenRuta(((CautaTrenuriAdapter) autoText.getAdapter()).getSelected().getNumar(),null,null);
                        Objects.requireNonNull(getActivity()).getIntent().putExtra("tren", new Ruta(tren,null,0));
                        statiiTrenFragment = new StatiiTrenFragment();
                        getChildFragmentManager().beginTransaction().replace(R.id.frametren,statiiTrenFragment,StatiiTrenFragment.class.getName()).commit();
                        activeFragment = statiiTrenFragment;
                    }
                    return true;
                }
                return false;
            }
        });
        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CautaTrenuriAdapter)autoText.getAdapter()).save(position);
                autoText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoText.getWindowToken(), 0);
                tren = activity.getmDBHelper().getTrenRuta(((CautaTrenuriAdapter) autoText.getAdapter()).getSelected().getNumar(),null,null);
                Objects.requireNonNull(getActivity()).getIntent().putExtra("tren", new Ruta(tren,null,0));
                statiiTrenFragment = new StatiiTrenFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.frametren,statiiTrenFragment,StatiiTrenFragment.class.getName()).commit();
                activeFragment = statiiTrenFragment;
            }
        });
        final CardView qrcode = v.findViewById(R.id.qrcode);
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                        new AlertDialog.Builder(activity)
                                .setCancelable(true)
                                .setMessage("Îmi trebuie permisiune la CAMERA pentru a putea scana biletul")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 0);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.CAMERA},
                                0);
                    }
                }
                else {
                    startqrCode();
                }
            }
        });
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startqrCode();
                } else {
                    Toast.makeText(getContext(),"Nu pot determina trenul fara camera",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void startqrCode(){
        if(qrCodeFragment == null)
            qrCodeFragment = new QrCodeFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.frametren,qrCodeFragment,QrCodeFragment.class.getName()).commit();
        activeFragment = qrCodeFragment;
    }

    public void setCalendar(Calendar c){
        cd = c;
        setDate();
        onBackPressed();
    }
    public void setDate(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy",Locale.getDefault());
        choosedate.setText(dateFormat.format(cd.getTime()));
    }
    public void setTrenRuta(TrenRuta tr){
        tren = tr;
        if(tr==null) {
            statiiTrenFragment = null;
            getChildFragmentManager().beginTransaction().replace(R.id.frametren, new Fragment(), null).commit();
        }else {
            Objects.requireNonNull(getActivity()).getIntent().putExtra("tren", new Ruta(tren, null, 0));
            statiiTrenFragment = new StatiiTrenFragment();
            getChildFragmentManager().beginTransaction().replace(R.id.frametren, statiiTrenFragment, StatiiTrenFragment.class.getName()).commit();
        }
        activeFragment = statiiTrenFragment;
    }
    public boolean onBackPressed(){
        if(activeFragment instanceof QrCodeFragment || activeFragment instanceof CalendarFragment){
            if(statiiTrenFragment!=null){
                getChildFragmentManager().beginTransaction().replace(R.id.frametren,statiiTrenFragment,StatiiTrenFragment.class.getName()).commit();
                activeFragment = statiiTrenFragment;
            }else{
                getChildFragmentManager().beginTransaction().remove(activeFragment).commit();
                activeFragment = null;
            }
            return false;
        }
        return true;
    }
}
