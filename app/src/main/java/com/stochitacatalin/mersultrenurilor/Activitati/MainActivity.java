package com.stochitacatalin.mersultrenurilor.Activitati;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.stochitacatalin.mersultrenurilor.DataBaseHelper;
import com.stochitacatalin.mersultrenurilor.Fragmente.FavoriteFragment;
import com.stochitacatalin.mersultrenurilor.Fragmente.TrenFragment;
import com.stochitacatalin.mersultrenurilor.Fragmente.TrenulMeuFragment;
import com.stochitacatalin.mersultrenurilor.GpsService;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Fragmente.SearchFragment;
import com.stochitacatalin.mersultrenurilor.Ruta;
import com.stochitacatalin.mersultrenurilor.TrenRuta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public ArrayList<Ruta> trenurifav;
    private DataBaseHelper mDBHelper;
    private static MainActivity activity;
    private NavigationView navigationView;
    public static MainActivity getActivity(){
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initFragments();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadtrain();
        if(tren != null && tren.getTrenuri()!= null && tren.getTrenuri().size() != 0 && calendarDate != null)
            startService(tren.getTrenuri().get(0),calendarDate);
        //Daca pornim activitatea din notificare trebuie sa deschidem fragmentul TrenulMeu
        String b = getIntent().getStringExtra("fragment");
        if(b != null && b.equals(TrenulMeuFragment.class.getName())) {
            onNavigationItemSelected(navigationView.getMenu().getItem(3));
            navigationView.getMenu().getItem(3).setChecked(true);
        }
        else {
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        loadFav();
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

    public Fragment getActiveFragment(){
        return activeFragment;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(activeFragment instanceof  TrenFragment){
                if(((TrenFragment) activeFragment).onBackPressed()){
                    onNavigationItemSelected(navigationView.getMenu().getItem(0));
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
            else if(activeFragment instanceof SearchFragment){
                if(((SearchFragment)activeFragment).onBackPressed())
                    super.onBackPressed();
            }
            else if(activeFragment instanceof TrenulMeuFragment){
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
                navigationView.getMenu().getItem(0).setChecked(true);
                loadtrain();
                getSupportFragmentManager().beginTransaction().remove(trenulMeuFragment).commit();
                trenulMeuFragment = new TrenulMeuFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.frametren,trenulMeuFragment,TrenulMeuFragment.class.getName()).hide(trenulMeuFragment).commit();
            }
            else if(activeFragment == null){
                super.onBackPressed();
            }
            else{
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private SearchFragment searchFragment;
    private FavoriteFragment favoriteFragment;
    private TrenFragment trenFragment;
    private TrenulMeuFragment trenulMeuFragment;
    private Fragment activeFragment;
    void initFragments(){
        searchFragment = new SearchFragment();
        favoriteFragment = new FavoriteFragment();
        trenFragment = new TrenFragment();
        trenulMeuFragment = new TrenulMeuFragment();
        activeFragment = searchFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.frametren,trenulMeuFragment,TrenulMeuFragment.class.getName()).hide(trenulMeuFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frametren, trenFragment, TrenFragment.class.getName()).hide(trenFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frametren, favoriteFragment, FavoriteFragment.class.getName()).hide(favoriteFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frametren, searchFragment, SearchFragment.class.getName()).commit();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_search) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(searchFragment).commit();
            activeFragment = searchFragment;
        } else if (id == R.id.nav_fav) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(favoriteFragment).commit();
            activeFragment = favoriteFragment;
        } else if (id == R.id.nav_tren) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(trenFragment).commit();
            activeFragment = trenFragment;
        } else if (id == R.id.nav_trenul_meu){
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(trenulMeuFragment).commit();
            activeFragment = trenulMeuFragment;
        } else if (id == R.id.nav_paypal) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/catalinstochita"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public DataBaseHelper getmDBHelper(){
        if(mDBHelper == null){
            mDBHelper = new DataBaseHelper(this);
        }
        return mDBHelper;
    }
    public void loadFav(){
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File(getFilesDir(), "trenurifav")));
            ArrayList<?> list = (ArrayList<?>) os.readObject();
            if(trenurifav==null)
                trenurifav = new ArrayList<>();
            else
                trenurifav.clear();

            for(int i = 0;i<list.size();i++)
                if(list.get(i) instanceof Ruta)
                    trenurifav.add((Ruta) list.get(i));
            os.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(trenurifav == null)
            trenurifav = new ArrayList<>();
    }

    public void saveFav(){
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir(), "trenurifav")));
            os.writeObject(trenurifav);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(favoriteFragment!=null)
            favoriteFragment.refresh();
    }

    public int favContainsTrain(Ruta t){
        if(t==null)
            return -1;
        for(int i = 0;i<trenurifav.size();i++){
            if(trenurifav.get(i) != null && trenurifav.get(i).getTrenuri().size() == t.getTrenuri().size()){
                boolean ex = false;
                for(int j = 0;j<trenurifav.get(i).getTrenuri().size();j++){
                    if(trenurifav.get(i).getTrenuri().get(j).getTren().compareTo(t.getTrenuri().get(j).getTren())==0){
                        ex = true;
                    }
                    if(!ex)
                        break;
                }
                if(ex)
                    return i;
            }
        }
        return -1;
    }

    public void startService(TrenRuta tr, Calendar cd){

        // if(!isMyServiceRunning(GpsService.class)) {
        Intent intent = new Intent(this, GpsService.class);
        intent.setAction(GpsService.ACTION_START_FOREGROUND_SERVICE);
        intent.putExtra("tren", tr);
        intent.putExtra("date", cd);
        startService(intent);

        //}
    }

    public void stopService(){
        Intent intent = new Intent(this, GpsService.class);
        intent.setAction(GpsService.ACTION_STOP_FOREGROUND_SERVICE);
        startService(intent);
    }

    public Ruta tren;
    public Calendar calendarDate;

    void loadtrain() {
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File(getFilesDir(), "trenulmeu")));
            tren = (Ruta) os.readObject();
            calendarDate = (Calendar) os.readObject();
            os.close();
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            tren = null;
            calendarDate = null;
        }
    }

    public void refreshSelTren(){
        loadtrain();
        if(tren != null && calendarDate != null) {
            startService(tren.getTrenuri().get(0), calendarDate);
            getSupportFragmentManager().beginTransaction().remove(trenulMeuFragment).commit();
            trenulMeuFragment = new TrenulMeuFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frametren,trenulMeuFragment,TrenulMeuFragment.class.getName()).hide(trenulMeuFragment).commit();
            if(activeFragment instanceof TrenulMeuFragment) {
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(trenulMeuFragment).commit();
                activeFragment = trenulMeuFragment;
            }
        }
       // else
         //   stopService();

        /**
         * TODO start service
         */
    }
}
