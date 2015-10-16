package com.example.android.cardemulation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.common.logger.Log;

/**
 * This class is a database "model" that helps retrieve values from the MySQLiteHelper class in order
 * to formulate the Account objects. It is also a class that helps initialize this app's database for
 * testing purposes using some predetermined values.
 *
 *      Classes:
 *          AccountDataSource(Context context)
 *
 *      Methods:
 *          public void open()
 *          public void close()
 *          public void addAccount(String username, String account, String key,
 *                                 String iv, String publicKey, String privateKey)
 *          public Account getAccount(String username, String account)
 *          private Account cursorToAccount(Cursor cursor)
 *          public void initDatabase()
 *
 *      Author: David Tran
 *      Created on: March 9, 2015
 *
 */
public class AccountsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_USERNAME,
        MySQLiteHelper.COLUMN_ACCOUNT_NUMBER,
        MySQLiteHelper.COLUMN_SECRET_KEY,
        MySQLiteHelper.COLUMN_IV,
        MySQLiteHelper.COLUMN_PUBLIC_KEY,
        MySQLiteHelper.COLUMN_PRIVATE_KEY
    };

    private static String WHERE_CLAUSE =
        MySQLiteHelper.COLUMN_USERNAME + " = ? AND " +
        MySQLiteHelper.COLUMN_ACCOUNT_NUMBER + " = ?";

    private final static String TAG = "AccountDataSource";

    /**
     * public AccountsDataSource
     *
     * This initializer creates an instance of the MySQLiteHelper. Once the helper class is created,
     * we can begin to retrieve and write to the database as we need to.
     *
     * @param context
     */
    public AccountsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /**
     * public void open
     *
     * Opens the database in writable format.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * public void close
     *
     * Closes the database once the logic has finished its transaction.
     *
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * public void addAccount
     *
     * This method converts a set of String values into an ContentValues object, and executes
     * an INSERT query into our table. The INSERT query method returns an ID in which we do store,
     * but we have yet to implement logic or error checking in this implementation.
     *
     * TODO: Implement error checking with returned ID.
     *
     * @param username
     * @param account
     * @param key
     * @param iv
     * @param publicKey
     * @param privateKey
     */
    public void addAccount(String username, String account, String key,
                           String iv, String publicKey, String privateKey) {

        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_USERNAME, username);
        values.put(MySQLiteHelper.COLUMN_ACCOUNT_NUMBER, account);
        values.put(MySQLiteHelper.COLUMN_SECRET_KEY, key);
        values.put(MySQLiteHelper.COLUMN_IV, iv);
        values.put(MySQLiteHelper.COLUMN_PUBLIC_KEY, publicKey);
        values.put(MySQLiteHelper.COLUMN_PRIVATE_KEY, privateKey);

        long insertID = database.insert(MySQLiteHelper.TABLE_NAME, null, values);
    }

    /**
     * public Account getAccount
     *
     * This method converts two String parameters into an array that is utilized in our query to
     * specify which records to retrieve. In other words, the array acts as the WHERE arguments in
     * the WHERE clause.
     *
     * The method returns an Account object that's converted from the query results.
     *
     * @param username
     * @param account
     * @return Account object
     */
    public Account getAccount(String username, String account) {

        String[] whereArgs = new String[] {
            username,
            account
        };

        Account accountInfo = null;
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns,
                WHERE_CLAUSE, whereArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            accountInfo = cursorToAccount(cursor);
        } else {
            Log.e(TAG, "No results from database query...");
        }

        cursor.close();
        return accountInfo;
    }

    /**
     * private Account cursorToAccount
     *
     * This method converts String values returned from our query results into an Account object.
     *
     * @param cursor
     * @return Account object
     */
    private Account cursorToAccount(Cursor cursor) {
        Account account = new Account();

        // Starts at [1], because [0] is the _id column
        account.setUsername(cursor.getString(1));
        account.setAccount(cursor.getString(2));
        account.setSecretKey(cursor.getString(3));
        account.setIv(cursor.getString(4));
        account.setPublicKey(cursor.getString(5));
        account.setPrivateKey(cursor.getString(6));

        return account;
    }

    /**
     * public void initDatabase
     *
     * For testing purposes only. During deployment, this method should not be used. It is meant to
     * populate the database with some dummy data using the local addAccount() method.
     *
     * As a bit of trivia, "Trankalinos" is a play on David Tran's last name. "Phaticus" is a play
     * on David's brother's name, Phat and "Amaryillis" is a play on David's sister-in-law's name
     * "Amy." Finally, "Cherubie" is simply David's girlfriend's name, Cherrie, but with a twist.
     *
     */
    public void initDatabase() {
        addAccount("Trankalinos", "7147025", "This is a key123", "This is an IV456", "note-4-public-key.txt", "note-4-private-key.txt");
        addAccount("Phaticus", "7147026", "This is a key123", "This is an IV456", "note-4-public-key.txt", "note-4-private-key.txt");
        addAccount("Amaryllis", "7147027", "This is a key123", "This is an IV456", "note-4-public-key.txt", "note-4-private-key.txt");
        addAccount("Cherubie", "7147028", "This is a key123", "This is an IV456", "note-4-public-key.txt", "note-4-private-key.txt");
    }
}
