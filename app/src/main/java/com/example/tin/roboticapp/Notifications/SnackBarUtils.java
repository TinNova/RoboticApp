package com.example.tin.roboticapp.Notifications;

import android.support.design.widget.Snackbar;
import android.view.View;


public class SnackBarUtils {

    public static void snackBar(final View view, String message, int duration) {

        // Else if the connManager and networkInfo IS null, show a snakeBar informing the user
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        View snackBarView = snackbar.getView();

        // Set an action on it, and a handler
        snackbar.setAction("DISMISS", new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();

            }
        });

        snackbar.show();
    }
}
