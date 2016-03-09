package com.piscesstudio.databasetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button reg = (Button) findViewById(R.id.reg_btn_signup);
        final EditText un = (EditText) findViewById(R.id.reg_username);
        final EditText pw = (EditText) findViewById(R.id.reg_pw1);


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = un.getText().toString().trim();
                final String password = pw.getText().toString().trim();

                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.signUpInBackground(RegisterActivity.this);
            }
        });
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
