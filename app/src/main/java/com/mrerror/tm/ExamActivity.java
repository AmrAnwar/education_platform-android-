package com.mrerror.tm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mrerror.tm.fragments.choicesFragment;
import com.mrerror.tm.fragments.completeFragment;
import com.mrerror.tm.fragments.dialogFragment;
import com.mrerror.tm.fragments.mistakeFragment;
import com.mrerror.tm.fragments.testpartFragment;
import com.mrerror.tm.models.Test;

public class ExamActivity extends AppCompatActivity implements testpartFragment.OnListFragmentInteractionListener
        , dialogFragment.OnDialogFinishedListener, choicesFragment.OnDialogFinishedListener, completeFragment.OnDialogFinishedListener
        , mistakeFragment.OnDialogFinishedListener {
    Test test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_choices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        test = getIntent().getParcelableExtra("test");
        getSupportFragmentManager().beginTransaction().replace(R.id.container, testpartFragment.newInstance(test)).commit();

    }


    private void goToComplete() {
    }


    @Override
    public void onListFragmentInteraction(Test test, String questionType) {
        switch (questionType) {

            case "Choices":
                getSupportFragmentManager().beginTransaction().replace(R.id.container, choicesFragment.newInstance(test)).addToBackStack(null).commit();
                break;
            case "Complete":
                getSupportFragmentManager().beginTransaction().replace(R.id.container, completeFragment.newInstance(test)).addToBackStack(null).commit();
                break;
            case "Dialog":
                getSupportFragmentManager().beginTransaction().replace(R.id.container, dialogFragment.newInstance(test)).addToBackStack(null).commit();
                break;

            case "Correct the mistake":
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mistakeFragment.newInstance(test)).addToBackStack(null).commit();
                break;


        }

    }

    @Override
    public void onDialogFinished() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, testpartFragment.newInstance(test)).commit();
    }

}
