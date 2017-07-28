package com.mrerror.tm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mrerror.tm.adapter.InboxForStaffRecyclerViewAdapter;
import com.mrerror.tm.adapter.InboxRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Question;
import com.mrerror.tm.models.QuestionForStaff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.mrerror.tm.connection.NetworkConnection.url;

public class Inbox extends AppCompatActivity implements NetworkConnection.OnCompleteFetchingData {

    SharedPreferences sp;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getData(sp.getString("group","normal"));
    }

    private void getData(String group) {
        if(group.equals("normal")) {
            url = "http://educationplatform.pythonanywhere.com/api/asks/" + sp.getString("username", "");
            new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                @Override
                public void onCompleted(String result) throws JSONException {
                    JSONArray jsonArray = new JSONArray(result);
                    ArrayList<Question> questions = new ArrayList<>();
                    for(int i = 0 ; i < jsonArray.length();i++){
                        JSONObject questionObj = jsonArray.getJSONObject(i);
                        Question question;
                        question = new Question(questionObj.getString("question"), questionObj.getString("replay")
                                , questionObj.getString("image_staff"), questionObj.getString("file_staff"));
                        questions.add(question);
                    }
                    InboxRecyclerViewAdapter adapter = new InboxRecyclerViewAdapter(questions);
                    recyclerView.setAdapter(adapter);

                }
            }).getDataAsJsonArray(this);

        }else{
            url = "http://educationplatform.pythonanywhere.com/api/asks/";
            new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                @Override
                public void onCompleted(String result) throws JSONException {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    ArrayList<QuestionForStaff> questions = new ArrayList<>();
                    for(int i = 0 ; i < jsonArray.length();i++){
                        JSONObject questionObj = jsonArray.getJSONObject(i);
                        QuestionForStaff question = new QuestionForStaff(questionObj.getString("question"),
                                null,questionObj.getString("edit_url"),questionObj.getString("delete_url")
                                ,questionObj.getJSONObject("user").getString("username")
                        ,questionObj.getString("image_sender"),questionObj.getString("file_sender"));
                        questions.add(question);
                    }
                    InboxForStaffRecyclerViewAdapter adapter = new InboxForStaffRecyclerViewAdapter(questions);
                    recyclerView.setAdapter(adapter);
                }
            }).getDataAsJsonObject(this);
        }
        Log.e("urlll",url);
    }

    @Override
    public void onCompleted(String result) throws JSONException {

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
