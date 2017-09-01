package com.mrerror.tm.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrerror.tm.R;
import com.mrerror.tm.adapter.WordsRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.mrerror.tm.connection.NetworkConnection.url;

public class WordsFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData {
    // TODO: Customize parameter argument names
    private static final String ARG_TYPE = "type";
    // TODO: Customize parameters
    private String mWordsUrl = null;
    private ArrayList<Word> mWordsList = null;
    private WordsRecyclerViewAdapter adapter;
    String noInterNet = "No_InterNet";
    String no_list = "List_is_empty";
    TextView blankText;
    ProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    CheckBox exam,cFav,other;
    SharedPreferences sp;

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
         sp= PreferenceManager.getDefaultSharedPreferences(getContext());
        View view = inflater.inflate(R.layout.fragment_modelanswer_with_checbox, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        blankText = (TextView) view.findViewById(R.id.blanktextview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        exam = (CheckBox) view.findViewById(R.id.exams);
        cFav=(CheckBox) view.findViewById(R.id.sheet);
        other=(CheckBox) view.findViewById(R.id.other);
        exam.setVisibility(View.INVISIBLE);
        other.setVisibility(View.INVISIBLE);

        cFav.setText("FAVORITE");

        cFav.setOnClickListener(cFavOnClick);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    blankText.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);

                   if(cFav.isChecked()){getFavData();}
                   else {
                       mWordsList = new ArrayList<>();
                    getData();}
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {

                    Toast.makeText(getContext(), "No InterNet", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        // Set the adapter

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.model_answerlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mWordsList = new ArrayList<>();
        adapter = new WordsRecyclerViewAdapter(mWordsList);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        getData();
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void getData() {
        if (isOnline()) {
            mProgressBar.setVisibility(View.VISIBLE);
            blankText.setVisibility(View.GONE);

            url = mWordsUrl;
            new NetworkConnection(this).getDataAsJsonObject(getContext());
        } else {
            mProgressBar.setVisibility(View.GONE);
            blankText.setText(noInterNet);
            blankText.setVisibility(View.VISIBLE);

        }

    }

    View.OnClickListener cFavOnClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         if(cFav.isChecked()){
             getFavData();
         }else {
             adapter.onChange(mWordsList);
             adapter.notifyDataSetChanged();
         }
        }
    };

    private  void getFavData(){
        NetworkConnection.url=mWordsUrl+sp.getInt("id",0);
        Log.d("chekFav",NetworkConnection.url);
        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData()
        {
            @Override
            public void onCompleted(String result) throws JSONException {
                ArrayList<Word> favWords=new ArrayList<>();

                JSONArray wordsJsonArray = new JSONArray(result);
                for (int i = 0; i < wordsJsonArray.length(); i++) {
                    JSONObject wordObj = wordsJsonArray.getJSONObject(i);
                    JSONArray users= wordObj.getJSONArray("users");
                    Word word = new Word(wordObj.getString("name"), wordObj.getString("translation"),users,wordObj.getInt("id"),sp);
                    favWords.add(word);
                }
                adapter.onChange(favWords);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(String error) {

            }
        }).getDataAsJsonArray(getContext());


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
        mSwipeRefreshLayout.setRefreshing(false);
        if (mWordsList.size() > 0)
            mWordsList = new ArrayList<>();

        JSONArray wordsJsonArray = unitsObj.getJSONArray("words");
        for (int i = 0; i < wordsJsonArray.length(); i++) {
            JSONObject wordObj = wordsJsonArray.getJSONObject(i);
            JSONArray users= wordObj.getJSONArray("users");
            Word word = new Word(wordObj.getString("name"), wordObj.getString("translation"),users,wordObj.getInt("id"),sp);
            mWordsList.add(word);
        }
        adapter.onChange(mWordsList);
        adapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);
        if (mWordsList.isEmpty()) {
            blankText.setText(no_list);
            blankText.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onError(String error) {
        mProgressBar.setVisibility(View.GONE);
        blankText.setText("an error happened ");
        blankText.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }
}
