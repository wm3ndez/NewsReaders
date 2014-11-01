package com.wmendez.elcaribe;

import android.app.Application;

import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class App extends Application {

    @Override
    public void onCreate() {
        addItem(new Feeds.FeedItem("Portada", "http://www.elcaribe.com.do/rss", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Panorama", "http://www.elcaribe.com.do/rss/seccion/panorama", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Deportes", "http://www.elcaribe.com.do/rss/seccion/deportes", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Gente", "http://www.elcaribe.com.do/rss/seccion/gente", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Opiniones", "http://www.elcaribe.com.do/rss/seccion/opiniones", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc) {
                return doc.getElementsByClass("cuerpoNoticia").html();
            }
        };
    }

}
