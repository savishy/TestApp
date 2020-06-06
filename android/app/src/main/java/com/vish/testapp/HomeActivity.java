package com.vish.testapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * http://stackoverflow.com/questions/10644914/android-expandablelistview-and-sqlite-database
 * https://thinkandroid.wordpress.com/2010/01/09/simplecursoradapters-and-listviews/
 * http://stackoverflow.com/questions/18069678/how-to-use-asynctask-to-display-a-progress-bar-that-counts-down
 * http://stackoverflow.com/questions/3105080/output-values-found-in-cursor-to-logcat-android
 */
public class HomeActivity extends AppCompatActivity {

    private static String TAG = "RecipeHomeActivity";
    protected RecipeDbHelper dbHelper;
    protected SQLiteDatabase thisDb;
    protected Cursor cursor;
    protected ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addRecipe(view);
            }
        });

        //initialize database
        Log.d(TAG,this.getClass().getSimpleName() + " onCreate()");
        dbHelper = new RecipeDbHelper(this);
        progress = new ProgressDialog(this);
        progress.setMessage("Loading database...");
        executeAsync();
    }

    public void addRecipe(View view) {
        Intent intent = new Intent(this,AddRecipeActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * populate {@link R.id#recipeListView} with recipes obtained from cursor.
     */
    private void storeCursorDataIntoListView() {
        this.startManagingCursor(cursor);
        cursor.moveToFirst();
        Common.printCursor(cursor);
        ListView lv = (ListView) this.findViewById(R.id.recipeListView);
        RecipeListViewAdapter adapter = new RecipeListViewAdapter(this,
                R.layout.list_item,
                cursor,
                new String[]{RecipeDbHelper.KEY_ID,RecipeDbHelper.KEY_NAME},
                new int[]{R.id.lblListItemId,R.id.lblListItem},
                SimpleCursorAdapter.FLAG_AUTO_REQUERY);

        lv.setAdapter(adapter);                         // set the list adapter.

    }

    private void executeAsync() {
        Log.d(TAG, this.getClass().getSimpleName() + ".executeAsync()");
        DbAsync async = new DbAsync(progress);
        AsyncTask t = async.execute();
    }

    class RecipeListViewAdapter extends SimpleCursorAdapter {
        public RecipeListViewAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }
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
            dbHelper.initializeDatabase(thisDb);
            publishProgress(2);
            if (thisDb == null || !thisDb.isOpen()) {
                Log.w(TAG, this.getClass().getSimpleName() + " data-base not created or not opened!");
                return 1;
            }
            else {
                Log.w(TAG, this.getClass().getSimpleName() + " database isOpen() " + thisDb.isOpen());
                cursor = dbHelper.getRecipesCursor(thisDb);
                publishProgress(3);
                return 0;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG,this.getClass().getSimpleName() + " progress:" + progress[0]);
            this.progress.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute (Integer result) {
            Log.d(TAG, this.getClass().getSimpleName() + " onPostExecute() result:" + result);
            Log.d(TAG, "Cursor isNull():" + String.valueOf(cursor == null));
            progress.dismiss();
            if (result > 0) {
                Common.showSimpleDialog(HomeActivity.this,"Warning!","Database not created");
            } else {
                Common.showSimpleDialog(HomeActivity.this,"Debug","Database created successfully!");
                storeCursorDataIntoListView();
            }
        }



    }

}

