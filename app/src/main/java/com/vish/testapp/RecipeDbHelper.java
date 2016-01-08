package com.vish.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vish on 1/7/2016.
 * references:
 * http://stackoverflow.com/questions/8041511/best-way-to-store-tags-or-categories-like-stack-overflow
 * http://stackoverflow.com/questions/6935620/sqlite-many-to-many-relationship
 * https://www.sqlite.org/foreignkeys.html
 * https://www.sqlite.org/autoinc.html
 * http://www.techotopia.com/index.php/An_Android_Studio_SQLite_Database_Tutorial
 * http://stackoverflow.com/questions/7594541/android-table-creation-failure-near-autoincrement-syntax-error
 * http://stackoverflow.com/questions/21881992/when-is-sqliteopenhelper-oncreate-onupgrade-run
 * http://daulatbachhav18.blogspot.in/2013/01/sqlite-and-asynctask-simple-example.html
 */
public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "RecipeDbHelper";
    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 2;
    private static final String RECIPE_TABLE_NAME = "recipelist",
                                INGREDIENTS_TABLE_NAME="ingredients",
                                INGPERRECIPE_TABLE_NAME="recipe_ingredients";
    private static final String KEY_ID = "_id", KEY_ING_ID = "ing_id", KEY_REC_ID = "recipe_id", KEY_NAME = "name";

    private static final String RECIPE_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + RECIPE_TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY NOT NULL, " + KEY_NAME + " TEXT);";

    private static final String INGREDIENTS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + INGREDIENTS_TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY NOT NULL, " + KEY_NAME + " TEXT);";

    private static final String INGPERRECIPE_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + INGPERRECIPE_TABLE_NAME + " (" + KEY_ING_ID + " INTEGER, " +
                    KEY_REC_ID + " INTEGER, " +
                    "FOREIGN KEY(" + KEY_ING_ID + ") REFERENCES " + INGREDIENTS_TABLE_NAME + "(" + KEY_ID + "), " +
                    "FOREIGN KEY(" + KEY_REC_ID + ") REFERENCES " + RECIPE_TABLE_NAME + "(" + KEY_ID + ")" +
                    ");";


    RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDatabase openWritableDbWithForeignKeyConstraint() {
        SQLiteDatabase db = getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,this.getClass().getSimpleName() + "onCreate()");
        Log.d(TAG,this.getClass().getSimpleName() + RECIPE_TABLE_CREATE);
        Log.d(TAG,this.getClass().getSimpleName() + INGREDIENTS_TABLE_CREATE);
        Log.d(TAG,this.getClass().getSimpleName() + INGPERRECIPE_TABLE_CREATE);
        db.execSQL(RECIPE_TABLE_CREATE);
        db.execSQL(INGREDIENTS_TABLE_CREATE);
        db.execSQL(INGPERRECIPE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * get cursor for all recipes.
     * @return
     */
    public Cursor getRecipesCursor(SQLiteDatabase thisDb) {
//        SQLiteDatabase thisDb = getWritableDatabase();
        String query = "SELECT " + KEY_ID + "," + KEY_NAME + " FROM " + RECIPE_TABLE_NAME;
        Log.d(TAG,this.getClass().getSimpleName() + " getRecipesCursor() " + query);
        Cursor retVal =  thisDb.rawQuery(query, null);
        return retVal;
    }

    /**
     * get cursor for all ingredients.
     * @return
     */
    public Cursor getIngredientsCursor(SQLiteDatabase thisDb) {
//        SQLiteDatabase thisDb = getWritableDatabase();
        String query = "SELECT " + KEY_ID + "," + KEY_NAME + " FROM " + INGREDIENTS_TABLE_NAME;
        Log.d(TAG,this.getClass().getSimpleName() + " getIngredientsCursor() " + query);
        Cursor retVal =  thisDb.rawQuery(query, null);
        return retVal;
    }
    /**
     * get Cursor (ING_ID,REC_ID) for ingredients for a particular recipe ID.
     * @param recipeID
     * @return
     */
    public Cursor getIngredientsForRecipeCursor(long recipeID) {
        SQLiteDatabase thisDb = getWritableDatabase();
        String query =  "SELECT " + KEY_ING_ID + "," + KEY_REC_ID + " FROM " +
                        INGPERRECIPE_TABLE_NAME + " WHERE " + KEY_REC_ID + " = '" + recipeID + "'";
        Cursor retVal = thisDb.rawQuery(query, null);
        return retVal;
    }


    /**
     * Populate the database with some initial recipes
     * @param db
     */
    public void initializeDatabase(SQLiteDatabase db) {
        //create some ingredient objects
        Ingredient[] ingredients = new Ingredient[6];
        for (int i=0; i<6; i++) {
            ingredients[i] = new Ingredient("ing " + i);
            //put each ingredient into table and update its ID.
            ingredients[i].setID(createIngredient(ingredients[i],db));
        }
        //create some recipe objects that use above ingredients.
        Recipe[] recipes = new Recipe[3];
        recipes[0] = new Recipe("recipe 1", new Ingredient[]{ingredients[0],ingredients[1]});
        recipes[1] = new Recipe("recipe 2", new Ingredient[]{ingredients[2],ingredients[3]});
        recipes[2] = new Recipe("recipe 3", new Ingredient[]{ingredients[4],ingredients[5]});
        //store each recipe into table.
        for (int i=0;i<3;i++) {
            recipes[i].setID(createRecipe(recipes[i], db));
        }
        findAllRecipesWithIngredients(db);
    }

    /**
     * creates ingredient in table from {@link Ingredient} object
     * and returns the row ID of newly-created ingredient.
     * @param i
     * @param thisDb
     */
    private long createIngredient(Ingredient i, SQLiteDatabase thisDb) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, i.getName());
        return thisDb.insert(INGREDIENTS_TABLE_NAME,null,values);
    }

    /**
     * insert new recipe into recipes table.
     * Map recipe to ingredients in foreign-key map table.
     * return row ID of recipe.
     *
     * @param r
     */
    private long createRecipe(Recipe r, SQLiteDatabase thisDb) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,r.getRecipeName());
        long rID = thisDb.insert(RECIPE_TABLE_NAME,null,values);
        r.setID(rID);
        mapRecipeToIngredients(r, thisDb);
        return rID;
    }

    /**
     * map each recipe to its ingredients.
     * @param r
     * @param db
     */
    private void mapRecipeToIngredients(Recipe r, SQLiteDatabase db) {
        Log.d(TAG, this.getClass().getSimpleName() + " mapRecipeToIngredients() recipe ID:" + r.getID());
        for (Ingredient i : r.getIngredients()) {
            ContentValues values = new ContentValues();
            values.put(KEY_ING_ID,i.getID());
            values.put(KEY_REC_ID,r.getID());
            db.insert(INGPERRECIPE_TABLE_NAME,null,values);
        }
    }

    public Map<Long,String> findIngredientsAsMap (SQLiteDatabase db) {
        Cursor ingCursor = getIngredientsCursor(db);
        Map<Long,String> ingredient = new HashMap<Long,String>();
        if (ingCursor.moveToFirst()) {
            while (ingCursor.moveToNext()) {
                ingredient.put(ingCursor.getLong(0), ingCursor.getString(1));
            }
        }
        return ingredient;
    }

    public Recipe[] findAllRecipesWithIngredients(SQLiteDatabase db) {
        //get all recipes Cursor
        Cursor recipeCursor = getRecipesCursor(db);
        List<Recipe> recipes = new ArrayList<Recipe>();
        //get all ingredients as map.
        Map<Long,String> ingredients = findIngredientsAsMap(db);

        //first get all recipes will null ingredients
        if (recipeCursor.moveToFirst()) {
            while (recipeCursor.moveToNext()) {
                Recipe r = new Recipe();
                r.setID(recipeCursor.getLong(0));
                r.setRecipeName(recipeCursor.getString(1));

                //for this recipe, get ingredients cursor
                Cursor ingCursor = getIngredientsForRecipeCursor(r.getID());
                List<Ingredient> ingList = new ArrayList<Ingredient>();

                if (ingCursor.moveToFirst()) {
                    while (ingCursor.moveToNext()) {
                        long id = ingCursor.getLong(0);
                        String ingName = ingredients.get(id);
                        Ingredient i = new Ingredient();
                        i.setID(id);
                        i.setName(ingName);
                        ingList.add(i);
                    }
                    ingCursor.close();
                }
                r.setIngredients(ingList.toArray(new Ingredient[]{}));
                Log.d(TAG,r.asString());
            }
            recipeCursor.close();
        }


        return recipes.toArray(new Recipe[]{});
    }

    public Recipe findRecipeByName(String recipeName) {

        String query = "SELECT " + KEY_ID + "," + KEY_NAME + RECIPE_TABLE_NAME + " WHERE " + KEY_NAME + " =  \"" + recipeName + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        Recipe r = new Recipe();

        if (cursor.moveToFirst()) {
            //move cursor to first row
            cursor.moveToFirst();
            r.setID(Integer.parseInt(cursor.getString(0)));
            r.setRecipeName(cursor.getString(1));
            cursor.close();
        } else {
            r = null;
        }
        return r;
    }
}
