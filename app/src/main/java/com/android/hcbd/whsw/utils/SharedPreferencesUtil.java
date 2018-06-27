package com.android.hcbd.whsw.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by guocheng on 2017/5/11.
 */

public class SharedPreferencesUtil {
    public static final String SETTING_FILE = "setting_file";

    /**
     * 保存
     */
    public static void save(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取
     */
    public static String get(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    /**
     * 获取 default
     */
    public static String getByDefault(Context context, String key, String defValue) {
        SharedPreferences pref = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        return pref.getString(key, defValue);
    }
}
