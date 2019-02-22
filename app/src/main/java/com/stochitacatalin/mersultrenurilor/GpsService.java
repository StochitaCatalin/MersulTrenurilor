package com.stochitacatalin.mersultrenurilor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.Fragmente.TrenulMeuFragment;
import com.stochitacatalin.mersultrenurilor.Retrofit.ResponseData;
import com.stochitacatalin.mersultrenurilor.Retrofit.RetrofitClient;
import com.stochitacatalin.mersultrenurilor.Retrofit.UpdateTrainApi;
//import com.stochitacatalin.mersultrenurilor.Activities.MainActivity;
//import com.stochitacatalin.mersultrenurilor.Http.ResponseData;
//import com.stochitacatalin.mersultrenurilor.Http.RetrofitClient;
//import com.stochitacatalin.mersultrenurilor.Http.UpdateTrainApi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

public class GpsService extends Service {
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    private static final int NOTIFICATION_ID = 211;
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    String NOTIFICATION_CHANNEL_ID = "GpsMersulTrenurilorChannel";


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    TrenRuta trenRuta;
    Calendar calendarDate;
    Calendar sosire,plecare;
    private boolean mRunning;

    @SuppressLint("MissingPermission")
    public void updateLocationTrain() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                //final TrenRuta trenRuta = fragmentMy.getTren();
                if(trenRuta == null){
                    removeLocation();
                    return;
                }
                for (final Location location : locationResult.getLocations()) {
                    location.getTime();
                    final Date date = new Date(location.getTime());
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    System.out.println("Time:"+simpleDateFormat.format(date));
                    System.out.println(location);
                    //contentView.setTextViewText(R.id.lat,"Lat:"+String.valueOf(location.getLatitude()));
                    //contentView.setTextViewText(R.id.lon,"Lon:"+String.valueOf(location.getLongitude()));
                    updateTextOnNot();
                    RetrofitClient.getClient("https://stochitacatalin.com/mersultrenurilor/").create(UpdateTrainApi.class)
                            .update(trenRuta.getTren(),String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),simpleDateFormat.format(date), (int) location.getAccuracy())
                            .enqueue(new Callback<ResponseData>() {
                                @Override
                                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                    System.out.println(response.body().isStatus());
                                    if(getmDBHelper().getIntarzieriSize() > 0)
                                        sendIntarzieri();
                                }

                                @Override
                                public void onFailure(Call<ResponseData> call, Throwable t) {
                                    System.out.println("FAILURE");
                                    getmDBHelper().insertIntarziere(trenRuta.getTren(),String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),simpleDateFormat.format(date), (int) location.getAccuracy());
                                    System.out.println("Numar intarzieri memorate:"+getmDBHelper().getIntarzieriSize());
                                }
                            });
                    // Update UI with location data
                    // ...
                }
                if(needtosend() == -1)
                    stopForegroundService();
            };
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(300000); // two minute interval or half a minute
        mLocationRequest.setFastestInterval(300000); // half a minute
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }
    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }
    public void removeLocation(){
         mFusedLocationClient.removeLocationUpdates(mLocationCallback);
         stopForegroundService();
    }

    public void sendIntarzieri(){
        final Intarziere intarziere = getmDBHelper().getIntarziere();
        if(intarziere!=null) {
            RetrofitClient.getClient("https://stochitacatalin.com/mersultrenurilor/").create(UpdateTrainApi.class)
                    .update(intarziere.getTren(), intarziere.getLat(),intarziere.getLon(), intarziere.getTime(), intarziere.getAcuracy())
                    .enqueue(new Callback<ResponseData>() {
                        @Override
                        public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                            System.out.println(response.body().isStatus());
                            getmDBHelper().removeIntarziere(intarziere.getId());
                            if (getmDBHelper().getIntarzieriSize() > 0)
                                sendIntarzieri();
                        }

                        @Override
                        public void onFailure(Call<ResponseData> call, Throwable t) {
                            //TRY in 5 minute
                        }
                    });
        }
    }

    private DataBaseHelper mDBHelper;

    public DataBaseHelper getmDBHelper(){
        if(mDBHelper == null){
            mDBHelper = new DataBaseHelper(this);
            try{
                mDBHelper.prepareDataBase();
            }catch (IOException io){
                throw new Error("Unable to create DB");}
        }
        return mDBHelper;
    }

    @Override
    public void onCreate() {
        // The service is being created
        mRunning = false;

    }

    Handler mHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()

        if(intent != null)
        {
            String action = intent.getAction();
            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    if(!mRunning) {
                        mHandler = new Handler(Looper.getMainLooper());
                        trenRuta = (TrenRuta) intent.getSerializableExtra("tren");
                        calendarDate = (Calendar) intent.getSerializableExtra("date");
                        startForegroundService();
                        initCSP();
                        scheduleNext();
                        Toast.makeText(getApplicationContext(), "Te anuntam cat timp intarzie trenul.", Toast.LENGTH_LONG).show();
                        updateLocationTrain();
                        mRunning = true;
                    }
                    else{
                        trenRuta = (TrenRuta) intent.getSerializableExtra("tren");
                        calendarDate = (Calendar) intent.getSerializableExtra("date");
                        initCSP();
                        startForegroundService();
                        scheduleNext();
                    }
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }
    Notification notification;
    RemoteViews contentView;
    public void startForegroundService(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("fragment", TrenulMeuFragment.class.getName()); //Your id
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setTextViewText(R.id.numar, trenRuta.getTren());
        contentView.setTextViewText(R.id.statieplecare, trenRuta.getFstatie().getName());
        contentView.setTextViewText(R.id.statiesosire, trenRuta.getTstatie().getName());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "GPS_MERSUL_TRENURILOR", NotificationManager.IMPORTANCE_NONE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                   // .setContentTitle("Gps Mersul Trenurilor")
                    //.setContentText("Locatie tren pentru intarzieri")
                    .setContent(contentView)
                    .setSmallIcon(R.drawable.ic_train)
                    .setContentIntent(pendingIntent)
                    //  .setTicker(getText(R.string.ticker_text))
                    .build();
            startForeground(NOTIFICATION_ID, notification);
        }else{
             notification  = new Notification.Builder(this)
                  //  .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentTitle(trenRuta.getTren())
                    .setContentText(trenRuta.getFstatie().getName()+"->"+trenRuta.getTstatie().getName())
                  //  .setContent(contentView)
                    .setStyle(new Notification.InboxStyle()
                            .addLine(trenRuta.getFstatie().getName())
                            .addLine(trenRuta.getTstatie().getName()))
                    .setSmallIcon(R.drawable.ic_train)
                   // .setContentIntent(pendingIntent)
                   // .setAutoCancel(true)
                   // .setVisibility(visibility)
                    .build();
            startForeground(NOTIFICATION_ID,notification);
            //notificationManager.notify(NOTIFICATION_ID, notification );
        }

    }

    public void updateTextOnNot(){
        startForeground(NOTIFICATION_ID,notification);
    }

    private void stopForegroundService()
    {
       // Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    void initCSP(){
        sosire = (Calendar) calendarDate.clone();
        plecare = (Calendar) sosire.clone();
        int plect = trenRuta.getForaPlecare()%(86400);
        plecare.set(Calendar.HOUR_OF_DAY,plect/3600);
        plecare.set(Calendar.MINUTE,(plect%3600)/60);
        plecare.set(Calendar.SECOND,0);

        int sosirt = trenRuta.getToraSosire()%(86400);
        int zdf = trenRuta.getToraSosire()/86400 - trenRuta.getForaPlecare()/86400;
        sosire.add(Calendar.DATE, zdf);
        sosire.set(Calendar.HOUR_OF_DAY,sosirt/3600);
        sosire.set(Calendar.MINUTE,(sosirt%3600)/60);
        sosire.set(Calendar.SECOND,0);
    }

    int needtosend(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
       // System.out.println("SOSIRE:"+simpleDateFormat.format(sosire.getTime()));
       // System.out.println("NOW"+simpleDateFormat.format(now.getTime()));
       // System.out.println("PLECARE"+simpleDateFormat.format(plecare.getTime()));
        //Not yet return 0
        contentView.setTextViewText(R.id.lastupdate,"Actualizat(Local):"+simpleDateFormat.format(now.getTime()));
        updateTextOnNot();
        System.out.println("Actualizat:"+simpleDateFormat.format(now.getTime()));
        if(now.before(plecare))
            return 0;
        //Is over return -1
        if(now.after(sosire))
            return -1;
        //Need to send return 1
        return 1;
    }

    private void ping() {
        int resp = needtosend();
        System.out.println("Status of GPS SERVICE : "+resp);
        switch (resp) {
            case 0:
                scheduleNext();
                break;
            case 1:
                startLocationUpdates();
                break;
            case -1:
                stopForegroundService();
                break;
        }
    }

    Runnable runCheck = new Runnable() {
        @Override
        public void run() {
            ping();
        }
    };

    private void scheduleNext() {
        //mHandler.postDelayed(runCheck, 60000 * 10);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(runCheck, 1000 * 60 * 5);
    }
}
