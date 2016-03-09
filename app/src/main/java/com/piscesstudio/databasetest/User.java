package com.piscesstudio.databasetest;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.piscesstudio.databasetest.Callback.ResultCallback;

import java.util.HashMap;
import java.util.Set;

public class User extends VorkObject {

    public User() {
        setObjectClass(this.getClass());
        setPrimaryKey("user_id");
    }

    public String getUserID() {
        return get(getPrimaryKey());
    }

    public String getUsername() {
        return get("username");
    }

    public void setUsername(String username) {
        put("username", username);
    }

    public String getPassword() {
        return get("password");
    }

    public void setPassword(String password) {
        put("password", password);
    }

    public String getFirstName() {
        return get("first_name");
    }

    public void setFirstName(String firstName) {
        put("first_name", firstName);
    }

    public String getLastName() {
        return get("last_name");
    }

    public void setLastName(String lastName) {
        put("last_name", lastName);
    }

    public static String getProfilePicURL(){
        return  "http://192.168.1.100:8080/uploads/" + App.session.getUserID() + ".jpg";
    }

    public static void updateProfilePicture(String imagePath, final ResultCallback callback){
        new UploadImageInBackground(callback).execute(imagePath);
    }

    public void signUpInBackground(final Context context){
        final HashMap<String, String> object = getHashMap();
        Set<String> keys = object.keySet();
        String ATTRIBUTE = "";
        String VALUE = "";
        String SQL;
        String METHOD = "LOGIN";

        for (String attr : keys) {
            String value = object.get(attr);
            ATTRIBUTE += attr + ", ";
            VALUE += "'" + value + "', ";
        }

        ATTRIBUTE = " (" + ATTRIBUTE + "updated_at) ";
        VALUE = "(" + VALUE + "now())";
        SQL = "INSERT INTO " + this.getClass().getSimpleName() + ATTRIBUTE + "VALUES " + VALUE + ";";

        new RunInBackground<>(this.getClass(), new ResultCallback() {
            @Override
            public void done(Exception e) {
                if(e == null){
                    loginInBackground(context, getUsername(), getPassword());
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("<<<ERROR>>>", e.toString());
                }
            }
        }).execute(SQL, METHOD);
    }

    public static void loginInBackground(Context context, String username, String password){
        new LoginInBackground(context).execute(username, password);
    }

    public static void logoutInBackground(final Context context){
        String METHOD = "DELETE";
        String SQL = "DELETE FROM session WHERE user_id = '" + App.session.getUserID() + "' AND token = '" + App.session.getToken() + "';";

        new RunInBackground<>(User.class, new ResultCallback() {
            @Override
            public void done(Exception e) {
                if (e == null || e.getMessage().equals("Invalid Session. Please Logout and Login Again")) {
                    Toast.makeText(context, "Logout Successfully", Toast.LENGTH_LONG).show();
                    new SessionManager(context).clearSession();
                    context.startActivity(new Intent(context, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    ((Activity) context).finish();
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).execute(SQL, METHOD);
    }

}