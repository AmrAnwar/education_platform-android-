package com.mrerror.tm.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.mrerror.tm.R;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.ExerciseQuestions;

import org.json.JSONException;
import org.json.JSONObject;
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
    void onShowAnswer(ExerciseQuestions question);

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
public void onChange(ArrayList<ExerciseQuestions> newItems){

    mItems=newItems;
    this.notifyDataSetChanged();
}

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, final int position) {

     holder.mQuestionTv.setHtml(  mItems.get(position).getQuestion());
     holder.mFav.setChecked(mItems.get(position).ismHasFav());
     holder.mItem=mItems.get(position);

    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class  QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        HtmlTextView mQuestionTv;
        CheckBox mFav;
        ExerciseQuestions mItem;
         SharedPreferences sp;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            mQuestionTv=(HtmlTextView)  itemView.findViewById(R.id.textForQuestionEx);
            itemView.setOnClickListener(this);
            mQuestionTv.setOnClickListener(this);
            mFav= (CheckBox) itemView.findViewById(R.id.fav_question);
            mFav.setOnClickListener(checkFav);
           sp= PreferenceManager.getDefaultSharedPreferences(mContext);
        }


        @Override
        public void onClick(View v) {
            mOnShowAnswer.onShowAnswer(mItems.get(getAdapterPosition()));
        }

        View.OnClickListener checkFav= new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkConnection.url=mContext.getString(R.string.domain)+"/api/study/exetoggle/"+mItem.getId()+"/"+sp.getInt("id",0);;
                new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                    @Override
                    public void onCompleted(String result) throws JSONException {
                        JSONObject obj= new JSONObject(result);
                        Boolean toggle=obj.getBoolean("toggle");
                        mFav.setChecked(toggle);
                    }

                    @Override
                    public void onError(String error) {

                    }
                }).getDataAsJsonObject(mContext);

            }
        };

    }
}
