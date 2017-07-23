package com.mrerror.tm.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.News;

import java.util.ArrayList;

public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {

    private  ArrayList<News> mValues;

    public MyNewsRecyclerViewAdapter(ArrayList<News> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if(holder.mItem.getFile_url().length()<=0 || holder.mItem.getFile_url() == null ||
                holder.mItem.getFile_url().equals("null")){
            holder.mFileView.setVisibility(View.GONE);
        }
        if(holder.mItem.getImg_url().length()<=0 || holder.mItem.getImg_url() == null||
                holder.mItem.getImg_url().equals("null")){
            holder.mImgView.setVisibility(View.GONE);
        }


        holder.mContentView.setText(mValues.get(position).getContent());

    }
    public void newData(ArrayList<News> items){
        this.mValues = items;
        this.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFileView;
        public final ImageView mImgView;
        public final TextView mContentView;
        public News mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFileView = (TextView) view.findViewById(R.id.file);
            mImgView = (ImageView) view.findViewById(R.id.img);
            mContentView = (TextView) view.findViewById(R.id.text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
