package com.wmendez.newsreader.lib.ui;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.Target;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.adapters.FeedListAdapter;
import com.wmendez.newsreader.lib.provider.NewsDatabase;
import com.wmendez.newsreader.lib.event.FavoriteChangedEvent;
import com.wmendez.newsreader.lib.event.NewsItemSelectedEvent;
import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.util.Utils;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A fragment representing a single Feed detail screen.
 * This fragment is either contained in a {@link FeedCategoryListActivity}
 * in two-pane mode (on tablets) or a {@link FeedListActivity}
 * on handsets.
 */
public class FeedListFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnShowcaseEventListener {
    private static final String TAG = FeedListFragment.class.getSimpleName();
    private Feeds.FeedItem mItem;
    private GridView gridView;
    private FeedListAdapter adapter;


    /**
     * The current activated item position. Only used on tablets.
     */
    private SwipeRefreshLayout swipeLayout;
    private SQLiteDatabase db;
    private Cursor cursor;

    final String[] formats = new String[]{
            "EEE, dd MMM yyyy HH:mm:ss",
            "EEE, dd MMM yyyy HH:mm:ss zzz"
    };

    @Override
    public void onRefresh() {

        final FragmentActivity activity = getActivity();
        Ion.with(activity, mItem.uri)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        swipeLayout.setRefreshing(false);
                        Document doc;
                        try {
                            doc = Jsoup.parse(result);
                        } catch (IllegalArgumentException ex) {
                            if (activity != null)
                                Toast.makeText(activity, activity.getString(R.string.fetch_news_error), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Elements entries = doc.getElementsByTag("item");
                        for (org.jsoup.nodes.Element element : entries) {
                            String url = element.getElementsByTag("guid").text();
                            String category = element.getElementsByTag("category").text().replace("<![CDATA[", "").replace("]]>", "");
                            if(category.equals(""))
                                category = mItem.title;
                            if (newsExist(url, category))
                                continue;
                            insertNews(element, url, category);
                        }

                        cursor.close();
                        cursor = getQuery();
                        adapter.changeCursor(cursor);
                    }
                });
    }

    private void insertNews(Element element, String url, String category) {
        ContentValues values = new ContentValues();
        String title = element.getElementsByTag("title").text().replace("<![CDATA[", "").replace("]]>", "");
        String description = element.getElementsByTag("description").text().replace("<![CDATA[", "").replace("]]>", "");
        String pub_date = element.getElementsByTag("pubdate").text().replace("<![CDATA[", "").replace("]]>", "");
        Long pubDate = 0L;
        try {
            pubDate = DateUtils.parseDate(pub_date, formats).getTime();
        } catch (DateParseException ex) {
            ex.printStackTrace();
        }
        String image = element.getElementsByTag("enclosure").attr("url").replace("<![CDATA[", "").replace("]]>", "");
        values.clear();
        values.put(NewsDatabase.NEWS_CATEGORY, category);
        values.put(NewsDatabase.NEWS_TITLE, title);
        values.put(NewsDatabase.NEWS_URL, url);
        values.put(NewsDatabase.NEWS_DESCRIPTION, description);
        values.put(NewsDatabase.NEWS_PUB_DATE, pubDate);
        values.put(NewsDatabase.NEWS_IMAGE, image);
        values.put(NewsDatabase.IS_NEW, true);
        values.put(NewsDatabase.NEWS_IS_FAVORITE, false);

        db.insert(NewsDatabase.NEWS_TABLE, null, values);
    }

    private boolean newsExist(String link, String category) {
        Cursor c = db.rawQuery(
                "select " + NewsDatabase.NEWS_URL + ", " + NewsDatabase.NEWS_CATEGORY + " from " + NewsDatabase.NEWS_TABLE
                        + " where " + NewsDatabase.NEWS_URL + " = ?",
                new String[]{link}
        );
        boolean exists = c.getCount() > 0;
        if (exists) {
            c.moveToFirst();
            String categories = c.getString(c.getColumnIndex(NewsDatabase.NEWS_CATEGORY));
            String[] cats = categories.split("|");
            List<String> strings = Arrays.asList(cats);
            if (!strings.contains(category)) {
                String[] catList = new String[strings.size() + 1];
                for (int i = 0; i < strings.size(); i++) {
                    catList[i] = strings.get(i);
                }
                catList[strings.size()] = category;
                String newCategories = Utils.join(catList, "|");
                ContentValues values = new ContentValues();
                values.put(NewsDatabase.NEWS_CATEGORY, newCategories);
                db.update(NewsDatabase.NEWS_TABLE, values, NewsDatabase.NEWS_URL + " = ?", new String[]{link});
            }
        }
        c.close();
        return exists;
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

        Bundle arguments = getArguments();
        if (arguments.containsKey("uri")) {
            // Load the dummy resource specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load resource from a resource provider.
            String uri = arguments.getString("uri");
            mItem = Feeds.ITEM_MAP.get(uri);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_feed_list, container, false);
        SharedPreferences mPrefs = activity.getPreferences(activity.MODE_PRIVATE);
        if (!mPrefs.getBoolean("showcase_triggered", false)) {
            setShowCaseView(activity);
            SharedPreferences.Editor edit = mPrefs.edit();
            edit.putBoolean("showcase_triggered", true);
            edit.commit();
        }


        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        db = NewsDatabase.getInstance(activity).getWritableDatabase();
        cursor = getQuery();
        if (cursor.getCount() == 0) {
            swipeLayout.setRefreshing(true);
            onRefresh();
        }
        adapter = new FeedListAdapter(activity, cursor, true);
        gridView = (GridView) rootView.findViewById(R.id.feed_list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        setAdmob(rootView);


        return rootView;
    }

    private void setShowCaseView(FragmentActivity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int width = displaymetrics.widthPixels;

        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
        co.hideOnClickOutside = true;
        co.shotType = ShowcaseView.TYPE_ONE_SHOT;
        ShowcaseView sv;
        Target target = new Target() {
            @Override
            public Point getPoint() {
                return new Point(width / 2, 300);
            }
        };
        sv = ShowcaseView.insertShowcaseView(
                target,
                activity,
                activity.getString(R.string.showcase_main_title),
                activity.getString(R.string.showcase_main_message),
                co
        );
        sv.setBackgroundColor(activity.getResources().getColor(R.color.black_overlay));
        sv.setOnShowcaseEventListener(this);
    }

    private Cursor getQuery() {
        String categoryName;
        if (Feeds.ITEMS.size() == 1)
            categoryName = "";
        else
            categoryName = mItem.title;

        return db.query(true, NewsDatabase.NEWS_TABLE, null, NewsDatabase.NEWS_CATEGORY + " like ?",
                new String[]{"%" + categoryName + "%"}, null, null, NewsDatabase.NEWS_PUB_DATE + " DESC", null);
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventBus.getDefault().post(new NewsItemSelectedEvent(adapter.getEntry(position)));
    }


    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
