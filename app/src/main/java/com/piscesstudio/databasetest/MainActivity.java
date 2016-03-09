package com.piscesstudio.databasetest;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.piscesstudio.databasetest.Callback.FetchCallback;
import com.piscesstudio.databasetest.Callback.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private HomeAdapter mAdapter;

    String imagePath;
    private static int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(App.session == null){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mAdapter = new HomeAdapter(MainActivity.this, new ArrayList<User>());
        mRecyclerView.setAdapter(mAdapter);

        Button select = (Button) findViewById(R.id.btn_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });

        final EditText input = (EditText) findViewById(R.id.input);
        Button go = (Button) findViewById(R.id.btn_go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                load(input.getText().toString().trim());
            }
        });

//        Query q = new Query(User.class.getSimpleName());
//        q.whereEqualsTo("user_id", 1000000002);
//        q.orderByAsc("user_id");
//        q.build();
//
//        Query qq = new Query(User.class.getSimpleName());
//        qq.whereEqualsTo("username", "skcc");
//        qq.orderByAsc("user_id");
//        qq.build();
//
//        Query q1 = new Query(User.class.getSimpleName());
//        q1.whereNotEqualsTo("username", "skc");
//        q1.orderByDesc("user_id");
//        q1.build();
//
//        Query q11 = new Query(User.class.getSimpleName());
//        q11.whereNotEqualsTo("user_id", 1000000002);
//        q11.orderByDesc("user_id");
//        q11.build();
//
//        Query q2 = new Query(Session.class.getSimpleName());
//        q2.join(User.class.getSimpleName(), "user_id");
//        q2.whereLessThan("user_id", 1000000011);
//        q2.whereGreaterThan("created_at", "2016-02-18 17:50:00");
//        q2.build();
//
//        Query q3 = new Query(User.class.getSimpleName());
//        q3.whereGreaterThan("user_id", 1000000005);
//        q3.orderByAsc("user_id");
//        q3.build();
//
//        Query q33 = new Query(User.class.getSimpleName());
//        q33.whereGreaterThanOrEqualTo("user_id", 1000000008);
//        q33.orderByAsc("user_id");
//        q33.build();
//
//        Query q4 = new Query(User.class.getSimpleName());
//        q4.whereLessThan("user_id", 1000000005);
//        q4.orderByDesc("user_id");
//        q4.build();
//
//        Query q44 = new Query(User.class.getSimpleName());
//        q44.whereLessThanOrEqualTo("user_id", 1000000009);
//        q44.orderByDesc("user_id");
//        q44.build();
//
//        Query q444 = new Query(User.class.getSimpleName());
//        q444.whereLessThanOrEqualTo("user_id", 1000000009);
//        q444.whereNotEqualsTo("user_id", 1000000002);
//        q444.whereLike("first_name", "%e%");
//        q444.orderByDesc("user_id");
//        q444.build();

    }

    public void loadImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    User.updateProfilePicture(imagePath, new ResultCallback() {
                        @Override
                        public void done(Exception e) {
                            if (e == null) {
                                Toast.makeText(MainActivity.this, "Upload Successfully", Toast.LENGTH_LONG).show();
                                Log.e(">>>>>>", "Upload Successfully");
                            } else {
                                Log.e(">>>>>>>>>", e.toString());
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(">>>>>>>>>>", e.toString());
            }
        }
    }

    private void load(){
        new Query<User>(User.class).loadInBackground(new FetchCallback<User>() {
            @Override
            public void done(List<User> object, Exception e) {
                if (e == null) {
                    if (mAdapter != null) {
                        mAdapter.refreshAdapter(object);
                    } else {
                        mAdapter = new HomeAdapter(MainActivity.this, object);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("<<<ERROR>>>1", e.toString());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_profile){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_insert) {
            loadImageFromGallery();
            return true;
        } else if(id == R.id.action_logout){
            User.logoutInBackground(MainActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
