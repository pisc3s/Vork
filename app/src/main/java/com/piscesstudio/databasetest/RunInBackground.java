package com.piscesstudio.databasetest;

import android.net.Uri;
import android.os.AsyncTask;

import com.piscesstudio.databasetest.Callback.FetchCallback;
import com.piscesstudio.databasetest.Callback.ResultCallback;
import com.piscesstudio.databasetest.Callback.GetCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunInBackground<T extends VorkObject> extends AsyncTask<String, Void, List<T>> {
    private Class clazz;
    private Exception error = null;
    private FetchCallback<T> delegate;
    private ResultCallback delegate2;
    private GetCallback<T> delegate3;
    private final String SELECT_METHOD = "SELECT";
    private final String LOGIN_METHOD = "LOGIN";

    public RunInBackground(Class clazz, FetchCallback<T> callback) {
        this.clazz = clazz;
        this.delegate = callback;
    }

    public RunInBackground(Class clazz, ResultCallback callback){
        this.clazz = clazz;
        this.delegate2 = callback;
    }

    public RunInBackground(Class clazz, GetCallback<T> callback){
        this.clazz = clazz;
        this.delegate3 = callback;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<T> doInBackground(String... arg0) {
        List<T> listT = new ArrayList<>();
        final String param1 = "token";
        final String param2 = "sql";
        final String param3 = "method";
        final String RUN_METHOD = arg0[1];
        String output;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("192.168.1.100:8080")
                    .appendPath("run.php");
            if(RUN_METHOD.equals(LOGIN_METHOD)){
                builder.appendQueryParameter(param1, "")
                        .appendQueryParameter(param2, arg0[0])
                        .appendQueryParameter(param3, RUN_METHOD);
            } else {
                builder.appendQueryParameter(param1, App.session.getToken())
                        .appendQueryParameter(param2, arg0[0])
                        .appendQueryParameter(param3, RUN_METHOD);
            }
            String link = builder.build().toString();

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder("");
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            output = sb.toString().trim();

            JSONObject main = new JSONObject(output);
            int code = main.getInt("code");
            String message = main.getString("message");
            if(code == 200) {
                if(RUN_METHOD.equals(SELECT_METHOD)) {
                    JSONArray array = main.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        T t = (T) clazz.newInstance();
                        for (Iterator<String> iter = obj.keys(); iter.hasNext(); ) {
                            String key = iter.next();
                            String value = obj.getString(key);
                            t.put(key, value);
                        }
                        listT.add(t);
                    }
                }
            } else {
                throw new Exception(message);
            }

            bufferedReader.close();
        } catch (Exception e) {
            error = e;
        }
        return listT;
    }

    @Override
    protected void onPostExecute(List<T> result) {
        if(delegate != null && delegate2 == null && delegate3 == null) {
            delegate.done(result, error);
        } else if(delegate == null && delegate2 != null && delegate3 == null){
            delegate2.done(error);
        } else {
            if(result.size() != 0) {
                delegate3.done(result.get(0), error);
            } else {
                delegate3.done(null, error);
            }
        }
    }
}
