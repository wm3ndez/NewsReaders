package com.wmendez.elnacional;

import android.app.Application;

import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class ElNacionalApp extends Application {

    @Override
    public void onCreate() {
        addItem(new Feeds.FeedItem("El Nacional", "http://elnacional.com.do/feed/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc) {
                return doc.getElementsByClass("pf-content").html();
            }
        };
    }
}
