package com.wmendez.listindiario;

import com.wmendez.newsreader.lib.NewsApp;
import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class App extends NewsApp {

    @Override
    public void onCreate() {
        super.onCreate();

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
