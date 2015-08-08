package com.wmendez.newsreader.lib.ui.views;
// https://github.com/lucasr/android-layout-samples/blob/master/src/main/java/org/lucasr/layoutsamples/widget/TweetLayoutView.java
/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.wmendez.diariolibre.R;
import com.wmendez.newsreader.lib.helpers.Entry;
import com.wmendez.newsreader.lib.provider.Contract;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import it.subito.masaccio.MasaccioImageView;

public class NewsItemView extends ViewGroup {

    @InjectView(R.id.news_image)
    MasaccioImageView image;
    @InjectView(R.id.favorite_indicator)
    ImageView favorite;
    @InjectView(R.id.news_title)
    TextView title;
    @InjectView(R.id.summary)
    TextView summary;
    @InjectView(R.id.pub_date)
    TextView pubDate;

    Context mContext;
    private Entry mEntry;
    Handler mHandler;


    public NewsItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mHandler = new Handler();
        LayoutInflater.from(context).inflate(R.layout.news_item_view, this, true);
        ButterKnife.inject(this);

    }


    private void layoutView(View view, int left, int top, int width, int height) {
        MarginLayoutParams margins = (MarginLayoutParams) view.getLayoutParams();
        final int leftWithMargins = left + margins.leftMargin;
        final int topWithMargins = top + margins.topMargin;

        view.layout(leftWithMargins, topWithMargins,
                leftWithMargins + width, topWithMargins + height);
    }

    private int getWidthWithMargins(View child) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getWidth() + lp.leftMargin + lp.rightMargin;
    }

    private int getHeightWithMargins(View child) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }

    private int getMeasuredWidthWithMargins(View child) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
    }

    private int getMeasuredHeightWithMargins(View child) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightUsed = 0;

        if (image.getVisibility() == VISIBLE) {
            measureChildWithMargins(image, widthMeasureSpec, 0, heightMeasureSpec, 0);
            heightUsed += getMeasuredHeightWithMargins(image);
        }

        measureChildWithMargins(favorite, widthMeasureSpec, 0, heightMeasureSpec, 0);

        measureChildWithMargins(title, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
        heightUsed += getMeasuredHeightWithMargins(title);

        measureChildWithMargins(pubDate, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
        heightUsed += getMeasuredHeightWithMargins(pubDate);

        if (summary.getVisibility() == VISIBLE) {
            measureChildWithMargins(summary, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
            heightUsed += getMeasuredHeightWithMargins(summary);
        }

        setMeasuredDimension(widthSize, heightUsed);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();

        int currentTop = getPaddingTop();

        if (image.getVisibility() == VISIBLE) {
            layoutView(image, paddingLeft, currentTop, image.getMeasuredWidth(), image.getMeasuredHeight());
            currentTop += getHeightWithMargins(image);

            // int favoritePosition = getWidthWithMargins(image) + paddingLeft + getPaddingRight() - 80;
            // layoutView(favorite, favoritePosition, 16, newsInfo.getMeasuredWidth(), title.getMeasuredHeight() + pubDate.getMeasuredHeight());

            layoutView(title, paddingLeft, currentTop, title.getMeasuredWidth(), title.getMeasuredHeight());
            currentTop += getHeightWithMargins(title);

            layoutView(pubDate, paddingLeft, currentTop, pubDate.getMeasuredWidth(), pubDate.getMeasuredHeight());

        } else {
            layoutView(title, paddingLeft, currentTop, title.getMeasuredWidth(), title.getMeasuredHeight());
            currentTop += getHeightWithMargins(title);

            layoutView(pubDate, paddingLeft, currentTop, pubDate.getMeasuredWidth(), pubDate.getMeasuredHeight());
            currentTop += getHeightWithMargins(pubDate);

            layoutView(summary, paddingLeft, currentTop, summary.getMeasuredWidth(), summary.getMeasuredHeight());
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }


    public void setContent(final Entry entry) {
        mEntry = entry;

        if (mEntry.image.equals("")) {
            image.setVisibility(GONE);
            summary.setVisibility(VISIBLE);
        } else {
            image.setVisibility(VISIBLE);
            summary.setVisibility(GONE);
            fetchImage();
        }

        title.setText(mEntry.title);
        pubDate.setText(DateUtils.getRelativeTimeSpanString(mEntry.pubDate));

        summary.setText(Html.fromHtml(mEntry.description));


        if (mEntry.isFavorite) {
            favorite.setImageResource(R.drawable.ic_favorite_grey);
            favorite.setColorFilter(mContext.getResources().getColor(R.color.favorite_icon_active_tint));
        } else {
            favorite.setImageResource(R.drawable.ic_favorite_outline_grey);
            favorite.setColorFilter(Color.WHITE);
        }

        ViewCompat.setElevation(favorite, 5.0f);
    }

    private void fetchImage() {
        Glide.with(getContext())
                .load(mEntry.image)
                .asBitmap()
                .into(new BitmapImageViewTarget(image) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        Palette.Builder builder = new Palette.Builder(bitmap);
                        builder.generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                try {
                                    setBackgroundColor(mutedSwatch.getRgb());
                                    int titleTextColor = mutedSwatch.getTitleTextColor();
                                    title.setTextColor(titleTextColor);
                                    pubDate.setTextColor(titleTextColor);
                                    summary.setTextColor(titleTextColor);
                                } catch (NullPointerException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        image.setVisibility(GONE);
                        summary.setVisibility(VISIBLE);
                    }

                });

    }

    @OnClick(R.id.favorite_indicator)
    public void setFavorite(View v) {
        if (mEntry == null) return;
        mEntry.isFavorite = !mEntry.isFavorite;
        ContentValues values = new ContentValues();
        values.put(Contract.NewsTable.COLUMN_NAME_FAVORITE, mEntry.isFavorite);
        mContext.getContentResolver()
                .update(Contract.NewsTable.CONTENT_URI,
                        values,
                        Contract.NewsTable.COLUMN_NAME_URL + " = ? ",
                        new String[]{mEntry.link}
                );

        if (mEntry.isFavorite) {
            favorite.setImageResource(R.drawable.ic_favorite_grey);
            favorite.setColorFilter(mContext.getResources().getColor(R.color.favorite_icon_active_tint));
        } else {
            favorite.setImageResource(R.drawable.ic_favorite_outline_grey);
            favorite.setColorFilter(Color.WHITE);
        }
    }
}
