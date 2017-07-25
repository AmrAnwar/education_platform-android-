package com.mrerror.tm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.ReplyActivity;
import com.mrerror.tm.fragments.UnitFragment;
import com.mrerror.tm.models.Question;

import java.util.ArrayList;

public class InboxRecyclerViewAdapter extends RecyclerView.Adapter<InboxRecyclerViewAdapter.ViewHolder>  {

    private  ArrayList<Question> mValues;
    private Context mContext ;
    private UnitFragment.OnListFragmentInteractionListener mListener;
    public InboxRecyclerViewAdapter(ArrayList<Question> items) {
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
        holder.mQuest.setText(mValues.get(position).getQuestion());
    }
    public void newData(ArrayList<Question> items){
        this.mValues = items;
        this.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        final TextView mQuest;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mQuest = (TextView) view.findViewById(R.id.text1);
            mView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mQuest.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            mContext.startActivity(new Intent(mContext, ReplyActivity.class).putExtra("question",mValues.get(getAdapterPosition())));
        }
    }
}
