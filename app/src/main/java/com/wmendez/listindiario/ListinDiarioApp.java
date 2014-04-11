package com.wmendez.listindiario;

import android.app.Application;

import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static com.wmendez.newsreader.lib.helpers.Feeds.addItem;

public class ListinDiarioApp extends Application {

    private static final String TAG = ListinDiarioApp.class.getSimpleName();

    @Override
    public void onCreate() {
        addItem(new Feeds.FeedItem("Portada", "http://listin.com.do/rss/portada/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("El Norte", "http://listin.com.do/rss/elnorte/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Economía & Negocios", "http://listin.com.do/rss/economia/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Editorial", "http://listin.com.do/rss/editorial/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Puntos de vista", "http://listin.com.do/rss/opinion/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Ventana", "http://listin.com.do/rss/ventana/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("La República", "http://listin.com.do/rss/larepublica/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Las Mundiales", "http://listin.com.do/rss/lasmundiales/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("La Vida", "http://listin.com.do/rss/lavida/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("El Deporte", "http://listin.com.do/rss/eldeporte/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Entretenimiento", "http://listin.com.do/rss/entretenimiento/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));
        addItem(new Feeds.FeedItem("Las Sociales", "http://listin.com.do/rss/sociales/", com.wmendez.newsreader.lib.R.drawable.ic_action_web_site));

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
