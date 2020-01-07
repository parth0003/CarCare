package com.androidapp.carcare.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class GetJsonDetails extends AsyncTask<String, Void, String> {
    private String progress;
    String currentUrl=null;
    public GetJsonDetails(String progress){
        this.progress=progress;

    }
    public GetJsonDetails(){}


    @Override
    protected void onPreExecute(){

    }

    protected String doInBackground(String... arg0) {



        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(currentUrl+"jsonobjeresult",result);




    }




    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();

        return result.toString();
    }



}

