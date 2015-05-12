package com.wmendez.diariolibre;

import com.squareup.leakcanary.LeakCanary;
import com.wmendez.newsreader.lib.NewsApp;
import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

public class App extends NewsApp {

    @Override
    public void onCreate() {
        super.onCreate();

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
