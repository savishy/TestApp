package com.vish.testapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by vish on 1/8/2016.
 */
public class Common {

    public static void showSimpleDialog(Context c, String dialogTitle, String msg) {
        new AlertDialog.Builder(c)
                .setTitle(dialogTitle)
                .setMessage(msg)
                .setCancelable(true)
                .setNeutralButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
}
