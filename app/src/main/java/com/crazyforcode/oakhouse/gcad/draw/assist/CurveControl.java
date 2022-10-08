package com.crazyforcode.oakhouse.gcad.draw.assist;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/14.
 * 一个cubicTo()的两个控制点，分别控制path的当前点和end点，坐标分别相对这两个点考虑
 * 上一个点endX，Y的bezier To ,该点(起始)的bezier From
 * Bezier 还是 Bezier角点？
 * 多出来无用的对象：开头的点和结尾的点本来就只有单边bezier，bezier点集合中却依然存了一对
 */
public class CurveControl {
    //boolean isCurl=false;//draw object有这个对象就是曲线
    //public ArrayList<AlgoPoint> bezierL = new ArrayList<AlgoPoint>();
    //public ArrayList<AlgoPoint> bezierR = new ArrayList<AlgoPoint>();
    private static final Paint stickPaint1, stickPaint2;
//    public static final byte FROM = 1,
//                            TO = 0;
    public enum Direction {FROM, TO}

    public ArrayList<AlgoPoint> bezierF  = new ArrayList<AlgoPoint>(),
                        bezierT = new ArrayList<AlgoPoint>();//TODO private this
    private int size = 0;//size = drawObject.points.size
//    public static Path pathCache = new Path();
    boolean isSecond = false;

    static {
        stickPaint1 = new Paint();
        stickPaint1.setStyle(Paint.Style.STROKE);
        stickPaint2 = new Paint();
        stickPaint2.setColor(Color.GREEN);
        stickPaint2.setStyle(Paint.Style.FILL);
    }

    public boolean editBezier(float fromX, float fromY, float toX, float toY, int index){//TODO not complete yet

        return false;
    }

    public void addOneSideBezier (float onesideX, float onesideY, Direction fromOrTo) {
        if (fromOrTo == Direction.FROM) {//(fromOrTo ^ FROM) == 0
            bezierF.add(new AlgoPoint(onesideX, onesideY));
        } else {
            bezierT.add(new AlgoPoint(onesideX, onesideY));
            size++;//随便啦反正两边都公用size，左右点都会同时加
        }

    }

    public void insOneSideBez(float onesideX, float onesideY, Direction fromOrTo, int index) {
        if (fromOrTo == Direction.FROM) {//(fromOrTo ^ FROM) == 0
            bezierF.add(index, new AlgoPoint(onesideX, onesideY));
        } else {
            bezierT.add(index, new AlgoPoint(onesideX, onesideY));
            size++;//随便啦反正两边都公用size，左右点都会同时加
        }
    }

    //绘制(增)时用;from is true/1;
    public void editOneSideBezier (float onesideX, float onesideY, Direction fromOrTo) {
        AlgoPoint p = null;
        if (fromOrTo == Direction.FROM) {//from: (fromOrTo ^ FROM) == 0
            p = bezierF.get(size - 1);
        } else {//to
            p = bezierT.get(size - 1);
        }

        p.set(onesideX, onesideY);
    }

    //修改时用
    public void editOneSideBez (float onesideX, float onesideY, Direction fromOrTo, int index) {
        if (fromOrTo == Direction.FROM) {//from: (fromOrTo ^ FROM) == 0
            bezierF.get(index).set(onesideX, onesideY);
        } else {//to
            bezierT.get(index).set(onesideX, onesideY);
        }
    }

    public boolean isSecond () {
        return isSecond;
    }
    public void setAsSecond (boolean second) {
        this.isSecond = second;
    }

    public static void drawCurControl(Canvas canvas, AlgoPoint body, AlgoPoint stick) {
        //trans to for screen, curve joy stick only show when handle
        float bodyX = KernelLayout.toScreenLocation(body.x, KernelLayout.COOX),
                bodyY =KernelLayout.toScreenLocation(body.y, KernelLayout.COOY);

        drawCurControl(canvas, bodyX, bodyY, stick);
    }

    //bodyX & bodyY is for screen
    public static void drawCurControl(Canvas canvas, float bodyX, float bodyY, AlgoPoint stick) {
        float stickX =KernelLayout.toScreenLocation(stick.x, KernelLayout.COOX),
                stickY =KernelLayout.toScreenLocation(stick.y, KernelLayout.COOY);

        canvas.drawLine(bodyX, bodyY, stickX, stickY, stickPaint1);
        canvas.drawRect(stickX - 5, stickY - 5, stickX + 5, stickY + 5, stickPaint2);
        canvas.drawRect(stickX - 5, stickY - 5, stickX + 5, stickY + 5, stickPaint1);
    }

    public void clean() {
        bezierT.clear();
        bezierF.clear();
    }

    public @Nullable
    AlgoPoint getControlPoint(int index, Direction fromOrTo) {
        try {
            if (fromOrTo == Direction.FROM) {
                return bezierF.get(index);
            }else {
                return bezierT.get(index);
            }
        }catch (IndexOutOfBoundsException ex) {
            return null;//curve size == drawObject size
        }
    }

    public @Nullable
    AlgoPoint removeControlPoint(int index, Direction fromOrTo) {
        try {
            if (fromOrTo == Direction.FROM) {
                return bezierF.remove(index);
            }else {
                return bezierT.remove(index);
            }
        }catch (IndexOutOfBoundsException ex) {
            return null;//curve size == drawObject size
        }
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }


}
