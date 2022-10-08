package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.util.Log;


import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import java.io.File;
import java.io.IOException;

public class MapInfo {

    private static int mapDPI = 0;
    private static double mapWidthCM = 0;
    private static double mapHeightCM = 0;

    public static void setMapDPI(File file) {
//        自动检测时
        try {
            ImageInfo imageInfo = Imaging.getImageInfo(file);
            mapDPI= imageInfo.getPhysicalWidthDpi();
            mapWidthCM = imageInfo.getPhysicalWidthInch() * 2.54;
            mapHeightCM = imageInfo.getPhysicalHeightInch() * 2.54;

            Log.i("map dpi", mapDPI + "");
            Log.i("map width", mapWidthCM + "cm");
            Log.i("map height", mapHeightCM + "cm");
        } catch (ImageReadException | IOException ignored) {

        }
    }

    public static void setMapDPI(int dpi) {
//        工程载入或者自定义时
        mapDPI = dpi;
        mapWidthCM = SaveTotalMap.getWidth() / (double) mapDPI * 2.54 * ObtainTotalMapRunnable.getSize();
        mapHeightCM = SaveTotalMap.getHeight() / (double) mapDPI * 2.54 * ObtainTotalMapRunnable.getSize();
        KernelLayout.setMmTrans();
        Log.i("自定义dpi", "手动输入或者工程载入");
        Log.i("map dpi", mapDPI + "");
        Log.i("map width", mapWidthCM + "cm");
        Log.i("map height", mapHeightCM + "cm");
    }

    public static int getMapDPI() {
        return mapDPI;
    }

    public static double getMapWidthCM() {
        if(mapWidthCM == 0)
            mapWidthCM = SaveTotalMap.getWidth() / (double) mapDPI * 2.54;

        return mapWidthCM;
    }

    public static double getMapHeightCM() {
        if(mapHeightCM == 0)
            mapHeightCM =  SaveTotalMap.getHeight() / (double) mapDPI * 2.54;

        return mapHeightCM;
    }
}
