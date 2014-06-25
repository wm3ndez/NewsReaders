package com.wmendez.newsreader.lib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.event.CategoryItemSelectedEvent;
import com.wmendez.newsreader.lib.event.NewsItemSelectedEvent;
import com.wmendez.newsreader.lib.helpers.Feeds;

import de.greenrobot.event.EventBus;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p/>
 */
public class FeedCategoryListActivity extends FragmentActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        if (findViewById(R.id.feed_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((FeedCategoryListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }
        EventBus.getDefault().register(this);

    }


    public void onEvent(CategoryItemSelectedEvent event) {
        Feeds.FeedItem item = event.getItem();
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString("title", item.title);
            arguments.putString("uri", item.uri);
            arguments.putInt("resource", item.resource);
            FeedListFragment fragment = new FeedListFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.feed_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, FeedListActivity.class);
            detailIntent.putExtra("title", item.title);
            detailIntent.putExtra("uri", item.uri);
            detailIntent.putExtra("resource", item.resource);
            startActivity(detailIntent);
        }
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
