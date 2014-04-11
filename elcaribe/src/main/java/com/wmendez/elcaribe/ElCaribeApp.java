package com.wmendez.elcaribe;

import android.app.Application;

import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class ElCaribeApp extends Application {

    @Override
    public void onCreate() {
        addItem(new Feeds.FeedItem("El Caribe", "http://www.elcaribe.com.do/rss", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc) {
                return doc.getElementById("newstext").html();
            }
        };
    }

}
