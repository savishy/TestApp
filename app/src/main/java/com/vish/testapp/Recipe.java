package com.vish.testapp;

import java.util.Arrays;

/**
 * An object representing a recipe. Contains one or more ingredients.
 * Created by vish on 1/7/2016.
 */
public class Recipe {
    private long id;
    private String recipeName;
    private Ingredient[] ingredients;

    public Recipe() {

    }

    /**
     * Initialize a recipe with one or more {@link Ingredient} objects.
     * @param recipeName
     * @param ingredients
     */
    public Recipe(String recipeName, Ingredient... ingredients) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
    }

    public void setID(long id) {
        this.id = id;
    }

    public long getID() {
        return this.id;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return this.recipeName;
    }

    public void setIngredients(Ingredient... ingredients) {
        this.ingredients = ingredients;
    }

    public Ingredient[] getIngredients() {
        return this.ingredients;
    }

    public String asString() {
        return "id:" + id + ", name:" + recipeName + ", ingredients: " + Arrays.asList(this.ingredients);
    }
}
