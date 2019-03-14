package pers.dg.skin_lib;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by b on 2019/3/12.
 */

class SkinSharedPreferences {

    public static void putString(Context context, String value) {
        SharedPreferences settings = context.getSharedPreferences(SkinConstants.SKIN_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SkinConstants.SKIN_SHAREDPREFERENCES_KEY_PATH, value);
        editor.commit();
    }

    public static String getString(Context context) {
        return getString(context, null);
    }

    private static String getString(Context context, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(SkinConstants.SKIN_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        return settings.getString(SkinConstants.SKIN_SHAREDPREFERENCES_KEY_PATH, defaultValue);
    }
}
