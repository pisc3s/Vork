package com.piscesstudio.databasetest;

import com.piscesstudio.databasetest.Callback.ResultCallback;

import java.util.HashMap;
import java.util.Set;

public class VorkObject {

    private HashMap<String, String> object = new HashMap<>();
    private Class CLASS;
    private String PRIMARY_KEY;

    public VorkObject(){}

    protected String get(String key){
        return object.get(key);
    }

    protected void put(String key, String value){
        object.put(key, value);
    }

    protected void setObjectClass(Class CLASS){
        this.CLASS = CLASS;
    }

    protected Class getObjectClass(){
        return CLASS;
    }

    protected void setPrimaryKey(String PRIMARY_KEY){
        this.PRIMARY_KEY = PRIMARY_KEY;
    }

    protected String getPrimaryKey(){
        return PRIMARY_KEY;
    }

    protected String getUpdatedAt() {
        return get("updated_at");
    }

    protected String getCreatedAt() {
        return get("created_at");
    }

    protected HashMap<String, String> getHashMap(){
        return object;
    }

    protected void saveInBackground(final ResultCallback callback){
        Set<String> keys = object.keySet();
        String ATTRIBUTE = "";
        String VALUE = "";
        String UPDATE_DATA = "";
        String SQL;
        String METHOD;

        if(object.containsKey("created_at")){
            METHOD = "UPDATE";
            for (String attr : keys) {
                if(!attr.equals("updated_at")){
                    String value = object.get(attr);
                    UPDATE_DATA += attr + " = '" + value + "', ";
                }
            }
            UPDATE_DATA += "updated_at = now() ";
            SQL = "UPDATE " + CLASS.getSimpleName() + " SET " + UPDATE_DATA + "WHERE " + PRIMARY_KEY + " = '" + object.get(PRIMARY_KEY) + "';";
        } else {
            METHOD = "INSERT";
            for (String attr : keys) {
                String value = object.get(attr);
                ATTRIBUTE += attr + ", ";
                VALUE += "'" + value + "', ";
            }
            ATTRIBUTE = " (" + ATTRIBUTE + "updated_at) ";
            VALUE = "(" + VALUE + "now())";
            SQL = "INSERT INTO " + CLASS.getSimpleName() + ATTRIBUTE + "VALUES " + VALUE + ";";
        }

        new RunInBackground<>(CLASS, new ResultCallback() {
            @Override
            public void done(Exception e) {
                callback.done(e);
            }
        }).execute(SQL, METHOD);
    }

    protected void deleteInBackground(final ResultCallback callback){
        String METHOD = "DELETE";
        String SQL = "DELETE FROM " + CLASS.getSimpleName() + " WHERE " + PRIMARY_KEY + " = '" + object.get(PRIMARY_KEY) + "';";

        new RunInBackground<>(CLASS, new ResultCallback() {
            @Override
            public void done(Exception e) {
                callback.done(e);
            }
        }).execute(SQL, METHOD);
    }
}
