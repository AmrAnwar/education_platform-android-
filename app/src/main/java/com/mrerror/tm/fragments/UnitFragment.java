package com.mrerror.tm.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrerror.tm.MainActivity;
import com.mrerror.tm.R;
import com.mrerror.tm.adapter.UnitsRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.Part;
import com.mrerror.tm.models.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.mrerror.tm.connection.NetworkConnection.url;

public class UnitFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData {
    ArrayList<Unit> unitsArrayList;
    // TODO: Customize parameter argument names
    private static final String ARG_TYPE = "type";
    // TODO: Customize parameters
    private String mLink = "";
    private UnitsRecyclerViewAdapter adapter;
    private OnListFragmentInteractionListener mListener;
    private ProgressDialog progressdialog;
    LoadMoreData loadMoreData;
    String nextURl = "";
    int scrolFalg = 0;
    String noInterNet = "No_InterNet";
    String no_list = "List_is_empty";
    TextView blankText;
    ProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;


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
            scrolFalg = 0;
            unitsArrayList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        mProgressBar = (ProgressBar) ((MainActivity) getActivity()).findViewById(R.id.progressbar);
        blankText = (TextView) ((MainActivity) getActivity()).findViewById(R.id.no_list_net);
        loadMoreData = new LoadMoreData() {
            @Override
            public void loadMorData(String url) {

                getData(url);

            }
        };
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshnewsunit);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    blankText.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    unitsArrayList = new ArrayList<>();
                    scrolFalg = 0;
                    getData(mLink);
                } else {

                    Toast.makeText(getContext(), "No InterNet", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        // Set the adapter

        unitsArrayList = new ArrayList<>();
        Context context = view.getContext();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int x = linearLayoutManager.findLastVisibleItemPosition();

                if (x % 4 == 0 && x >= 4 && x > scrolFalg && !nextURl.equals("null") && !nextURl.isEmpty()) {
                    {
                        loadMoreData.loadMorData(nextURl);

                        scrolFalg = x;
                    }
                }


            }
        });

        adapter = new UnitsRecyclerViewAdapter(unitsArrayList, mListener);
        getData(mLink);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void getData(String link) {
        if (isOnline()) {
            mProgressBar.setVisibility(View.VISIBLE);
            blankText.setVisibility(View.GONE);

            url = link;
            new NetworkConnection(this).getDataAsJsonObject(getContext());
        } else {
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
        nextURl = unitsObj.getString("next");
        mSwipeRefreshLayout.setRefreshing(false);

        JSONArray resultArray = unitsObj.getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject obj = resultArray.getJSONObject(i);
            ArrayList<Part> partsList = new ArrayList<>();
            JSONArray partsArrayJson = obj.getJSONArray("parts");
            for (int j = 0; j < partsArrayJson.length(); j++) {
                JSONObject partObj = partsArrayJson.getJSONObject(j);
                String partTitle = partObj.getString("title");
                String wordsLink = partObj.getString("urlwords");
                String testLink = partObj.getString("urltests");
                Part part = new Part(partTitle, wordsLink, testLink);
                partsList.add(part);
            }

            Unit unit = new Unit(obj.getString("title"), partsList);

            unitsArrayList.add(unit);
        }
        adapter.newData(unitsArrayList);
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);
        if (unitsArrayList.isEmpty()) {
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
