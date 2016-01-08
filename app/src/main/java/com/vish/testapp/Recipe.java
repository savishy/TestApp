package com.vish.testapp;

/**
 * Created by vish on 1/7/2016.
 */
public class Recipe {
    private int id;
    private String recipeName;
    private Ingredient ingredients;

    public Recipe() {

    }
    public Recipe(String recipeName, Ingredient ingredients) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return this.recipeName;
    }

    public void setIngredients(Ingredient ingredients) {
        this.ingredients = ingredients;
    }

    public Ingredient getIngredients() {
        return this.ingredients;
    }
}
