package com.xunchijn.zblocation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Administrator on 2018/5/8 0008.
 */

public class PreferHelper {
    private static final String TAG = "PreferHelper";
    private static final String PREFERENCES = "PREFERENCES";
    private static final String NUMBER = "number";
    private static final String LON = "lon";
    private static final String LAT = "lat";
    private static final String ADDRESS = "address";
    private SharedPreferences mSharedPreferences;

    public PreferHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    private void save(String key, String value) {
        if (mSharedPreferences == null) {
            return;
        }
        Log.d(TAG, String.format("save: key: %s, value: %s", key, value));
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String get(String key) {
        if (mSharedPreferences == null) {
            return "";
        }
        String value = mSharedPreferences.getString(key, "");
        Log.d(TAG, String.format("get: key: %s, value: %s", key, value));
        return value;
    }

    public String getNumber() {

        return get(NUMBER);
    }

    public void saveNumber(String number) {

        save(NUMBER, number);
    }

    public String getLon() {

        return get(LON);
    }

    public void saveLon(String lon) {

        save(LON, lon);
    }

    public String getLat() {

        return get(LAT);
    }

    public void saveLat(String lat) {

        save(LAT, lat);
    }

    public String getAddress() {

        return get(ADDRESS);
    }

    public void saveAddress(String address) {

        save(ADDRESS, address);
    }


}