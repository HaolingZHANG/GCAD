package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Message;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MRunnable;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import java.io.File;
import java.io.FileOutputStream;

public class FinalMapOutputRunnable extends MRunnable {

    private String outputPath;
    private Bitmap finalMap;
    private double scale;
    private AlgoPoint mapPositionForA4;

    public FinalMapOutputRunnable(MHandler handler, Bitmap finalMap, double scale,
                                  AlgoPoint mapPositionForA4, String outputPath) {
        super(handler);
        this.finalMap = finalMap;
        this.scale = scale;
        this.mapPositionForA4 = mapPositionForA4;
        this.outputPath = outputPath;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void running() {
        Message message;
        try {
            SharedPreferences preferences = getMHandler().getUserPromptContext().getSharedPreferences("config", Context.MODE_PRIVATE);
            String rootPath = preferences.getString("RootPath", "");
            int count = 1;
            File localFile;

            while(true) {
                localFile = new File(rootPath + "/" + MainActivity.getProjectName() + "/Map/FinalMap" + count +".jpg");
                if(localFile.exists())
                    count++;
                else
                    break;
            }

            FileOutputStream outputLocal = new FileOutputStream(localFile);
            FileOutputStream outputUser = new FileOutputStream(new File(outputPath));

            Bitmap bitmap = adaptA4Cutting();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputLocal);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputUser);

//            ???????????????????????????
            outputLocal.flush();
            outputUser.flush();

            outputLocal.close();
            outputUser.close();

            message = getMHandler().obtainMessage(MHandler.OUTPUT_MAP, MHandler.SUCCESS, 0);
            getMHandler().sendMessageAtTime(message, 0);

        } catch (Exception e) {
            message = getMHandler().obtainMessage(MHandler.OUTPUT_MAP, MHandler.FAILURE, 0);
            getMHandler().sendMessageAtTime(message, 0);
        }
    }

    private Bitmap adaptA4Cutting() {
        int width = (int)((SaveTotalMap.getWidth() / MapInfo.getMapWidthCM() + SaveTotalMap.getHeight() / MapInfo.getMapHeightCM()) / 2.0 * scale);
        int height = (int)(width / 21.0 * 29.7);

        Log.i("??????????????????????????????????????????", scale + "???1");
        Log.i("A4??????,???:???", width + ":" + height);

        Bitmap map;
        if(scale > 1)
            map = Bitmap.createBitmap((int)(width * scale), (int)(height * scale), Bitmap.Config.ARGB_4444);
        else
            map = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(map);

        /**
         * drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint)???
         * Rect src: ????????????????????????????????????null?????????????????????
         * RectF dst???????????????Canvas???????????????????????????
         * ??????src??????src???????????????????????????src??????src?????????????????????
         */

        //TODO ?????? ???????????? ?????? return finalMap ????????? map
        RectF dst = new RectF();

        float left = (float)(mapPositionForA4.x * scale * MapInfo.getMapDPI());
        float top = (float)(mapPositionForA4.y * scale * MapInfo.getMapDPI());
        float right = left + finalMap.getWidth();
        float bottom = top + finalMap.getHeight();

        dst.set(left, top, right, bottom);

        canvas.drawBitmap(finalMap, null, dst, null);

        return finalMap;
    }
}
