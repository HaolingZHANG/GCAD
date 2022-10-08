package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.MapInfo;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.ObtainTotalMapRunnable;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MThreadPool;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.FinalMapOutputRunnable;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.others.surfaces.FinalMapActivity;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import java.io.File;
import java.util.ArrayList;

public class EditView extends SurfaceView {

    private Bitmap finalMap;

    private double changeScale = 1;

    private Matrix savedMatrix;
    private Matrix currentMatrix;

    private AlgoPoint borderPoint;
    private AlgoPoint lastCenter;
    private AlgoPoint mapPosition;

    private boolean needTitle = false;
    private boolean needNorth = false;
    private boolean needScale = false;
    private boolean initChange = true;

    public EditView(Context context) {
        super(context);
        init();
    }

    public EditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setWillNotDraw(false);
        this.setClickable(true);
        initFinalMap();
        initLimit();
        initMatrix();
    }

    private void initFinalMap() {
        finalMap = Bitmap.createBitmap(SaveTotalMap.getOriginMap().getWidth(),
                SaveTotalMap.getOriginMap().getHeight(), SaveTotalMap.getOriginMap().getConfig());

        Canvas canvas = new Canvas(finalMap);

        Paint paint = new Paint();

//        设置为上图重叠下图的模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        canvas.drawBitmap(SaveTotalMap.getOriginMap(), 0, 0, paint);

        /*if(KernelLayout.drawViews[0].background != null) {
            for (int i = 0; i < KernelLayout.drawViews.length - 1; i++)
                canvas.drawBitmap(KernelLayout.drawViews[i].background, null, new RectF(0, 0, finalMap.getWidth(), finalMap.getHeight()), paint);
        }*/
        for (int i = 0; i < KernelLayout.LEVEL_NUM - 1; i++)
            if (KernelLayout.drawView.backgrounds[i] != null)
                canvas.drawBitmap(KernelLayout.drawView.backgrounds[i], null, new RectF(0, 0,
                        finalMap.getWidth(), finalMap.getHeight()), paint);
    }

    private void initLimit() {
        if(this.getWidth() != 0 && this.getHeight() != 0) {
            if(this.getWidth() / 21.0 == this.getHeight() / 29.7)
                borderPoint = new AlgoPoint(0, 0);
            else if(this.getWidth() / 21.0 > this.getHeight() / 29.7)
                borderPoint = new AlgoPoint((float) (this.getWidth() - (this.getHeight() / 29.7 * 21)) / 2, 0);
            else
                borderPoint = new AlgoPoint(0, (float) (this.getHeight() - (this.getWidth() / 21.0 * 29.7)) / 2);
        }
    }

    private void initMatrix() {
        if(this.getWidth() != 0 && this.getHeight() != 0) {
//            创建操作图片用的matrix对象
            currentMatrix = new Matrix();
            savedMatrix = new Matrix();

            double cm21 = this.getWidth() - borderPoint.x * 2;

//            计算宽高缩放率
            double compressionScale = finalMap.getWidth() / cm21 * (MapInfo.getMapWidthCM() / 21.0) * Math.sqrt(ObtainTotalMapRunnable.getSize());
//            缩放图片动作
            currentMatrix.postScale((float) compressionScale, (float) compressionScale);
//
            double moveX = (this.getWidth() - finalMap.getWidth() * compressionScale) / 2;
            double moveY = (this.getHeight() - finalMap.getHeight() * compressionScale) / 2;

            currentMatrix.postTranslate((float)moveX, (float)moveY);
            savedMatrix.set(currentMatrix);

            AlgoPoint[] vertex = getVertex();
            if(vertex != null)
                mapPosition = vertex[0];
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);
        if(currentMatrix != null) {
            canvas.drawBitmap(finalMap, currentMatrix, null);
            drawLimit(canvas);
        } else {
            initLimit();
            initMatrix();
        }
    }

    private void drawLimit(Canvas canvas) {
//        根据比例尺裁成A4纸张大小
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DADBDA"));

        if(borderPoint.x != 0) {
            canvas.drawRect(0, 0,(float)borderPoint.x, this.getHeight(), paint);
            canvas.drawRect(this.getWidth() - (float)borderPoint.x, 0, this.getWidth(), this.getHeight(), paint);
        } else if(borderPoint.y != 0) {
            canvas.drawRect(0, 0, this.getWidth(), (float)borderPoint.y, paint);
            canvas.drawRect(0, this.getHeight() - (float)borderPoint.y, this.getWidth(), this.getHeight(), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                resumeSafetyPosition(isSafetyState());
                AlgoPoint[] vertex = getVertex();
                mapPosition = vertex[0];
                savedMatrix.set(currentMatrix);
                initChange = true;
                return true;
            case MotionEvent.ACTION_DOWN:
                if(FinalMapActivity.isVisible())
                    FinalMapActivity.hideButton();
                else
                    FinalMapActivity.showButton();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() >= 2) {
                    AlgoPoint figOne = new AlgoPoint(event.getX(0), event.getY(0));
                    AlgoPoint figTwo = new AlgoPoint(event.getX(1), event.getY(1));

                    AlgoPoint center = new AlgoPoint((figOne.x + figTwo.x) / 2,
                            (figOne.y + figTwo.y) / 2);

                    if(initChange) {
                        lastCenter = new AlgoPoint(center);
                        initChange = false;
                    }

                    double moveX = center.x - lastCenter.x;
                    double moveY = center.y - lastCenter.y;

                    currentMatrix.set(savedMatrix);

                    if(Math.abs(moveX) > 10 || Math.abs(moveY) > 10) {
                        currentMatrix.postTranslate((float)moveX, (float)moveY);
                        invalidate();
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void addTitle() {
        needTitle = true;
    }

    public void removeTitle() {
        needTitle = false;
    }

    public boolean needTitle() {
        return needTitle;
    }

    public void addNorth() {
        needNorth = true;
    }

    public void removeNorth() {
        needNorth = false;
    }

    public boolean needNorth() {
        return needNorth;
    }

    public void addScale() {
        needScale = true;
    }

    public void removeScale() {
        needScale = false;
    }

    public boolean needScale() {
        return needScale;
    }

    public void setChangeScale(double scale) {
        /**
         * 图片选中比例的变化
         * 当前打印出来的图片宽度/初始化时期打印出来的图片宽度
         * */
        currentMatrix.postScale((float)scale, (float)scale, this.getWidth() / 2, this.getHeight() / 2);
        savedMatrix.set(currentMatrix);

        changeScale /= scale;
        Log.i("当前的比例尺：初始化的比例尺", changeScale + "：1");
        invalidate();
    }

    private AlgoPoint[] getVertex() {
        AlgoPoint[] vertex = new AlgoPoint[4];
        float[] f = new float[9];
        currentMatrix.getValues(f);

        // 图片4个顶点的坐标
        vertex[0] = new AlgoPoint(f[0] * 0 + f[1] * 0 + f[2],
                f[3] * 0 + f[4] * 0 + f[5]);
        vertex[1] = new AlgoPoint(f[0] * finalMap.getWidth() + f[1] * 0 + f[2],
                f[3] * finalMap.getWidth() + f[4] * 0 + f[5]);
        vertex[2] = new AlgoPoint(f[0] * 0 + f[1] * finalMap.getHeight() + f[2],
                f[3] * 0 + f[4] * finalMap.getHeight() + f[5]);
        vertex[3] = new AlgoPoint(f[0] * finalMap.getWidth() + f[1] * finalMap.getHeight() + f[2],
                f[3] * finalMap.getWidth() + f[4] * finalMap.getHeight() + f[5]);

        return vertex;
    }

    private ArrayList<Integer> isSafetyState() {
        ArrayList<Integer> state = new ArrayList<>(4);
        AlgoPoint[] vertex = getVertex();

//        左上角
        if(vertex[0].x > (this.getWidth() / 2)
                || vertex[0].y > (this.getHeight() / 2))
            state.add(1);
//        右上角
        if(vertex[1].x < (this.getWidth() / 2)
                || vertex[1].y > (this.getHeight() / 2))
            state.add(2);
//        左下角
        if(vertex[2].x > (this.getWidth() / 2)
                || vertex[2].y < (this.getHeight() / 2))
            state.add(3);
//        右下角
        if(vertex[3].x < (this.getWidth() / 2)
                || vertex[3].y < (this.getHeight() / 2))
            state.add(4);

//        是安全位置
        if(state.size() == 0)
            state.add(0);

        return state;
    }

    private void resumeSafetyPosition(ArrayList<Integer> unusualPositions) {
        AlgoPoint[] vertex = getVertex();
        double moveX = 0;
        double moveY = 0;

        if(unusualPositions.size() == 2) {
//            边还原
            if(unusualPositions.get(0) == 1
                    && unusualPositions.get(1) == 2) {
//                还原上边
                moveY = this.getHeight() / 2 - vertex[1].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unusualPositions.get(0) == 2
                    && unusualPositions.get(1) == 4) {
//                还原右边
                moveX = this.getWidth() / 2 - vertex[1].x;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unusualPositions.get(0) == 3
                    && unusualPositions.get(1) == 4) {
//                还原下边
                moveY = this.getHeight() / 2 - vertex[2].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unusualPositions.get(0) == 1
                    && unusualPositions.get(1) == 3) {
//                还原左边
                moveX = this.getWidth() / 2 - vertex[2].x;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            }
            invalidate();
        } else if(unusualPositions.size() == 3) {
//            点还原
            if(unusualPositions.get(0) == 1
                    && unusualPositions.get(1) == 2
                    && unusualPositions.get(2) == 3) {
//                还原左上角
                moveX = this.getWidth() / 2 - vertex[0].x;
                moveY = this.getHeight() / 2 - vertex[0].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unusualPositions.get(0) == 1
                    && unusualPositions.get(1) == 2
                    && unusualPositions.get(2) == 4) {
//                还原右上角
                moveX = this.getWidth() / 2 - vertex[1].x;
                moveY = this.getHeight() / 2 - vertex[1].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unusualPositions.get(0) == 1
                    && unusualPositions.get(1) == 3
                    && unusualPositions.get(2) == 4) {
//                还原左下角
                moveX = this.getWidth() / 2 - vertex[2].x;
                moveY = this.getHeight() / 2 - vertex[2].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unusualPositions.get(0) == 2
                    && unusualPositions.get(1) == 3
                    && unusualPositions.get(2) == 4) {
//                还原右下角
                moveX = this.getWidth() / 2 - vertex[3].x;
                moveY = this.getHeight() / 2 - vertex[3].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            }
            invalidate();
        }
    }

    public void putInDir(String outputPath) {

        if(!(new File(outputPath).exists()))
            TextToast.showTextToast("导出路径设置有误", getContext());
        else if(!canPut()) {
            TextToast.showTextToast("图片不在A4纸内", getContext());
        } else {
            AlgoPoint mapPositionForA4 = new AlgoPoint(mapPosition.x + borderPoint.x, mapPosition.y + borderPoint.y);

            MHandler mHandler = new MHandler(getContext());

            FinalMapOutputRunnable mapOutputRunnable = new FinalMapOutputRunnable(mHandler, finalMap,
                            changeScale, mapPositionForA4,  outputPath + "/" + MainActivity.getProjectName() + ".jpg");

//            将该线程放入线程池并执行
            MThreadPool.addTask(mapOutputRunnable);
            MThreadPool.start();
        }
    }

    private boolean canPut() {
        return (MapInfo.getMapWidthCM() / changeScale <= 21.0
                && MapInfo.getMapHeightCM() / changeScale <= 29.7)
                && isSafety();
    }

    private boolean isSafety() {
        AlgoPoint[] vertex = getVertex();

//        左上角
        if(vertex[0].x < borderPoint.x
                || vertex[0].y < borderPoint.y)
            return false;
//        右上角
        else if(vertex[1].x > (this.getWidth() - borderPoint.x)
                || vertex[1].y < borderPoint.y)
            return false;
//        左下角
        else if(vertex[2].x < borderPoint.x
                || vertex[2].y > (this.getHeight() - borderPoint.y))
            return false;
//        右下角
        else if(vertex[3].x > (this.getWidth() - borderPoint.x)
                || vertex[3].y > (this.getHeight() - borderPoint.y))
            return false;

        return true;
    }
}
