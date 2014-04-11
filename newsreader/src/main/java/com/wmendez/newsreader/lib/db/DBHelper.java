package com.wmendez.newsreader.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String NAME = "newsreader.db";
    private static final int VERSION = 1;
    private static DBHelper instance = null;
    private Context context;

    public static final String NEWS_TABLE = "news";
    public static final String NEWS_CATEGORY = "category";
    public static final String NEWS_URL = "url";
    public static final String NEWS_TITLE = "title";
    public static final String NEWS_IMAGE = "image";
    public static final String NEWS_DESCRIPTION = "description";
    public static final String NEWS_PUB_DATE = "pub_date";
    public static final String NEWS_IS_FAVORITE = "favorite";
    public static final String IS_NEW = "is_new";
    public static final String CREATE_SQL =
            "create table " + NEWS_TABLE +
                    " (" + BaseColumns._ID + " integer primary key autoincrement, " + NEWS_CATEGORY + " text, "
                    + NEWS_URL + " text, " + NEWS_TITLE + " text, " + NEWS_DESCRIPTION + " text, "
                    + NEWS_PUB_DATE + " long, " + NEWS_IMAGE + " text, "
                    + NEWS_IS_FAVORITE + " integer, " + IS_NEW + " integer default 1)";

    private DBHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null)
            instance = new DBHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_SQL);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}