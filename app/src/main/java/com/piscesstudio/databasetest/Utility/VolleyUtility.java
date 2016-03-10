package com.piscesstudio.databasetest.Utility;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

public class VolleyUtility {
    private static VolleyUtility mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private VolleyUtility(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(cacheSize));
    }

    public static synchronized VolleyUtility getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyUtility(context.getApplicationContext());
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 20 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueueWithTag(Request<T> req, Object tag) {
        getRequestQueue().add(req).setTag(tag);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
