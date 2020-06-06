package com.vish.testapp;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

/**
 *
 * <p>
 * This is an autocomplete view for displaying ingredients. It is part of
 * {@link AddRecipeActivity} activity.
 *
 * </p>
 * This is an easy way to implement an autocomplete text view similar to Gmail.
 * I tried using MultiAutocompleteTextView, but it looked really bad.
 *
 * I needed a good-looking interface so used the reference below as a simple
 * way to create an autocomplete view.
 *
 * Created by vish on 1/18/2016.
 * References:
 * https://github.com/splitwise/TokenAutoComplete
 *
 */
public class IngredientsCompletionView extends TokenCompleteTextView<Ingredient> {

    private LayoutInflater l;
    public IngredientsCompletionView(Context context) {
        super(context);
        l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }
    public IngredientsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected View getViewForObject(Ingredient ingredient) {
        Log.w(TAG,"getViewForObject " + ingredient.toString());
        LinearLayout view = (LinearLayout)l.inflate(R.layout.layout_token, (ViewGroup) IngredientsCompletionView.this.getParent(), false);
        Log.w(TAG, "Setting text for ingredient " + ingredient.getName());
        TextView tv = (TextView) view.findViewById(R.id.lblToken);
        tv.setText(ingredient.getName());
//        ((TextView)view.findViewById(R.id.lblToken)).setText(ingredient.getName());
        return tv;
    }

    @Override
    protected Ingredient defaultObject(String completionText) {
        Log.w(TAG,"defaultObject() " + completionText);
        return new Ingredient(completionText);
    }
}
