package com.mrerror.tm.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrerror.tm.MainActivity;
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
    String noInterNet="No_InterNet";
    String no_list="List_is_empty";
    TextView blankText;
    ProgressBar mProgressBar;

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
        mProgressBar= (ProgressBar) ((MainActivity)getActivity()).findViewById(R.id.progressbar);
        blankText=(TextView) ((MainActivity)getActivity()).findViewById(R.id.no_list_net);
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
        if(isOnline()) {
            mProgressBar.setVisibility(View.VISIBLE);
            blankText.setVisibility(View.GONE);

            NetworkConnection.url = mWordsUrl;
            new NetworkConnection(this).getDataAsJsonObject(getContext());
        }else {
            mProgressBar.setVisibility(View.GONE);
            blankText.setText(noInterNet);
            blankText.setVisibility(View.VISIBLE);

        }

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);
        if(mWordsList.isEmpty()){
            blankText.setText(no_list);
            blankText.setVisibility(View.VISIBLE);

        }
    }
}
