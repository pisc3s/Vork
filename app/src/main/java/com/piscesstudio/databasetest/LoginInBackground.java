package com.piscesstudio.databasetest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginInBackground extends AsyncTask<String, Void, Session> {
    String result;
    Exception error = null;
    BufferedReader bufferedReader = null;
    Context context;

    public LoginInBackground(Context context) {
        this.context = context;
    }

    @Override
    protected Session doInBackground(String... arg0) {
        final String param1 = "username";
        final String param2 = "pw";
        final String value1 = arg0[0];
        final String value2 = arg0[1];
        Session session = null;
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("192.168.1.100:8080")
                    .appendPath("login.php")
                    .appendQueryParameter(param1,value1)
                    .appendQueryParameter(param2, value2);
            String link = builder.build().toString();

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder("");
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            result = sb.toString().trim();

            JSONObject obj = new JSONObject(result);
            int code = obj.getInt("code");
            String message = obj.getString("message");
            if(code == 200) {
                String user_id = obj.getString("user_id");
                String token = obj.getString("token");
                session = new Session(user_id, token);
            } else {
                throw new Exception(message);
            }

            bufferedReader.close();
        } catch (Exception e) {
            error = e;
        }
        return session;
    }

    @Override
    protected void onPostExecute(Session result) {
        if(error == null) {
            SessionManager sm = new SessionManager(context);
            sm.createLoginSession(result.getUserID(), result.getToken());
            App.session = sm.getCurrentSession();

            Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(context, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            ((Activity) context).finish();
        } else {
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("<<<ERROR>>>", error.toString());
        }
    }
}
