package com.wmendez.diariolibre;

import android.app.Application;

import com.wmendez.newsreader.lib.accounts.AccountUtils;
import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc) {
                try {
                    return doc.getElementById("newstext").html();
                } catch (NullPointerException e) {
                    //No News
                    return "<h4 style='text-align:center;'>Oh oh! Esta noticia no existe.</h4>";
                }
            }
        };
    }

}
