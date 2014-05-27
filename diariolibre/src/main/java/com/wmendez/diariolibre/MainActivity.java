package com.wmendez.diariolibre;


import android.os.Bundle;

import com.bugsense.trace.BugSenseHandler;
import com.wmendez.newsreader.lib.ui.FeedCategoryListActivity;


public class MainActivity extends FeedCategoryListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BugSenseHandler.initAndStartSession(MainActivity.this, getString(R.string.bugsense_key));
        super.onCreate(savedInstanceState);
    }

}
