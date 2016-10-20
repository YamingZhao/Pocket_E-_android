package net.wezu.jxg.util;

import net.wezu.log.Log;

/**
 * 防止按钮重复点击
 * Created by snox on 2016/4/26.
 */
public class FastClickUtil {
    private static final String TAG = "FastClickUtil";

    private static long lastClickTime;

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        boolean result = time - lastClickTime < 1000;

        Log.w(TAG, String.format("%s FastClickUtil.isFastClick %d  %d", result ? "*" : " ", time, lastClickTime));

        lastClickTime = time;

        return result;
    }
}
