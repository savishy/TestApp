package com.vish.testapp;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

/**
 *
 * This is an easy way to implement an autocomplete text view similar to Gmail.
 * I needed a good-looking interface so used the reference below.
 *
 * Created by vish on 1/18/2016.
 * References:
 * https://github.com/splitwise/TokenAutoComplete
 *
 */
public class IngredientsCompletionView extends TokenCompleteTextView<Ingredient> {

    public IngredientsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Ingredient ingredient) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.layout_ingredient, (ViewGroup)IngredientsCompletionView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.lblIngredient)).setText(ingredient.getName());
        return null;
    }

    @Override
    protected Ingredient defaultObject(String completionText) {
        return null;
    }
}
