package com.mrerror.tm.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mrerror.tm.Inbox;
import com.mrerror.tm.R;
import com.mrerror.tm.ReplyForStaffActivity;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.fragments.UnitFragment;
import com.mrerror.tm.models.QuestionForStaff;

import org.json.JSONException;

import java.util.ArrayList;

public class InboxForStaffRecyclerViewAdapter extends RecyclerView.Adapter<InboxForStaffRecyclerViewAdapter.ViewHolder>  {

    private  ArrayList<QuestionForStaff> mValues;
    private Context mContext ;
    private UnitFragment.OnListFragmentInteractionListener mListener;
    public InboxForStaffRecyclerViewAdapter(ArrayList<QuestionForStaff> items) {
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

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void onChange(ArrayList<QuestionForStaff> newData){
        mValues=newData;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        final TextView mQuest;
        final ImageView mDelete;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mQuest = (TextView) view.findViewById(R.id.text1);
            mDelete = (ImageView) view.findViewById(R.id.delete);
            mDelete.setVisibility(View.VISIBLE);
            mDelete.setOnClickListener(this);
            mView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mQuest.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            if(v.equals(mDelete)){
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                alertBuilder.setTitle("Delete Question");
                alertBuilder.setMessage("Are you sure you want to delete this question?\n" +
                        "this cannot be undone. The student will not be notified about this.");
                alertBuilder.setPositiveButton("I'm sure, Delete!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                            @Override
                            public void onCompleted(String result) throws JSONException {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                dialog.setTitle("Delete question!");
                                dialog.setMessage("The question has been deleted!");
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        ((Inbox)mContext).finish();
                                    }
                                });
                                dialog.show();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
                            }
                        }).deleteData(mContext,mValues.get(getAdapterPosition()).getLinkToDelete());
                    }
                });
                alertBuilder.setNegativeButton("Cancel!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertBuilder.show();
            }else {
                mContext.startActivity(new Intent(mContext, ReplyForStaffActivity.class)
                        .putExtra("question", mValues.get(getAdapterPosition())));
            }

        }
    }
}
