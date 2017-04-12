package com.sbai.httplib;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;

public class RequestManager {

    public static final String VOLLEY_TAG = "VolleyHttp";
    public static String TAG = VOLLEY_TAG;

    private static class Volley {

        private static final String DEFAULT_CACHE_DIR = "inv_volley";

        public static RequestQueue newRequestQueue(File cacheDirectory) {
            File cacheDir = new File(cacheDirectory, DEFAULT_CACHE_DIR);
            Network network = new BasicNetwork(new CookieHurlStack());
            RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
            queue.start();
            return queue;
        }
    }

    public static void init(File cacheDir) {
        sRequestQueue = Volley.newRequestQueue(cacheDir);
    }

    public static void enqueue(Request<?> request) {
        if (sRequestQueue != null) {
            sRequestQueue.add(request);
        } else {
            throw new NullPointerException("Request queue isn't initialized.");
        }
    }

    public static void cancel(String tag) {
        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(tag);
        }
    }

    private static RequestQueue sRequestQueue;
}
