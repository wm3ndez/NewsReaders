package com.wmendez.newsreader.lib.event;

import com.wmendez.newsreader.lib.helpers.Feeds;

public class CategoryItemSelectedEvent {
    Feeds.FeedItem item;

    public CategoryItemSelectedEvent(Feeds.FeedItem item) {
        this.item = item;
    }

    public Feeds.FeedItem getItem() {
        return item;
    }
}
