package com.wmendez.newsreader.lib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.event.NewsItemSelectedEvent;

import de.greenrobot.event.EventBus;


/**
 * An activity representing a single Feed detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link FeedCategoryListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link FeedListFragment}.
 */
public class FeedListActivity extends FragmentActivity {

    private static final String TAG = FeedListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            String title = getIntent().getStringExtra("title");
            setTitle(title);
            arguments.putString("title", title);
            arguments.putString("uri", getIntent().getStringExtra("uri"));
            arguments.putInt("resource", getIntent().getIntExtra("resource", 0));
            FeedListFragment fragment = new FeedListFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.feed_detail_container, fragment)
                    .commit();
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
