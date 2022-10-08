package com.crazyforcode.oakhouse.gcad.kernel.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import java.util.ArrayList;

public class MapView extends SurfaceView {

    //  底图信息
    private Bitmap template;
    private AlgoPoint mapPosition = new AlgoPoint();
    private boolean showMap = false;

    //  初始化信息确定
    private AlgoPoint lastCenter = new AlgoPoint();
    private double lastDistance;
    private float lastTransformScale = 1f;
    private double compressionScale;

    private boolean initChange = true;

    //  当前信息
    private AlgoPoint center;
    private float scale = 1f;
    private float transformScale = 1f;

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.setClickable(true);
        this.setWillNotDraw(false);
        KernelLayout.currentMatrix = new Matrix();
        KernelLayout.savedMatrix = new Matrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setCurrentMapPosition();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                KernelLayout.isDrawing = false;
                resumeSafetyScale(center);
                resumeSafetyPosition(isSafetyState());
                invalidate();
                ensurePositionAndScale();
                KernelLayout.savedMatrix.set(KernelLayout.currentMatrix);
                scale = getScale();
                lastTransformScale = transformScale;
                KernelLayout.isTransform = false;
                initChange = true;
                lastDistance = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                KernelLayout.isDrawing = false;
                if(event.getPointerCount() >= 2) {

                    KernelLayout.isTransform = true;
                    PointF figOne = new PointF(event.getX(0), event.getY(0));
                    PointF figTwo = new PointF(event.getX(1), event.getY(1));

                    center = new AlgoPoint((figOne.x + figTwo.x) / 2,
                            (figOne.y + figTwo.y) / 2);
                    double distance = AlgoPoint.distance(figOne, figTwo);
                    /*Math.hypot(figOne.x - figTwo.x
                            , figOne.y - figTwo.y);*/

                    if(initChange) {
                        lastDistance = distance;
                        lastCenter = center.copy();
                        initChange = false;
                    }

                    double scaleXY = distance / lastDistance;

                    double moveX = center.x
                            - lastCenter.x;
                    double moveY = center.y
                            - lastCenter.y;

                    KernelLayout.currentMatrix.set(KernelLayout.savedMatrix);

                    if(Math.abs(moveX) > 10 || Math.abs(moveY) > 10) {
                        KernelLayout.currentMatrix.postTranslate((float) moveX, (float) moveY);
                        invalidate();
                    }

                    if(allowChangeScale()) {
                        if(distance - lastDistance > 10) {
                            setTransformScale(scaleXY);
                            KernelLayout.currentMatrix.postScale((float)scaleXY, (float)scaleXY,
                                    center.x, center.y);
                            invalidate();
                        } else if(distance - lastDistance < -10) {
                            setTransformScale(scaleXY);
                            KernelLayout.currentMatrix.postScale((float) scaleXY, (float) scaleXY,
                                    center.x, center.y);
                            invalidate();
                        }
                        MainActivity.changeScale();
                    }

                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        if(template == null && SaveTotalMap.getOriginMap() != null) {
            setTemplate();
            //KernelLayout.initBackgrounds();
        }

        if(template != null && showMap)
            canvas.drawBitmap(template, KernelLayout.currentMatrix, null);
    }

    public void setTemplate() {
        deleteTemplate();

//        获取这个图片的宽和高
        double mapWidth = SaveTotalMap.getOriginMap().getWidth();
        double mapHeight = SaveTotalMap.getOriginMap().getHeight();

//        创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        KernelLayout.savedMatrix = new Matrix();
        KernelLayout.currentMatrix = new Matrix();

//        计算宽高缩放率
        compressionScale = Math.min((this.getWidth() / mapWidth), (this.getHeight() / mapHeight));
//        缩放图片动作
        matrix.postScale((float)compressionScale, (float)compressionScale);

//        将压缩好的图片输出
        this.template = Bitmap.createBitmap(SaveTotalMap.getOriginMap(), 0, 0,
                (int)mapWidth, (int)mapHeight, matrix, true);

        float moveX = (float) (this.getWidth() - template.getWidth()) / 2;
        float moveY = (float) (this.getHeight() - template.getHeight()) / 2;

        mapPosition = new AlgoPoint(moveX, moveY);
        KernelLayout.savedMatrix.postTranslate(moveX, moveY);
        KernelLayout.currentMatrix.postTranslate(moveX, moveY);

        scale = getScale();
    }

