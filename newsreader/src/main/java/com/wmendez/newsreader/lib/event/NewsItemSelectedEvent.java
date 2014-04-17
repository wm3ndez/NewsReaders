package com.wmendez.newsreader.lib.event;

import com.wmendez.newsreader.lib.helpers.Entry;

public class NewsItemSelectedEvent {
    Entry entry;

    public NewsItemSelectedEvent(Entry entry) {
        this.entry = entry;
    }

    public Entry getEntry() {
        return entry;
    }
}
