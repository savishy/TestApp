package com.vish.testapp;

/**
 * Created by vish on 1/7/2016.
 */
public class Ingredient {
    private int id;
    private String name;

    public Ingredient() {

    }
    public Ingredient(String name) {
        this.name = name;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
