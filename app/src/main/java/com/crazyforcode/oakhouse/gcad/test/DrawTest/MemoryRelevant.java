package com.crazyforcode.oakhouse.gcad.test.DrawTest;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Formatter;
import android.util.Log;

public class MemoryRelevant {

    public static void print(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);

        Log.i("当前可用内存", Formatter.formatFileSize(context, memoryInfo.availMem));
    }

    public static void bitmapPrint(Bitmap bitmap, String title) {
        Log.i(title, (bitmap.getByteCount() * 1024 * 1024) + "M");
    }
}
