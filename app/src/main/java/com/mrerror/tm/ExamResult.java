package com.mrerror.tm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.mrerror.tm.adapter.ResultRecyclerViewAdapter;
import com.mrerror.tm.models.TestChoices;

import java.util.ArrayList;

public class ExamResult extends AppCompatActivity {

    private static ArrayList<TestChoices> testChoicesArrayList;
    private static TextView resultScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);
        testChoicesArrayList = getIntent().getParcelableArrayListExtra("tests");
        ResultRecyclerViewAdapter adapter = new ResultRecyclerViewAdapter(testChoicesArrayList);
        RecyclerView result = (RecyclerView) findViewById(R.id.list);
        result.setLayoutManager(new LinearLayoutManager(this));
        result.setAdapter(adapter);
        resultScore = (TextView) findViewById(R.id.score);
    }
    public static void setScore(){

        Log.e("testChoicesArrayList",testChoicesArrayList.size()+"");
        Log.e("wrong",ResultRecyclerViewAdapter.wrongAnswers+"");
        resultScore.setText("Your Score is "+(testChoicesArrayList.size()-ResultRecyclerViewAdapter.wrongAnswers)+"/"+testChoicesArrayList.size());

    }
}
