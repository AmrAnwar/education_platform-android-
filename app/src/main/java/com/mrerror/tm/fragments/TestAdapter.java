package com.mrerror.tm.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.Test;

import java.util.ArrayList;

/**
 * Created by kareem on 7/30/2017.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {
Context mContext;
ArrayList<Test> mArrayTest;
    TestAdapter(ArrayList<Test>arrayTest){
        mArrayTest=arrayTest;

    }
    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_cardview, parent, false);
        return new TestViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mArrayTest.size();
    }

    public void newData(ArrayList<Test> items){
        mArrayTest = items;
        this.notifyDataSetChanged();

    }

    public  class TestViewHolder extends RecyclerView.ViewHolder{
        TextView mTestTitle;
        public TestViewHolder(View itemView) {
            super(itemView);
            mTestTitle= (TextView) itemView.findViewById(R.id.text1);
        }
        public  void bindView(int position){
            mTestTitle.setText(mArrayTest.get(position).getTitle());
        }
    }
}
