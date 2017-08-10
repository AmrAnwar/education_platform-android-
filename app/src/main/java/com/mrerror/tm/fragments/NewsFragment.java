package com.mrerror.tm.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.mrerror.tm.adapter.MyNewsRecyclerViewAdapter;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.models.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements NetworkConnection.OnCompleteFetchingData,
        MyNewsRecyclerViewAdapter.OnListFragmentInteractionListener {
    ArrayList<News> newsArrayList;
    // TODO: Customize parameter argument names
    private static final String ARG_TYPE = "type";
    // TODO: Customize parameters
    private String mType = "general";
    private MyNewsRecyclerViewAdapter adapter;
    private ProgressDialog progressdialog;
    LoadMoreData loadMoreData;
    int scrolFalg = 0;
    String nextURl = "";
    String url;

       ProgressBar mProgressBar;
    TextView blankText;
    String noInterNet="No_InterNet";
      String no_list="List_is_empty";

    SwipeRefreshLayout mSwipeRefreshLayout;
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
            newsArrayList = new ArrayList<>();
            scrolFalg = 0;

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadMoreData = new LoadMoreData() {
            @Override
            public void loadMorData(String urlNext) {getData(urlNext);}};
        url = "http://educationplatform.pythonanywhere.com/api/news/?type="+mType;
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
         mProgressBar= (ProgressBar) ((MainActivity)getActivity()).findViewById(R.id.progressbar);

         blankText=(TextView) ((MainActivity)getActivity()).findViewById(R.id.no_list_net);
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.refreshnewsunit);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isOnline())
                {   blankText.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    scrolFalg=0;
                    newsArrayList=new ArrayList<>();
                    getData(url);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        // Set the adapter

            newsArrayList = new ArrayList<>();
            Context context = view.getContext();

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
          final    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);

            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int x = linearLayoutManager.findLastVisibleItemPosition();

                    if (x % 4 == 0 && x >= 4 && x > scrolFalg && !nextURl.equals("null") && !nextURl.isEmpty()) {
                        {
                            loadMoreData.loadMorData(nextURl);

                            scrolFalg = x;
                        }
                    }
                }
            });
            adapter = new MyNewsRecyclerViewAdapter(newsArrayList, this);
            getData(url);
            recyclerView.setAdapter(adapter);

        return view;
    }

    private void getData(String url) {
        if (isOnline()) {
            mProgressBar.setVisibility(View.VISIBLE);
            blankText.setVisibility(View.GONE);

            NetworkConnection.url = url;
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
        JSONObject newsObj = new JSONObject(result);

        mSwipeRefreshLayout.setRefreshing(false);
        nextURl=newsObj.getString("next");

        JSONArray resultArray = newsObj.getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
            News news = new News();
            JSONObject obj = resultArray.getJSONObject(i);
            news.setContent(obj.getString("content"));
            news.setImg_url(obj.getString("image"));
            news.setFile_url(obj.getString("file"));
            newsArrayList.add(news);
        }
        adapter.newData(newsArrayList);
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);


    }

    @Override
    public void onError(String error) {
        mProgressBar.setVisibility(View.GONE);
        blankText.setText("an error happened ");
        blankText.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onListFragmentInteraction(News item) {
        downLoad(Uri.parse(item.getFile_url()));
    }

    DownloadManager downloadManager;
    long reference;

    public  void downLoad(Uri uri ){


        downloadManager=  (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request= new DownloadManager.Request(uri);

        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        reference= downloadManager.enqueue(request);

    }

//    class DownloadFileFromURL extends AsyncTask<String, String, String> {
//
//        /**
//         * Before starting background thread
//         * Show Progress Bar Dialog
//         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            verifyStoragePermissions(getActivity());
//            showDialog();
//        }
//
//        /**
//         * Downloading file in background thread
//         */
//        @Override
//        protected String doInBackground(String... f_url) {
//            int count;
//            try {
//                URL url = new URL(f_url[0]);
//                URLConnection conection = url.openConnection();
//                conection.connect();
//                // this will be useful so that you can show a tipical 0-100%           progress bar
//                int lenghtOfFile = conection.getContentLength();
//
//                // type the file
//                InputStream input = new BufferedInputStream(url.openStream(), 8192);
//
//                // Output stream
//                String[] splitlist = url.toString().split("/");
//                OutputStream output = new FileOutputStream("/sdcard/" + splitlist[splitlist.length - 1]);
//
//                byte data[] = new byte[1024];
//
//                long total = 0;
//
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//                    // publishing the progress....
//                    // After this onProgressUpdate will be called
//                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
//
//                    // writing data to file
//                    output.write(data, 0, count);
//                }
//
//                // flushing output
//                output.flush();
//
//                // closing streams
//                output.close();
//                input.close();
//
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//            }
//
//            return null;
//        }
//
//        /**
//         * Updating progress bar
//         */
//        protected void onProgressUpdate(String... progress) {
//            // setting progress percentage
//            progressdialog.setProgress(Integer.parseInt(progress[0]));
//        }
//
//        /**
//         * After completing background task
//         * Dismiss the progress dialog
//         **/
//        @Override
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog after the file was downloaded
//            dismissDialog();
//
//        }
//
//    }
//
//    private void dismissDialog() {
//        progressdialog.dismiss();
//    }
//
//    private void showDialog() {
//        progressdialog = new ProgressDialog(getContext());
//        progressdialog.setMessage("Downloading ...) ");
//        progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressdialog.setIndeterminate(true);
//        progressdialog.show();
//    }


    // permissions
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
