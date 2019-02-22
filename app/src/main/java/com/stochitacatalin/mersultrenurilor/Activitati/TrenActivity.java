package com.stochitacatalin.mersultrenurilor.Activitati;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.stochitacatalin.mersultrenurilor.Fragmente.StatiiTrenFragment;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Ruta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;


public class TrenActivity extends AppCompatActivity {
    Ruta tren;
    Calendar calendarData;
    StatiiTrenFragment statiiTrenFragment;
    MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tren);
        activity = MainActivity.getActivity();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        tren = (Ruta) Objects.requireNonNull(getIntent().getExtras()).getSerializable("tren");
        getIntent().putExtra("tren", tren);
        calendarData = (Calendar) getIntent().getExtras().get("date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD", Locale.getDefault());
        ((TextView) findViewById(R.id.date)).setText(simpleDateFormat.format(calendarData.getTime()));
        String tnume = "";
        if(tren.getTrenuri().size() == 1)
            tnume = tren.getTrenuri().get(0).getCategorie() + " " +tren.getTrenuri().get(0).getTren();
        else if(tren.getTrenuri().size() == 2)
            tnume = tren.getTrenuri().get(0).getCategorie() + " " +tren.getTrenuri().get(0).getTren() + "-" + tren.getTrenuri().get(1).getCategorie() + " " +tren.getTrenuri().get(1).getTren();
        ((TextView) findViewById(R.id.numar)).setText(tnume);
        statiiTrenFragment = new StatiiTrenFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frametren,statiiTrenFragment,StatiiTrenFragment.class.getName()).commit();
        final ImageView fav = findViewById(R.id.addfav);
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
        final Button selecttren = findViewById(R.id.stergetren);
        selecttren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(TrenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(TrenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(TrenActivity.this)
                                .setCancelable(true)
                                .setMessage("Îmi trebuie permisiune la GPS ca să pot determina întârzierea")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(TrenActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(TrenActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                0);
                    }
                }
                else {
                    selecttren();
                }

            }
        });
        initAds();
    }

    public void initAds(){
        MobileAds.initialize(this, "ca-app-pub-8792771981687550~7721288175");
        AdView mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("528C77C051138A0F9853FD6725C500A1").build();
        //ad unit ca-app-pub-8792771981687550/4476355349
        //ad unit test ca-app-pub-3940256099942544/6300978111
        mAdView.loadAd(adRequest);
    }

    public void selecttren(){
        savetren();
        activity.refreshSelTren();
        finish();
    }
    void savetren(){
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir(), "trenulmeu")));
            os.writeObject(tren);
            os.writeObject(calendarData);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selecttren();
                } else {
                    Toast.makeText(TrenActivity.this,"Nu pot determina locația dacă nu am acces la GPS",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
