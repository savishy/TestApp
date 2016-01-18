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
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.tokenautocomplete.TokenCompleteTextView;

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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

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
        executeAsync();


        //initialize TokenAutocompleteView
        completionView = (IngredientsCompletionView) findViewById(R.id.searchView);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, new String[]{"ing1","ing2","ing3"});
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
     * execute the asynctask for database access.
     */
    private void executeAsync() {
        Log.d(TAG, this.getClass().getSimpleName() + ".executeAsync()");
        DbAsync async = new DbAsync(progress);
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
        Log.d(TAG,"Added Token:" + o);
    }

    @Override
    public void onTokenRemoved(Object o) {
        Log.d(TAG,"Removed Token:" + o);
    }

    class DbAsync extends AsyncTask<Object, Integer, Integer> {
        private String TAG = "DbAsync";
        private ProgressDialog progress;

        public DbAsync(ProgressDialog p) {
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
                dbHelper.findIngredientsAsMap(thisDb);
                cursor = dbHelper.getIngredientsCursor(thisDb);
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
