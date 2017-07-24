package com.mrerror.tm.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrerror.tm.R;
import com.mrerror.tm.adapter.UnitsRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Unit;
import com.mrerror.tm.models.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UnitFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData {
    ArrayList<Unit> unitsArrayList;
    // TODO: Customize parameter argument names
    private static final String ARG_TYPE = "type";
    // TODO: Customize parameters
    private String mLink = "";
    private UnitsRecyclerViewAdapter adapter;
    private OnListFragmentInteractionListener mListener;
    private ProgressDialog progressdialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UnitFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UnitFragment newInstance(String type) {
        UnitFragment fragment = new UnitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mLink = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            unitsArrayList = new ArrayList<>();
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter =new UnitsRecyclerViewAdapter(unitsArrayList,mListener);
            getData(mLink);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private void getData(String link) {
        NetworkConnection.url = link;
        new NetworkConnection(this).getDataAsJsonObject(getContext());
    }

    @Override
    public void onCompleted(String result) throws JSONException {
        JSONObject unitsObj = new JSONObject(result);
        unitsArrayList = new ArrayList<>();
        JSONArray resultArray = unitsObj.getJSONArray("results");
        for(int i = 0 ; i < resultArray.length();i++) {
            JSONObject obj = resultArray.getJSONObject(i);
            HashMap<String,ArrayList<Word>> partsHashMap = new HashMap<>();
            JSONArray partsArrayJson = obj.getJSONArray("parts");
            for(int j = 0 ; j < partsArrayJson.length();j++){
                JSONObject partObj = partsArrayJson.getJSONObject(j);
                String partTitle = partObj.getString("title");
                JSONArray wordsArray = partObj.getJSONArray("words");
                ArrayList<Word> wordArrayList = new ArrayList<>();
                for(int k = 0 ; k < wordsArray.length();k++) {
                    Word word = new Word(wordsArray.getJSONObject(k).getString("name")
                            ,wordsArray.getJSONObject(k).getString("translation"));
                    wordArrayList.add(word);
                }
                partsHashMap.put(partTitle,wordArrayList);

            }

            Unit unit = new Unit(obj.getString("title"),partsHashMap);

            unitsArrayList.add(unit);
        }
        adapter.newData(unitsArrayList);
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
        void onListFragmentInteraction(Unit item);
    }
}
