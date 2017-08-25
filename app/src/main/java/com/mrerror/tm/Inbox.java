package com.mrerror.tm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mrerror.tm.adapter.InboxForStaffRecyclerViewAdapter;
import com.mrerror.tm.adapter.InboxRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.fragments.LoadMoreData;
import com.mrerror.tm.models.Question;
import com.mrerror.tm.models.QuestionForStaff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.mrerror.tm.connection.NetworkConnection.url;

public class Inbox extends AppCompatActivity implements NetworkConnection.OnCompleteFetchingData, ReplyForStaffActivity.onReply {

    SharedPreferences sp;
    private RecyclerView recyclerView;
    String nextURl = "";
    LoadMoreData loadMoreData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int scrolFalg = 0;
    public static ReplyForStaffActivity.onReply replyListener;
    String urlForStuff = "http://educationplatform.pythonanywhere.com/api/asks/";
    InboxForStaffRecyclerViewAdapter adapter;
    ArrayList<QuestionForStaff> questionsForStuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        scrolFalg = 0;
        loadMoreData = new LoadMoreData() {
            @Override
            public void loadMorData(String url) {
                urlForStuff = nextURl;
                getData(sp.getString("group", ""));
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshnewsunit);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        replyListener = this;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(sp.getString("group", "normal"));
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int x = linearLayoutManager.findLastVisibleItemPosition();

                if (x % 4 == 0 && x >= 4 && x > scrolFalg && !nextURl.equals("null") && !nextURl.isEmpty()) {
                    {
                        loadMoreData.loadMorData(nextURl);

                        scrolFalg = x;
                    }
                }


            }
        });
        getData(sp.getString("group", "normal"));
    }

    private void getData(String group) {
        questionsForStuff = new ArrayList<>();
        if (group.equals("normal")) {
            url = "http://educationplatform.pythonanywhere.com/api/asks/" + sp.getString("username", "");
            new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                @Override
                public void onCompleted(String result) throws JSONException {
                    JSONArray jsonArray = new JSONArray(result);
                    ArrayList<Question> questions = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject questionObj = jsonArray.getJSONObject(i);
                        Question question;
                        question = new Question(questionObj.getString("question"), questionObj.getString("replay")
                                , questionObj.getString("image_staff"), questionObj.getString("file_staff"));
                        questions.add(question);
                    }
                    InboxRecyclerViewAdapter adapter = new InboxRecyclerViewAdapter(questions);
                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onError(String error) {

                }
            }).getDataAsJsonArray(this);

        } else {
            url = urlForStuff;

            new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                @Override
                public void onCompleted(String result) throws JSONException {
                    JSONObject jsonObject = new JSONObject(result);
                    nextURl = jsonObject.getString("next");
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject questionObj = jsonArray.getJSONObject(i);
                        QuestionForStaff question = new QuestionForStaff(questionObj.getString("question"),
                                null, questionObj.getString("edit_url"), questionObj.getString("delete_url")
                                , questionObj.getJSONObject("user").getString("username")
                                , questionObj.getString("image_sender"), questionObj.getString("file_sender"));
                        questionsForStuff.add(question);
                    }
                    adapter = new InboxForStaffRecyclerViewAdapter(questionsForStuff);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String error) {

                }
            }).getDataAsJsonObject(this);
        }
        Log.e("urlll", url);
    }

    @Override
    public void onCompleted(String result) throws JSONException {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onReply() {
        getData(sp.getString("group", "normal"));
    }
}
