package com.mrerror.tm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.Test;
import com.mrerror.tm.models.TestChoices;

import java.util.ArrayList;


public class choicesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TEST = "param2";
    private OnDialogFinishedListener mListener;

    // TODO: Rename and change types of parameters
    private Test mTest;


    Button submit,showAnswer, next;
    //for choices
    TextView questionTV, choice1TV, choice2TV, choice3TV;
    ImageView choice1True, choice1False, choice2True, choice2False, choice3True, choice3False;
    int choicesCounter = 0;
    TestChoices choiceQuestion;
    String selectedAnswer = null;
    TextView selectedTextView;
    private ArrayList<TestChoices> testChoicesArrayList;

    //

    public choicesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static choicesFragment newInstance(Test test) {
        choicesFragment fragment = new choicesFragment();
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

        View v = inflater.inflate(R.layout.content_exam_choices, container, false);
        next = (Button) v.findViewById(R.id.next_btn);
        submit = (Button) v.findViewById(R.id.submit_btn);
        showAnswer = (Button) v.findViewById(R.id.showAnswer_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });
        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer(v);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next(v);
            }
        });
        testChoicesArrayList = mTest.getTestChoicesList();
        questionTV = (TextView) v.findViewById(R.id.question);
        choice1TV = (TextView) v.findViewById(R.id.choice_1);
        choice2TV = (TextView) v.findViewById(R.id.choice_2);
        choice3TV = (TextView) v.findViewById(R.id.choice_3);
        choice1True = (ImageView) v.findViewById(R.id.choice_1_true);
        choice1False = (ImageView) v.findViewById(R.id.choice_1_false);
        choice2True = (ImageView) v.findViewById(R.id.choice_2_true);
        choice2False = (ImageView) v.findViewById(R.id.choice_2_false);
        choice3True = (ImageView) v.findViewById(R.id.choice_3_true);
        choice3False = (ImageView) v.findViewById(R.id.choice_3_false);
        setChoicesData();

        return v;
    }


    private void setChoicesData() {
        choiceQuestion = testChoicesArrayList.get(choicesCounter);
        questionTV.setText(choiceQuestion.getQuestion());
        choice1TV.setText(choiceQuestion.getChoice1());
        choice2TV.setText(choiceQuestion.getChoice2());
        choice3TV.setText(choiceQuestion.getChoice3());
        choice1TV.setTextColor(getContext().getResources().getColor(R.color.black));
        choice2TV.setTextColor(getContext().getResources().getColor(R.color.black));
        choice3TV.setTextColor(getContext().getResources().getColor(R.color.black));
        choice1True.setVisibility(View.GONE);
        choice2True.setVisibility(View.GONE);
        choice3True.setVisibility(View.GONE);
        choice1False.setVisibility(View.GONE);
        choice2False.setVisibility(View.GONE);
        choice3False.setVisibility(View.GONE);
        choice1TV.setEnabled(true);
        choice2TV.setEnabled(true);
        choice3TV.setEnabled(true);
        choice1TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice1TV.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                choice2TV.setTextColor(getContext().getResources().getColor(R.color.black));
                choice3TV.setTextColor(getContext().getResources().getColor(R.color.black));
                selectedAnswer = choice1TV.getText().toString();
                selectedTextView = choice1TV;
            }
        });
        choice3TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice3TV.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                choice2TV.setTextColor(getContext().getResources().getColor(R.color.black));
                choice1TV.setTextColor(getContext().getResources().getColor(R.color.black));
                selectedAnswer = choice3TV.getText().toString();
                selectedTextView = choice3TV;
            }
        });
        choice2TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice2TV.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                choice1TV.setTextColor(getContext().getResources().getColor(R.color.black));
                choice3TV.setTextColor(getContext().getResources().getColor(R.color.black));
                selectedAnswer = choice2TV.getText().toString();
                selectedTextView = choice2TV;
            }
        });
    }

    public void submit(View view) {
        if (selectedAnswer != null) {
            if (selectedAnswer.equals(mTest.getTestChoicesList().get(choicesCounter).getAnswer())) {
                switch (selectedTextView.getId()) {
                    case R.id.choice_1:
                        choice1TV.setTextColor(getContext().getResources().getColor(R.color.true_text));
                        choice2TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice3TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice1True.setVisibility(View.VISIBLE);
                        choice1False.setVisibility(View.GONE);
                        choice2False.setVisibility(View.GONE);
                        choice3False.setVisibility(View.GONE);
                        break;
                    case R.id.choice_2:
                        choice2TV.setTextColor(getContext().getResources().getColor(R.color.true_text));
                        choice1TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice3TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice2True.setVisibility(View.VISIBLE);
                        choice2False.setVisibility(View.GONE);
                        choice1False.setVisibility(View.GONE);
                        choice3False.setVisibility(View.GONE);
                        break;
                    case R.id.choice_3:
                        choice3TV.setTextColor(getContext().getResources().getColor(R.color.true_text));
                        choice2TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice1TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice3True.setVisibility(View.VISIBLE);
                        choice3False.setVisibility(View.GONE);
                        choice1False.setVisibility(View.GONE);
                        choice2False.setVisibility(View.GONE);
                        break;

                }
                choice1TV.setEnabled(false);
                choice2TV.setEnabled(false);
                choice3TV.setEnabled(false);
                showAnswer.setVisibility(View.GONE);
                submit.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
            } else {
                switch (selectedTextView.getId()) {
                    case R.id.choice_1:
                        choice1TV.setTextColor(getContext().getResources().getColor(R.color.false_text));
                        choice2TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice3TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice1False.setVisibility(View.VISIBLE);
                        choice2False.setVisibility(View.GONE);
                        choice3False.setVisibility(View.GONE);
                        break;
                    case R.id.choice_2:
                        choice2TV.setTextColor(getContext().getResources().getColor(R.color.false_text));
                        choice1TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice3TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice2False.setVisibility(View.VISIBLE);
                        choice1False.setVisibility(View.GONE);
                        choice3False.setVisibility(View.GONE);
                        break;
                    case R.id.choice_3:
                        choice3TV.setTextColor(getContext().getResources().getColor(R.color.false_text));
                        choice1TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice2TV.setTextColor(getContext().getResources().getColor(R.color.black));
                        choice3False.setVisibility(View.VISIBLE);
                        choice2False.setVisibility(View.GONE);
                        choice1False.setVisibility(View.GONE);
                        break;

                }
            }
        } else {
            Snackbar.make(view, "Make your choice!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
    public void showAnswer(View view) {
        String answer = mTest.getTestChoicesList().get(choicesCounter).getAnswer();
        choice1TV.setTextColor(getContext().getResources().getColor(R.color.black));
        choice2TV.setTextColor(getContext().getResources().getColor(R.color.black));
        choice3TV.setTextColor(getContext().getResources().getColor(R.color.black));
        choice2False.setVisibility(View.GONE);
        choice1False.setVisibility(View.GONE);
        choice3False.setVisibility(View.GONE);

        if(answer.equals(choice1TV.getText().toString())){
            choice1TV.setTextColor(getContext().getResources().getColor(R.color.true_text));
            choice1True.setVisibility(View.VISIBLE);
        }else if(answer.equals(choice2TV.getText().toString())){
            choice2TV.setTextColor(getContext().getResources().getColor(R.color.true_text));
            choice2True.setVisibility(View.VISIBLE);
        }else if(answer.equals(choice3TV.getText().toString())){
            choice3TV.setTextColor(getContext().getResources().getColor(R.color.true_text));
            choice3True.setVisibility(View.VISIBLE);
        }
        submit.setVisibility(View.GONE);
        showAnswer.setVisibility(View.GONE);
        next.setVisibility(View.VISIBLE);
    }

    public void next(View view) {
        submit.setVisibility(View.VISIBLE);
        showAnswer.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);

        if ((choicesCounter + 1) < testChoicesArrayList.size()) {
            ++choicesCounter;
            setChoicesData();
        } else {
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
