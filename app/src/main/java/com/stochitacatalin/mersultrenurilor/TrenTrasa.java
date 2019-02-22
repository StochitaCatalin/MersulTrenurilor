package com.stochitacatalin.mersultrenurilor;

import java.util.ArrayList;

public class TrenTrasa {
    private TrenRuta ruta;
    private ArrayList<ElementTrasa> etrasa;

    TrenTrasa(TrenRuta ruta, ArrayList<ElementTrasa> etrasa) {
        this.ruta = ruta;
        this.etrasa = etrasa;
    }
    public int getETSize(){
        return etrasa.size();
    }
    public ElementTrasa getETrasa(int index){
        return etrasa.get(index);
    }

    public ArrayList<ElementTrasa> getEtrasa() {
        return etrasa;
    }
}
