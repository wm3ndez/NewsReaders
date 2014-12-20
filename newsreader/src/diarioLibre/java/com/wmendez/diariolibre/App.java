package com.wmendez.diariolibre;

import android.app.Application;

import com.wmendez.newsreader.lib.accounts.AccountUtils;
import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class App extends Application {

    @Override
    public void onCreate() {
        if (!AccountUtils.accountExists(getApplicationContext()))
            AccountUtils.createAccount(getApplicationContext());
//        addItem(new Feeds.FeedItem("Portada", "http://www.diariolibre.com/rss/diariolibre/"));
//        addItem(new Feeds.FeedItem("Cinema", "http://www.diariolibre.com/rss/?id=72"));
        addItem(new Feeds.FeedItem("Noticias", "http://www.diariolibre.com/rss/noticias/"));
        addItem(new Feeds.FeedItem("Economía", "http://www.diariolibre.com/rss/?id=10"));
        addItem(new Feeds.FeedItem("Actualidad", "http://www.diariolibre.com/rss/?id=64"));
        addItem(new Feeds.FeedItem("Revista", "http://www.diariolibre.com/rss/revista/"));
        addItem(new Feeds.FeedItem("Deportes", "http://www.diariolibre.com/rss/deportes/"));
        addItem(new Feeds.FeedItem("Opinión", "http://www.diariolibre.com/rss/opinion/"));
        addItem(new Feeds.FeedItem("Ecos", "http://www.diariolibre.com/rss/ecos/"));
        addItem(new Feeds.FeedItem("Lecturas", "http://www.diariolibre.com/rss/lecturas/"));
        addItem(new Feeds.FeedItem("DL English", "http://www.diariolibre.com/rss/dlenglish/"));
        addItem(new Feeds.FeedItem("DL Educación", "http://www.diariolibre.com/rss/educacion/"));
        addItem(new Feeds.FeedItem("Tecnología", "http://www.diariolibre.com/rss/?id=82"));
        addItem(new Feeds.FeedItem("Espacios", "http://www.diariolibre.com/rss/?id=63"));
        addItem(new Feeds.FeedItem("Habitat", "http://www.diariolibre.com/rss/?id=68"));
        addItem(new Feeds.FeedItem("Fotonoticias", "http://www.diariolibre.com/rss/?id=67"));

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
