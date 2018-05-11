package com.insignissolutions.newsapplication;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    /** Query URL */
    private String mUrl;

    public NewsLoader(Context context, String Url){
        super(context);
        mUrl= Url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list news events.
        List<News> news = QueryUtils.fetchEarthquakeData(mUrl);
        return news;
    }
}
