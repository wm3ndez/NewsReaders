package com.wmendez.newsreader.lib.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wmendez.diariolibre.R;
import com.wmendez.newsreader.lib.helpers.Entry;
import com.wmendez.newsreader.lib.ui.views.NewsItemView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FeedListAdapter extends CursorRecyclerViewAdapter<FeedListAdapter.ViewHolder> {

    public FeedListAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        viewHolder.newsItemView.setContent(Entry.fromCursor(cursor));
    }

    public Entry getEntry(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return Entry.fromCursor(cursor);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.news_item_view)
        NewsItemView newsItemView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}