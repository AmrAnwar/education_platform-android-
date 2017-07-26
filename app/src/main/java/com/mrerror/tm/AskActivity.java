package com.mrerror.tm;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mrerror.tm.connection.NetworkConnection;

import org.json.JSONException;

public class AskActivity extends AppCompatActivity implements NetworkConnection.OnCompleteFetchingData {

    private EditText question;
    SharedPreferences sp ;
    int user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Have a question?");
        setSupportActionBar(toolbar);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = sp.getInt("id",0);
        Button ask = (Button) findViewById(R.id.send);
        question = (EditText) findViewById(R.id.question);
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getText().toString().length() <= 7) {
                    Toast.makeText(AskActivity.this, "Very short question!", Toast.LENGTH_SHORT).show();
                } else {
                    String url = "http://educationplatform.pythonanywhere.com/api/asks/create/";
                    new NetworkConnection(AskActivity.this).postData(AskActivity.this, url, new String[]{"user", "question"},
                            new String[]{String.valueOf(user_id), question.getText().toString()});
                }
            }
        });
    }

    @Override
    public void onCompleted(String result) throws JSONException {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Ask question!");
        dialog.setMessage("Your question has been delivered!");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AskActivity.this.finish();
            }
        });
        dialog.setCancelable(false);
        dialog.setNegativeButton("Another question?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
