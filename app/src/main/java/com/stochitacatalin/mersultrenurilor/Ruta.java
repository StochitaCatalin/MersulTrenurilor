package com.stochitacatalin.mersultrenurilor;

import java.io.Serializable;
import java.util.ArrayList;

public class Ruta implements Serializable {
    ArrayList<TrenRuta> trenuri;
    ArrayList<Integer> asteptare;
    public Ruta(TrenRuta origine, TrenRuta destinatie, int asteptare){
        trenuri = new ArrayList<>();
        this.asteptare = new ArrayList<>();
        if(origine != null)
            trenuri.add(origine);
        if(destinatie != null) {
            trenuri.add(destinatie);
            this.asteptare.add(asteptare);
        }
    }

    public ArrayList<TrenRuta> getTrenuri() {
        return trenuri;
    }

    public ArrayList<Integer> getAsteptare() {
        return asteptare;
    }
}
