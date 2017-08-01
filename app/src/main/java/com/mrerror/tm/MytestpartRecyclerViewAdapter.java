package com.mrerror.tm;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrerror.tm.models.Test;
import com.mrerror.tm.fragments.testpartFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

public class MytestpartRecyclerViewAdapter extends RecyclerView.Adapter<MytestpartRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Test mTest;
    public MytestpartRecyclerViewAdapter(ArrayList<String> items, Test test, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mTest = test;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mTest;
        holder.mQuestView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem,mValues.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mQuestView;
        public Test mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mQuestView = (TextView) view.findViewById(R.id.text1);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mQuestView.getText() + "'";
        }
    }
}
