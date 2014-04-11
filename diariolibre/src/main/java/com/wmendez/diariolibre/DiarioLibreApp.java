package com.wmendez.diariolibre;

import android.app.Application;

import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class DiarioLibreApp extends Application {

    @Override
    public void onCreate() {
        addItem(new Feeds.FeedItem("Portada", "http://www.diariolibre.com/rss/diariolibre/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Noticias", "http://www.diariolibre.com/rss/noticias/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Economía", "http://www.diariolibre.com/rss/?id=10", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Actualidad", "http://www.diariolibre.com/rss/?id=64", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Revista", "http://www.diariolibre.com/rss/revista/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Cinema", "http://www.diariolibre.com/rss/?id=72", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Deportes", "http://www.diariolibre.com/rss/deportes/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Opinión", "http://www.diariolibre.com/rss/opinion/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Ecos", "http://www.diariolibre.com/rss/ecos/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Lecturas", "http://www.diariolibre.com/rss/lecturas/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("DL English", "http://www.diariolibre.com/rss/dlenglish/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("DL Educación", "http://www.diariolibre.com/rss/educacion/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Tecnología", "http://www.diariolibre.com/rss/?id=82", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Espacios", "http://www.diariolibre.com/rss/?id=63", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Habitat", "http://www.diariolibre.com/rss/?id=68", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Fotonoticias", "http://www.diariolibre.com/rss/?id=67", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc) {
                return doc.getElementById("newstext").html();
            }
        };
    }

}
