package com.polaris.utils;

/**
 * Created by kgopal on 4/20/17.
 */

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LRUBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    public LRUBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    public LRUBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}