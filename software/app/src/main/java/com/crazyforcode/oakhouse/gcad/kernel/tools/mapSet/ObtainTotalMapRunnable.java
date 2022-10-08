package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MRunnable;

public class ObtainTotalMapRunnable extends MRunnable {

    private Uri uri;
    private static int size;

    private int type;

    public ObtainTotalMapRunnable(MHandler handler, Uri uri, int type) {
        super(handler);
        this.type = type;
        this.uri = uri;
    }

    @Override
    public void running() {
        Log.i("current running", "obtain total map");
        if(SaveTotalMap.haveMap()) {
            SaveTotalMap.deleteMap();
            System.gc();
        }

        Message message;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

//            默认是Bitmap.Config.ARGB_8888
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
//            不进行图片抖动处理
            options.inDither = false;

            // 此时不会把图片读入内存，只会获取图片宽高等信息
            BitmapFactory.decodeStream
                    (getMHandler().getUserPromptContext().getContentResolver().openInputStream(uri),
                            null, options);

            int width = options.outWidth;
            int height = options.outHeight;

            Log.i("total map before", "width = " + options.outWidth
                    + ",height = " +  options.outHeight);

//            根据需要设置压缩比
            options.inSampleSize = computeCompression(width, height);

            options.inJustDecodeBounds = false;

//            此时图片会按比例压缩后被载入内存中
            Bitmap totalMap = BitmapFactory.decodeStream
                    (getMHandler().getUserPromptContext().getContentResolver().openInputStream(uri), null, options);

            SaveTotalMap.initTotalMap(totalMap);


            if(type == MHandler.INPUT_MAP_INIT)
                message = getMHandler().obtainMessage(MHandler.INPUT_MAP_INIT, MHandler.SUCCESS, 0);
            else
                message = getMHandler().obtainMessage(MHandler.INPUT_MAP_AGAIN, MHandler.SUCCESS, 0);

            getMHandler().sendMessageAtTime(message, 0);

        } catch(Exception e) {
            if(type == MHandler.INPUT_MAP_INIT)
                message = getMHandler().obtainMessage(MHandler.INPUT_MAP_INIT, MHandler.FAILURE, 0);
            else
                message = getMHandler().obtainMessage(MHandler.INPUT_MAP_AGAIN, MHandler.FAILURE, 0);

            getMHandler().sendMessageAtTime(message, 0);
        }
    }

    public static int computeCompression(int mapWidth, int mapHeight) {

        /** 直接更换为实际值，避免高分辨率手机加载时OOM */
        size = (int)Math.max(mapWidth / 720.0, mapHeight / 1280.0);
        if(size <= 0)
            size = 1;

        size++;

        if(size > 2 && size % 2 != 0)
            size--;

        return size;
    }

    public static int getSize() {
        return size;
    }
}
