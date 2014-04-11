package com.wmendez.newsreader.lib.adapters;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmendez.newsreader.lib.ui.FeedCategoryListFragment;
import com.wmendez.newsreader.lib.R;
import com.wmendez.newsreader.lib.helpers.Feeds;

import java.util.List;


public class FeedCategoryListAdapter extends BaseAdapter {

    private final List<Feeds.FeedItem> items;
    private final FragmentActivity mContext;
    private LayoutInflater mInflater;

    public FeedCategoryListAdapter(FeedCategoryListFragment feedCategoryListFragment, List<Feeds.FeedItem> items) {
        this.mContext = feedCategoryListFragment.getActivity();
        this.items = items;
        mInflater = LayoutInflater.from(mContext);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Feeds.FeedItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.feed_category_list_item, null);
        ((TextView)view.findViewById(R.id.category)).setText(getItem(position).title);
        ((ImageView)view.findViewById(R.id.feed_category_icon)).setImageResource(getItem(position).resource);
        return view;
    }
}
