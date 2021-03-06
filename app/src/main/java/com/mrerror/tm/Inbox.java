package com.mrerror.tm;

import android.content.Intent;
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

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.mrerror.tm.connection.NetworkConnection.url;

public class Inbox extends AppCompatActivity implements NetworkConnection.OnCompleteFetchingData, ReplyForStaffActivity.onReply {

    SharedPreferences sp;
    private RecyclerView recyclerView;
    String nextURl = "";
    LoadMoreData loadMoreData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int scrolFalg = 0;
    int countAll = 0;
    public static ReplyForStaffActivity.onReply replyListener;
    String urlForStuff = "";
    InboxForStaffRecyclerViewAdapter adapter;
    ArrayList<QuestionForStaff> questionsForStuff;
    private boolean refreshing = false;
    private int firstVisibleInListview;
    private SharedPreferences.Editor editor;
    private String urlForPublic="";
    public static Boolean isPublic=false;
    ArrayList<Question> questions;
    InboxRecyclerViewAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        urlForStuff = getString(R.string.domain)+"/api/asks/";
        urlForPublic=getString(R.string.domain)+"/api/asks/?public=true";
        questions=new ArrayList<>();
        Intent mIntent=getIntent();
       isPublic= mIntent.getBooleanExtra("fp",false);
        if(isPublic){
            setTitle("Public Questions");
        }

        scrolFalg = 0;
        loadMoreData = new LoadMoreData() {
            @Override
            public void loadMorData(String url) {
               if(isPublic){
                   urlForPublic=nextURl;
               }else{ urlForStuff = nextURl;}
                getData(sp.getString("group", ""));
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshnewsunit);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        replyListener = this;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing = true;
                questions=null;
                pAdapter=null;
                urlForPublic=getString(R.string.domain)+"/api/asks/?public=true" ;

                questionsForStuff = null;
                adapter = null;
                scrolFalg = 0;

                urlForStuff =  getString(R.string.domain)+"/api/asks/";
                nextURl = "null";
                getData(sp.getString("group", "normal"));
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        if(!sp.getString("group","normal").equals("normal")){
            editor.putInt("questionsCount",0);
            editor.commit();
            ShortcutBadger.applyCount(this, sp.getInt("questionsCount",0));
        }else{
            editor.putInt("questionsAnswersCount",0);
            editor.commit();
        }


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentFirstVisible = linearLayoutManager.findFirstVisibleItemPosition();

                if (currentFirstVisible > firstVisibleInListview){
                    Log.i("RecyclerView scrolled: ", "scroll up!");
                if (!sp.getString("group", "normal").equals("normal")) {
                    if (((currentFirstVisible % 9 == 0 && currentFirstVisible >= 9 && currentFirstVisible > scrolFalg) || questionsForStuff.size() < countAll) && !nextURl.equals("null") && !nextURl.isEmpty()) {
                        {
                            loadMoreData.loadMorData(nextURl);

                            scrolFalg = currentFirstVisible;
                        }
                    }
                }
            }else {
                    Log.i("RecyclerView scrolled: ", "scroll down!");
                }

                firstVisibleInListview = currentFirstVisible;


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });


        getData(sp.getString("group", "normal"));
    }

    private void getData(String group) {

        if(isPublic){
            getPublicData();

        }
        else {

            if (group.equals("normal")) {
                getNormalData();

            } else {
                getStaffData();
            }
        }
            Log.e("urlll", url);

    }

    private void  getPublicData(){

        url = urlForPublic;
        nextURl = "null";


        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
            @Override
            public void onCompleted(String result) throws JSONException {
                if (questions == null) {
                questions = new ArrayList<>();
                }
                JSONObject jsonObject = new JSONObject(result);
                nextURl = jsonObject.getString("next");
                Log.d("hell", url);
                countAll = jsonObject.getInt("count");
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject questionObj = jsonArray.getJSONObject(i);
                    Question question = new Question(questionObj.getString("question"), questionObj.getString("replay")
                            , questionObj.getString("image_sender"), questionObj.getString("file_sender")
                    ,questionObj.getString("timestamp"));
                    questions.add(question);
                }


                if (pAdapter == null) {
                    pAdapter = new InboxRecyclerViewAdapter(questions);
                    recyclerView.setAdapter(pAdapter);
                }


                if(refreshing){
                    refreshing=false;
                }

                pAdapter.onChange(questions);
                pAdapter.notifyDataSetChanged();
                if(refreshing){
                    refreshing=false;
                }
            }

            @Override
            public void onError(String error) {

            }
        }).getDataAsJsonObject(this);

    }

// this method for staff priavte inBox Question
    private void getStaffData() {
        nextURl = "null";
        url = urlForStuff;

        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
            @Override
            public void onCompleted(String result) throws JSONException {
                if (questionsForStuff == null) {
                    questionsForStuff = new ArrayList<>();
                }
                JSONObject jsonObject = new JSONObject(result);
                nextURl = jsonObject.getString("next");
                Log.d("hell", url);
                countAll = jsonObject.getInt("count");
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject questionObj = jsonArray.getJSONObject(i);
                    QuestionForStaff question = new QuestionForStaff(questionObj.getString("question"),
                            null, questionObj.getString("edit_url"), questionObj.getString("delete_url")
                            , questionObj.getJSONObject("user").getString("username")
                            , questionObj.getString("image_sender"), questionObj.getString("file_sender")
                    ,questionObj.getString("timestamp"));
                    questionsForStuff.add(question);
                }


                if (adapter == null) {
                    adapter = new InboxForStaffRecyclerViewAdapter(questionsForStuff);
                    recyclerView.setAdapter(adapter);
                }


                adapter.onChange(questionsForStuff);
                adapter.notifyDataSetChanged();
                if(refreshing){
                    refreshing=false;
                }
            }

            @Override
            public void onError(String error) {

            }
        }).getDataAsJsonObject(this);
    }

    // this for student private inbox
    private void getNormalData() {
        url = getString(R.string.domain)+"/api/asks/" + sp.getString("username", "");
        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
            @Override
            public void onCompleted(String result) throws JSONException {

                JSONArray jsonArray = new JSONArray(result);
                ArrayList<Question> questions = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject questionObj = jsonArray.getJSONObject(i);
                    Question question;
                    question = new Question(questionObj.getString("question"), questionObj.getString("replay")
                            , questionObj.getString("image_staff"), questionObj.getString("file_staff"),
                            "");
                    questions.add(question);
                }
                InboxRecyclerViewAdapter adapter = new InboxRecyclerViewAdapter(questions);
                recyclerView.setAdapter(adapter);
                if(refreshing){
                    refreshing=false;
                }
            }

            @Override
            public void onError(String error) {

            }
        }).getDataAsJsonArray(this);
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

        questionsForStuff = null;
        getData(sp.getString("group", "normal"));
    }

}
