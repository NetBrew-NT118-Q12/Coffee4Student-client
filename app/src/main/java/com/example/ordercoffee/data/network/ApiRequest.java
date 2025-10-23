package com.example.ordercoffee.data.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ApiRequest {

    public static void post(Context context, String url, JSONObject body,
                            Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void get(Context context, String url,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}