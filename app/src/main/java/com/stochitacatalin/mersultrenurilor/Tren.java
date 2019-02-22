package com.stochitacatalin.mersultrenurilor;

public class Tren {
    private String numar;

    Tren(String numar) {
        this.numar = numar;
    }

    public String getNumar() {
        return numar;
    }

    @Override
    public String toString() {
        return numar;
    }
}
