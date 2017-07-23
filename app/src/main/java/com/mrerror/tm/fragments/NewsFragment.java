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
import com.mrerror.tm.fragments.connection.NetworkConnection;
import com.mrerror.tm.models.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData {
    ArrayList<News> newsArrayList;
    // TODO: Customize parameter argument names
    private static final String ARG_TYPE = "type";
    // TODO: Customize parameters
    private String mType = "general";
    private MyNewsRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NewsFragment newInstance(String type) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            newsArrayList = new ArrayList<>();
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter =new MyNewsRecyclerViewAdapter(newsArrayList);
            getData(mType);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private void getData(String mType) {
        String url = "http://educationplatform.pythonanywhere.com/api/news/?type="+mType;
        NetworkConnection.url = url;
        new NetworkConnection(this).getDataAsJsonObject(getContext());
    }

    @Override
    public void onCompleted(String result) throws JSONException {
        JSONObject newsObj = new JSONObject(result);
        newsArrayList = new ArrayList<>();
        JSONArray resultArray = newsObj.getJSONArray("results");
        for(int i = 0 ; i < resultArray.length();i++) {
            News news = new News();
            JSONObject obj = resultArray.getJSONObject(i);
            news.setContent(obj.getString("content"));
            news.setImg_url(obj.getString("image"));
            news.setFile_url(obj.getString("file"));
            newsArrayList.add(news);
        }
        adapter.newData(newsArrayList);
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

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

}
