package com.mrerror.tm.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kareem on 7/30/2017.
 */

public class TestFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData{
    private static final String ARG_TYPE = "type";
    private String mTestUrl = null;
    RecyclerView recyclerView;
    ArrayList<Test> allTestArray;
    TestAdapter mAdapter;
    String noInterNet="No_InterNet";
    String no_list="List_is_empty";
    TextView blankText;
    ProgressBar mProgressBar;

   public  TestFragment(){
    }
    public static TestFragment newInstance(String testUrl) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, testUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTestUrl = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        allTestArray=new ArrayList<>();
        View view=inflater.inflate(R.layout.fragment_news_list,container,false);
        mProgressBar= (ProgressBar) ((MainActivity)getActivity()).findViewById(R.id.progressbar);
        blankText=(TextView) ((MainActivity)getActivity()).findViewById(R.id.no_list_net);
        recyclerView=(RecyclerView) view.findViewById(R.id.list);
        mAdapter =new TestAdapter(allTestArray);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        getData();
        return view;
    }
    private void getData() {
        if(isOnline()) {
            mProgressBar.setVisibility(View.VISIBLE);
            blankText.setVisibility(View.GONE);

            NetworkConnection.url = mTestUrl;
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
        JSONObject testJsonObj= new JSONObject(result);
        JSONArray tests=testJsonObj.getJSONArray("tests");
        for(int i=0;i<tests.length();i++){
            Test newTest=new Test();
            newTest.setTitle(tests.getJSONObject(i).getString("title"));
            newTest.setComplete(tests.getJSONObject(i).getJSONArray("complete"));
            newTest.setDialog(tests.getJSONObject(i).getJSONArray("dialog"));
            newTest.setMistake(tests.getJSONObject(i).getJSONArray("mistake"));
            allTestArray.add(newTest);
        }
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);
        if(allTestArray.isEmpty()||allTestArray.get(allTestArray.size()-1).getTitle()==null){
            blankText.setText(no_list);
            blankText.setVisibility(View.VISIBLE);

        }


    }
}
