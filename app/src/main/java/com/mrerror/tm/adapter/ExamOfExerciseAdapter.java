package com.mrerror.tm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.ExamsForExercises;
import com.mrerror.tm.models.QuestionsTypeOfExercises;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

/**
 * Created by kareem on 9/12/2017.
 */

public class ExamOfExerciseAdapter extends
        ExpandableRecyclerViewAdapter<ExamOfExerciseAdapter.ExamViewHolder,ExamOfExerciseAdapter.QuestionsTypeViewHolder> {

Context mContext;
    OnChiledClick mOnChiledClick;

    public interface  OnChiledClick{
        public void onChiledClickOfExcercise(ExamsForExercises mExam, QuestionsTypeOfExercises mQuestion);
    }

    public ExamOfExerciseAdapter(List<? extends ExpandableGroup> groups , OnChiledClick onChiledClick) {
        super(groups);
        mOnChiledClick=onChiledClick;
    }

    @Override
    public ExamViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.simple_cardview,parent,false);
        ExamViewHolder examViewHolder=new ExamViewHolder(view);

        return examViewHolder;
    }


    @Override
    public QuestionsTypeViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.simple_cardview,parent,false);
        QuestionsTypeViewHolder questionsTypeViewHolder=new QuestionsTypeViewHolder(view);

        return questionsTypeViewHolder;
    }


    @Override
    public void onBindChildViewHolder(QuestionsTypeViewHolder holder, int flatPosition, final ExpandableGroup group, int childIndex) {
         final QuestionsTypeOfExercises question= (QuestionsTypeOfExercises)group.getItems().get(childIndex);

        holder.mQuestionType.setText((childIndex+1)+"-"+question.getmTypeQuestion());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnChiledClick.onChiledClickOfExcercise((ExamsForExercises) group,question);
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(ExamViewHolder holder, int flatPosition, ExpandableGroup group) {
         holder.examName.setText(group.getTitle());
    }


    public  class  ExamViewHolder extends GroupViewHolder{
        private ImageView arrow;
         TextView examName;

    public ExamViewHolder(View itemView) {
        super(itemView);

        examName=(TextView) itemView.findViewById(R.id.text1);
        arrow =(ImageView) itemView.findViewById(R.id.delete);
        arrow.setVisibility(View.VISIBLE);
        arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);


    }




        @Override
        public void expand() {
            arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
        }

        @Override
        public void collapse() {
            arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
        }



}


public class QuestionsTypeViewHolder extends ChildViewHolder{

    TextView mQuestionType;
    public QuestionsTypeViewHolder(View itemView) {
        super(itemView);
        mQuestionType=(TextView) itemView.findViewById(R.id.text1);
    }
}
}
