package com.mrerror.tm.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.ModelAnswer;

import java.util.ArrayList;

/**
 * Created by kareem on 7/24/2017.
 */

public class ModelAnswerAdapter extends RecyclerView.Adapter<ModelAnswerAdapter.ModelAnswerViewHolder> {


    ArrayList<ModelAnswer> mModelAnswers;
    OnModelAnswerClick mModelAnswerClick;



    public interface OnModelAnswerClick{

        public void onModelAnserClicked(ModelAnswer modelAnswer);
    }



   public ModelAnswerAdapter(ArrayList<ModelAnswer> answers,OnModelAnswerClick modelAnswerClick ){
       mModelAnswers=answers;
       mModelAnswerClick=modelAnswerClick;


   }

   public  void addNewData(ArrayList<ModelAnswer> answers){

       mModelAnswers=answers;
     //  this.notifyDataSetChanged();
   }
    @Override
    public ModelAnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.fragment_modelanswer,parent,false);

        ModelAnswerViewHolder viewHolder=new ModelAnswerViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ModelAnswerViewHolder holder, int position) {

          final ModelAnswer modelAnswer = mModelAnswers.get(position);
          holder.bind(modelAnswer);

    }



    @Override
    public int getItemCount() {
        return mModelAnswers.size();
    }

    public  class  ModelAnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView note;
        TextView type;
        View mView;

        ModelAnswerViewHolder(View view){
            super(view);
            mView=view;
            title= (TextView) view.findViewById(R.id.title);
            note=(TextView) view.findViewById(R.id.note);
            type =(TextView) view.findViewById(R.id.downlaodbutton);

            mView.setOnClickListener(this);


        }
        public  void bind(ModelAnswer modelAnswer){
            type.setText(modelAnswer.getType());
            title.setText(modelAnswer.getTitle());
            note.setText(modelAnswer.getNote());

        }
        @Override
        public void onClick(View v) {

            ModelAnswer item=mModelAnswers.get(getPosition());
            mModelAnswerClick.onModelAnserClicked(item);

        }
    }
}
