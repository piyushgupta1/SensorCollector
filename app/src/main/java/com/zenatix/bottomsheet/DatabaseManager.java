/*
 *  ZENATIX CONFIDENTIAL
 * __________________
 *
 * [2016] Zenatix Solutions Private Limited
 * All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains
 * the property of Zenatix Solutions Private Limited and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Zenatix Solutions Private Limited
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Zenatix Solutions Private Limited.
 */

package com.zenatix.bottomsheet;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RC on 4/1/2016.
 * For com.zenatix.smartmeter.Utils
 */
public class DatabaseManager {

    /**
     * The constant instance.
     */
    private static DatabaseManager instance;
    /**
     * The constant mDatabaseHelper.
     */
    private static SQLiteOpenHelper mDatabaseHelper;
    /**
     * The M open counter.
     */
    private int mOpenCounter;
    /**
     * The M database.
     */
    private SQLiteDatabase mDatabase;

    /**
     * Initialize instance.
     *
     * @param helper
     *         the helper
     */
    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    /**
     * Open database sq lite database.
     *
     * @return the sq lite database
     */
    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter++;
        if (mOpenCounter == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
            mDatabase.enableWriteAheadLogging();
        }
        return mDatabase;
    }

    /**
     * Close database.
     */
    public synchronized void closeDatabase() {
        mOpenCounter--;
        if (mOpenCounter == 0) {
            // Closing database
            mDatabase.close();

        }
    }

}