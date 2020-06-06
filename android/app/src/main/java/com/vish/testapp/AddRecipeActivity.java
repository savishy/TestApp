package com.vish.testapp;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * References:
 * http://sampleprogramz.com/android/multiautocompletetextview.php
 */
public class AddRecipeActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener {

    private static String TAG = "AddRecipeActivity";
    protected RecipeDbHelper dbHelper;
    protected SQLiteDatabase thisDb;
    protected Cursor cursor;
    protected ProgressDialog progress;
    /** list of ingredients chosen by user. Gets updated by
     * {@link #onTokenAdded(Object)} and
     * {@link #onTokenRemoved(Object)}
     */
    List<Ingredient> ingredients = new ArrayList<Ingredient>();

    /**
     * List of available ingredients.
     */
    List<Ingredient> availableIngredients = new ArrayList<Ingredient>();

    /**
     * an enum that decides the Async Database Task to execute.
     */
    private static enum DbTasks {
        getIngredients,
        addRecipe,
        addIngredient
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        IngredientsCompletionView completionView;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
        //initialize database
        Log.d(TAG, this.getClass().getSimpleName() + " onCreate()");
        dbHelper = new RecipeDbHelper(this);
        progress = new ProgressDialog(this);
        progress.setMessage("Loading database...");
        //execute async task to get ingredients
        executeAsyncTask(DbTasks.getIngredients);


        //initialize TokenAutocompleteView from ArrayAdapter
        completionView = (IngredientsCompletionView) findViewById(R.id.searchView);
        ArrayAdapter<Ingredient> adp = new ArrayAdapter<Ingredient>(this,
                android.R.layout.simple_dropdown_item_1line, new Ingredient[]{
                new Ingredient("ing1"),
                new Ingredient("ing2"),
                new Ingredient("ing3")
        });

        completionView.setAdapter(adp);
        completionView.setTokenListener(this);



        /**
         * MultiAutoCompleteTextView Approach. Being deprecated.
         */
//
//        MultiAutoCompleteTextView multiAutoCompleteTextView
//                = (MultiAutoCompleteTextView) findViewById(R.id.ingredientsAutocompleteTextView);
//        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
//        multiAutoCompleteTextView.setThreshold(1);
//
//        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, new String[]{"ing1","ing2","ing3"});
//        multiAutoCompleteTextView.setAdapter(adp);

//        Common.printCursor(cursor);
//        RecipeCursorAdapter rcAdapter = new RecipeCursorAdapter(this,cursor,0);
//        multiAutoCompleteTextView.setAdapter(rcAdapter);
    }


    /**
     * execute {@link com.vish.testapp.AddRecipeActivity.DbAsync} task
     * for database access.
     * @param task
     */
    private void executeAsyncTask(DbTasks task) {
        Log.d(TAG, this.getClass().getSimpleName() + ".executeAsyncTask() " + task);
        DbAsync async = new DbAsync(progress,task);
        AsyncTask t = async.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    @Override
    public void onTokenAdded(Object o) {
        Log.d(TAG, "Added Token:" + o);
        ingredients.add((Ingredient) o);
    }

    @Override
    public void onTokenRemoved(Object o) {
        Log.d(TAG,"Removed Token:" + o);
        ingredients.remove((Ingredient) o);

    }

    /**
     * add recipe with checks. Is tied to button
     * {@link R.id#btnSave}
     * @param view
     */
    public void addRecipe(View view) {
        EditText recipeTitle = (EditText) findViewById(R.id.recipeTitle);

        String ingDisplay = new String();
        for (Ingredient i : ingredients) {
            ingDisplay += i;
        }

        Common.showSimpleDialog(AddRecipeActivity.this, "Debug", "Add recipe with title: " +
                        recipeTitle.getText() +
                        " and ingredients: " +
                        ingDisplay
        );

        //execute DB Async Task.

    }


    /**
     * an AsyncTask to both read and write to database.
     */
    class DbAsync extends AsyncTask<Object, Integer, Integer> {
        private String TAG = "DbAsync";
        private DbTasks task;
        private ProgressDialog progress;

        /**
         *
         * @param p a progress dialog
         * @param task a {@link com.vish.testapp.AddRecipeActivity.DbTasks} object that signifies the
         *             db task to perform.
         */
        public DbAsync(ProgressDialog p,DbTasks task) {
            this.task = task;
            this.progress = p;
        }

        @Override
        public void onPreExecute() {
            progress.show();
        }

        @Override
        protected Integer doInBackground(Object... params) {
            Log.d(TAG, this.getClass().getSimpleName() + " doInBackground()");
            thisDb = dbHelper.openWritableDbWithForeignKeyConstraint();
            publishProgress(1);
            publishProgress(2);
            if (thisDb == null || !thisDb.isOpen()) {
                Log.w(TAG, this.getClass().getSimpleName() + " data-base not created or not opened!");
                return 1;
            } else {
                Log.w(TAG, this.getClass().getSimpleName() + " database isOpen() " + thisDb.isOpen());

                //perform the task needed
                switch (task) {
                    case getIngredients:
                        Log.d(TAG,"getIngredients");
                        cursor = dbHelper.getIngredientsCursor(thisDb);
                        availableIngredients = dbHelper.getIngredientsAsArrayList(cursor);
                        break;
                    case addRecipe:

                        break;
                    default: Log.w(TAG,task + " unsupported");
                }
                publishProgress(3);
                return 0;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, this.getClass().getSimpleName() + " progress:" + progress[0]);
            this.progress.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, this.getClass().getSimpleName() + " onPostExecute() result:" + result);
            Log.d(TAG, "Cursor isNull():" + String.valueOf(cursor == null));
            progress.dismiss();
            if (result > 0) {
                Common.showSimpleDialog(AddRecipeActivity.this, "Warning!", "Ingredients not fetched");
            } else {
                Common.showSimpleDialog(AddRecipeActivity.this, "Debug", "Ingredients fetched successfully!");
                ;
            }
        }


    }
}
