package com.example.milan.proba;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BlogList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);

        TextView internetNotification = (TextView) findViewById(R.id.internet_notification);

        if(!NetworkCheck.isNetworkAvailable(this)) {
            internetNotification.setVisibility(View.VISIBLE);
        }



        new AsyncTask<Void,Void,String>(){
            ProgressDialog pDialog;
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog
                pDialog = new ProgressDialog(BlogList.this);
                if(!NetworkCheck.isNetworkAvailable(BlogList.this))
                    pDialog.setMessage("No internet connection");
                else
                    pDialog.setMessage("Please wait...");
                //pDialog.setCancelable(false);
                pDialog.show();

            }
            protected String doInBackground(Void... params) {
                while(!NetworkCheck.isNetworkAvailable(BlogList.this)){
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String url="http://blogsdemo.creitiveapps.com:16427/blogs";
                URL object= null;
                try {
                    object = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    SharedPreferences sharedpreferences = BlogList.this.getSharedPreferences("com.example.milan.proba", Context.MODE_PRIVATE);
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
                            sb.append(line).append("\n");
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
            protected void onPostExecute(String response) {
                if(response!=null){
                    try {
                        TextView internetNotification = (TextView) findViewById(R.id.internet_notification);
                        internetNotification.setVisibility(View.INVISIBLE);
                        JSONArray jsonBlogs = new JSONArray(response);
                        List<Blog> blogs = new ArrayList<Blog>();

                        // looping through All Contacts
                        for (int i = 0; i < jsonBlogs.length(); i++) {
                            JSONObject c = jsonBlogs.getJSONObject(i);

                            String id = c.getString("id");
                            String title = c.getString("title");
                            String image_url = c.getString("image_url");
                            String description = c.getString("description");
                            blogs.add(new Blog(Integer.parseInt(id),title,description,image_url));
                        }


                        BlogAdapter adapter = new BlogAdapter(BlogList.this, blogs);

                        ListView listView = (ListView) findViewById(R.id.blog_list);

                        listView.setAdapter(adapter);
                        if (pDialog.isShowing())
                            pDialog.dismiss();

                    } catch (final JSONException e) {
                        Log.e("MP", "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }
                }
            }
        }.execute();

        ListView listView = (ListView) findViewById(R.id.blog_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String uri = "http://blogsdemo.creitiveapps.com:16427/blogs/" + parent.getAdapter().getItemId((int) id);
                    Intent intent = new Intent(getApplicationContext(), BlogDisplay.class);
                    intent.putExtra("Uri",uri);
                    startActivity(intent, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
