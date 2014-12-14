package com.wmendez.newsreader.lib.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.wmendez.newsreader.lib.Constants;


public class Contract {
    private Contract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = Constants.CONTENT_AUTHORITY;

    /**
     * Base URI.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "news"-type resources..
     */
    private static final String PATH_NEWS = "news";

    /**
     * Columns supported by "news" records.
     */
    public static class NewsTable implements BaseColumns {
        /**
         * MIME type for lists of news.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.syncadapter.news";
        /**
         * MIME type for individual articule.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.syncadapter.articule";

        /**
         * Fully qualified URI for "news" resources.
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        /**
         * Table name where records are stored for "news" resources.
         */
        public static final String TABLE_NAME = "news";

        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PUB_DATE = "pub_date";
        public static final String COLUMN_NAME_FAVORITE = "favorite";
        public static final String COLUMN_NAME_IS_NEW = "is_new";

        public static final String DEFAULT_SORTING = COLUMN_NAME_PUB_DATE + " DESC";
    }

}