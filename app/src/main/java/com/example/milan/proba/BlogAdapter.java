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

import com.squareup.picasso.Picasso;

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
        Picasso.with(context).load(imageUrl).resize(100,100).centerCrop().into(iconView);

        titleView.setText(title);
        descriptionView.setText(Html.fromHtml(description));

        return view;
    }
}
