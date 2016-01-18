package com.vish.testapp;

import java.io.Serializable;

/**
 * Created by vish on 1/7/2016.
 */
public class Ingredient implements Serializable {
    private long id;
    private String name;

    public Ingredient() {

    }
    public Ingredient(String name) {
        this.name = name;
    }

    public void setID(long id) {
        this.id = id;
    }

    public long getID() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
