package com.mrerror.tm.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    ArrayList<ModelAnswer> mExam;
    ArrayList<ModelAnswer>mSheet;
    ArrayList<ModelAnswer>mOthers;


    public  void inilaize(ArrayList<ModelAnswer> exam,ArrayList<ModelAnswer>sheet,ArrayList<ModelAnswer> others){

        mExam=exam;
        mOthers=others;
        mSheet=sheet;

    }


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

        ModelAnswerViewHolder viewHolder=new ModelAnswerViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ModelAnswerViewHolder holder, int position) {

      final ModelAnswer modelAnswer=mModelAnswers.get(position);
        holder.bind(modelAnswer,modelAnswer.getDwonload());

    }



    @Override
    public int getItemCount() {
        return mModelAnswers.size();
    }

    public  class  ModelAnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView note;
        TextView download;
        ImageView readOrDownLoad;

        ModelAnswerViewHolder(View view){
            super(view);
            title= (TextView) view.findViewById(R.id.title);
            note=(TextView) view.findViewById(R.id.note);
            download=(TextView) view.findViewById(R.id.downlaodbutton);
            readOrDownLoad=(ImageView)view.findViewById(R.id.readOrDwonload);
            view.setOnClickListener(this);


        }
        public  void bind(ModelAnswer modelAnswer,boolean downloadOrNot){
            download.setText("Click To DownLoad");
            readOrDownLoad.setImageResource(R.drawable.ic_cloud_download_black_48px);
            title.setText(modelAnswer.getTitle());
            note.setText(modelAnswer.getNote());
            if(downloadOrNot){download.setText("Read");
                readOrDownLoad.setImageResource(R.drawable.ic_import_contacts_black_48px);


            }
        }
        @Override
        public void onClick(View v) {
        mModelAnswerClick.onModelAnserClicked(mModelAnswers.get(getPosition()));

        }
    }
}
