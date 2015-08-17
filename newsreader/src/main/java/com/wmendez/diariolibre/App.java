package com.wmendez.diariolibre;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.wmendez.newsreader.lib.accounts.AccountUtils;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!AccountUtils.accountExists(getApplicationContext()))
            AccountUtils.createAccount(getApplicationContext());
        Fabric.with(this, new Crashlytics());

    }

}
