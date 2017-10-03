package com.mrerror.tm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrerror.tm.ExamResult;
import com.mrerror.tm.R;
import com.mrerror.tm.models.News;
import com.mrerror.tm.models.TestChoices;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class ResultRecyclerViewAdapter extends RecyclerView.Adapter<ResultRecyclerViewAdapter.ViewHolder> {

    private ArrayList<TestChoices> mValues;
    private Context mContext;
    public static int wrongAnswers = 0;
    public ResultRecyclerViewAdapter(ArrayList<TestChoices> items) {
        wrongAnswers = 0;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_exam_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.e("here adapter","yes");
        TestChoices choiceQuestion = mValues.get(position);
        holder.questionTV.setText(choiceQuestion.getQuestion());
        holder.choice1TV.setText(choiceQuestion.getChoice1());
        holder.choice2TV.setText(choiceQuestion.getChoice2());
        holder.choice3TV.setText(choiceQuestion.getChoice3());
        holder.choice1TV.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.choice2TV.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.choice3TV.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.choice1True.setVisibility(View.GONE);
        holder.choice2True.setVisibility(View.GONE);
        holder.choice3True.setVisibility(View.GONE);
        holder.choice1False.setVisibility(View.GONE);
        holder.choice2False.setVisibility(View.GONE);
        holder.choice3False.setVisibility(View.GONE);
        if(choiceQuestion.getAnswer().equals(holder.choice1TV.getText().toString())){
            holder.choice1TV.setTextColor(mContext.getResources().getColor(R.color.true_text));
            holder.choice1True.setVisibility(View.VISIBLE);
        }else if(choiceQuestion.getAnswer().equals(holder.choice2TV.getText().toString())){
            holder.choice2TV.setTextColor(mContext.getResources().getColor(R.color.true_text));
            holder.choice2True.setVisibility(View.VISIBLE);
        }else if(choiceQuestion.getAnswer().equals(holder.choice3TV.getText().toString())){
            holder.choice3TV.setTextColor(mContext.getResources().getColor(R.color.true_text));
            holder.choice3True.setVisibility(View.VISIBLE);
        }
        if(choiceQuestion.getUser_answer()!=null) {
            if (!choiceQuestion.getUser_answer().equals(choiceQuestion.getAnswer())) {
                if (choiceQuestion.getUser_answer().equals(holder.choice1TV.getText().toString())) {
                    wrongAnswers++;
                    holder.choice1False.setVisibility(View.VISIBLE);
                    holder.choice1TV.setTextColor(mContext.getResources().getColor(R.color.false_text));
                } else if (choiceQuestion.getUser_answer().equals(holder.choice2TV.getText().toString())) {
                    wrongAnswers++;
                    holder.choice2False.setVisibility(View.VISIBLE);
                    holder.choice2TV.setTextColor(mContext.getResources().getColor(R.color.false_text));
                } else if (choiceQuestion.getUser_answer().equals(holder.choice3TV.getText().toString())) {
                    wrongAnswers++;
                    holder.choice3False.setVisibility(View.VISIBLE);
                    holder.choice3TV.setTextColor(mContext.getResources().getColor(R.color.false_text));
                }
            }
        }else{
            wrongAnswers++;
            holder.questionTV.setText(holder.questionTV.getText().toString()+" (Skipped)");
        }
        ExamResult.setScore();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        TextView questionTV, choice1TV, choice2TV, choice3TV;
        ImageView choice1True, choice1False, choice2True, choice2False, choice3True, choice3False;
        TestChoices mItem;

        ViewHolder(View v) {
            super(v);
            mView = v;
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
        }

    }


}
