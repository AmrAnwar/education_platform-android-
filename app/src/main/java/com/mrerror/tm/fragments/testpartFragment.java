package com.mrerror.tm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrerror.tm.MytestpartRecyclerViewAdapter;
import com.mrerror.tm.R;
import com.mrerror.tm.models.Test;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class testpartFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_TEST = "test";
    Test mTest;
    // TODO: Customize parameters
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public testpartFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static testpartFragment newInstance(Test test) {
        testpartFragment fragment = new testpartFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEST, test);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTest = getArguments().getParcelable(ARG_TEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_testpart_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            ArrayList<String> questions = new ArrayList<>();
            if (mTest.getTestChoicesList() != null && mTest.getTestChoicesList().size() > 0)
                questions.add("Choices");
            if (mTest.getTestCompleteList() != null && mTest.getTestCompleteList().size() > 0)
                questions.add("Complete");
            if (mTest.getTestDialogList() != null && mTest.getTestDialogList().size() > 0)
                questions.add("Dialog");
            if (mTest.getTestMistakeList() != null && mTest.getTestMistakeList().size() > 0)
                questions.add("Correct the mistake");
            recyclerView.setAdapter(new MytestpartRecyclerViewAdapter(questions, mTest, mListener));
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Test test, String questionType);
    }
}
