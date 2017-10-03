package com.mrerror.tm.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrerror.tm.Inbox;
import com.mrerror.tm.R;
import com.mrerror.tm.ReplyActivity;
import com.mrerror.tm.models.Question;

import java.util.ArrayList;

public class InboxRecyclerViewAdapter extends RecyclerView.Adapter<InboxRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Question> mValues;
    private Context mContext;


    public InboxRecyclerViewAdapter(ArrayList<Question> items) {
        mValues = items;
    }
    public  void onChange(ArrayList<Question> newValues)
    {
        mValues=newValues;
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
        String quest = mValues.get(position).getQuestion();
        holder.mTime.setText(mValues.get(position).getDate());
        if (quest.length() > 2)
            holder.mQuest.setText(mValues.get(position).getQuestion());
        else
            holder.mQuest.setText("you got an answer!");
    }

    public void newData(ArrayList<Question> items) {
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
        final TextView mTime;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTime= (TextView) view.findViewById(R.id.timeTv);
            mQuest = (TextView) view.findViewById(R.id.text1);
            mView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mQuest.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (sp.getString("group", "normal").equals("normal")||Inbox.isPublic) {
                mContext.startActivity(new Intent(mContext, ReplyActivity.class).putExtra("question", mValues.get(getAdapterPosition())));
            } else {

            }
        }
    }
}
