package com.mrerror.tm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.fragments.PartsFragment;
import com.mrerror.tm.models.Part;

import java.util.ArrayList;

public class PartsRecyclerViewAdapter extends RecyclerView.Adapter<PartsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Part> mValues;
    private Context mContext;
    private PartsFragment.OnListFragmentInteractionListener mListener;

    public PartsRecyclerViewAdapter(ArrayList<Part> items, PartsFragment.OnListFragmentInteractionListener listener) {
        this.mListener = listener;
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
        holder.mItem = mValues.get(position);
        holder.mPartTitle.setText(mValues.get(position).getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mPartTitle;
        Part mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mPartTitle = (TextView) view.findViewById(R.id.text1);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPartTitle.getText() + "'";
        }


    }
}