    public void showTemplate() {
        showMap = true;
        invalidate();
    }

    public void hideTemplate() {
        showMap = false;
        invalidate();
    }

    public void deleteTemplate() {
        this.template = null;
    }

    public double getCompressionScale() {
        return compressionScale;
    }

    public AlgoPoint getMapPosition() {
        return mapPosition;
    }

    public void setCurrentMapPosition() {
        AlgoPoint[] vertex = getVertex();
        if(vertex != null)
            mapPosition = vertex[0];
    }

    public AlgoPoint[] getVertex() {
        try {
            AlgoPoint[] vertex = new AlgoPoint[4];
            float[] f = new float[9];
            KernelLayout.currentMatrix.getValues(f);

            // 图片4个顶点的坐标
            vertex[0] = new AlgoPoint(f[0] * 0 + f[1] * 0 + f[2],
                    f[3] * 0 + f[4] * 0 + f[5]);
            vertex[1] = new AlgoPoint(f[0] * template.getWidth() + f[1] * 0 + f[2],
                    f[3] * template.getWidth() + f[4] * 0 + f[5]);
            vertex[2] = new AlgoPoint(f[0] * 0 + f[1] * template.getHeight() + f[2],
                    f[3] * 0 + f[4] * template.getHeight() + f[5]);
            vertex[3] = new AlgoPoint(f[0] * template.getWidth() + f[1] * template.getHeight() + f[2],
                    f[3] * template.getWidth() + f[4] * template.getHeight() + f[5]);

            return vertex;
        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
            Log.i("mapView.getVertex()", ignored.toString());
            return null;
        }
    }

    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    public float getScale() {
        float temp = transformScale;
        PointF[] vertex = getVertex();
        try {
            float mapWidth = vertex[1].x - vertex[0].x;

            if (mapWidth != 0) {
                temp = this.getWidth() / mapWidth;
                transformScale = temp;
            }
        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
            Log.i("mapView.getScale()", ignored.toString());
        } finally {
            return temp;
        }
    }

    public boolean allowChangeScale() {
        if (showMap) {
            scale = getScale();
            if (scale >= 0.01 && scale <= 1.5)
                return true;
        }

        return transformScale >= 0.01 && transformScale <= 1.5;

    }

    public void setTransformScale(double scaleXY) {
        scale = getScale();
        transformScale = lastTransformScale;
        transformScale /= scaleXY;
    }

    public void changeByDrawViewOver(AlgoPoint point) {
        AlgoPoint[] vertex = getVertex();
        resumeSafetyScale(point);
        resumeSafetyPosition(isSafetyState());
        mapPosition = vertex[0];
        KernelLayout.savedMatrix.set(KernelLayout.currentMatrix);
        ensurePositionAndScale();
        lastTransformScale = transformScale;
        initChange = true;
        lastDistance = 0;
    }

    public void ensurePositionAndScale() {

        AlgoPoint[] vertex = getVertex();

        if(vertex != null)
            mapPosition = vertex[0];

        scale = getScale();
        MainActivity.changeScale();
    }

