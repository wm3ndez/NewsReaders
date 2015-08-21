package com.wmendez.newsreader.lib.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsActivity extends AppCompatActivity { //implements ObservableScrollView.Callbacks {

    @Bind(R.id.news_content)
    TextView mNewsContent;
    @Bind(R.id.backdrop)
    ImageView mImageView;
    @Bind(R.id.news_title)
    TextView newsTitle;
    @Bind(R.id.pub_date)
    TextView pubDate;
    @Bind(R.id.header_session)
    View mHeader;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    private Interpolator mInterpolator;
    private Entry entry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

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
        collapsingToolbar.setTitle(entry.title);
        newsTitle.setText(entry.title);
        pubDate.setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));

        Spanned text = Html.fromHtml(entry.description);
        mNewsContent.setText(text);
        setNewsImage();
    }


    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                                } catch (NullPointerException ex) {
                                    setDefaultStyle();
                                    return;
                                }
                                newsTitle.setTextColor(mutedSwatch.getTitleTextColor());
                                pubDate.setTextColor(mutedSwatch.getTitleTextColor());
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
        mHeader.setBackgroundColor(getResources().getColor(R.color.primary));
        setStatusBarColor(getResources().getColor(R.color.primary));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= 21)
                finishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.floating_button)
    public void shareButtonClicked(View v) {
        startShareActivity();
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


}
