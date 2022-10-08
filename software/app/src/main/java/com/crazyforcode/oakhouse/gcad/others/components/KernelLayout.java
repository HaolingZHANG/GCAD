package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.MapInfo;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.ObtainTotalMapRunnable;
import com.crazyforcode.oakhouse.gcad.kernel.views.DrawView;
import com.crazyforcode.oakhouse.gcad.kernel.views.MapView;

public class KernelLayout extends FrameLayout {
    public static MapView mapView;

    public static final int ONLY_MAP = -1;
    public static final boolean COOX = true;
    public static final boolean COOY = false;
    private static int topView = ONLY_MAP;
    public static int LEVEL_NUM = 5;

    //    0:植被、1:地质、2:水体、3:建筑物、4:特殊标记
    //public static DrawView[] drawViews = new DrawView[LEVEL_NUM];
    public static DrawView drawView;

    /**mm单位具体值*/
    private static double mmTrans;
    /**是否正在绘制*/
    public static boolean isDrawing = false;
//    是否正在更改
    public static boolean isEditing = false;
    //public static int editType = EditPicker.NONE;
//    是否正在进行放缩平移
    public static boolean isTransform = false;
//    是否显示特殊层次
    public static boolean showSpecial = false;

//    所有面板当前Matrix和之前正在被保存的正确Matrix
    public static Matrix currentMatrix;
    public static Matrix savedMatrix;

    public KernelLayout(Context context) {
        super(context);
        initKernelLayout(context);
    }

    public KernelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initKernelLayout(context);
    }

    private void initKernelLayout(Context context) {
        try {
            mapView = new MapView(context);
            this.addView(mapView);

            /*for (int i = 0; i < drawViews.length; i++) {
                drawViews[i] = new DrawView(context, i);
                this.addView(drawViews[i]);
            }*/
            drawView = new DrawView(context);
            this.addView(drawView);

//        特殊符号暂时不纳入最高层考虑，采用其他方式绘图。
            topView = ONLY_MAP;

            setMmTrans();

        } catch (NullPointerException ignored) {

        }
    }

    public static void setMmTrans() {
        mmTrans = (SaveTotalMap.getOriginMap().getWidth() + SaveTotalMap.getOriginMap().getHeight())
                / ((MapInfo.getMapWidthCM() + MapInfo.getMapHeightCM()) * 10)
                / ObtainTotalMapRunnable.getSize();

        Log.i("mmtrans set as", mmTrans + "");
    }

    public static double getMmTrans() {
        return mmTrans;
    }

    public static void initBackgrounds() {
        /*for(DrawView drawView : drawViews)
            drawView.initBackground();*/
        for (int i = 0; i < LEVEL_NUM; i++)
            drawView.initBackground(i);
    }

    public static void showMap() {
        mapView.showTemplate();
    }

    public static void hideMap()
    {
        mapView.hideTemplate();
    }

    public static void topLevelChange(int levelTop) {
        if(!isDrawing)
            topView = levelTop;
    }

    public static int getLevel() {
        return topView;
    }
//    public void startDraw() {
//        drawViews[topView].startDrawInThisView();
//        isDrawing = true;
//    }
    public void finishDraw() {
        if(topView >= 0)
            drawView.finishDraw(topView);//drawViews[topView].finishDraw();
        isDrawing = false;
    }

//    public static void startEdit(int type) {
//        isEditing = true;
//        isDrawing = false;
//        editType = type;
//    }
//
//    public static void stopEdit() {
//        editType = EditPicker.NONE;
//    }

    public static void deleteWhenQuit() {
        mapView.deleteTemplate();
        DrawView.cleanPainter();
        /*for (int i = 0; i < 4; i++)
            drawViews[i].deleteBackground();*/
        for (int i = 0; i < LEVEL_NUM; i++)
            drawView.deleteBackground(i);
    }

    /**mapPosition is a map point, return a new point trans from map point
     * to show the point set at where of the machine screen.
     * @param mapPosition a point in drawView , coodinate base on map;
     * @return a new instance of AlgoPoint that trans param to the
     * coodinate base on screen
     * */
    public static AlgoPoint toScreenLocation(AlgoPoint mapPosition) {
        AlgoPoint screP = new AlgoPoint();
        screP.set(toScreenLocation(mapPosition.x, COOX),
                toScreenLocation(mapPosition.y, COOY));

        return screP;
    }

    public static float toScreenLocation(float mapLocation, boolean isX) {
        /*return (mapLocation
                - KernelLayout.mapView.getMapPosition().getX())
                / KernelLayout.mapView.getScale();*/
        return mapLocation / mapView.getScale()
                + (isX ? mapView.getMapPosition().x
                : mapView.getMapPosition().y);
    }

    /**mapPosition is a screen point, return a new point trans from screen point
     * to show the point set at where of the real background picture.
     * @param screenPosition a point in drawView , coodinate base on map;
     * @return a new instance of AlgoPoint that trans param to the
     * coodinate base on map
     * */
    public static AlgoPoint toMapLocation(AlgoPoint screenPosition) {
        AlgoPoint mapP = new AlgoPoint();
        mapP.set(toMapLocation(screenPosition.x, COOX),
                toMapLocation(screenPosition.y, COOY));

        return mapP;
    }

    public static float toMapLocation(float screenLocation, boolean isX) {
        Log.i("getScale=="+mapView.getScale()+"",
                "getMapPosX=="+mapView.getMapPosition().x+"");
        Log.i("getScale::"+mapView.getScale(),
                "getMapPosY::"+mapView.getMapPosition().y);

        return (screenLocation
                - (isX ? mapView.getMapPosition().x
                : mapView.getMapPosition().y))
                * mapView.getScale();

    }
}
