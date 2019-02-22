package com.stochitacatalin.mersultrenurilor;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class TrenRuta implements Serializable {
    private String tren;
    private int foraSosire;
    private Statie fstatie;
    private int foraPlecare;
    private String ftipOprire;
    private int toraSosire;
    private Statie tstatie;
    private int toraPlecare;
    private String ttipOprire;
    private String categorie;
    private int[] servicii;

    TrenRuta(String categorie, String servicii, String tren, int foraSosire, Statie fstatie, int foraPlecare, String ftipOprire, int toraSosire, Statie tstatie, int toraPlecare, String ttipOprire) {
        this.tren = tren;
        this.categorie = categorie;
        this.foraSosire = foraSosire;
        this.fstatie = fstatie;
        this.foraPlecare = foraPlecare;
        this.ftipOprire = ftipOprire;
        this.toraSosire = toraSosire;
        this.tstatie = tstatie;
        this.toraPlecare = toraPlecare;
        this.ttipOprire = ttipOprire;
        try {
            JSONArray array = new JSONArray(servicii);
            this.servicii = new int[array.length()];
            for(int i = 0;i<array.length();i++)
                this.servicii[i] = array.getInt(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCategorie() {
        return categorie;
    }

    public int[] getServicii() {
        return servicii;
    }

    public String getTren() {
        return tren;
    }

    public int getForaSosire() {
        return foraSosire;
    }

    Statie getFstatie() {
        return fstatie;
    }

    public int getForaPlecare() {
        return foraPlecare;
    }

    public String getFtipOprire() {
        return ftipOprire;
    }

    public int getToraSosire() {
        return toraSosire;
    }

    public Statie getTstatie() {
        return tstatie;
    }

    public int getToraPlecare() {
        return toraPlecare;
    }

    public String getTtipOprire() {
        return ttipOprire;
    }
}
