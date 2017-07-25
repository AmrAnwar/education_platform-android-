package com.mrerror.tm.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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



   public ModelAnswerAdapter(ArrayList<ModelAnswer> answers,OnModelAnswerClick modelAnswerClick){
       mModelAnswers=answers;
       mModelAnswerClick=modelAnswerClick;

   }

   public  void addNewData(ArrayList<ModelAnswer> answers){

       mModelAnswers=answers;
       this.notifyDataSetChanged();
   }
    @Override
    public ModelAnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.fragment_modelanswer,parent,false);
        return new ModelAnswerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ModelAnswerViewHolder holder, int position) {

      final ModelAnswer modelAnswer=mModelAnswers.get(position);

       holder.title.setText(modelAnswer.getTitle());
        holder.note.setText(modelAnswer.getNote());
        if(modelAnswer.getDwonload()){
            holder.download.setVisibility(View.INVISIBLE);
        }


    }



    @Override
    public int getItemCount() {
        return mModelAnswers.size();
    }

    public  class  ModelAnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView note;
        Button download;

        ModelAnswerViewHolder(View view){
            super(view);
            title= (TextView) view.findViewById(R.id.title);
            note=(TextView) view.findViewById(R.id.note);
            download=(Button) view.findViewById(R.id.downlaodbutton);
            view.setOnClickListener(this);


        }
        @Override
        public void onClick(View v) {
        mModelAnswerClick.onModelAnserClicked(mModelAnswers.get(getPosition()));

        }
    }
}
