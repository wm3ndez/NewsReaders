package com.wmendez.newsreader.lib.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.squareup.picasso.Picasso;
import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.db.DBHelper;
import com.wmendez.newsreader.lib.helpers.Entry;
import com.wmendez.newsreader.lib.helpers.Feeds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NewsActivity extends Activity {
    private WebView webView;
    private ImageView imageView;
    private View fakeHeader_V;
    private ListView listview;
    private AdView adView;
    private Entry entry;
    private ShareActionProvider mShareActionProvider;
    private MenuItem favoriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        entry = getIntent().getParcelableExtra("news");
        setTitle(entry.title);

        listview = (ListView) findViewById(R.id.fullscreen_content);
        webView = (WebView) LayoutInflater.from(this).inflate(R.layout.news_webview, null);

        imageView = (ImageView) findViewById(R.id.news_image);
        fakeHeader_V = new View(this);
        fakeHeader_V.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        listview.addHeaderView(fakeHeader_V, null, false);
        listview.setAdapter(new MyAdapter());


        setNewsImage(entry);
        setFadingAnimation();
        fetchNews(entry);
        setAdmob();
    }

    private void setAdmob() {
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_id));
        adView.setAdSize(AdSize.BANNER);
        LinearLayout layout = (LinearLayout) findViewById(R.id.admob_view);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private Future<Response<String>> fetchNews(Entry entry) {
        return Ion.with(this, entry.link)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e != null) {
                            webView.loadData("<h3>Contenido no disponible</h3>", "text/html", "UTF-8");
                        } else {
                            Document doc = Jsoup.parse(result.getResult());
                            webView.loadData(Feeds.parser.getHtml(doc), "text/html", "UTF-8");
                        }

                    }
                });
    }

    private void setNewsImage(Entry entry) {
        if (!entry.image.equals(""))
            Picasso.with(this).load(entry.image).error(R.drawable.ic_launcher).into(imageView);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public WebView getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (position) {
                case 0:
                    View view = LayoutInflater.from(NewsActivity.this).inflate(R.layout.news_info, null);
                    ((TextView) view.findViewById(R.id.news_title)).setText(entry.title);
                    ((TextView) view.findViewById(R.id.pub_date)).setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));
                    return view;
                default:
                    return webView;

            }
        }
    }

    private void setFadingAnimation() {
        //noinspection ConstantConditions
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    //noinspection ConstantConditions
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    //noinspection ConstantConditions,deprecation
                    imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                fakeHeader_V.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, imageView.getHeight()));
            }
        });


        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && firstVisibleItem == 0) {
                    final float max = fakeHeader_V.getHeight() / 4;
                    final float delta = -fakeHeader_V.getTop();
                    if (delta <= max) {
                        final float progress = delta / max;
                        imageView.setAlpha(1.0f - (0.6f * progress));
                        imageView.setScaleX(1.0f - (0.1f * progress));
                        imageView.setScaleY(1.0f - (0.1f * progress));
                        imageView.setTranslationY(-delta / 3);
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menu_item_favorite) {
            setFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFavorite() {
        SQLiteDatabase db = DBHelper.getInstance(this).getWritableDatabase();
        entry.isFavorite = !entry.isFavorite;
        ContentValues values = new ContentValues();
        values.put(DBHelper.NEWS_IS_FAVORITE, entry.isFavorite);
        int update = db.update(DBHelper.NEWS_TABLE, values, DBHelper.NEWS_URL + " = ? ", new String[]{entry.link});
        if (entry.isFavorite) {
            favoriteItem.setIcon(R.drawable.ic_action_heart_red);
        } else {
            favoriteItem.setIcon(R.drawable.ic_action_heart);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.news_activity, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        favoriteItem = menu.findItem(R.id.menu_item_favorite);
        if (entry.isFavorite)
            favoriteItem.setIcon(R.drawable.ic_action_heart_red);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        setShareIntent(createShareIntent());

        // Return true to display menu
        return true;
    }

    public Intent createShareIntent() {
        String textToShare = entry.link;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, entry.title);
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, textToShare);
        return shareIntent;
    }


    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }


}
