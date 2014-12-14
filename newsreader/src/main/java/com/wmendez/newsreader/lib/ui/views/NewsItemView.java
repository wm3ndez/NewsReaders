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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmendez.newsreader.lib.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsItemView extends ViewGroup {

    @InjectView(R.id.news_image)
    ImageView image;
    @InjectView(R.id.favorite_indicator)
    ImageView favorite;
    @InjectView(R.id.news_title)
    TextView title;
    @InjectView(R.id.pub_date)
    TextView pubDate;
    @InjectView(R.id.news_info)
    View newsInfo;


    public NewsItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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


        measureChildWithMargins(image, widthMeasureSpec, 0, heightMeasureSpec, 0);
        heightUsed += getMeasuredHeightWithMargins(image);

        measureChildWithMargins(favorite, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChildWithMargins(newsInfo, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);

        measureChildWithMargins(title, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
        heightUsed += getMeasuredHeightWithMargins(title);

        measureChildWithMargins(pubDate, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
        heightUsed += getMeasuredHeightWithMargins(pubDate);

        setMeasuredDimension(widthSize, heightUsed);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();

        int currentTop = getPaddingTop();

        layoutView(image, paddingLeft, currentTop, image.getMeasuredWidth(), image.getMeasuredHeight());
        currentTop += getHeightWithMargins(image);

//        int favoritePosition = getWidthWithMargins(image) + paddingLeft + getPaddingRight() - getWidthWithMargins(favorite);
//        layoutView(favorite, favoritePosition, 80, newsInfo.getMeasuredWidth(), title.getMeasuredHeight() + pubDate.getMeasuredHeight());
        layoutView(newsInfo, paddingLeft, currentTop, newsInfo.getMeasuredWidth(), title.getMeasuredHeight() + pubDate.getMeasuredHeight());

        layoutView(title, paddingLeft, currentTop, title.getMeasuredWidth(), title.getMeasuredHeight());
        currentTop += getHeightWithMargins(title);

        layoutView(pubDate, paddingLeft, currentTop, pubDate.getMeasuredWidth(), pubDate.getMeasuredHeight());

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

}
