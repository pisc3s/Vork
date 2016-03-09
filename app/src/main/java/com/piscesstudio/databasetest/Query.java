package com.piscesstudio.databasetest;

import com.piscesstudio.databasetest.Callback.FetchCallback;
import com.piscesstudio.databasetest.Callback.GetCallback;

import java.util.ArrayList;
import java.util.List;

public class Query<T extends VorkObject> {
    private Class CLASS;
    private String CLASS_NAME;
    private String query;
    private ArrayList<String> QUERY_WHERE = new ArrayList<>();
    private ArrayList<String> QUERY_CONDITION = new ArrayList<>();
    private final String SELECT_METHOD = "SELECT";

    public Query(Class CLASS) {
        this.CLASS = CLASS;
        this.CLASS_NAME = this.CLASS.getSimpleName();
        query = "SELECT * FROM " + CLASS_NAME + " ";

    }

    public void loadInBackground(final FetchCallback<T> callback){
        new RunInBackground<>(CLASS, new FetchCallback<T>() {
            @Override
            public void done(List<T> object, Exception e) {
                callback.done(object, e);
            }
        }).execute(build(), SELECT_METHOD);
    }

    @SuppressWarnings("unchecked")
    public void loadInBackground(String objectId, final GetCallback<T> callback){
        try {
            T t = (T) CLASS.newInstance();
            whereEqualsTo(t.getPrimaryKey(), objectId);
            new RunInBackground<>(CLASS, new GetCallback<T>() {
                @Override
                public void done(T object, Exception e) {
                    callback.done(object, e);
                }
            }).execute(build(), SELECT_METHOD);
        } catch (Exception e){
            callback.done(null, e);
        }
    }

    private String build(){
        if(QUERY_WHERE.size() > 0){
            query += "WHERE ";
            if(QUERY_WHERE.size() == 1){
                query += QUERY_WHERE.get(0);
            } else {
                for(int i = 0; i < QUERY_WHERE.size(); i++){
                    if(i == QUERY_WHERE.size() - 1){
                        query += QUERY_WHERE.get(i);
                    } else {
                        query += QUERY_WHERE.get(i) + "AND ";
                    }
                }
            }
        }
        if(QUERY_CONDITION.size() > 0){
            for(int i = 0; i < QUERY_CONDITION.size(); i++){
                query += QUERY_CONDITION.get(i);
            }
        }
        query += ";";
        return query;
    }

    public void whereEqualsTo(String attr, String value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " = '" + value + "' ");
    }

    public void whereEqualsTo(String attr, int value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " = " + value + " ");
    }

    public void whereNotEqualsTo(String attr, String value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " <> '" + value + "' ");
    }

    public void whereNotEqualsTo(String attr, int value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " <> " + value + " ");
    }

    public void whereGreaterThan(String attr, String value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " > '" + value + "' ");
    }

    public void whereGreaterThan(String attr, int value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " > " + value + " ");
    }

    public void whereLessThan(String attr, String value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " < '" + value + "' ");
    }

    public void whereLessThan(String attr, int value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " < " + value + " ");
    }

    public void whereGreaterThanOrEqualTo(String attr, String value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " >= '" + value + "' ");
    }

    public void whereGreaterThanOrEqualTo(String attr, int value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " >= " + value + " ");
    }

    public void whereLessThanOrEqualTo(String attr, String value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " <= '" + value + "' ");
    }

    public void whereLessThanOrEqualTo(String attr, int value){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " <= " + value + " ");
    }

    public void whereLike(String attr, String wildcard){
        QUERY_WHERE.add(CLASS_NAME + "." + attr + " LIKE '" + wildcard + "' ");
    }

    public void orderByDesc(String attr){
        QUERY_CONDITION.add("ORDER BY " + CLASS_NAME + "." +  attr + " DESC ");
    }

    public void orderByAsc(String attr){
        QUERY_CONDITION.add("ORDER BY " + CLASS_NAME + "." + attr + " ASC ");
    }
}
