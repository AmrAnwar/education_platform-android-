package com.mrerror.tm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.models.News;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<News> mValues;
    private Context mContext;
    private OnListFragmentInteractionListener mListener;

    public MyNewsRecyclerViewAdapter(ArrayList<News> items, OnListFragmentInteractionListener listener) {
        this.mListener = listener;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mImgView.getLayoutParams().height = 650;
//        holder.mImgView.getLayoutParams().width = 830;
        holder.mItem = mValues.get(position);
        if (holder.mItem.getFile_url().length() <= 0 || holder.mItem.getFile_url() == null ||
                holder.mItem.getFile_url().equals("null")) {
            holder.mFileView.setVisibility(View.GONE);
        }
        if (holder.mItem.getImg_url().length() <= 0 || holder.mItem.getImg_url() == null ||
                holder.mItem.getImg_url().equals("null")) {

            holder.mImgView.setVisibility(View.GONE);
            holder.progress.setVisibility(View.GONE);
        } else {
            holder.progress.setVisibility(View.VISIBLE);

            Target toolbarlayoutTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.mImgView.setVisibility(View.VISIBLE);
                    holder.progress.setVisibility(View.GONE);
                    holder.mImgView.setImageBitmap(bitmap);
                    Log.e("done loading",holder.mItem.getContent());
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e("error loading",holder.mItem.getContent());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            holder.mImgView.setTag(toolbarlayoutTarget);
            Picasso.with(mContext).load(holder.mItem.getImg_url()).into((Target) holder.mImgView.getTag());

        }


        holder.mContentView.setText(mValues.get(position).getContent());

    }

    public void newData(ArrayList<News> items) {
        this.mValues = items;
        this.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        final TextView mFileView;
        final ImageView mImgView;
        final ProgressBar progress;
        final TextView mContentView;
        News mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mFileView = (TextView) view.findViewById(R.id.file);
            mImgView = (ImageView) view.findViewById(R.id.img);
            mContentView = (TextView) view.findViewById(R.id.text);
            progress = (ProgressBar) view.findViewById(R.id.progress);
            itemView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            if (!(mItem.getFile_url().length() <= 0 || mItem.getFile_url() == null ||
                    mItem.getFile_url().equals("null"))) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(mItem);
                }
            }
        }
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(News item);
    }

}
