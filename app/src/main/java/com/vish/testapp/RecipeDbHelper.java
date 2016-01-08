package com.vish.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

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

//    private static final String INGPERRECIPE_TABLE_CREATE =
//            "CREATE TABLE IF NOT EXISTS " + INGPERRECIPE_TABLE_NAME + " (" + KEY_ING_ID + " INTEGER, " +
//                    "FOREIGN KEY(" + KEY_ING_ID + ") REFERENCES " + INGREDIENTS_TABLE_NAME + "(" + KEY_ID + "), " +
//                    KEY_REC_ID + " INTEGER," +
//                    "FOREIGN KEY(" + KEY_REC_ID + ") REFERENCES " + RECIPE_TABLE_NAME + "(" + KEY_ID + ")" +
//                    ");";


    RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,this.getClass().getSimpleName() + "onCreate()");
        Log.d(TAG,RECIPE_TABLE_CREATE);
        Log.d(TAG,INGREDIENTS_TABLE_CREATE);
//        Log.d(TAG, INGPERRECIPE_TABLE_CREATE);
        db.execSQL(RECIPE_TABLE_CREATE);
        db.execSQL(INGREDIENTS_TABLE_CREATE);
//        db.execSQL(INGPERRECIPE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * get all recipes.
     * @return
     */
    public Cursor fetchGroup(SQLiteDatabase thisDb) {
//        SQLiteDatabase thisDb = getWritableDatabase();
        String query = "SELECT " + KEY_ID + "," + KEY_NAME + " FROM " + RECIPE_TABLE_NAME;
        Log.d(TAG,this.getClass().getSimpleName() + " fetchGroup() " + query);
        Cursor retVal =  thisDb.rawQuery(query, null);
        return retVal;
    }

    /**
     * get children for a particular recipe ID.
     * @param rec_id
     * @return
     */
    public Cursor fetchChildren(String rec_id) {
        SQLiteDatabase thisDb = getWritableDatabase();
        String query = "SELECT * FROM " + INGPERRECIPE_TABLE_NAME + " WHERE " + KEY_REC_ID + " = '" + rec_id + "'";
        Cursor retVal = thisDb.rawQuery(query, null);
        return retVal;
    }


    public void createInitialRecipes(SQLiteDatabase db) {
        for (String name : new String[]{
                "recipe 1",
                "recipe 2",
                "recipe 3"
        }) {
            Recipe r = new Recipe(name,null);
            createRecipe(r,db);
        }
    }

    /**
     * insert new recipe
     * @param r
     */
    public void createRecipe(Recipe r, SQLiteDatabase thisDb) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,r.getRecipeName());
        thisDb.insert(RECIPE_TABLE_NAME,null,values);
    }

    public Recipe findRecipeByName(String recipeName) {
        String query = "Select * FROM " + RECIPE_TABLE_NAME + " WHERE " + KEY_NAME + " =  \"" + recipeName + "\"";
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
