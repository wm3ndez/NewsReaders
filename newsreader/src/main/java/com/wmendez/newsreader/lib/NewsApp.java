package com.wmendez.newsreader.lib;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.wmendez.newsreader.lib.accounts.AccountUtils;

import io.fabric.sdk.android.Fabric;

public class NewsApp extends Application {

    @Override
    public void onCreate() {
        if (!AccountUtils.accountExists(getApplicationContext()))
            AccountUtils.createAccount(getApplicationContext());
        Fabric.with(this, new Crashlytics());
    }
}
