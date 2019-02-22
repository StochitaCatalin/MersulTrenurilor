package com.stochitacatalin.mersultrenurilor;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Statie implements Serializable {
    private String name;
    private int id;
    Statie(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
