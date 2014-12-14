package com.wmendez.newsreader.lib.net;

import android.content.Context;

import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.helpers.Entry;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public class NewsAPI {


    public static String getAPIUrl(Context mContext) {
        return mContext.getString(R.string.api_url);
    }


    public interface API {
        @GET("/news/?format=json")
        void newsList(Callback<List<Entry>> cb, @Query("diary") String diary, @Query("last_updated") String last_updated);

        @GET("/news/?format=json")
        List<Entry> newsList(@Query("diary") String diary, @Query("last_updated") Long last_updated);
    }
}
