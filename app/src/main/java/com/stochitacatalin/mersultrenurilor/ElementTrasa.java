package com.stochitacatalin.mersultrenurilor;

public class ElementTrasa {
    private int oraSosire;
    private Statie statie;
    private int oraPlecare;
    private String tipOprire;
    private boolean pss;

    ElementTrasa(int oraSosire, Statie statie, int oraPlecare, String tipOprire, boolean pss) {
        this.oraSosire = oraSosire;
        this.statie = statie;
        this.oraPlecare = oraPlecare;
        this.tipOprire = tipOprire;
        this.pss = pss;
    }

    public int getOraSosire() {
        return oraSosire;
    }

    public Statie getStatie() {
        return statie;
    }

    public int getOraPlecare() {
        return oraPlecare;
    }

    public String getTipOprire() {
        return tipOprire;
    }

    public boolean isPss() {
        return pss;
    }
}
