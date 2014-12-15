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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.helpers.Entry;
import com.wmendez.newsreader.lib.provider.Contract;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsItemView extends ViewGroup {

    @InjectView(R.id.news_image)
    ImageView image;
    @InjectView(R.id.favorite_indicator)
    ImageView favorite;
    @InjectView(R.id.news_title)
    TextView title;
    @InjectView(R.id.summary)
    TextView summary;
    @InjectView(R.id.pub_date)
    TextView pubDate;
    @InjectView(R.id.news_info)
    View newsInfo;

    Context context;


    public NewsItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
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
        measureChildWithMargins(newsInfo, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);

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
        }

//        int favoritePosition = getWidthWithMargins(image) + paddingLeft + getPaddingRight() - getWidthWithMargins(favorite);
//        layoutView(favorite, favoritePosition, 80, newsInfo.getMeasuredWidth(), title.getMeasuredHeight() + pubDate.getMeasuredHeight());
        if (summary.getVisibility() == VISIBLE)
            layoutView(newsInfo, paddingLeft, currentTop, newsInfo.getMeasuredWidth(), title.getMeasuredHeight() + pubDate.getMeasuredHeight() + summary.getMeasuredHeight());
        else
            layoutView(newsInfo, paddingLeft, currentTop, newsInfo.getMeasuredWidth(), title.getMeasuredHeight() + pubDate.getMeasuredHeight());

        layoutView(title, paddingLeft, currentTop, title.getMeasuredWidth(), title.getMeasuredHeight());
        currentTop += getHeightWithMargins(title);

        layoutView(pubDate, paddingLeft, currentTop, pubDate.getMeasuredWidth(), pubDate.getMeasuredHeight());
        currentTop += getHeightWithMargins(pubDate);
        if (summary.getVisibility() == VISIBLE)
            layoutView(summary, paddingLeft, currentTop, summary.getMeasuredWidth(), summary.getMeasuredHeight());


    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }


    public void reDraw() {
        requestLayout();
        invalidate();
    }

    public void setContent(final Entry entry) {
        title.setText(entry.title);
        pubDate.setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));

        String description = entry.description.replaceAll("<(.*?)\\>", " ");//Removes all items in brackets
        description = description.replaceAll("<(.*?)\\\n", " ");//Must be undeneath
        description = description.replaceFirst("(.*?)\\>", " ");//Removes any connected item to the last bracket
        description = description.replaceAll("&nbsp;", " ");
        description = description.replaceAll("&amp;", " ");
        summary.setText(description);


        if (entry.isFavorite) {
            favorite.setImageResource(R.drawable.ic_favorite_grey);
            favorite.setColorFilter(context.getResources().getColor(R.color.favorite_icon_active_tint));
        } else {
            favorite.setImageResource(R.drawable.ic_favorite_outline_grey);
            favorite.setColorFilter(Color.WHITE);
        }

        ViewCompat.setElevation(favorite, 5.0f);
        favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite(entry, v);
            }
        });

        if (!entry.image.equals("")) {
            Picasso.with(context).load(entry.image).placeholder(R.drawable.ic_launcher).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    Palette palette = Palette.generate(((BitmapDrawable) image.getDrawable()).getBitmap());
                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                    try {
                        image.setColorFilter(mutedSwatch.getRgb(), PorterDuff.Mode.MULTIPLY);
                        newsInfo.setBackgroundColor(mutedSwatch.getRgb());
                        title.setTextColor(mutedSwatch.getTitleTextColor());
                        pubDate.setTextColor(mutedSwatch.getBodyTextColor());
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                }

                @Override
                public void onError() {
                    image.setVisibility(GONE);
                    summary.setVisibility(VISIBLE);
                    reDraw();
                }
            });

        } else {
            image.setVisibility(GONE);
            summary.setVisibility(VISIBLE);
            reDraw();
        }

    }


    private void setFavorite(Entry entry, View v) {
        entry.isFavorite = !entry.isFavorite;
        ContentValues values = new ContentValues();
        values.put(Contract.NewsTable.COLUMN_NAME_FAVORITE, entry.isFavorite);
        context.getContentResolver().update(Contract.NewsTable.CONTENT_URI,
                values,
                Contract.NewsTable.COLUMN_NAME_URL + " = ? ",
                new String[]{entry.link}
        );

        if (entry.isFavorite) {
            ((ImageView) v).setImageResource(R.drawable.ic_favorite_grey);
            ((ImageView) v).setColorFilter(context.getResources().getColor(R.color.favorite_icon_active_tint));
        } else {
            ((ImageView) v).setImageResource(R.drawable.ic_favorite_outline_grey);
            ((ImageView) v).setColorFilter(Color.WHITE);
        }
    }
}
