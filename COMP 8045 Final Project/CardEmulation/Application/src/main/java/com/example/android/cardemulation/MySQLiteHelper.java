package com.example.android.cardemulation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.common.logger.Log;

/**
 * public class MySQLiteHelper (extends SQLiteOpenHelper)
 *
 * A database class that creates and upgrades the database used for this app. It is mostly used for
 * Account storage and accessed mostly by the AccountsDataStorage class, which acts like an account
 * "Model" in which information is transferred through an Accounts object.
 *
 *          Classes:
 *                  public MySQLiteHelper(Context context)
 *          Methods:
 *                  public void onCreate(SQLiteDatabase db)
 *                  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
 *
 *          Created by: David Tran
 *          Created on: March 9, 2015
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "ACCOUNTS";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_ACCOUNT_NUMBER = "account_number";
    public static final String COLUMN_SECRET_KEY = "secret_key";
    public static final String COLUMN_IV = "iv";
    public static final String COLUMN_PUBLIC_KEY = "public_key";
    public static final String COLUMN_PRIVATE_KEY = "private_key";

    private static final String DATABASE_NAME = "accounts.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_USERNAME + ", " + COLUMN_ACCOUNT_NUMBER + ", " + COLUMN_SECRET_KEY + ", " +
            COLUMN_IV + ", " + COLUMN_PUBLIC_KEY + ", " + COLUMN_PRIVATE_KEY + ");";

    private static final String DATABASE_DROP =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_DROP);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " +
                newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
