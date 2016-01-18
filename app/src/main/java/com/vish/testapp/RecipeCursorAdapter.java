package com.vish.testapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * https://github.com/codepath/android_guides/wiki/Populating-a-ListView-with-a-CursorAdapter
 * Created by vishy on 1/15/2016.
 */
public class RecipeCursorAdapter extends CursorAdapter {

    public RecipeCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }
    /**
     * The newView method is used to inflate a new view and return it,
     * you don't bind any data to the view at this point.
     * @param context
     * @param cursor
     * @param viewGroup
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.layout_token,viewGroup,false);
    }

    /**
     * This method binds the data to a given view (e.g. setting text on a TextView from
     * a cursor).
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView ingredientTv = (TextView) view.findViewById(R.id.lblToken);
        ingredientTv.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
    }
}
