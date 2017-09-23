package com.mrerror.tm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrerror.tm.adapter.ExamOfExerciseAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.ExamsForExercises;
import com.mrerror.tm.models.QuestionsTypeOfExercises;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ExamsForExercises> myItems;
    ExamOfExerciseAdapter adapter;
    String mUrl;
    SwipeRefreshLayout mSwiperRefersherLayout;
    ProgressBar mProgressBar;
    TextView  mBlankTextView;

    ExamOfExerciseAdapter.OnChiledClick onChiledClick=new ExamOfExerciseAdapter.OnChiledClick() {
        @Override
        public void onChiledClickOfExcercise(ExamsForExercises mExam, QuestionsTypeOfExercises mQuestion) {

            Intent intent=new Intent(ExerciseActivity.this,QuestionsForExerciseActivity.class);
            intent.putExtra("url",getString(R.string.domain)+"/api/study/exercise/?exam="+mExam.getmId()+"&filter="+mQuestion.getmType());
            intent.putExtra("title",mQuestion.getmTypeQuestion());
       startActivity(intent);
}
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excercise);

        initViews();
        getData();


    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mProgressBar= (ProgressBar) findViewById(R.id.progressbarEx);
        mBlankTextView=(TextView) findViewById(R.id.blanktextviewEx);

        mSwiperRefersherLayout= (SwipeRefreshLayout) findViewById(R.id.exerciseRefersher);
        mSwiperRefersherLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressBar.setVisibility(View.VISIBLE);
                myItems=new ArrayList<>();
                adapter=null;
                getData();
                mSwiperRefersherLayout.setRefreshing(false);
            }
        });


        setTitle("Exercise");
        mUrl=getString(R.string.domain)+"/api/study/exam/";
        recyclerView= (RecyclerView) findViewById(R.id.excerciserecycleView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myItems = new ArrayList<>();
    }

    @NonNull
    private List<QuestionsTypeOfExercises> getQuestionsOfExcercises() {
        return Arrays.
                    asList(new QuestionsTypeOfExercises("Situation",1),
                    new QuestionsTypeOfExercises("Dialog",2),
                    new QuestionsTypeOfExercises("Choices",3),
            new QuestionsTypeOfExercises("Find the mistake",4),
            new QuestionsTypeOfExercises("Article",5),
        new QuestionsTypeOfExercises("Story",6),
        new QuestionsTypeOfExercises("Translation",7),
        new QuestionsTypeOfExercises("Paragraph",8));
    }

  public  void  getData(){

      NetworkConnection.url=mUrl;
      new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
          @Override
          public void onCompleted(String result) throws JSONException {
              mProgressBar.setVisibility(View.GONE);
              mBlankTextView.setVisibility(View.GONE);
              JSONArray jasArray=new JSONArray(result);
              for(int i=0;i<jasArray.length();i++){
                  JSONObject obj=jasArray.getJSONObject(i);
                  myItems.add( new ExamsForExercises(obj.getString("title"),getQuestionsOfExcercises(),obj.getInt("id")));
              }
              if(adapter==null){
                  adapter=new ExamOfExerciseAdapter(myItems,onChiledClick);
                  recyclerView.setAdapter(adapter);

              }
              if(myItems.size()==0){
                  mBlankTextView.setText(getString(R.string.empty_list));
                  mBlankTextView.setVisibility(View.VISIBLE);
              }

          }

          @Override
          public void onError(String error) {
              mProgressBar.setVisibility(View.GONE);
              mBlankTextView.setText(getString(R.string.error));
              mBlankTextView.setVisibility(View.VISIBLE);
          }
      }).getDataAsJsonArray(this);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
