package com.wmendez.newsreader.lib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.wmendez.diariolibre.R;
import com.wmendez.newsreader.lib.event.NewsItemSelectedEvent;

import de.greenrobot.event.EventBus;


/**
 * An activity representing a list of Items.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.wmendez.newsreader.lib.ui.FeedListFragment}.
 * <p/>
 */
public class FeedCategoryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsColorsFragment fragment = new SlidingTabsColorsFragment();
            transaction.replace(R.id.content_fragment, fragment);
            transaction.commit();
        }

        EventBus.getDefault().register(this);
    }


    public void onEvent(NewsItemSelectedEvent event) {
        Intent intent = new Intent(this, NewsActivity.class);
        intent.putExtra("news", event.getEntry());
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