    private ArrayList<Integer> isSafetyState() {

        ArrayList<Integer> state = new ArrayList<>(4);
        if(showMap) {
            PointF[] vertex = getVertex();

//          左上角
            if(vertex[0].x > (this.getWidth() / 2)
                    || vertex[0].y > (this.getHeight() / 2))
                state.add(1);
//          右上角
            if(vertex[1].x < (this.getWidth() / 2)
                    || vertex[1].y > (this.getHeight() / 2))
                state.add(2);
//          左下角
            if(vertex[2].x > (this.getWidth() / 2)
                    || vertex[2].y < (this.getHeight() / 2))
                state.add(3);
//          右下角
            if(vertex[3].x < (this.getWidth() / 2)
                    || vertex[3].y < (this.getHeight() / 2))
                state.add(4);
        }
//      是安全位置
        if(state.size() == 0)
            state.add(0);

        return state;
    }

    public void resumeSafetyPosition(ArrayList<Integer> unvisualPositions) {

        PointF[] vertex = getVertex();
        double moveX = 0;
        double moveY = 0;

        if(unvisualPositions.size() == 2) {
//        边还原
            if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 2) {
//                还原上边
                moveY = this.getHeight() / 2 - vertex[1].y;
                KernelLayout.currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 2
                    && unvisualPositions.get(1) == 4) {
//                还原右边
                moveX = this.getWidth() / 2 - vertex[1].x;
                KernelLayout.currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 3
                    && unvisualPositions.get(1) == 4) {
//                还原下边
                moveY = this.getHeight() / 2 - vertex[2].y;
                KernelLayout.currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 3) {
//                还原左边
                moveX = this.getWidth() / 2 - vertex[2].x;
                KernelLayout.currentMatrix.postTranslate((float)moveX, (float)moveY);
            }
            /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                KernelLayout.drawViews[i].invalidate();*/
            KernelLayout.drawView.invalidate();
            this.invalidate();
        } else if(unvisualPositions.size() == 3) {
//        点还原
            if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 2
                    && unvisualPositions.get(2) == 3) {
//                还原左上角
                moveX = this.getWidth() / 2 - vertex[0].x;
                moveY = this.getHeight() / 2 - vertex[0].y;
                KernelLayout.currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 2
                    && unvisualPositions.get(2) == 4) {
//                还原右上角
                moveX = this.getWidth() / 2 - vertex[1].x;
                moveY = this.getHeight() / 2 - vertex[1].y;
                KernelLayout.currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 3
                    && unvisualPositions.get(2) == 4) {
//                还原左下角
                moveX = this.getWidth() / 2 - vertex[2].x;
                moveY = this.getHeight() / 2 - vertex[2].y;
                KernelLayout.currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 2
                    && unvisualPositions.get(1) == 3
                    && unvisualPositions.get(2) == 4) {
//                还原右下角
                moveX = this.getWidth() / 2 - vertex[3].x;
                moveY = this.getHeight() / 2 - vertex[3].y;
                KernelLayout.currentMatrix.postTranslate((float)moveX, (float)moveY);
            }
            /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                    KernelLayout.drawViews[i].invalidate();*/
            KernelLayout.drawView.invalidate();
            this.invalidate();
        }
    }

    public void resumeSafetyScale(PointF point) {
        if(showMap) {
            if(getScale() > 1.5) {
                double changScale = getScale() / 1.5 + 0.01;
                transformScale /= changScale;
                KernelLayout.currentMatrix.postScale((float) changScale, (float) changScale,
                        point.x, point.y);

                /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                    KernelLayout.drawViews[i].invalidate();*/
                KernelLayout.drawView.invalidate();
                this.invalidate();
            } else if(getScale() < 0.01){
                double changScale = getScale() / 0.01 - 0.01;
                transformScale /= changScale;
                KernelLayout.currentMatrix.postScale((float) changScale, (float) changScale,
                        point.x, point.y);

                /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                    KernelLayout.drawViews[i].invalidate();*/
                KernelLayout.drawView.invalidate();
                this.invalidate();
            }
            scale = getScale();
            transformScale = scale;
            lastTransformScale = transformScale;
        }
    }
}
