package com.wmendez.newsreader.lib.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class Provider extends ContentProvider {
    NewsDatabase mDatabaseHelper;

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = Contract.CONTENT_AUTHORITY;

    // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
    // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
    // IDs.
    //
    // When a incoming URI is run through sUriMatcher, it will be tested against the defined
    // URI patterns, and the corresponding route ID will be returned.
    /**
     * URI ID for route: /news
     */
    public static final int ROUTE_NEWS = 1;

    /**
     * URI ID for route: /news/{ID}
     */
    public static final int ROUTE_NEWS_ID = 2;


    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "news", ROUTE_NEWS);
        sUriMatcher.addURI(AUTHORITY, "news/*", ROUTE_NEWS_ID);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new NewsDatabase(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_NEWS:
                return Contract.NewsTable.CONTENT_TYPE;
            case ROUTE_NEWS_ID:
                return Contract.NewsTable.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     * <p/>
     * <p>Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();


        /**
         * Choose the projection and adjust the "where" clause based on URI pattern-matching.
         */
        switch (sUriMatcher.match(uri)) {
            // If the incoming URI is for notes, chooses the Notes projection
            case ROUTE_NEWS:
                qb.setTables(Contract.NewsTable.TABLE_NAME);
                break;

            default:
                // If the URI doesn't match any of the known patterns, throw an exception.
                throw new IllegalArgumentException("Unknown URI " + uri);
        }


        // Opens the database object in "read" mode, since no writes need to be done.
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

       /*
        * Performs the query. If no problems occur trying to read the database, then a Cursor
        * object is returned; otherwise, the cursor variable contains null. If no records were
        * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
        */
        Cursor c = qb.query(
                db,            // The database to query
                projection,    // The columns to return from the query
                selection,     // The columns for the where clause
                selectionArgs, // The values for the where clause
                null,          // don't group the rows
                null,          // don't filter by row groups
                sortOrder        // The sort order
        );

        // Tells the Cursor what URI to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (values == null) {
            values = new ContentValues();
        }

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long rowId;
        switch (sUriMatcher.match(uri)) {
            case ROUTE_NEWS:
                rowId = db.insert(Contract.NewsTable.TABLE_NAME, null, values);

                // If the insert succeeded, the row ID exists.
                if (rowId > 0) {
                    // Creates a URI with the note ID pattern and the new row ID appended to it.
                    Uri newsUri = ContentUris.withAppendedId(Contract.NewsTable.CONTENT_URI, rowId);

                    // Notifies observers registered against this provider that the data changed.
                    getContext().getContentResolver().notifyChange(newsUri, null);
                    return newsUri;
                }
                // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }

    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int count;

        // Does the delete based on the incoming URI pattern.
        switch (sUriMatcher.match(uri)) {

            case ROUTE_NEWS:
                count = db.delete(Contract.NewsTable.TABLE_NAME, selection, selectionArgs);
                break;

            // If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows deleted.
        return count;
    }

    /**
     * Update an entry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int count;

        // Does the update based on the incoming URI pattern
        switch (sUriMatcher.match(uri)) {

            case ROUTE_NEWS:
                count = db.update(
                        Contract.NewsTable.TABLE_NAME, // The database table name.
                        values,                   // A map of column names and new values to use.
                        selection,                    // The where clause column names.
                        selectionArgs                 // The where clause column values to select on.
                );
                break;


            // If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows updated.
        return count;
    }

    /**
     * SQLite backend for @{link Provider}.
     * <p/>
     * Provides access to an disk-backed, SQLite datastore which is utilized by Provider. This
     * database should never be accessed by other parts of the application directly.
     */
    static class NewsDatabase extends SQLiteOpenHelper {
        /**
         * Schema version.
         */
        public static final int DATABASE_VERSION = 2;
        /**
         * Filename for SQLite file.
         */
        public static final String DATABASE_NAME = "newsreader.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String TYPE_LONG = " LONG";
        private static final String TYPE_FLOAT = " REAL";
        private static final String COMMA_SEP = ",";
        /**
         * SQL statement to create "news" table.
         */
        private static final String SQL_CREATE_NEWS =
                "CREATE TABLE " + Contract.NewsTable.TABLE_NAME + " (" +
                        Contract.NewsTable._ID + " INTEGER PRIMARY KEY," +
                        Contract.NewsTable.COLUMN_NAME_CATEGORY + TYPE_TEXT + COMMA_SEP +
                        Contract.NewsTable.COLUMN_NAME_URL + TYPE_TEXT + COMMA_SEP +
                        Contract.NewsTable.COLUMN_NAME_TITLE + TYPE_TEXT + COMMA_SEP +
                        Contract.NewsTable.COLUMN_NAME_IMAGE + TYPE_TEXT + COMMA_SEP +
                        Contract.NewsTable.COLUMN_NAME_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                        Contract.NewsTable.COLUMN_NAME_PUB_DATE + TYPE_LONG + COMMA_SEP +
                        Contract.NewsTable.COLUMN_NAME_FAVORITE + TYPE_INTEGER + COMMA_SEP +
                        Contract.NewsTable.COLUMN_NAME_IS_NEW + TYPE_INTEGER + " default 1)";


        private static final String SQL_DELETE_NEWS = "DROP TABLE IF EXISTS " + Contract.NewsTable.TABLE_NAME;

        public NewsDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_NEWS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Delete database and build again.
            if (newVersion == 2) {
                db.execSQL(SQL_DELETE_NEWS);
                onCreate(db);
            }
        }
    }
}