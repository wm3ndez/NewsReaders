package com.wmendez.newsreader.lib.helpers;


import com.wmendez.newsreader.lib.util.NewsHTMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feeds {

    public static List<FeedItem> ITEMS = new ArrayList<FeedItem>();

    public static Map<String, FeedItem> ITEM_MAP = new HashMap<String, FeedItem>();

    public static NewsHTMLParser parser;

    public static void addItem(FeedItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.uri, item);
    }

    public static class FeedItem {
        public String title;
        public String uri;
        public int resource;

        public FeedItem(String title, String uri, int resource) {
            this.title = title;
            this.uri = uri;
            this.resource = resource;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
