package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class SaveTotalMap {

    private static Bitmap originTotalMap;
    private static boolean init = true;
    private static double mapScale = 0;

    public static void initTotalMap(Bitmap bitmap) {
        if(init) {
            originTotalMap = bitmap;
            init = false;
            Log.i("total map after", "width = " + originTotalMap.getWidth()
                    + ",height = " + originTotalMap.getHeight());
        }
    }

    public static Bitmap getOriginMap() {
        return originTotalMap;
    }

    public static void deleteWhenQuit() {
        if(originTotalMap != null)
            originTotalMap.recycle();
    }

    public static void deleteMap() {
        if(originTotalMap != null)
            originTotalMap = null;
        init = true;
    }

    public static boolean haveMap() {
        return originTotalMap != null;
    }

    public static void correctTotalMap(double degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float)degree, originTotalMap.getWidth() / 2, originTotalMap.getHeight() / 2);

        int widthBefore = originTotalMap.getWidth();

        originTotalMap = Bitmap.createBitmap(originTotalMap, 0, 0,
                originTotalMap.getWidth(), originTotalMap.getHeight(), matrix, true);

        int widthAfter = originTotalMap.getWidth();

        mapScale = (double)widthAfter / (double)widthBefore;

        Log.i("Correct Map Scale", mapScale + "");
    }

    public static void setMapScale(double scale) {
        mapScale = scale;
    }

    public static double getMapScale() {
        return mapScale;
    }

    public static int getWidth() {
        return originTotalMap.getWidth();
    }

    public static int getHeight() {
        return originTotalMap.getHeight();
    }

//    public static Bitmap paintToBackground(Bitmap background, int layer, AlgoPoint pointForMap) {
//
//        Paint paint = new Paint();
//        switch(layer) {
//            case 0: paint.setColor(Color.GREEN);break;
//            case 1: paint.setColor(Color.YELLOW);break;
//            case 2: paint.setColor(Color.BLUE);break;
//            case 3: paint.setColor(Color.BLACK);break;
//            case 4: paint.setColor(Color.RED);
//        }
//
//        Canvas canvas = new Canvas(background);
//
////        canvas.setBitmap(background);
////                设置为上图重叠下图的模式
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
//
//        canvas.drawRect((float) pointForMap.getX() - 10, (float) pointForMap.getY() - 10,
//                (float) pointForMap.getX() + 10, (float) pointForMap.getY() + 10, paint);
//
//        return background;
//    }
}
