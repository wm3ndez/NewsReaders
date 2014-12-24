package com.wmendez.elcaribe;

import com.wmendez.newsreader.lib.NewsApp;
import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import java.lang.String;

public class App extends NewsApp {

    @Override
    public void onCreate() {
        super.onCreate();

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc, String description) {
                return doc.getElementsByClass("cuerpoNoticia").html();
            }
        };
    }

}
