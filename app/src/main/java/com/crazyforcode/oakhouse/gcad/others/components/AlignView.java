package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.window.AlignActivity;

public class AlignView extends SurfaceView {

    private static double totalRotate = 0;

    private Matrix savedMatrix;
    private Matrix currentMatrix;

    private double lastRotate;
    private double changeRotate;
    private boolean initChange = true;

    private static int screenWidth;
    private static int screenHeight;

    public AlignView(Context context) {
        super(context);
        init();
    }

    public AlignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setWillNotDraw(false);
        this.setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(SaveTotalMap.getOriginMap() != null) {
            canvas.drawBitmap(SaveTotalMap.getOriginMap(), currentMatrix, null);
            drawForm(canvas);
            drawCenter(canvas);
        }
    }

    private void drawForm(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        PathEffect effects = new DashPathEffect(new float[] {10, 10}, 20);
        paint.setPathEffect(effects);

        int width = this.getWidth() / 10;
        int height = this.getHeight() / 10;
        Path path = new Path();

        for(int i = 1; i <= 9; i++) {
            path.moveTo(width * i, this.getHeight() / 2);
            path.lineTo(width * i, 0);
            canvas.drawPath(path, paint);
            path.moveTo(width * i, this.getHeight() / 2);
            path.lineTo(width * i, this.getHeight());
            canvas.drawPath(path, paint);
        }

        for(int i = 1; i <= 9; i++) {
            path.moveTo(this.getWidth() / 2, height * i);
            path.lineTo(0, height * i);
            canvas.drawPath(path, paint);
            path.moveTo(this.getWidth() / 2, height * i);
            path.lineTo(this.getWidth(), height * i);
            canvas.drawPath(path, paint);
        }
    }

    private void drawCenter(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setColor(Color.RED);

        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, 8, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_UP:
                initChange = true;
                savedMatrix.set(currentMatrix);
                totalRotate += changeRotate;

                if(totalRotate >= 360)
                    totalRotate %= 360;
                else if(totalRotate < - 360) {
                    totalRotate = Math.abs(totalRotate);
                    totalRotate %= 360;
                    totalRotate = 360 - totalRotate;
                }

                if(totalRotate > 180)
                    totalRotate = totalRotate - 360;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 1) {
                    AlgoPoint fig = new AlgoPoint(event.getX(), event.getY());

                    double k = Math.abs((fig.x - this.getWidth() / 2) / (fig.y - this.getHeight() / 2));

                    double rotate;
//                    第一象限
                    if(fig.x > this.getWidth() / 2 && fig.y < this.getHeight() / 2)
                        rotate = Math.toDegrees(Math.atan(k));
//                    第二象限
                    else if(fig.x > this.getWidth() / 2 && fig.y > this.getHeight() / 2)
                        rotate = 180 - Math.toDegrees(Math.atan(k));
//                    第三象限
                    else if(fig.x < this.getWidth() / 2 && fig.y > this.getHeight() / 2)
                        rotate = 180 + Math.toDegrees(Math.atan(k));
//                    第四象限
                    else
                        rotate = 360 - Math.toDegrees(Math.atan(k));

                    if(initChange) {
                        lastRotate = rotate;
                        initChange = false;
                    }

                    currentMatrix.set(savedMatrix);

                    if (Math.abs(rotate - lastRotate) > 1) {
                        currentMatrix.postRotate((float) (rotate - lastRotate),
                                this.getWidth() / 2, this.getHeight() / 2);
                        changeRotate = rotate - lastRotate;
                    }

                    double degree =  totalRotate + changeRotate;

                    if(degree >= 360)
                        degree %= 360;
                    else if(degree < - 360) {
                        degree = Math.abs(degree);
                        degree %= 360;
                        degree = 360 - degree;
                    }

                    if(degree > 180)
                        degree = degree - 360;

                    AlignActivity.setDegree(degree);

                    invalidate();
                }
        }
        return super.onTouchEvent(event);
    }

    public double getTotalRotate() {
        return totalRotate;
    }

    public void setMatrix() {
        try {
            screenWidth = this.getWidth();
            screenHeight = this.getHeight();
            currentMatrix = new Matrix();
            savedMatrix = new Matrix();
//        获取这个图片的宽和高
            double mapWidth = SaveTotalMap.getOriginMap().getWidth();
            double mapHeight = SaveTotalMap.getOriginMap().getHeight();
//        计算宽高缩放率
            double compressionScale = Math.min((screenWidth / mapWidth), (screenHeight / mapHeight));
//        缩放图片动作
            savedMatrix.postScale((float) compressionScale, (float) compressionScale);

            double moveX = (screenWidth - SaveTotalMap.getOriginMap().getWidth() * compressionScale) / 2;
            double moveY = (screenHeight - SaveTotalMap.getOriginMap().getHeight() * compressionScale) / 2;
            savedMatrix.postTranslate((float) moveX, (float) moveY);

            currentMatrix.set(savedMatrix);

            invalidate();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void littleLeft() {
        totalRotate -= 0.1;

        if(totalRotate >= 360)
            totalRotate %= 360;
        else if(totalRotate < - 360) {
            totalRotate = Math.abs(totalRotate);
            totalRotate %= 360;
            totalRotate = 360 - totalRotate;
        }

        if(totalRotate > 180)
            totalRotate = totalRotate - 360;

        currentMatrix.postRotate((float) (-0.1),
                this.getWidth() / 2, this.getHeight() / 2);
        savedMatrix.set(currentMatrix);
        invalidate();

        AlignActivity.setDegree(totalRotate);
    }

    public void littleRight() {
        totalRotate += 0.1;

        if(totalRotate >= 360)
            totalRotate %= 360;
        else if(totalRotate < - 360) {
            totalRotate = Math.abs(totalRotate);
            totalRotate %= 360;
            totalRotate = 360 - totalRotate;
        }

        if(totalRotate > 180)
            totalRotate = totalRotate - 360;

        currentMatrix.postRotate((float) (0.1),
                this.getWidth() / 2, this.getHeight() / 2);
        savedMatrix.set(currentMatrix);
        invalidate();

        AlignActivity.setDegree(totalRotate);
    }

    public void reset() {
        setMatrix();
        totalRotate = 0;
        AlignActivity.setDegree(totalRotate);
        invalidate();
    }
}