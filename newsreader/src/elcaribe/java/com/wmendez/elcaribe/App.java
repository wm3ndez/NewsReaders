package com.wmendez.elcaribe;

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
            public String getHtml(Document doc) {
                return doc.getElementsByClass("cuerpoNoticia").html();
            }
        };
    }

}
