package com.piscesstudio.databasetest;

import android.app.Application;

public class App extends Application {
    public static Session session;
    @Override
    public void onCreate() {
        super.onCreate();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        session = sessionManager.getCurrentSession();
    }

}