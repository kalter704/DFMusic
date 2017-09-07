package com.wiretech.df.dfmusic.classes;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.wiretech.df.dfmusic.R;

public class SnackBarCreator {

    public static void show(Context context, int stringResourceID) {
        show(context, context.getString(stringResourceID));
    }

    public static void show(Context context, String message) {
        Snackbar snackbar = Snackbar.make(
                ((Activity) context).findViewById(R.id.mainCoordLayout),
                message,
                Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackView.setBackgroundColor(context.getColor(R.color.snackNotificationColor));
        } else {
            snackView.setBackgroundColor(context.getResources().getColor(R.color.snackNotificationColor));
        }
        TextView snackTV = snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }

}
