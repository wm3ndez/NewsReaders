package com.wmendez.noticiassin;

import android.app.Application;

import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class NoticiasSINApp extends Application {
    @Override
    public void onCreate() {
        addItem(new Feeds.FeedItem("Noticias SIN", "http://feeds.feedburner.com/noticiassin1?format=xml", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc) {
                return doc.getElementsByClass("pf-content").html();
            }
        };
    }
}
