package com.wmendez.newsreader.lib.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.db.DBHelper;
import com.wmendez.newsreader.lib.helpers.Entry;

import java.util.ArrayList;

public class FeedListAdapter extends CursorAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private ArrayList<Entry> news = new ArrayList<Entry>();

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
    public void bindView(View view, Context context, Cursor cursor) {
        final Entry entry = getEntry(cursor);
        ImageView imageView = (ImageView) view.findViewById(R.id.news_image);
        ImageView favorite = (ImageView) view.findViewById(R.id.favorite_indicator);
        if (entry.isFavorite)
            favorite.setImageResource(R.drawable.ic_action_heart_red);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite(entry, v);
            }
        });

        if (!entry.image.equals("")) {
            Picasso.with(mContext).load(entry.image).placeholder(R.drawable.ic_launcher).into(imageView);
        } else {
            Picasso.with(mContext).load(R.drawable.ic_launcher)
                    .resize(125, 125).centerInside().into(imageView);
        }
        ((TextView) view.findViewById(R.id.news_title)).setText(entry.title);
        ((TextView) view.findViewById(R.id.pub_date)).setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));

    }

    private Entry getEntry(Cursor cursor) {
        Long pubDate = cursor.getLong(cursor.getColumnIndex(DBHelper.NEWS_PUB_DATE));
        return new Entry(
                cursor.getString(cursor.getColumnIndex(DBHelper.NEWS_TITLE)),
                cursor.getString(cursor.getColumnIndex(DBHelper.NEWS_URL)),
                cursor.getString(cursor.getColumnIndex(DBHelper.NEWS_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(DBHelper.NEWS_CATEGORY)),
                pubDate,
                cursor.getString(cursor.getColumnIndex(DBHelper.NEWS_IMAGE)),
                cursor.getInt(cursor.getColumnIndex(DBHelper.IS_NEW)) == 1,
                cursor.getInt(cursor.getColumnIndex(DBHelper.NEWS_IS_FAVORITE)) == 1
        );
    }

    private void setFavorite(Entry entry, View v) {
        SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
        entry.isFavorite = !entry.isFavorite;
        ContentValues values = new ContentValues();
        values.put(DBHelper.NEWS_IS_FAVORITE, entry.isFavorite);
        db.update(DBHelper.NEWS_TABLE, values, DBHelper.NEWS_URL + " = ? ", new String[]{entry.link});
        if (entry.isFavorite) {
            ((ImageView) v).setImageResource(R.drawable.ic_action_heart_red);
        } else {
            ((ImageView) v).setImageResource(R.drawable.ic_action_heart);
        }
    }

    public void setList(ArrayList<Entry> news) {
        this.news = news;
        notifyDataSetInvalidated();
    }

    public Entry getEntry(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return getEntry(cursor);

    }
}
