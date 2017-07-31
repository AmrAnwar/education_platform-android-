package com.mrerror.tm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.Test;
import com.mrerror.tm.models.TestComplete;
import com.mrerror.tm.testpartFragment;

import java.util.ArrayList;


public class completeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TEST = "param2";
    private OnDialogFinishedListener mListener;

    // TODO: Rename and change types of parameters
    private Test mTest;



    Button submit,next;
    //for choices
    TextView questionTV;
    EditText answerET;
    int questionsCounter =0;
    TestComplete completeQuestion;
    ImageView answerTrue,answerFalse;
    private ArrayList<TestComplete> testCompleteArrayList;

    //

    public completeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static completeFragment newInstance(Test test) {
        completeFragment fragment = new completeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEST, test);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTest = getArguments().getParcelable(ARG_TEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.content_exam_complete, container, false);
        next = (Button) v.findViewById(R.id.next_btn);
        submit = (Button) v.findViewById(R.id.submit_btn);
        answerET = (EditText) v.findViewById(R.id.answer);
        questionTV = (TextView) v.findViewById(R.id.question);
        answerFalse = (ImageView)v.findViewById(R.id.answer_false);
        answerTrue = (ImageView)v.findViewById(R.id.answer_true);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next(v);
            }
        });
        setCompleteData();

        return v;
    }

    private void setCompleteData() {
        testCompleteArrayList = mTest.getTestCompleteList();
        completeQuestion = testCompleteArrayList.get(questionsCounter);
        questionTV.setText(completeQuestion.getSentence());
    }
    public void submit(View view) {
        String userAnswer = answerET.getText().toString();
        userAnswer = userAnswer.toLowerCase();
        userAnswer = userAnswer.trim();
        if(userAnswer.length()<1){

            Snackbar.make(view, "Type your answer!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            if (userAnswer.equals((completeQuestion.getAnswer().toLowerCase()).trim())) {
                answerTrue.setVisibility(View.VISIBLE);
                answerFalse.setVisibility(View.GONE);
                answerET.setEnabled(false);
                submit.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
            }else{
                answerTrue.setVisibility(View.GONE);
                answerFalse.setVisibility(View.VISIBLE);
            }
        }
    }

    public void next(View view) {
        submit.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);
        if((questionsCounter+1)<testCompleteArrayList.size()) {
            ++questionsCounter;
            setCompleteData();
        }else{
            mListener.onDialogFinished();
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof testpartFragment.OnListFragmentInteractionListener) {
            mListener = (OnDialogFinishedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnDialogFinishedListener {
        // TODO: Update argument type and name
        void onDialogFinished();
    }
}
