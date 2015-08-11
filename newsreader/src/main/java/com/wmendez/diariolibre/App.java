package com.wmendez.diariolibre;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.ads.AdRequest;
import com.squareup.leakcanary.LeakCanary;
import com.wmendez.newsreader.lib.accounts.AccountUtils;
import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!AccountUtils.accountExists(getApplicationContext()))
            AccountUtils.createAccount(getApplicationContext());
        Fabric.with(this, new Crashlytics());

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc, String description) {
                try {
                    return doc.getElementById("newstext").html();
                } catch (NullPointerException e) {
                    //No News
                    return description;
                }
            }
        };

        LeakCanary.install(this);
    }

}
