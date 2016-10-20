package net.wezu.jxg.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class DismissKeyboardListener implements View.OnClickListener {

    Activity mAct;

    public DismissKeyboardListener(Activity act) {
        this.mAct = act;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ViewGroup) {
            hideSoftKeyboard(this.mAct);
        }
    }


    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
