package com.piscesstudio.databasetest;

public class Session extends VorkObject{

    public Session(String user_id, String token) {
        setObjectClass(this.getClass());
        setPrimaryKey("session_id");
        setUserID(user_id);
        setToken(token);
    }

    public String getSessionID(){
        return get(getPrimaryKey());
    }

    public String getToken() {
        return get("token");
    }

    public void setToken(String token) {
        put("token", token);
    }

    public String getUserID() {
        return get("user_id");
    }

    public void setUserID(String user_id) {
        put("user_id", user_id);
    }
}
