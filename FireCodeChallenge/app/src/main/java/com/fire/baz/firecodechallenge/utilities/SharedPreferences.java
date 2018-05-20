package com.fire.baz.firecodechallenge.utilities;

import android.content.Context;

public class SharedPreferences {

    private static SharedPreferences instance = null;

    public static final String SHARED_PREF_NAME = "userDetails";

    android.content.SharedPreferences userLocalDatabase;

    private SharedPreferences(Context context) {
        userLocalDatabase = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
    }

    // get instance
    public static synchronized SharedPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferences(context);
        }
        return instance;
    }


    public void clearUserSP() {
        android.content.SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }


    public boolean haveToken() {
        if (userLocalDatabase.getBoolean("haveToken", false) == true) {
            return true;
        } else {
            return false;
        }
    }


    // getters
    public String getToken() {
        return userLocalDatabase.getString(Constants.TOKEN, "");
    }


    // setters
    public void setHaveApiToken(boolean haveToken) {
        android.content.SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("haveToken", haveToken);
        spEditor.apply();
    }

    public void setToken(String token) {
        android.content.SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString(Constants.TOKEN, token);
        spEditor.apply();
    }

}