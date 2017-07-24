package com.mrerror.tm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mrerror.tm.adapter.InboxRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Inbox extends AppCompatActivity implements NetworkConnection.OnCompleteFetchingData {

    InboxRecyclerViewAdapter adapter;
    ArrayList<Question> questions;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        questions = new ArrayList<>();
        adapter = new InboxRecyclerViewAdapter(questions);
        recyclerView.setAdapter(adapter);
        getData();
    }

    private void getData() {
        NetworkConnection.url = "http://educationplatform.pythonanywhere.com/api/asks/"+sp.getString("username","");
        Log.e("hiii",NetworkConnection.url);
        new NetworkConnection(this).getDataAsJsonArray(this);
    }

    @Override
    public void onCompleted(String result) throws JSONException {
        Log.e("hiii",result);
        JSONArray jsonArray = new JSONArray(result);
        questions = new ArrayList<>();
        for(int i = 0 ; i < jsonArray.length();i++){
            JSONObject questionObj = jsonArray.getJSONObject(i);
            Question question = new Question(questionObj.getString("question"),questionObj.getString("replay"));
            questions.add(question);
        }
        adapter.newData(questions);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
