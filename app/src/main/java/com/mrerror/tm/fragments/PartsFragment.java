package com.mrerror.tm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrerror.tm.R;
import com.mrerror.tm.adapter.PartsRecyclerViewAdapter;
import com.mrerror.tm.models.Word;

import java.util.ArrayList;
import java.util.HashMap;

public class PartsFragment extends Fragment {
    // TODO: Customize parameter argument names
    private static final String ARG_TYPE = "type";
    // TODO: Customize parameters
    private HashMap<String,ArrayList<Word>> mPart = null;
    private PartsRecyclerViewAdapter adapter;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PartsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PartsFragment newInstance(HashMap<String,ArrayList<Word>> part) {
        PartsFragment fragment = new PartsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, part);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPart = (HashMap<String, ArrayList<Word>>) getArguments().getSerializable(ARG_TYPE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter =new PartsRecyclerViewAdapter(mPart,mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ArrayList<Word> item);
    }
}
