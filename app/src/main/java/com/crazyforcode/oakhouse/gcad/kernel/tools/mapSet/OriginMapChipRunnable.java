package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class OriginMapChipRunnable extends MRunnable {

    private boolean isChip = false;
    private InputStream stream = null;
    private String projectPath = null;

    public OriginMapChipRunnable(MHandler handler, String path) {
        super(handler);
        try {
            this.stream = new FileInputStream(new File(path));
            this.projectPath = path;
        } catch (FileNotFoundException e) {
            stream = null;
            projectPath = null;
        }
    }

    @Override
    public void runBefore() {
        if(stream != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

//            默认是Bitmap.Config.ARGB_8888
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
//            不进行图片抖动处理
            options.inDither = false;

//            此时不会把图片读入内存，只会获取图片宽高等信息
            BitmapFactory.decodeStream(stream, null, options);

            int width = options.outWidth;
            int height = options.outHeight;

            if (Math.max(width, height) > 2000) {
                isChip = true;
                Log.i("可裁剪性", true + "");
            }
        }
    }

    @Override
    public void running() {
        try {
            if(isChip && stream != null)
                MapCutting.split(stream, projectPath);
            else
                Log.i("图片未超过限度", "不用进行高清加载策略");

        } catch (IOException e) {
            Log.i("切割失败", e.toString());
        }
    }

    @Override
    public void runAfter() {
//        保存这个项目的切分个数
       Log.i("个数", MapCutting.getPiece() + "");
    }
}
