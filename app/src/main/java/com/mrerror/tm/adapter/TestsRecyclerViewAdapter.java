package com.mrerror.tm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrerror.tm.ExamActivity;
import com.mrerror.tm.R;
import com.mrerror.tm.fragments.UnitFragment;
import com.mrerror.tm.models.Test;

import java.util.ArrayList;

public class TestsRecyclerViewAdapter extends RecyclerView.Adapter<TestsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mValues;
    private ArrayList<Test> mTests;
    private Context mContext;
    private UnitFragment.OnListFragmentInteractionListener mListener;

    public TestsRecyclerViewAdapter(ArrayList<String> items, ArrayList<Test> tests) {
        this.mTests = tests;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTestTitle.setText(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        final TextView mTestTitle;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTestTitle = (TextView) view.findViewById(R.id.text1);
            view.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTestTitle.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            Test test = mTests.get(getAdapterPosition());
            mContext.startActivity(new Intent(mContext, ExamActivity.class).putExtra("test", test));
        }
    }
}
