package com.wmendez.elcaribe;

import android.app.Application;

import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class App extends Application {

    @Override
    public void onCreate() {
        addItem(new Feeds.FeedItem("Portada", "http://www.elcaribe.com.do/rss"));
        addItem(new Feeds.FeedItem("Panorama", "http://www.elcaribe.com.do/rss/seccion/panorama"));
        addItem(new Feeds.FeedItem("Deportes", "http://www.elcaribe.com.do/rss/seccion/deportes"));
        addItem(new Feeds.FeedItem("Gente", "http://www.elcaribe.com.do/rss/seccion/gente"));
        addItem(new Feeds.FeedItem("Opiniones", "http://www.elcaribe.com.do/rss/seccion/opiniones"));

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc) {
                return doc.getElementsByClass("cuerpoNoticia").html();
            }
        };
    }

}
