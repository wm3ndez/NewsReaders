package com.wmendez.newsreader.lib.ui;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.wmendez.diariolibre.Constants;
import com.wmendez.diariolibre.R;
import com.wmendez.newsreader.lib.adapters.FeedListAdapter;
import com.wmendez.newsreader.lib.adapters.RecyclerItemClickListener;
import com.wmendez.newsreader.lib.event.FavoriteChangedEvent;
import com.wmendez.newsreader.lib.event.NewsItemSelectedEvent;
import com.wmendez.newsreader.lib.event.SyncEndedEvent;
import com.wmendez.newsreader.lib.helpers.Entry;
import com.wmendez.newsreader.lib.provider.Contract;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class FeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;
    @Bind(R.id.feed_list)
    RecyclerView gridView;

    private FeedListAdapter adapter;
    Handler mHandler = new Handler();
    private Cursor cursor;
    private ContentResolver contentResolver;
    private String category = "";
    private FragmentActivity activity;
    private String newspaper = "";

    @Override
    public void onRefresh() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE),
                Constants.CONTENT_AUTHORITY, settingsBundle);

    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (arguments.containsKey("category")) {
            category = arguments.getString("category");
            newspaper = arguments.getString("newspaper");
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        contentResolver = activity.getContentResolver();
        View rootView = inflater.inflate(R.layout.fragment_feed_list, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        setUpGridView();
        setAdmob(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpGridView() {
        gridView.addOnItemTouchListener(new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (Build.VERSION.SDK_INT >= 21)
                    startNewsActivityWithTransition(view.findViewById(R.id.news_title), adapter.getEntry(position));
                else
                    EventBus.getDefault().post(new NewsItemSelectedEvent(adapter.getEntry(position)));

            }
        }));

        cursor = getQuery();
        if (cursor.getCount() == 0) refreshFeed();

        adapter = new FeedListAdapter(cursor);
        final int spanCount = activity.getResources().getInteger(R.integer.columns_count);
        gridView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
        gridView.setAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startNewsActivityWithTransition(View toolbar, Entry entry) {

        // Avoid system UI glitches as described here:
        // https://plus.google.com/+AlexLockwood/posts/RPtwZ5nNebb
        View decor = activity.getWindow().getDecorView();
        View statusBar = decor.findViewById(android.R.id.statusBarBackground);
        View navBar = decor.findViewById(android.R.id.navigationBarBackground);

        // Create pair of transition participants.
        List<Pair> participants = new ArrayList<>(3);
        participants.add(new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        addNonNullViewToTransitionParticipants(statusBar, participants);
        addNonNullViewToTransitionParticipants(navBar, participants);
        @SuppressWarnings("unchecked")
        ActivityOptions sceneTransitionAnimation = ActivityOptions
                .makeSceneTransitionAnimation(activity, participants.toArray(new Pair[participants.size()]));

        // Starts the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        activity.startActivity(NewsActivity.getStartIntent(activity, entry), transitionBundle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addNonNullViewToTransitionParticipants(View view, List<Pair> participants) {
        if (view == null) {
            return;
        }
        participants.add(new Pair<>(view, view.getTransitionName()));
    }


    private void refreshFeed() {
        swipeLayout.setRefreshing(true);
        onRefresh();
    }

    private Cursor getQuery() {
        return contentResolver.query(
                Contract.NewsTable.CONTENT_URI,
                null,
                Contract.NewsTable.COLUMN_NAME_CATEGORY + " LIKE ? AND " + Contract.NewsTable.COLUMN_NAME_NEWSPAPER + " = ? ",
                new String[]{"%" + category + "%", newspaper},
                Contract.NewsTable.DEFAULT_SORTING
        );
    }

    private void setAdmob(View view) {
        AdView adView = new AdView(getActivity());
        adView.setAdUnitId(getActivity().getString(R.string.admob_id));
        adView.setAdSize(AdSize.BANNER);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.admob_view);
        layout.addView(adView);
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(getString(R.string.test_device));
        AdRequest adRequest = builder.build();
        adView.loadAd(adRequest);

    }

    public void onEvent(FavoriteChangedEvent event) {
        cursor.close();
        cursor = getQuery();
        adapter.changeCursor(cursor);
    }

    public void onEvent(SyncEndedEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                cursor.close();
                cursor = getQuery();
                adapter.changeCursor(cursor);

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate menu resource file.
        inflater.inflate(R.menu.feed_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh_feed) {
            refreshFeed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
