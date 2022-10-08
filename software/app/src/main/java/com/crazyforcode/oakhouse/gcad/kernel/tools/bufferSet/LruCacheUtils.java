package com.crazyforcode.oakhouse.gcad.kernel.tools.bufferSet;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

public class LruCacheUtils {
    /*在Android中，有一个叫做LruCache类专门用来做图片缓存处理的。
    它有一个特点，当缓存的图片达到了预先设定的值的时候，那么近期使用次数最少的图片就会被回收掉。*/

    private LruCache<String, Bitmap> mMemoryCache;

    private static int MAXMEMONRY = (int)(Runtime.getRuntime().maxMemory() / 1024);

    public LruCacheUtils() {

        if(mMemoryCache == null)
            mMemoryCache = new LruCache<String, Bitmap>(MAXMEMONRY / 8) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {

                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            Bitmap oldValue, Bitmap newValue) {

                    Log.v("tag", "hard cache is full , push to soft cache");
                }
            };
    }

    public void clearCache() {

        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                Log.d("CacheUtils", "mMemoryCache.size() " + mMemoryCache.size());

                mMemoryCache.evictAll();

                Log.d("CacheUtils", "mMemoryCache.size()" + mMemoryCache.size());
            }
            mMemoryCache = null;
        }
    }

    public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if(mMemoryCache.get(key) == null)
            if(key != null && bitmap != null)
                mMemoryCache.put(key, bitmap);
        else
            Log.w("TAG", "the res is aready exits");
    }

    public synchronized Bitmap getBitmapFromMemCache(String key) {

        Bitmap bitmap = mMemoryCache.get(key);

        if (key != null)
            return bitmap;

        return null;
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    public synchronized void removeImageCache(String key) {

        if(key != null)
            if(mMemoryCache != null)
            {
                Bitmap bitmap = mMemoryCache.remove(key);

                if(bitmap != null)
                    bitmap.recycle();
            }
    }
}
