package com.wmendez.newsreader.lib.util;

import org.jsoup.nodes.Document;

public abstract class NewsHTMLParser {
    public abstract String getHtml(Document doc);
}
