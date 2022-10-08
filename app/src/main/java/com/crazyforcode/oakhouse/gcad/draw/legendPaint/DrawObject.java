package com.crazyforcode.oakhouse.gcad.draw.legendPaint;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class DrawObject  implements Comparator<DrawObject> {
    //TODO paint.setStrokeJoin ROUND 转角圆滑
    protected ArrayList<AlgoPoint> points ;//save point for map
    protected int sign;
    protected int weight;
    //private int size = 0;
//    public CurveControl curve = null;
    protected float[] bounds = new float[]{Float.MAX_VALUE,
            Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};//TODO region代替
    public static final int BOUNDS_TOP = 0,
    BOUNDS_LEFT = 1,
    BOUNDS_BOTTOM = 2,
    BOUNDS_RIGHT = 3;

    protected static float mmTrans = 1f;
    protected static Context context = null;

    //public abstract void Draw();
    protected void initRes() {
        /*if (context == null && KernelLayout.drawViews[0] != null)
            context = KernelLayout.drawViews[0].getContext();*/
        context = KernelLayout.drawView.getContext();
    }
    public abstract void setSign (int sign);
    protected abstract void setStyle();
    public abstract void zoomStyle(boolean isForMap);
    public abstract int getSign ();
    public abstract int getWeight ();

/*    //only for non-straight object, curve is the key judge is a object straight or curve(/free);
    public void initCurve() {
        if (curve == null)
            return;

        if (!curve.isEmpty()) {
            curve.clean();
        }

        AlgoPoint bis = null;
        double distan1 = 0, distan2 = 0;
        for (int i = 1; i + 1 < size; i++) {
            if (i == 1) {
                curve.addOneSideBezier(getPoint(i).getX(), getPoint(i).getY(), CurveControl.TO);
                curve.addOneSideBezier(getPoint(i).getX(), getPoint(i).getY(), CurveControl.FROM);
                continue;
            }

            bis = AlgoPoint.bisector(getPoint(i - 1), getPoint(i), getPoint(i + 1));
            distan1 = AlgoPoint.length(getPoint(i - 1), getPoint(i));
            distan2 = AlgoPoint.length(getPoint(i), getPoint(i + 1));
            if (i - 1 == 0) {
                curve.addOneSideBezier(getPoint(i - 1).getX()
                                - distan1 * bis.getY(),
                        getPoint(i - 1).getY()
                                - distan1 * bis.getX(),
                        CurveControl.FROM);
                curve.addOneSideBezier(getPoint(i - 1).getX(),
                        getPoint(i - 1).getY(),
                        CurveControl.TO);
            }

            curve.addOneSideBezier(getPoint(i).getX() - distan1 * bis.getY(),
                    getPoint(i).getY() - distan1 * bis.getX(),
                    CurveControl.TO);
            curve.addOneSideBezier(getPoint(i).getX() + distan2 * bis.getY(),
                    getPoint(i).getY() + distan2 * bis.getY(),
                    CurveControl.FROM);

            if (i + 1 == size - 1) {
                curve.addOneSideBezier(getPoint(i + 1).getX() - distan2 * bis.getY(),
                        getPoint(i).getY() - distan2 * bis.getX(),
                        CurveControl.TO);
                curve.addOneSideBezier(getPoint(i + 1).getX(),
                        getPoint(i + 1).getY(),
                        CurveControl.FROM);
            }
        }
    }*/

    public void addPoint (AlgoPoint point) {
        Log.i("add Point", "pointX"+point.x+":::pointY"+point.y);
        addPoint(getSize(), point);
    }

    public void addPoint (int index, AlgoPoint point) {
        points.add(index, point);
        //size++;
        if (point.x < bounds[BOUNDS_TOP])
            bounds[BOUNDS_TOP] = point.x;
        if (point.x > bounds[BOUNDS_BOTTOM])
            bounds[BOUNDS_BOTTOM] = point.x;

        if (point.y < bounds[BOUNDS_LEFT])
            bounds[BOUNDS_LEFT] = point.y;
        if (point.y > bounds[BOUNDS_RIGHT])
            bounds[BOUNDS_RIGHT] = point.y;
    }

    public AlgoPoint removePoint (int index) {
        if (index < getSize()) {
            //this.size--;
            return points.remove(index);
        } else {
            return null;
        }
    }

    public AlgoPoint popPoint () {
        return points.remove(getSize() - 1);
    }
    public AlgoPoint peekPoint() { return points.get(getSize() - 1);}


    /**bounds for map*/
    public float getBound(int boundIndex) {
        return bounds[boundIndex];
    }
    public int getSize() {
        return points.size();
    }
    public AlgoPoint getPoint(int index) {
        return points.get(index);
    }
    //public abstract boolean isHead();
    //public abstract void setAsHead(boolean isHead);

    public abstract @Nullable Path getPathMain ();
    public abstract Paint getPaintMain ();
    public abstract @Nullable Paint getPaintAuxiliary ();
    public abstract void drawSelf (Canvas canvas/*, AlgoPoint screenXY*/);//given point for screen
    public abstract void drawSelf (Canvas canvas, @Nullable Paint mainCopy, @Nullable Paint auxiliCopy);
    public abstract void drawSelf (Canvas canvas, @Nullable Paint mainCopy, @Nullable Paint auxiliCopy, DrawObjects.CanvasTarget isForMap);

    @Override
    public int compare(DrawObject lhs, DrawObject rhs) {
        int signLhs = lhs.getSign();
        int signRhs = rhs.getSign();
        //TODO compare

        return 0;
    }
}
