package net.wezu.jxg.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class ShowKeyboardListener implements View.OnClickListener {

    Activity mAct;

    public ShowKeyboardListener(Activity act) {
        this.mAct = act;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ViewGroup) {
            showSoftKeyboard(this.mAct);
        }
    }

    public void showSoftKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}