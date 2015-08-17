package com.wmendez.newsreader.lib.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.wmendez.diariolibre.R;
import com.wmendez.newsreader.lib.helpers.Entry;
import com.wmendez.newsreader.lib.ui.views.ObservableScrollView;
import com.wmendez.newsreader.lib.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.subito.masaccio.MasaccioImageView;

public class NewsActivity extends AppCompatActivity implements ObservableScrollView.Callbacks {

    @InjectView(R.id.news_content)
    TextView mNewsContent;
    @InjectView(R.id.news_image)
    MasaccioImageView mImageView;
    @InjectView(R.id.news_title)
    TextView newsTitle;
    @InjectView(R.id.pub_date)
    TextView pubDate;
    @InjectView(R.id.scrollview)
    ObservableScrollView mScrollView;
    @InjectView(R.id.image_container)
    View mImageViewContainer;
    @InjectView(R.id.details_container)
    View mDetailsContainer;
    @InjectView(R.id.header_session)
    View mHeader;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private boolean mHasImage = false;
    private float mMaxHeaderElevation;
    private int mImageHeightPixels = 0;
    private Handler mHandler = new Handler();
    private Interpolator mInterpolator;
    private Entry entry;
    private MenuItem favoriteItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);

        if (Build.VERSION.SDK_INT >= 21)
            mInterpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        entry = getIntent().getParcelableExtra("news");
        populate();

        if (toolbar != null)
            setUpToolbar();
        setAdmob();

        Linkify.addLinks(mNewsContent, Linkify.ALL);
    }


    public static Intent getStartIntent(Context context, Entry entry) {
        Intent starter = new Intent(context, NewsActivity.class);
        starter.putExtra("news", entry);
        return starter;
    }

    public void populate() {
        mScrollView.addCallbacks(this);

        newsTitle.setText(entry.title);
        pubDate.setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));

        mMaxHeaderElevation = getResources().getDimensionPixelSize(R.dimen.header_elevation);
        mNewsContent.setText(Html.fromHtml(entry.description));
        setNewsImage();

    }


    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("");
            }
        });
    }

    private void setAdmob() {
        AdView adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_id));
        adView.setAdSize(AdSize.BANNER);
        LinearLayout layout = (LinearLayout) findViewById(R.id.admob_view);
        layout.addView(adView);
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(getString(R.string.test_device));
        AdRequest adRequest = builder.build();
        adView.loadAd(adRequest);

    }


    private void setNewsImage() {
        if (entry.image.equals("")) {
            setDefaultStyle();
            return;
        }

        mImageView.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(entry.image)
                .asBitmap()
                .into(new BitmapImageViewTarget(mImageView) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);

                        if (Build.VERSION.SDK_INT >= 21) {
                            mImageView.setScaleX(0);
                            mImageView.setScaleY(0);
                            mImageView.animate().scaleX(1).scaleY(1).setInterpolator(mInterpolator).setStartDelay(300);
                        }

                        Palette.Builder builder = new Palette.Builder(bitmap);
                        builder.generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                try {
                                    mHeader.setBackgroundColor(mutedSwatch.getRgb());
                                    setStatusBarColor(mutedSwatch.getRgb());
                                    mImageViewContainer.setBackgroundColor(mutedSwatch.getRgb());
                                } catch (NullPointerException ex) {
                                    setDefaultStyle();
                                    return;
                                }
                                newsTitle.setTextColor(mutedSwatch.getTitleTextColor());
                                pubDate.setTextColor(mutedSwatch.getTitleTextColor());
                                mHasImage = true;
                                recomputeImageAndScrollingMetrics();
                            }
                        });
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        setDefaultStyle();
                    }
                });

    }

    private void setStatusBarColor(int mutedColor) {
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(mutedColor);
    }

    private void setDefaultStyle() {
        mHasImage = false;
        mHeader.setBackgroundColor(getResources().getColor(R.color.primary));
        setStatusBarColor(getResources().getColor(R.color.primary));
        recomputeImageAndScrollingMetrics();
    }

    private void recomputeImageAndScrollingMetrics() {
        int mHeaderHeightPixels = mHeader.getHeight();

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
            if (Build.VERSION.SDK_INT >= 21)
                finishAfterTransition();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.news_activity, menu);
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
