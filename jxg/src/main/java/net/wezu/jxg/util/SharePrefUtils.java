package net.wezu.jxg.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SharePrefUtils {
    private static SharePrefUtils pref;
    public SharedPreferences mSharePreferences;
    protected SharedPreferences.Editor mEditor;

    public SharedPreferences fbSharePreferences;
    protected SharedPreferences.Editor fEditor;

    private SharedPreferences mSharedPreferencesUserInfo;
    private SharedPreferences.Editor mEditorUserInfo;






    private static final String FIRST_TIME = "first_time";
    private static final String ONE_TIME = "one_time";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email name (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Email name (make variable public to access from outside)
    public static final String KEY_ID = "userId";

    public SharePrefUtils(Context context) {
        mSharePreferences = context.getSharedPreferences("CPref", 0);
        mEditor = mSharePreferences.edit();

        fbSharePreferences = context.getSharedPreferences("FB", 0);
        fEditor = fbSharePreferences.edit();


        //mSharedPreferencesUserInfo = context.getSharedPreferences(CommonConfig.SHARE_PREFERENCE_USER_INFO, Context.MODE_PRIVATE);
        mEditorUserInfo = mSharedPreferencesUserInfo.edit();
    }

    public static SharePrefUtils getInstance(Context context) {
        if (pref == null) {
            pref = new SharePrefUtils(context);
        }
        return pref;
    }

    public boolean isFirstTime() {
        return mSharePreferences.getBoolean(FIRST_TIME, true);
    }

    public void setFirstTime(boolean isStart){
        SharedPreferences.Editor editor = mSharePreferences.edit();
        editor.putBoolean(FIRST_TIME, isStart);
        editor.commit();
    }

    public boolean isMyanmarText(String s) {
        Pattern p = Pattern.compile("[^\\u0000-\\u0080]+");
        Matcher matcher = p.matcher(s);
        return matcher.find();
    }

    public void noMoreFirstTime() {
        mEditor.putBoolean(FIRST_TIME, false).commit();
    }

    public void setOneTime() {
        mEditor.putBoolean(ONE_TIME, true).commit();
    }

    public boolean toShowOneTime() {
        return mSharePreferences.getBoolean(ONE_TIME, true);
    }

    public void showedOneTime() {
        mEditor.putBoolean(ONE_TIME, false).commit();
    }


    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email , String userID){
        // Storing login value as TRUE
        fEditor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        fEditor.putString(KEY_NAME, name);

        // Storing email in pref
        fEditor.putString(KEY_EMAIL, email);

        fEditor.putString(KEY_ID, userID);


        //editor.putString(KEY_FRIEND_CODE, "sldjfklsdj");
        // commit changes
        fEditor.commit();
    }
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, fbSharePreferences.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, fbSharePreferences.getString(KEY_EMAIL, null));

        //KEY_ID
        user.put(KEY_ID, fbSharePreferences.getString(KEY_ID, null));//KEY_FRIEND_CODE
        // return user

        return user;
    }
    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return fbSharePreferences.getBoolean(IS_LOGIN, false);
    }

    /**
     * Clear session details
     * */
    public void FacebookLogOut() {
        // Clearing all data from Shared Preferences
        fEditor.clear();
        fEditor.commit();


    }
    public void ClearLogOut(){
        mEditorUserInfo.clear();
        mEditorUserInfo.commit();
    }
}
