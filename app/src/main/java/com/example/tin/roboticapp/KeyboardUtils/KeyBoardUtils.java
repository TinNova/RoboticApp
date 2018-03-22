package com.example.tin.roboticapp.KeyboardUtils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Tin on 22/03/2018.
 */

public class KeyBoardUtils {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
