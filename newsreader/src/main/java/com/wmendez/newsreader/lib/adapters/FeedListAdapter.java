package com.wmendez.newsreader.lib.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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

//public class FeedListAdapter extends CursorAdapter {
//    private final Context context;
//    private final LayoutInflater mInflater;
//
//    public FeedListAdapter(Context context, Cursor c, boolean autoRequery) {
//        super(context, c, autoRequery);
//        context = context;
//        mInflater = LayoutInflater.from(context);
//    }
//
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        return mInflater.inflate(R.layout.news_list_item, null);
//    }
//
//    @Override
//    public void bindView(View view, final Context context, Cursor cursor) {
//        final Entry entry = Entry.fromCursor(cursor);
//        final ImageView mImageView = (ImageView) view.findViewById(R.id.news_image);
//        ImageView favorite = (ImageView) view.findViewById(R.id.favorite_indicator);
//        if (entry.isFavorite) {
//            favorite.setImageResource(R.drawable.ic_favorite_grey);
//            favorite.setColorFilter(context.getResources().getColor(R.color.favorite_icon_active_tint));
//        } else {
//            favorite.setImageResource(R.drawable.ic_favorite_outline_grey);
//            favorite.setColorFilter(Color.WHITE);
//        }
//        ViewCompat.setElevation(favorite, 5.0f);
//        favorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setFavorite(entry, v);
//            }
//        });
//        final int primaryColor = context.getResources().getColor(R.color.primary);
//        final View newsInfo = view.findViewById(R.id.news_info);
//        final TextView newsTitle = (TextView) view.findViewById(R.id.news_title);
//        final TextView pubDate = (TextView) view.findViewById(R.id.pub_date);
//
//        if (!entry.image.equals("")) {
//            Picasso.with(context).load(entry.image).placeholder(R.drawable.ic_launcher).into(mImageView, new Callback() {
//                @Override
//                public void onSuccess() {
//                    Palette palette = Palette.generate(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
//                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
//                    try {
//                        mImageView.setColorFilter(mutedSwatch.getRgb(), PorterDuff.Mode.MULTIPLY);
//                        newsInfo.setBackgroundColor(mutedSwatch.getRgb());
//                        newsTitle.setTextColor(mutedSwatch.getTitleTextColor());
//                        pubDate.setTextColor(mutedSwatch.getBodyTextColor());
//                    } catch (NullPointerException ex) {
//                        ex.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void onError() {
//                    mImageView.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
//                }
//            });
//
//        } else {
//            Picasso.with(context).load(R.drawable.picture_not_available).into(mImageView);
//            mImageView.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
//        }
//        newsTitle.setText(entry.title);
//        pubDate.setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));
//
//    }
//
//
//    private void setFavorite(Entry entry, View v) {
//        entry.isFavorite = !entry.isFavorite;
//        ContentValues values = new ContentValues();
//        values.put(Contract.NewsTable.COLUMN_NAME_FAVORITE, entry.isFavorite);
//        context.getContentResolver().update(Contract.NewsTable.CONTENT_URI,
//                values,
//                Contract.NewsTable.COLUMN_NAME_URL + " = ? ",
//                new String[]{entry.link}
//        );
//
//        if (entry.isFavorite) {
//            ((ImageView) v).setImageResource(R.drawable.ic_favorite_grey);
//            ((ImageView) v).setColorFilter(context.getResources().getColor(R.color.favorite_icon_active_tint));
//        } else {
//            ((ImageView) v).setImageResource(R.drawable.ic_favorite_outline_grey);
//            ((ImageView) v).setColorFilter(Color.WHITE);
//        }
//    }
//
//}

public class FeedListAdapter extends CursorRecyclerViewAdapter<FeedListAdapter.ViewHolder> {

    private final Context context;

    public FeedListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        final Entry entry = Entry.fromCursor(cursor);

        if (entry.isFavorite) {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_grey);
            viewHolder.favorite.setColorFilter(context.getResources().getColor(R.color.favorite_icon_active_tint));
        } else {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_outline_grey);
            viewHolder.favorite.setColorFilter(Color.WHITE);
        }
        ViewCompat.setElevation(viewHolder.favorite, 5.0f);
        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite(entry, v);
            }
        });
        final int primaryColor = context.getResources().getColor(R.color.primary);

        if (!entry.image.equals("")) {
            Picasso.with(context).load(entry.image).placeholder(R.drawable.ic_launcher).into(viewHolder.image, new Callback() {
                @Override
                public void onSuccess() {
                    Palette palette = Palette.generate(((BitmapDrawable) viewHolder.image.getDrawable()).getBitmap());
                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                    try {
                        viewHolder.image.setColorFilter(mutedSwatch.getRgb(), PorterDuff.Mode.MULTIPLY);
                        viewHolder.newsInfo.setBackgroundColor(mutedSwatch.getRgb());
                        viewHolder.title.setTextColor(mutedSwatch.getTitleTextColor());
                        viewHolder.pubDate.setTextColor(mutedSwatch.getBodyTextColor());
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                }

                @Override
                public void onError() {
                    viewHolder.image.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
                }
            });

        } else {
            Picasso.with(context).load(R.drawable.picture_not_available).into(viewHolder.image);
            viewHolder.image.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        }
        viewHolder.title.setText(entry.title);
        viewHolder.pubDate.setText(DateUtils.getRelativeTimeSpanString(entry.pubDate));

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

    public Entry getEntry(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return Entry.fromCursor(cursor);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
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


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }
}