package com.mrerror.tm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrerror.tm.R;
import com.mrerror.tm.models.ExerciseQuestions;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;

/**
 * Created by kareem on 9/13/2017.
 */

public class QuestionForExerciseAdapater extends RecyclerView.Adapter<QuestionForExerciseAdapater.QuestionViewHolder> {
ArrayList<ExerciseQuestions> mItems;
    Context mContext;
    OnShowAnswer mOnShowAnswer;

    public interface OnShowAnswer{
  public  void    onShowAnswer(ExerciseQuestions question);

    }

public QuestionForExerciseAdapater(ArrayList<ExerciseQuestions> myIyems, OnShowAnswer onShowAnswer){

    mItems=myIyems;
    mOnShowAnswer=onShowAnswer;

}

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.question_for_exercise_item,parent,false);


        return new QuestionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(QuestionViewHolder holder, final int position) {

     holder.mQuestionTv.setHtml(  mItems.get(position).getQuestion());

    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class  QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        HtmlTextView mQuestionTv;


        public QuestionViewHolder(View itemView) {
            super(itemView);
            mQuestionTv=(HtmlTextView)  itemView.findViewById(R.id.textForQuestionEx);
            itemView.setOnClickListener(this);
            mQuestionTv.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            mOnShowAnswer.onShowAnswer(mItems.get(getAdapterPosition()));
        }
    }
}
