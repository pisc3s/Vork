package com.piscesstudio.databasetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.piscesstudio.databasetest.Utility.VolleyUtility;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView img = (ImageView) findViewById(R.id.profile_img);
        TextView fn = (TextView) findViewById(R.id.profile_fn);
        TextView ln = (TextView) findViewById(R.id.profile_ln);

        //TODO: The cache remain after update profile pic. Must make the image file name unique.
        //Use of Volley
        ImageLoader imageLoader = VolleyUtility.getInstance(this).getImageLoader();
        imageLoader.get(User.getProfilePicURL(), ImageLoader.getImageListener(img, R.drawable.pic, R.drawable.te));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
