package com.wmendez.newsreader.lib;

import android.app.Application;

import com.wmendez.newsreader.lib.accounts.AccountUtils;

public class NewsApp extends Application {

    @Override
    public void onCreate() {
        if (!AccountUtils.accountExists(getApplicationContext()))
            AccountUtils.createAccount(getApplicationContext());
    }
}
