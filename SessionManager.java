package com.example.annaheventsls;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AnnahEventsSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_API_TOKEN = "apiToken";
    private static final String KEY_FULLNAME = "userFullname";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String email, String token, String fullname) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_API_TOKEN, token);
        editor.putString(KEY_FULLNAME, fullname);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getApiToken() {
        return pref.getString(KEY_API_TOKEN, null);
    }

    public String getFullName() {
        return pref.getString(KEY_FULLNAME, "User");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
