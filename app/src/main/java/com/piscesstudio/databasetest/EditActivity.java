package com.piscesstudio.databasetest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.piscesstudio.databasetest.Callback.ResultCallback;
import com.piscesstudio.databasetest.Callback.GetCallback;

public class EditActivity extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView uid = (TextView) findViewById(R.id.edit_uid);
        final EditText fn = (EditText) findViewById(R.id.edit_fn);
        final EditText ln = (EditText) findViewById(R.id.edit_ln);
        final Button update = (Button) findViewById(R.id.edit_update);
        Button delete = (Button) findViewById(R.id.edit_dlt);
        final Button save = (Button) findViewById(R.id.edit_save);

        fn.setEnabled(false);
        ln.setEnabled(false);
        save.setEnabled(false);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fn.setEnabled(true);
                ln.setEnabled(true);
                save.setEnabled(true);
                update.setEnabled(false);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = fn.getText().toString().trim();
                String lname = ln.getText().toString().trim();

                user.setFirstName(fname);
                user.setLastName(lname);
                user.saveInBackground(new ResultCallback() {
                    @Override
                    public void done(Exception e) {
                        if(e == null){
                            Toast.makeText(EditActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("<<<ERROR>>>", e.toString());
                        }
                    }
                });


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditActivity.this);
                alertDialogBuilder.setTitle(null);

                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this user?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                user.deleteInBackground(new ResultCallback() {
                                    @Override
                                    public void done(Exception e) {
                                        if(e == null){
                                            Toast.makeText(EditActivity.this, "Delete Successfully", Toast.LENGTH_LONG).show();
                                            finish();
                                        } else {
                                            Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.e("<<<ERROR>>>", e.toString());
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        Intent i = getIntent();
        if(i != null && i.hasExtra(Intent.EXTRA_TEXT)){
            String user_id = i.getStringExtra(Intent.EXTRA_TEXT);
            Query<User> q = new Query<>(User.class);
            q.loadInBackground(user_id, new GetCallback<User>() {
                @Override
                public void done(User object, Exception e) {
                    if(e == null) {
                        user = object;
                        uid.setText(user.getUserID());
                        fn.setText(user.getFirstName());
                        ln.setText(user.getLastName());
                    } else {
                        Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

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
