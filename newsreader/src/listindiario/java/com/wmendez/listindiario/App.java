package com.wmendez.listindiario;

import android.app.Application;

import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        addItem(new Feeds.FeedItem("Portada", "http://listin.com.do/rss/portada/"));
        addItem(new Feeds.FeedItem("El Norte", "http://listin.com.do/rss/elnorte/"));
        addItem(new Feeds.FeedItem("Economía & Negocios", "http://listin.com.do/rss/economia/"));
        addItem(new Feeds.FeedItem("Editorial", "http://listin.com.do/rss/editorial/"));
        addItem(new Feeds.FeedItem("Puntos de vista", "http://listin.com.do/rss/opinion/"));
        addItem(new Feeds.FeedItem("Ventana", "http://listin.com.do/rss/ventana/"));
        addItem(new Feeds.FeedItem("La República", "http://listin.com.do/rss/larepublica/"));
        addItem(new Feeds.FeedItem("Las Mundiales", "http://listin.com.do/rss/lasmundiales/"));
        addItem(new Feeds.FeedItem("La Vida", "http://listin.com.do/rss/lavida/"));
        addItem(new Feeds.FeedItem("El Deporte", "http://listin.com.do/rss/eldeporte/"));
        addItem(new Feeds.FeedItem("Entretenimiento", "http://listin.com.do/rss/entretenimiento/"));
        addItem(new Feeds.FeedItem("Las Sociales", "http://listin.com.do/rss/sociales/"));

        Feeds.parser = new NewsHTMLParser() {
            @Override
            public String getHtml(Document doc) {
                Element articleBody = doc.getElementById("ArticleBody");
                if (articleBody == null) {
                    //Is mobile version
                    articleBody = doc.getElementsByClass("principal").first();

                    // Remove post info
                    articleBody.getElementsByTag("h1").first().remove();
                    articleBody.getElementsByTag("a").first().remove();
                    articleBody.getElementsByTag("p").first().remove();
                    articleBody.getElementsByTag("span").first().remove();
                    try {
                        articleBody.getElementsByTag("img").first().remove();
                    } catch (NullPointerException e) {

                    }
                }
                return articleBody.html();
            }
        };
    }

}
