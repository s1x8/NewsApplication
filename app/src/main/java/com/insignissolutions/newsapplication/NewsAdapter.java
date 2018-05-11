package com.insignissolutions.newsapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    NewsAdapter(Activity context, ArrayList<News> NewsData){
        super(context,0,NewsData);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View dataItem = convertView;
        if(dataItem ==null){
            dataItem = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent,false);
        }
        ViewGroup.LayoutParams params = dataItem.getLayoutParams();
        params.height=400;
        dataItem.setLayoutParams(params);

        News currentNews= getItem(position);

        TextView title = (TextView)dataItem.findViewById(R.id.textViewHeadline);
        title.setText(currentNews.getTitle());

        TextView section = (TextView)dataItem.findViewById(R.id.textViewSection);
        section.setText(currentNews.getSection());

        TextView date = (TextView)dataItem.findViewById(R.id.textViewDate);
        date.setText(currentNews.getDate());

        ImageView thumbnail = (ImageView)dataItem.findViewById(R.id.ImageViewNews);
        Bitmap thumbnailImage = currentNews.getThumbnail();
        thumbnail.setImageBitmap(thumbnailImage);
//        if (url != null){
//            Picasso.with(getContext()).load(url).into(thumbnail);
//        }
//        else
//            thumbnail.setImageDrawable(null);
        TextView author = (TextView)dataItem.findViewById(R.id.textViewAuthor);
        author.setText(currentNews.getAuthor());

        return dataItem;
    }
}
