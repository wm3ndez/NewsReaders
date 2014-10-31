package com.wmendez.newsreader.lib.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.provider.NewsDatabase;
import com.wmendez.newsreader.lib.event.FavoriteChangedEvent;
import com.wmendez.newsreader.lib.helpers.Entry;
import com.wmendez.newsreader.lib.helpers.Feeds;
import com.wmendez.newsreader.lib.ui.views.ObservableScrollView;
import com.wmendez.newsreader.lib.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.greenrobot.event.EventBus;

public class NewsActivity extends ActionBarActivity implements ObservableScrollView.Callbacks {
    private TextView mNewsContent;
    private ImageView mImageView;
    private AdView adView;
    private Entry entry;
    private MenuItem favoriteItem;
    private TextView newsTitle;
    private TextView pubDate;
    private ObservableScrollView mScrollView;
    private int mImageHeightPixels = 0;
    private View mImageViewContainer;

    private View mHeader;
    private Handler mHandler = new Handler();
    private View mScrollViewChild;
    private View mDetailsContainer;
    private int mHeaderHeightPixels;
    private boolean mHasImage = false;
    private float mMaxHeaderElevation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("");
            }
        });

        mScrollView = (ObservableScrollView) findViewById(R.id.scrollview);
        mScrollView.addCallbacks(this);


        mScrollViewChild = findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.INVISIBLE);

        mDetailsContainer = findViewById(R.id.details_container);
        mImageViewContainer = findViewById(R.id.image_container);
        entry = getIntent().getParcelableExtra("news");

        mHeader = findViewById(R.id.header_session);
        mNewsContent = (TextView) findViewById(R.id.news_content);

        mImageView = (ImageView) findViewById(R.id.news_image);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsTitle.setText(entry.title);
        pubDate = (TextView) findViewById(R.id.pub_date);
        pubDate.setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));


        mMaxHeaderElevation = getResources().getDimensionPixelSize(R.dimen.header_elevation);
        fetchNews(entry);
        setAdmob();
    }


    private void setAdmob() {
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_id));
        adView.setAdSize(AdSize.BANNER);
        LinearLayout layout = (LinearLayout) findViewById(R.id.admob_view);
        layout.addView(adView);
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(getString(R.string.test_device));
        AdRequest adRequest = builder.build();
        adView.loadAd(adRequest);

    }

    private Future<Response<String>> fetchNews(final Entry entry) {
        return Ion.with(this, entry.link)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        String data;
                        if (e != null) {
                            data = getString(R.string.no_content) + "<p> " + entry.description + "</p>";
                        } else {
                            Document doc = Jsoup.parse(result.getResult());
                            data = Feeds.parser.getHtml(doc);
                        }
                        setNewsImage(entry);
                        mNewsContent.setText(Html.fromHtml(data));
                    }
                });
    }

    private void setNewsImage(Entry entry) {
        mScrollViewChild.setVisibility(View.VISIBLE);
        if (entry.image.equals("")) {
            setDefaultStyle();
            return;
        }
        Picasso.with(this).load(entry.image).error(R.drawable.picture_not_available).into(mImageView);
        Picasso.with(this).load(entry.image).into(mImageView, new Callback() {
            @Override
            public void onSuccess() {
                Palette palette = Palette.generate(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                mHeader.setBackgroundColor(mutedSwatch.getRgb());
                setStatusBarColor(mutedSwatch.getRgb());
                mImageViewContainer.setBackgroundColor(mutedSwatch.getRgb());
                newsTitle.setTextColor(mutedSwatch.getTitleTextColor());
                pubDate.setTextColor(mutedSwatch.getTitleTextColor());
                mHasImage = true;
                recomputeImageAndScrollingMetrics();
            }

            @Override
            public void onError() {
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.picture_not_available));
                setDefaultStyle();
            }
        });

    }

    private void setStatusBarColor(int mutedColor) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mutedColor);
        }
    }

    private void setDefaultStyle() {
        mHasImage = false;
        mHeader.setBackgroundColor(getResources().getColor(R.color.primary));
        setStatusBarColor(getResources().getColor(R.color.primary));
        recomputeImageAndScrollingMetrics();
    }

    private void recomputeImageAndScrollingMetrics() {
        mHeaderHeightPixels = mHeader.getHeight();

        mImageHeightPixels = 0;
        if (mHasImage) {
            mImageHeightPixels = getResources().getDimensionPixelSize(R.dimen.news_image_size);
            mImageHeightPixels = Math.min(mImageHeightPixels, mScrollView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = mImageViewContainer.getLayoutParams();
        if (lp.height != mImageHeightPixels) {
            lp.height = mImageHeightPixels;
            mImageViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mImageHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mImageHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
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
        } else if (id == R.id.menu_item_share) {
            startShareActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startShareActivity() {
        String textToShare = entry.title + " - " + entry.link;
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(textToShare);
        Intent intent = builder.getIntent();
        intent.putExtra(Intent.EXTRA_SUBJECT, entry.title);
        intent.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(intent);

    }

    private void setFavorite() {
        SQLiteDatabase db = NewsDatabase.getInstance(this).getWritableDatabase();
        entry.isFavorite = !entry.isFavorite;
        ContentValues values = new ContentValues();
        values.put(NewsDatabase.NEWS_IS_FAVORITE, entry.isFavorite);
        db.update(NewsDatabase.NEWS_TABLE, values, NewsDatabase.NEWS_URL + " = ? ", new String[]{entry.link});
        setFavoriteIconColorFilter();


        EventBus.getDefault().post(new FavoriteChangedEvent());
    }

    private void setFavoriteIconColorFilter() {
        Drawable icon;
        if (entry.isFavorite) {
            icon = getResources().getDrawable(R.drawable.ic_favorite_grey);
            icon.setColorFilter(getResources().getColor(R.color.favorite_icon_active_tint), PorterDuff.Mode.SRC_ATOP);
        } else {
            icon = getResources().getDrawable(R.drawable.ic_favorite_outline_grey);
            icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        favoriteItem.setIcon(icon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.news_activity, menu);

        favoriteItem = menu.findItem(R.id.menu_item_favorite);
        setFavoriteIconColorFilter();
//        Return true to display menu
        return true;
    }


    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();
        float gapFillProgress = 1;

        if (mHasImage) {
            mImageHeightPixels = getResources().getDimensionPixelSize(R.dimen.news_image_size);
            gapFillProgress = Math.min(Math.max(Utils.getProgress(scrollY, 0, mImageHeightPixels), 0), 1);
        } else {
            mImageHeightPixels = 0;
        }

        float newTop = Math.max(mImageHeightPixels, scrollY);
        mHeader.setTranslationY(newTop);
        ViewCompat.setElevation(mHeader, gapFillProgress * mMaxHeaderElevation);

        // Move background photo (parallax effect)
        mImageViewContainer.setTranslationY(scrollY * 0.5f);
    }
}