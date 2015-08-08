package com.wmendez.newsreader.lib.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.wmendez.diariolibre.Constants;

public class AccountUtils {

    public static boolean accountExists(Context context) {
        AccountManager mAccountManager = AccountManager.get(context);


        if (mAccountManager != null) {
            Account[] accountsByType = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
            try {
                accountsByType[0].toString();
                return true;
            } catch (IndexOutOfBoundsException e) {
            }
        }
        return false;
    }

    public static void createAccount(Context context) {

        String name = Constants.ACCOUNT_NAME;
        final Account account = new Account(name, Constants.ACCOUNT_TYPE);
        AccountManager mAccountManager = AccountManager.get(context);
        mAccountManager.addAccountExplicitly(account, null, null);

        ContentResolver.setSyncAutomatically(account, Constants.CONTENT_AUTHORITY, true);
        ContentResolver.setIsSyncable(account, Constants.CONTENT_AUTHORITY, 1);
        ContentResolver.addPeriodicSync(account, Constants.CONTENT_AUTHORITY, new Bundle(),
                Constants.UPDATE_FREQUENCY);


    }
}
