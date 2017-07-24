package com.mrerror.tm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrerror.tm.R;
import com.mrerror.tm.fragments.UnitFragment;
import com.mrerror.tm.models.Unit;

import java.util.ArrayList;

public class UnitsRecyclerViewAdapter extends RecyclerView.Adapter<UnitsRecyclerViewAdapter.ViewHolder>  {

    private  ArrayList<Unit> mValues;
    private Context mContext ;
    private UnitFragment.OnListFragmentInteractionListener mListener;
    public UnitsRecyclerViewAdapter(ArrayList<Unit> items, UnitFragment.OnListFragmentInteractionListener listener) {
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
        holder.mUnitTitle.setText(mValues.get(position).getTitle());

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
    public void newData(ArrayList<Unit> items){
        this.mValues = items;
        this.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mUnitTitle;
        Unit mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mUnitTitle = (TextView) view.findViewById(R.id.text1);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUnitTitle.getText() + "'";
        }
    }
}
