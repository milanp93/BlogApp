package com.example.milan.proba;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Milan on 2/24/2017.
 */

public class BlogAdapter extends BaseAdapter implements ListAdapter {
    private List<Blog> blogs;
    private Context context;
    private String imageUrl;
    private URL url;
    private ImageView iconView;

    public BlogAdapter(Context context, List<Blog> blogs) {
        this.context = context;
        this.blogs = blogs;
    }

    @Override
    public int getCount() {
        return blogs.size();
    }

    @Override
    public Object getItem(int position) {
        return blogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return blogs.get(position).getId();
    }

    @Override
    public synchronized View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_blog_item, null);

        Blog blog = blogs.get(position);
        String title = blog.getTitle();
        String description = blog.getDescription();
        imageUrl = blog.getImageUrl();

        TextView titleView = (TextView) view.findViewById(R.id.list_blog_title);
        TextView descriptionView = (TextView) view.findViewById(R.id.list_blog_description);
        iconView = (ImageView) view.findViewById(R.id.list_blog_icon);

        url = null;
        new AsyncTask<Void,Void,Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                try

                {
                    Log.d("MP", "radi1" + imageUrl);
                    url = new URL(imageUrl);
                    Log.d("MP", "radi2");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    Log.d("MP", "radi3");
                    con.setRequestProperty("Content-Type", "application/json");
                    Log.d("MP", "radi4");
                    con.setRequestProperty("Accept", "application/json");
                    Log.d("MP", "radi5");
                    SharedPreferences sharedpreferences = context.getSharedPreferences("com.example.milan.proba", Context.MODE_PRIVATE);
                    Log.d("MP", "radi6");
                    con.setRequestProperty("X-Authorize", sharedpreferences.getString("token", ""));
                    Log.d("MP", "radi7");
                    con.setRequestMethod("GET");
                    Log.d("MP", "radi8");
                    con.setDoOutput(false);
                    Log.d("MP", "radi9");
                    con.setDoInput(true);
                    Log.d("MP", "radi10");
                    con.connect();
                    Log.d("MP", "radi11");
                    Bitmap bmp = BitmapFactory.decodeStream(con.getInputStream());
                    Log.d("MP", "radi12");
                    return bmp;
                }
                catch(Exception e)
                {
                    Log.d("MP", e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap!=null)
                    iconView.setImageBitmap(bitmap);
            }
        }.execute();
        titleView.setText(title);
        descriptionView.setText(Html.fromHtml(description));

        return view;
    }
}
