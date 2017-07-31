package com.mrerror.tm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.Test;
import com.mrerror.tm.models.TestDialog;

import java.util.ArrayList;


public class dialogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TEST = "param2";
    private OnDialogFinishedListener mListener;
    // TODO: Rename and change types of parameters
    private Test mTest;



    Button submit,next;
    //for choices
    TextView questionTV;
    EditText answer1ET,answer2ET,answer3ET;
    int questionsCounter =0;
    TestDialog dialogQuestion;
    ImageView answerTrue,answerFalse,answer2True,answer2False,answer3True,answer3False;
    boolean trueAnswer = true;
    private ArrayList<TestDialog> testDialogsArrayList;

    //

    public dialogFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static dialogFragment newInstance(Test test) {
        dialogFragment fragment = new dialogFragment();
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
        Log.e(" fragment","dialog");
        View v = inflater.inflate(R.layout.content_exam_dialog, container, false);
        next = (Button) v.findViewById(R.id.next_btn);
        submit = (Button) v.findViewById(R.id.submit_btn);
        answer1ET = (EditText) v.findViewById(R.id.answer);
        answer2ET = (EditText) v.findViewById(R.id.answer2);
        answer3ET = (EditText) v.findViewById(R.id.answer3);
        questionTV = (TextView) v.findViewById(R.id.question);
        answerFalse = (ImageView)v.findViewById(R.id.answer_false);
        answer2False = (ImageView)v.findViewById(R.id.answer2_false);
        answer3False = (ImageView)v.findViewById(R.id.answer3_false);
        answerTrue = (ImageView)v.findViewById(R.id.answer_true);
        answer2True = (ImageView)v.findViewById(R.id.answer2_true);
        answer3True = (ImageView)v.findViewById(R.id.answer3_true);
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
        testDialogsArrayList = mTest.getTestDialogList();
        dialogQuestion = testDialogsArrayList.get(questionsCounter);
        questionTV.setText(dialogQuestion.getDialog());
    }
    public void submit(View view) {
        String speakerA = answer1ET.getText().toString();
        String speakerB = answer2ET.getText().toString();
        String location = answer3ET.getText().toString();
        speakerA = speakerA.toLowerCase();
        speakerB = speakerB.toLowerCase();
        location = location.toLowerCase();
        speakerA = speakerA.trim();
        speakerB = speakerB.trim();
        location = location.trim();
        if(speakerA.length()<1 && speakerB.length()<1&& location.length()<1){

            Snackbar.make(view, "Type your answer!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            trueAnswer = true;
            if (speakerA.equals((dialogQuestion.getfSpeaker().toLowerCase()).trim())) {
                answerTrue.setVisibility(View.VISIBLE);
                answerFalse.setVisibility(View.GONE);
            }else{
                answerTrue.setVisibility(View.GONE);
                answerFalse.setVisibility(View.VISIBLE);
                trueAnswer = false;
            }
            if (speakerB.equals((dialogQuestion.getsSpeaker().toLowerCase()).trim())) {
                answer2True.setVisibility(View.VISIBLE);
                answer2False.setVisibility(View.GONE);
            }else{
                answer2True.setVisibility(View.GONE);
                answer2False.setVisibility(View.VISIBLE);
                trueAnswer = false;
            }

            if (location.equals((dialogQuestion.getLocation().toLowerCase()).trim())) {
                answer3True.setVisibility(View.VISIBLE);
                answer3False.setVisibility(View.GONE);
            }else{
                answer3True.setVisibility(View.GONE);
                answer3False.setVisibility(View.VISIBLE);
                trueAnswer = false;
            }

        }
        if(trueAnswer) {
            answer1ET.setEnabled(false);
            answer2ET.setEnabled(false);
            answer3ET.setEnabled(false);
            submit.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
    }

    public void next(View view) {
        submit.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);
        if((questionsCounter+1)<testDialogsArrayList.size()) {
            ++questionsCounter;
            setCompleteData();
        }else{
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            mListener.onDialogFinished();
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof testpartFragment.OnListFragmentInteractionListener) {
            mListener = (dialogFragment.OnDialogFinishedListener) context;
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
