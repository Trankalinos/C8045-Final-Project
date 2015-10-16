/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.cardemulation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * public class AccountStorage
 *
 * This class retrieves the account values as soon as the textboxes are finished "editing". It is
 * also thread-safe by applying an account lock object to each of the following methods.
 *
 *      Methods:
 *              public static void SetAccount(Context c, String s)
 *              public static String GetAccount(Context c)
 *              public static void SetUsername(Context c, String s)
 *              public static String GetUsername(Context c)
 *
 *      Created by: Android Developers
 *      Modified by: David Tran
 *      Modified on: March 9, 2015
 *
 */
public class AccountStorage {
    private static final String PREF_ACCOUNT_NUMBER = "account_number";
    private static final String PREF_ACCOUNT_NAME = "account_name";

    private static final String DEFAULT_ACCOUNT_NUMBER = "250624";
    private static final String DEFAULT_ACCOUNT_NAME = "Trankalinos";

    private static final String TAG = "AccountStorage";

    private static String sAccount = null;
    private static String sUsername = null;

    private static final Object sAccountLock = new Object();

    /**
     * public static void SetAccount
     *
     * Sets the account number by obtaining an instance of a lock object, and then releasing it
     * once finished.
     *
     * @param c
     * @param s
     */
    public static void SetAccount(Context c, String s) {
        synchronized(sAccountLock) {
            Log.i(TAG, "Setting account number: " + s);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_ACCOUNT_NUMBER, s).commit();
            sAccount = s;
        }
    }

    /**
     * public static String GetAccount
     *
     * Gets the account number by obtaining an instance of a lock object, and then releasing it
     * once finished.
     *
     * @param c
     * @return
     */
    public static String GetAccount(Context c) {
        synchronized (sAccountLock) {
            if (sAccount == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                String account = prefs.getString(PREF_ACCOUNT_NUMBER, DEFAULT_ACCOUNT_NUMBER);
                sAccount = account;
            }
            return sAccount;
        }
    }

    /**
     * public static void SetUsername
     *
     * Sets the username by obtaining an instance of the lock object, and then releasing it once
     * it has finished.
     *
     * @param c
     * @param s
     */
    public static void SetUsername(Context c, String s) {
        synchronized (sAccountLock) {
            Log.i(TAG, "Setting username: " + s);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_ACCOUNT_NAME, s).commit();
            sUsername = s;
        }
    }

    /**
     * public static String GetUsername
     *
     * Gets the username by obtaining an instance of the lock object, and then releasing it once
     * it has finished.
     *
     * @param c
     * @return
     */
    public static String GetUsername(Context c) {
        synchronized (sAccountLock) {
            if (sUsername == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                String username = prefs.getString(PREF_ACCOUNT_NAME, DEFAULT_ACCOUNT_NAME);
                sUsername = username;
            }
            return sUsername;
        }
    }
}
