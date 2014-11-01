package com.wmendez.newsreader.lib.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.provider.NewsDatabase;
import com.wmendez.newsreader.lib.helpers.Entry;


public class FeedListAdapter extends CursorAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;

    public FeedListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.news_list_item, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final Entry entry = getEntry(cursor);
        final ImageView mImageView = (ImageView) view.findViewById(R.id.news_image);
        ImageView favorite = (ImageView) view.findViewById(R.id.favorite_indicator);
        if (entry.isFavorite) {
            favorite.setImageResource(R.drawable.ic_favorite_grey);
            favorite.setColorFilter(context.getResources().getColor(R.color.favorite_icon_active_tint));
        } else {
            favorite.setImageResource(R.drawable.ic_favorite_outline_grey);
            favorite.setColorFilter(Color.WHITE);
        }
        ViewCompat.setElevation(favorite, 5.0f);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite(entry, v);
            }
        });
        final int primaryColor = context.getResources().getColor(R.color.primary);
        final View newsInfo = view.findViewById(R.id.news_info);
        final TextView newsTitle = (TextView) view.findViewById(R.id.news_title);
        final TextView pubDate = (TextView) view.findViewById(R.id.pub_date);

        if (!entry.image.equals("")) {
            Picasso.with(mContext).load(entry.image).placeholder(R.drawable.ic_launcher).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    Palette palette = Palette.generate(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                    mImageView.setColorFilter(mutedSwatch.getRgb(), PorterDuff.Mode.MULTIPLY);
                    newsInfo.setBackgroundColor(mutedSwatch.getRgb());
                    newsTitle.setTextColor(mutedSwatch.getTitleTextColor());
                    pubDate.setTextColor(mutedSwatch.getBodyTextColor());

                }

                @Override
                public void onError() {
                    mImageView.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
                }
            });

        } else {
            Picasso.with(mContext).load(R.drawable.picture_not_available).into(mImageView);
            mImageView.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        }
        newsTitle.setText(entry.title);
        pubDate.setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));

    }

    private Entry getEntry(Cursor cursor) {
        Long pubDate = cursor.getLong(cursor.getColumnIndex(NewsDatabase.NEWS_PUB_DATE));
        return new Entry(
                cursor.getString(cursor.getColumnIndex(NewsDatabase.NEWS_TITLE)),
                cursor.getString(cursor.getColumnIndex(NewsDatabase.NEWS_URL)),
                cursor.getString(cursor.getColumnIndex(NewsDatabase.NEWS_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(NewsDatabase.NEWS_CATEGORY)),
                pubDate,
                cursor.getString(cursor.getColumnIndex(NewsDatabase.NEWS_IMAGE)),
                cursor.getInt(cursor.getColumnIndex(NewsDatabase.IS_NEW)) == 1,
                cursor.getInt(cursor.getColumnIndex(NewsDatabase.NEWS_IS_FAVORITE)) == 1
        );
    }

    private void setFavorite(Entry entry, View v) {
        SQLiteDatabase db = NewsDatabase.getInstance(mContext).getWritableDatabase();
        entry.isFavorite = !entry.isFavorite;
        ContentValues values = new ContentValues();
        values.put(NewsDatabase.NEWS_IS_FAVORITE, entry.isFavorite);
        db.update(NewsDatabase.NEWS_TABLE, values, NewsDatabase.NEWS_URL + " = ? ", new String[]{entry.link});
        if (entry.isFavorite) {
            ((ImageView) v).setImageResource(R.drawable.ic_favorite_grey);
            ((ImageView) v).setColorFilter(mContext.getResources().getColor(R.color.favorite_icon_active_tint));
        } else {
            ((ImageView) v).setImageResource(R.drawable.ic_favorite_outline_grey);
            ((ImageView) v).setColorFilter(Color.WHITE);
        }
    }

    public Entry getEntry(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return getEntry(cursor);

    }
}
