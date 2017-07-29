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
import com.mrerror.tm.adapter.WordsRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WordsFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData {
    // TODO: Customize parameter argument names
    private static final String ARG_TYPE = "type";
    // TODO: Customize parameters
    private String mWordsUrl = null;
    private ArrayList<Word> mWordsList = null;
    private WordsRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WordsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static WordsFragment newInstance(String wordsUrl) {
        WordsFragment fragment = new WordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, wordsUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mWordsUrl = getArguments().getString(ARG_TYPE);
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
            mWordsList = new ArrayList<>();
            adapter =new WordsRecyclerViewAdapter(mWordsList);
            getData();
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private void getData() {

        NetworkConnection.url = mWordsUrl;
        new NetworkConnection(this).getDataAsJsonObject(getContext());
    }

    @Override
    public void onCompleted(String result) throws JSONException {
        JSONObject unitsObj = new JSONObject(result);
        if(mWordsList.size()>0)
            mWordsList = new ArrayList<>();
        JSONArray wordsJsonArray = unitsObj.getJSONArray("words");
        for(int i = 0 ; i < wordsJsonArray.length();i++){
            JSONObject wordObj = wordsJsonArray.getJSONObject(i);
            Word word = new Word(wordObj.getString("name"),wordObj.getString("translation"));
            mWordsList.add(word);
        }
        adapter.notifyDataSetChanged();
    }
}
