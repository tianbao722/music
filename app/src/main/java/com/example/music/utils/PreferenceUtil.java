package com.example.music.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.example.music.MyApplication;

/**
 * 首选项工具类
 */
public class PreferenceUtil {
    private static final String PREFERENCE_NAME = "helloo";
    private static PreferenceUtil preferenceUtil;
    private SharedPreferences sp;

    private PreferenceUtil() {
        init();
    }

    public void init() {
        if (sp == null) {
            try {
                sp = MyApplication.getInstance().getSharedPreferences(
                        PREFERENCE_NAME, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static PreferenceUtil getInstance() {
        if (preferenceUtil == null) {
            preferenceUtil = new PreferenceUtil();
        }
        return preferenceUtil;
    }

    public void saveLong(String key, long l) {
        Editor ed = sp.edit();
        ed.putLong(key, l);
        ed.commit();
    }

    public long getLong(String key, long defaultlong) {
        return sp.getLong(key, defaultlong);
    }

    public void saveBoolean(String key, boolean value) {
        Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.commit();
    }

    public boolean getBoolean(String key, boolean defaultboolean) {
        return sp.getBoolean(key, defaultboolean);
    }


    public void saveInt(String key, int value) {
        Editor ed = sp.edit();
        if (ed != null) {
            ed.putInt(key, value);
            ed.commit();
        }
    }

    public int getInt(String key, int defaultInt) {
        return sp.getInt(key, defaultInt);
    }

    public String getString(String key, String defaultInt) {
        return sp.getString(key, defaultInt);
    }

    public void saveString(String key, String value) {
        Editor ed = sp.edit();
        ed.putString(key, value);
        ed.commit();
    }


    public void remove(String key) {
        Editor ed = sp.edit();
        ed.remove(key);
        ed.commit();
    }

    public void saveFloat(String key, float value) {
        Editor ed = sp.edit();
        if (ed != null) {
            ed.putFloat(key, value);
            ed.commit();
        }
    }

    public float getFloat(String key, float defaultboolean) {
        return sp.getFloat(key, defaultboolean);
    }


}
