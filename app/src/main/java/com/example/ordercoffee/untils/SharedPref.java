package com.example.ordercoffee.untils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.ordercoffee.data.model.User;
import com.google.gson.Gson;

public class SharedPref {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USER = "user_data";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SharedPref(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    public void saveUser(User user) {
        String json = gson.toJson(user);
        editor.putString(KEY_USER, json);
        editor.apply();
    }

    public User getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json == null) return null;
        return gson.fromJson(json, User.class);
    }

    public void clearUser() {
        editor.remove(KEY_USER);
        editor.apply();
    }
}
