package com.wmendez.diariolibre;


import android.os.Bundle;

import com.splunk.mint.Mint;
import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.ui.FeedCategoryListActivity;


public class MainActivity extends FeedCategoryListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mint.initAndStartSession(MainActivity.this, getString(R.string.bugsense_key));
        super.onCreate(savedInstanceState);
    }

}
