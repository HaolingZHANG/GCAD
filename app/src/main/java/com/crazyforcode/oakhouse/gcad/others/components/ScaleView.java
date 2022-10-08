package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.MapInfo;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ScaleView extends SurfaceView {
    //  底图信息
    private AlgoPoint mapPosition;

    private AlgoPoint lastCenter = new AlgoPoint();
    private double lastDistance;
    private boolean initChange = true;

    //  当前信息
    private AlgoPoint center;
    private double scale = 1;

    private Matrix savedMatrix;
    private Matrix currentMatrix;

    private Bitmap positionPic;

    private boolean isPosition = false;

    private static AlgoPoint positionOne;
    private static AlgoPoint positionTwo;

    private double distanceM = 0;

    public ScaleView(Context context) {
        super(context);
        init();
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
//        设置可以使用onDraw()方法
        this.setWillNotDraw(false);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.setClickable(true);
        positionPic = BitmapFactory.decodeResource(this.getContext().getResources(), R.mipmap.position);
    }

    private void initMatrix() {
//        获取这个图片的宽和高
        double mapWidth = SaveTotalMap.getOriginMap().getWidth();
        double mapHeight = SaveTotalMap.getOriginMap().getHeight();

//        创建操作图片用的matrix对象
        savedMatrix = new Matrix();
        currentMatrix = new Matrix();

//        计算宽高缩放率
        double compressionScale = Math.min((this.getWidth() / mapWidth), (this.getHeight() / mapHeight));
//        缩放图片动作
        currentMatrix.postScale((float) compressionScale, (float) compressionScale);

        float moveX = (float)(this.getWidth() - SaveTotalMap.getOriginMap().getWidth() * compressionScale) / 2;
        float moveY = (float)(this.getHeight() - SaveTotalMap.getOriginMap().getHeight() * compressionScale) / 2;

        mapPosition = new AlgoPoint(moveX, moveY);
        currentMatrix.postTranslate((float)moveX, (float)moveY);
        savedMatrix.set(currentMatrix);

        scale = getScale();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        if(currentMatrix == null)
            initMatrix();

        canvas.drawBitmap(SaveTotalMap.getOriginMap(), currentMatrix, null);

        Paint paint = new Paint();

        float pic_x = positionPic.getWidth() / 2;
        float pic_y = positionPic.getHeight();

        if(positionOne != null && positionTwo != null) {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);

            float x1 = (float)(positionOne.x / scale + mapPosition.x);
            float y1 = (float)(positionOne.y / scale + mapPosition.y);
            float x2 = (float)(positionTwo.x / scale + mapPosition.x);
            float y2 = (float)(positionTwo.y / scale + mapPosition.y);

            canvas.drawLine(x1, y1, x2, y2, paint);

            distanceM = Math.sqrt(Math.pow((x1 - x2) * scale, 2) + Math.pow((y1 - y2) * scale, 2)) / (SaveTotalMap.getWidth() / MapInfo.getMapWidthCM()) / 100.0;

            DecimalFormat df = new DecimalFormat("#.00");
            String number = df.format(distanceM * 100) + "CM";
            if(number.charAt(0) == '.')
                number = '0' + number;

            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            canvas.drawText(number, (float)((x1 + x2) / 2.0 - number.length() / 2.0), (float)((y1 + y2) / 2.0), paint);
        }

        if(positionOne != null) {
            canvas.drawBitmap(positionPic, (float)(positionOne.x / scale + mapPosition.x- pic_x),
                    (float)(positionOne.y / scale + mapPosition.y - pic_y),  paint);
            if(positionTwo != null)
                canvas.drawBitmap(positionPic, (float)(positionTwo.x / scale + mapPosition.x- pic_x),
                        (float)(positionTwo.y / scale + mapPosition.y - pic_y),  paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if(isPosition) {
                    scale = getScale();
                    if(positionOne == null)
                        positionOne = new AlgoPoint((float) ((event.getX() - mapPosition.x) * scale),
                                (float) (((event.getY() - mapPosition.y) * scale)));
                    else if(positionTwo == null)
                        positionTwo = new AlgoPoint((float) ((event.getX() - mapPosition.x) * scale),
                                (float) (((event.getY() - mapPosition.y) * scale)));
                }
                resumeSafetyScale(center);
                resumeSafetyPosition(isSafetyState());
                invalidate();
                ensurePositionAndScale();
                savedMatrix.set(currentMatrix);
                scale = getScale();
                initChange = true;
                isPosition = false;
                lastDistance = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() >= 2) {
                    AlgoPoint figOne = new AlgoPoint(event.getX(0), event.getY(0));
                    AlgoPoint figTwo = new AlgoPoint(event.getX(1), event.getY(1));

                    center = new AlgoPoint((figOne.x + figTwo.x) / 2,
                            (figOne.y + figTwo.y) / 2);
                    double distance = Math.sqrt(Math.pow(figOne.x - figTwo.x, 2)
                            + Math.pow(figOne.y - figTwo.y, 2));

                    if(initChange) {
                        lastDistance = distance;
                        lastCenter = new AlgoPoint(center);
                        initChange = false;
                    }

                    double scaleXY = distance / lastDistance;

                    double moveX = center.x - lastCenter.x;
                    double moveY = center.y - lastCenter.y;

                    currentMatrix.set(savedMatrix);

                    if(Math.abs(moveX) > 10 || Math.abs(moveY) > 10) {
                        currentMatrix.postTranslate((float) moveX, (float) moveY);
                        invalidate();
                    }

                    if(allowChangeScale()) {
                        if(distance - lastDistance > 10 ||distance - lastDistance < -10) {
                            currentMatrix.postScale((float) scaleXY, (float) scaleXY,
                                    (float) center.x, (float) center.y);
                            invalidate();
                        }
                    }
                    ensurePositionAndScale();
                } else
                    isPosition = true;
                break;
        }
        return super.onTouchEvent(event);
    }

    public void resetPosition() {
        positionOne = null;
        positionTwo = null;
        invalidate();
    }

    public AlgoPoint[] getVertex() {
        AlgoPoint[] vertex = new AlgoPoint[4];
        float[] f = new float[9];
        currentMatrix.getValues(f);

        // 图片4个顶点的坐标
        vertex[0] = new AlgoPoint(f[0] * 0 + f[1] * 0 + f[2],
                f[3] * 0 + f[4] * 0 + f[5]);
        vertex[1] = new AlgoPoint(f[0] * SaveTotalMap.getWidth() + f[1] * 0 + f[2],
                f[3] * SaveTotalMap.getWidth() + f[4] * 0 + f[5]);
        vertex[2] = new AlgoPoint(f[0] * 0 + f[1] * SaveTotalMap.getHeight() + f[2],
                f[3] * 0 + f[4] * SaveTotalMap.getHeight() + f[5]);
        vertex[3] = new AlgoPoint(f[0] * SaveTotalMap.getWidth() + f[1] * SaveTotalMap.getHeight()+ f[2],
                f[3] * SaveTotalMap.getWidth() + f[4] * SaveTotalMap.getHeight() + f[5]);

        return vertex;
    }

    public double getScale() {
        AlgoPoint[] vertex = getVertex();

        return this.getWidth() / (vertex[1].x - vertex[0].x);
    }

    public boolean allowChangeScale() {
        scale = getScale();
        return scale >= 0.01 && scale <= 1.5;
    }

    public void ensurePositionAndScale() {
        AlgoPoint[] vertex = getVertex();
        if(vertex != null)
            mapPosition = vertex[0];

        scale = getScale();
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

    public void resumeSafetyPosition(ArrayList<Integer> unvisualPositions) {
        AlgoPoint[] vertex = getVertex();
        double moveX = 0;
        double moveY = 0;

        if(unvisualPositions.size() == 2) {
//        边还原
            if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 2) {
//                还原上边
                moveY = this.getHeight() / 2 - vertex[1].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 2
                    && unvisualPositions.get(1) == 4) {
//                还原右边
                moveX = this.getWidth() / 2 - vertex[1].x;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 3
                    && unvisualPositions.get(1) == 4) {
//                还原下边
                moveY = this.getHeight() / 2 - vertex[2].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 3) {
//                还原左边
                moveX = this.getWidth() / 2 - vertex[2].x;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            }
            invalidate();
        } else if(unvisualPositions.size() == 3) {
//        点还原
            if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 2
                    && unvisualPositions.get(2) == 3) {
//                还原左上角
                moveX = this.getWidth() / 2 - vertex[0].x;
                moveY = this.getHeight() / 2 - vertex[0].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 2
                    && unvisualPositions.get(2) == 4) {
//                还原右上角
                moveX = this.getWidth() / 2 - vertex[1].x;
                moveY = this.getHeight() / 2 - vertex[1].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 1
                    && unvisualPositions.get(1) == 3
                    && unvisualPositions.get(2) == 4) {
//                还原左下角
                moveX = this.getWidth() / 2 - vertex[2].x;
                moveY = this.getHeight() / 2 - vertex[2].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            } else if(unvisualPositions.get(0) == 2
                    && unvisualPositions.get(1) == 3
                    && unvisualPositions.get(2) == 4) {
//                还原右下角
                moveX = this.getWidth() / 2 - vertex[3].x;
                moveY = this.getHeight() / 2 - vertex[3].y;
                currentMatrix.postTranslate((float)moveX, (float)moveY);
            }
            invalidate();
        }
    }

    public void resumeSafetyScale(AlgoPoint point) {
        if(getScale() > 1.5) {
            double changScale = getScale() / 1.5 + 0.01;
            currentMatrix.postScale((float) changScale, (float) changScale,
                    (float) point.x, (float) point.y);
            invalidate();
        } else if(getScale() < 0.01){
            double changScale = getScale() / 0.01 - 0.01;
            currentMatrix.postScale((float) changScale, (float) changScale,
                    (float) point.x, (float) point.y);
            invalidate();
        }
        scale = getScale();
    }

    public double getDistanceM() {
        return distanceM;
    }
}
