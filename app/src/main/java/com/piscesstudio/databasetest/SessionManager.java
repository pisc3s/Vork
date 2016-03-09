package com.piscesstudio.databasetest;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "VorkSession";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_TOKEN = "token";

    public SessionManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String user_id, String token) {
        editor.putString(KEY_USER_ID, user_id);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public Session getCurrentSession() {
        String uid = pref.getString(KEY_USER_ID, null);
        String token = pref.getString(KEY_TOKEN, null);
        if(uid == null || token == null){
            return null;
        } else {
            return new Session(uid, token);
        }
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
        App.session = null;
    }
}