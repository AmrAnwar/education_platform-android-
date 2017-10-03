package com.mrerror.tm;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.models.Test;
import com.mrerror.tm.models.TestChoices;

import java.util.ArrayList;

public class NewExamActivity extends AppCompatActivity implements View.OnClickListener {
    private Test mTest;


    Button next;
    //for choices
    TextView questionTV, choice1TV, choice2TV, choice3TV;
    ImageView choice1True, choice1False, choice2True, choice2False, choice3True, choice3False;
    int choicesCounter = 0;
    TestChoices choiceQuestion;
    String selectedAnswer = null;
    TextView selectedTextView;
    private ArrayList<TestChoices> testChoicesArrayList;
    private TextView mTimerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exam);
        getSupportActionBar().setTitle("Exam");
        mTest = getIntent().getParcelableExtra("test");
        next = (Button) findViewById(R.id.next_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next(v);
            }
        });
        testChoicesArrayList = mTest.getTestChoicesList();
        mTimerTV = (TextView) findViewById(R.id.timer);
        questionTV = (TextView) findViewById(R.id.question);
        choice1TV = (TextView) findViewById(R.id.choice_1);
        choice2TV = (TextView) findViewById(R.id.choice_2);
        choice3TV = (TextView) findViewById(R.id.choice_3);
        choice1True = (ImageView) findViewById(R.id.choice_1_true);
        choice1False = (ImageView) findViewById(R.id.choice_1_false);
        choice2True = (ImageView) findViewById(R.id.choice_2_true);
        choice2False = (ImageView) findViewById(R.id.choice_2_false);
        choice3True = (ImageView) findViewById(R.id.choice_3_true);
        choice3False = (ImageView) findViewById(R.id.choice_3_false);
        setChoicesData();
        startCountDownTimer();

    }

    private void startCountDownTimer() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTimerTV.setText(millisUntilFinished / 1000 +" Sec");
            }

            public void onFinish() {
                testChoicesArrayList.get(choicesCounter).setUser_answer(null);
                nextQuestion();
            }
        }.start();
    }

    private void setChoicesData() {
        choiceQuestion = testChoicesArrayList.get(choicesCounter);
        questionTV.setText(choiceQuestion.getQuestion());
        choice1TV.setText(choiceQuestion.getChoice1());
        choice2TV.setText(choiceQuestion.getChoice2());
        choice3TV.setText(choiceQuestion.getChoice3());
        choice1TV.setTextColor(NewExamActivity.this.getResources().getColor(R.color.black));
        choice2TV.setTextColor(NewExamActivity.this.getResources().getColor(R.color.black));
        choice3TV.setTextColor(NewExamActivity.this.getResources().getColor(R.color.black));
        choice1True.setVisibility(View.GONE);
        choice2True.setVisibility(View.GONE);
        choice3True.setVisibility(View.GONE);
        choice1False.setVisibility(View.GONE);
        choice2False.setVisibility(View.GONE);
        choice3False.setVisibility(View.GONE);
        choice1TV.setEnabled(true);
        choice2TV.setEnabled(true);
        choice3TV.setEnabled(true);
        choice1TV.setOnClickListener(this);
        choice3TV.setOnClickListener(this);
        choice2TV.setOnClickListener(this);
    }


    public void next(View view) {
        testChoicesArrayList.get(choicesCounter).setUser_answer(null);
        nextQuestion();
    }
    public void nextQuestion(){
        if ((choicesCounter + 1) < testChoicesArrayList.size()) {
            ++choicesCounter;
            setChoicesData();
            startCountDownTimer();
        } else {
            startActivity(new Intent(NewExamActivity.this,ExamResult.class).putExtra("tests",testChoicesArrayList));
            this.finish();
        }
    }
    @Override
    public void onClick(View v) {
        testChoicesArrayList.get(choicesCounter).setUser_answer(((TextView)v).getText().toString());
        Log.e("answerrr",testChoicesArrayList.get(choicesCounter).getUser_answer());
        nextQuestion();
    }
}
