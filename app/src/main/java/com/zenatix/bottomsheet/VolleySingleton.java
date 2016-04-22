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

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

/**
 * This is volleySingleton class. Will be Mainly used for all the network calls
 */
public class VolleySingleton {
    /**
     * The constant TAG.
     */
    private static final String TAG = VolleySingleton.class
            .getSimpleName();
    /**
     * The constant mInstance.
     */
    private static VolleySingleton mInstance = null;
    /**
     * The M request queue.
     */
    private RequestQueue mRequestQueue;

    /**
     * Instantiates a new Volley singleton.
     *
     * @param mCtx
     *         the m ctx
     */
    private VolleySingleton(final Context mCtx) {
        mRequestQueue = getRequestQueue();
        VolleyLog.DEBUG = true;
    }

    /**
     * Gets instance.
     *
     * @param mCtx
     *         the m ctx
     * @return the instance
     */
    public static VolleySingleton getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(mCtx);
        }
        return mInstance;
    }

    /**
     * Gets request queue.
     *
     * @return the request queue
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            File ext = Environment.getExternalStorageDirectory();
            File cacheDir = new File(ext, "Bijli/data/etc/data");
            final Cache cache = new DiskBasedCache(cacheDir, 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }
        return this.mRequestQueue;
    }

    /**
     * Add to request queue.
     *
     * @param <T>
     *         the type parameter
     * @param req
     *         the req
     * @param tag
     *         the tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        mRequestQueue.add(req);
    }
}
