package com.stochitacatalin.mersultrenurilor;

public class Intarziere {
    private int id;
    private String tren,lat,lon,time;
    private int acuracy;

    Intarziere(int id, String tren, String lat, String lon, String time, int acuracy) {
        this.id = id;
        this.tren = tren;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.acuracy = acuracy;
    }

    public int getId() {
        return id;
    }

    public String getTren() {
        return tren;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getTime() {
        return time;
    }

    public int getAcuracy() {
        return acuracy;
    }
}
