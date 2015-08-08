package com.wmendez.newsreader.lib.syncadapter;


import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.wmendez.diariolibre.BuildConfig;
import com.wmendez.newsreader.lib.event.SyncEndedEvent;
import com.wmendez.newsreader.lib.helpers.Entry;
import com.wmendez.newsreader.lib.net.NewsAPI;
import com.wmendez.newsreader.lib.provider.Contract;
import com.wmendez.newsreader.lib.util.PrefUtils;
import com.wmendez.newsreader.lib.util.Utils;

import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

/**
 * Define a sync adapter for the app.
 * <p/>
 * <p>This class is instantiated in {@link com.wmendez.newsreader.lib.services.SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 * <p/>
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;

    private final Context mContext;
    private final NewsAPI.API api;

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mContext = context;
        api = new RestAdapter.Builder()
                .setClient(getUnsafeOkHttpClient())
                .setEndpoint(NewsAPI.getAPIUrl(mContext))
                .build()
                .create(NewsAPI.API.class);

    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mContext = context;
        api = new RestAdapter.Builder()
                .setClient(getUnsafeOkHttpClient())
                .setEndpoint(NewsAPI.getAPIUrl(mContext))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(NewsAPI.API.class);
    }

    private static OkClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return new OkClient(okHttpClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");
        Long datetime = PrefUtils.getLastSync(mContext);

        try {
            List<Entry> entryList = api.newsList("diariolibre", datetime);
            for (Entry entry : entryList) {
                insertEntry(entry);
            }
            PrefUtils.setLastSync(mContext, new Date().getTime());
        } catch (RetrofitError retrofitError) {
            Log.e(TAG, "Network synchronization complete", retrofitError);
        }
        EventBus.getDefault().post(new SyncEndedEvent());
        Log.i(TAG, "Network synchronization complete");
    }


    private void insertEntry(Entry entry) {
        if (newsExist(entry.link, entry.category))
            return;

        ContentValues values = new ContentValues();
        values.clear();
        values.put(Contract.NewsTable.COLUMN_NAME_CATEGORY, entry.category);
        values.put(Contract.NewsTable.COLUMN_NAME_TITLE, entry.title);
        values.put(Contract.NewsTable.COLUMN_NAME_URL, entry.link);
        values.put(Contract.NewsTable.COLUMN_NAME_DESCRIPTION, entry.description);
        values.put(Contract.NewsTable.COLUMN_NAME_PUB_DATE, entry.pubDate);
        values.put(Contract.NewsTable.COLUMN_NAME_IMAGE, entry.image);
        values.put(Contract.NewsTable.COLUMN_NAME_IS_NEW, true);
        values.put(Contract.NewsTable.COLUMN_NAME_FAVORITE, false);
        mContentResolver.insert(Contract.NewsTable.CONTENT_URI, values);
    }

    private boolean newsExist(String link, String category) {
        Cursor c = mContentResolver.query(
                Contract.NewsTable.CONTENT_URI,
                new String[]{Contract.NewsTable.COLUMN_NAME_CATEGORY},
                Contract.NewsTable.COLUMN_NAME_URL + " = ?",
                new String[]{link},
                null
        );


        boolean exists = c.getCount() > 0;
        if (exists) {
            c.moveToFirst();
            String categories = c.getString(c.getColumnIndex(Contract.NewsTable.COLUMN_NAME_CATEGORY));
            String[] cats = categories.split("|");
            List<String> strings = Arrays.asList(cats);
            if (!strings.contains(category)) {
                String[] catList = new String[strings.size() + 1];
                for (int i = 0; i < strings.size(); i++) {
                    catList[i] = strings.get(i);
                }
                catList[strings.size()] = category;
                String newCategories = Utils.join(catList, "|");
                ContentValues values = new ContentValues();
                values.put(Contract.NewsTable.COLUMN_NAME_CATEGORY, newCategories);
                mContentResolver.update(Contract.NewsTable.CONTENT_URI, values, Contract.NewsTable.COLUMN_NAME_URL + " = ?", new String[]{link});
            }
        }
        c.close();
        return exists;
    }
}