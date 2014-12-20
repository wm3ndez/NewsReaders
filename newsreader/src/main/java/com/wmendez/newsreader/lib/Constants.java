package com.wmendez.newsreader.lib;

public class Constants {
    public static String ACCOUNT_NAME = "News Reader";
    public static String ACCOUNT_TYPE = "com.wmendez." + BuildConfig.FLAVOR + ".syncadapter";
    public static String CONTENT_AUTHORITY = ACCOUNT_TYPE;
    public static final int UPDATE_FREQUENCY = 2 * 60 * 60; // Every two hours

}