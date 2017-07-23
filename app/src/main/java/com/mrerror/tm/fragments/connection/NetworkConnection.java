package com.mrerror.tm.fragments.connection;

/**
 * Created by ahmed on 23/07/17.
 */


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahmed on 20/06/17.
 */

public class NetworkConnection {

    public  static String url = "";
    private OnCompleteFetchingData OnCompleteFetchingData;

    public NetworkConnection(OnCompleteFetchingData onComplete) {
        OnCompleteFetchingData = onComplete;
    }

    public void getDataAsJsonArray(Context context) {
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            OnCompleteFetchingData.onCompleted(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsArrRequest);
    }

    public void getDataAsJsonObject(Context context) {
        JsonObjectRequest jsArrRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            OnCompleteFetchingData.onCompleted(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsArrRequest);
    }

    public interface OnCompleteFetchingData {
        void onCompleted(String result) throws JSONException;
    }
}