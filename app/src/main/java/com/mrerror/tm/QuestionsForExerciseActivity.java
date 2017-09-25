package com.mrerror.tm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrerror.tm.adapter.QuestionForExerciseAdapater;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.ExerciseQuestions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestionsForExerciseActivity extends AppCompatActivity {
    RecyclerView recyclerView;
   String mUrl;
    ArrayList<ExerciseQuestions> myQuestions;
    QuestionForExerciseAdapater mAdapater;

    SwipeRefreshLayout mSwiperRefersherLayout;
    ProgressBar mProgressBar;
    TextView mBlankTextView;
    SharedPreferences sp;
    CheckBox cFav;

    QuestionForExerciseAdapater.OnShowAnswer onShowAnswer= new QuestionForExerciseAdapater.OnShowAnswer(){
        public void onShowAnswer(ExerciseQuestions question){

            AlertDialog.Builder dialog = new AlertDialog.Builder(QuestionsForExerciseActivity.this);
            dialog.setTitle("Answer :");
            dialog.setMessage(question.getAnswer());
            dialog.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(true);
            dialog.show();
        }


    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_for_excercise);
        Intent intent=getIntent();
        mUrl=intent.getStringExtra("url");
        setTitle(intent.getStringExtra("title"));
        initView();
        getData();
    }



    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        cFav= (CheckBox) findViewById(R.id.fav_question_for_exercises);
        cFav.setOnClickListener(cFavOnClick);
        mProgressBar= (ProgressBar) findViewById(R.id.progressbarQu);
        mBlankTextView=(TextView) findViewById(R.id.blanktextviewQu);
        sp= PreferenceManager.getDefaultSharedPreferences(this);

        mSwiperRefersherLayout= (SwipeRefreshLayout) findViewById(R.id.questionExRefersher);
        mSwiperRefersherLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mProgressBar.setVisibility(View.VISIBLE);



                if(cFav.isChecked()){getFavData();}else {
                    myQuestions=new ArrayList<>();
                    mAdapater=null;
                getData();}
                mSwiperRefersherLayout.setRefreshing(false);
            }
        });



        myQuestions=new ArrayList<>();
        recyclerView=(RecyclerView) findViewById(R.id.recycleViewForQuestionForExcercise);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }




    View.OnClickListener cFavOnClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(cFav.isChecked()){
                getFavData();
            }else
                {
                mAdapater.onChange(myQuestions);
                    mAdapater.notifyDataSetChanged();
            }
        }
    };

    private  void getFavData(){
        NetworkConnection.url=mUrl+"&user_id="+sp.getInt("id",0);
        Log.d("chekFav",NetworkConnection.url);
        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData()
        {
            @Override
            public void onCompleted(String result) throws JSONException {
                mProgressBar.setVisibility(View.GONE);
                ArrayList<ExerciseQuestions> favQuestions=new ArrayList<>();

                JSONArray questionArray = new JSONArray(result);
                for (int i = 0; i < questionArray.length(); i++) {
                    JSONObject object = questionArray.getJSONObject(i);
                    ExerciseQuestions ref=new ExerciseQuestions();
                    ref.setQuestion(object.getString("question"));
                    ref.setAnswer(object.getString("answer"));
                    ref.setId(object.getInt("id"));
                    ref.setmUsers(object.getJSONArray("users"));
                    ref.setSp(sp);
                    favQuestions.add(ref);
                }
                mAdapater.onChange(favQuestions);

                if(favQuestions.size()==0){
                    mBlankTextView.setText(getString(R.string.empty_list));
                    mBlankTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {

            }
        }).getDataAsJsonArray(this);


    }
















    public void getData(){
      NetworkConnection.url=mUrl;
        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
            @Override
            public void onCompleted(String result) throws JSONException {
                mProgressBar.setVisibility(View.GONE);
                mBlankTextView.setVisibility(View.GONE);
                JSONArray jsonArray=new JSONArray(result);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject object=jsonArray.getJSONObject(i);
                    ExerciseQuestions ref=new ExerciseQuestions();
                    ref.setQuestion(object.getString("question"));
                    ref.setAnswer(object.getString("answer"));
                    ref.setId(object.getInt("id"));
                    ref.setmUsers(object.getJSONArray("users"));
                    ref.setSp(sp);
                    myQuestions.add(ref);
                }
                mAdapater=new QuestionForExerciseAdapater(myQuestions,onShowAnswer);
                recyclerView.setAdapter(mAdapater);
                if(myQuestions.size()==0){
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
