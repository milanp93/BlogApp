package com.example.milan.proba;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BlogDisplay extends AppCompatActivity {
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webview = new WebView(this);
        setContentView(webview);
        //setContentView(R.layout.activity_blog_display);
        String uri;
        SharedPreferences sharedpreferences = BlogDisplay.this.getSharedPreferences("com.example.milan.proba", Context.MODE_PRIVATE);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                uri= null;
            } else {
                uri= extras.getString("Uri");
                sharedpreferences.edit().putString("Uri",uri).commit();
            }
            Log.d("MP","radi");
        } else {
            uri= sharedpreferences.getString("Uri",null);
            Log.d("MP","ne radi");
        }
        if(uri==null || sharedpreferences.getString("token",null)==null)
            finish();


        if(!isNetworkAvailable()) {
            webview.loadData("<p>No internet connection</p>","text/html",null);
            Log.d("MP","nema neta");
        }

        new AsyncTask<String,Void,String>(){

            protected String doInBackground(String... Url) {
                while(!isNetworkAvailable()){
                    try {
                        Log.d("MP","nema neta bre");
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String url=Url[0];
                URL object= null;
                Log.d("MP","radiiiii");
                try {
                    object = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    SharedPreferences sharedpreferences = BlogDisplay.this.getSharedPreferences("com.example.milan.proba", Context.MODE_PRIVATE);
                    con.setRequestProperty("X-Authorize", sharedpreferences.getString("token",""));
                    con.setRequestMethod("GET");
                    con.setDoOutput(false);
                    con.setDoInput(true);
                    con.connect();

                    int HttpResult = con.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        StringBuilder sb = new StringBuilder();
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            Log.d("MPP",line);
                            sb.append(line);
                        }
                        br.close();
                        Log.d("MP",sb.toString());
                        return sb.toString();
                    } else {
                        Log.d("MPP",con.getResponseMessage());
                    }
                } catch (Exception e) {
                    Log.d("MPPP",e.toString());
                }
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    Log.d("MP",json.getString("content"));
                    webview.loadData(json.getString("content"),"text/html",null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.execute(uri);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
