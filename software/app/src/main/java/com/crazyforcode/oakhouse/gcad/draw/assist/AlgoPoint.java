package com.crazyforcode.oakhouse.gcad.draw.assist;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DecimalFormat;

@SuppressLint("ParcelCreator")
public class AlgoPoint extends PointF {
    public static final int CLICK_TOLERANCE = 10;//pixiv
    //private double x;
    //private double y;
    private int count;
    private boolean head = false;

    public AlgoPoint()
    {
        this(0, 0);
    }

    public AlgoPoint(float x, float y)
    {
        super(x, y);
    }

    public AlgoPoint(AlgoPoint point)
    {
        super(point.x, point.y);
    }

    /*super already have this method:
    public void setPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
*/
    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

/*
    super x,y is public
    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }
*/

    public void setScale(double scale) {
        this.x *= scale;
        this.y *= scale;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public int getCount()
    {
        return count;
    }

    public void setAsHead(boolean isHead) {
        this.head = isHead;
    }

    public boolean isHead() {
        return this.head;
    }

    public boolean anear(AlgoPoint point2) {
        Log.i("anear::::::", "point2x:"+point2.x+"::point2Y"+point2.y);
        Log.i("anear::::::", "thisx:"+this.x+"::"+this.y);
        if (Math.hypot(this.x - point2.x, this.y - point2.y) < CLICK_TOLERANCE)
            return true;
        else
            return false;
    }

    public double distance(AlgoPoint point) {
        return distance(this, point);
    }

    public static <E extends PointF> double distance(E p1, E p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
        /*Math.sqrt(Math.pow(p1.x - p2.x, 2)
                + Math.pow(p1.y - p2.y, 2));*/
    }

    /**点相对直线的对称点*/
    public static AlgoPoint symmetric(AlgoPoint endPoint1, AlgoPoint endPoint2, AlgoPoint point) {
        //A = y2 - y1, B = x1 - x2, C = x2*y1 - x1*y2
        //点到直线距离|Ax+By+C|/√A方+B方
        //k = y2 - y1 / x2 - x1
        //xside = slopeside*1/√1+k方, yside = slopeside*k/√1+k方
        double distance = /*Math.abs*/((endPoint2.y - endPoint1.y) * point.x
                + (endPoint1.x - endPoint2.x) * point.y
                + (endPoint2.x * endPoint1.y - endPoint1.x * endPoint2.y))
                / Math.sqrt(Math.pow(endPoint2.y - endPoint1.y, 2)
                + Math.pow(endPoint1.x - endPoint2.x, 2));
        double vertSlope = -1/((endPoint2.y - endPoint1.y)/(endPoint2.x - endPoint1.x));

        return new AlgoPoint(point.x + (float) (distance * 2 / Math.sqrt(1 + vertSlope*vertSlope)),
                point.y + (float) (distance * 2 * vertSlope / Math.sqrt(1 + vertSlope*vertSlope)));
    }

    /**p1 ot p2 is one line, p2 ot p3 is the other, return x/bevel,y/bevel
     * @param p1 one of end-point of angle;
     * @param p2 the angle vertex;
     * @param p3 one of endpoint of angle;
     * @return (The slope of the straight line connecting two points
     *  to
     *  Perpendicular bisectors of a single line between two points)
     * or the slope of the center line of the angle formed by three-point,
     * element 'head' marks is the fx of the line positive.
     * ( The second parameter is the angle vertex )*/
    public static AlgoPoint bisector(@Nullable AlgoPoint p1, AlgoPoint p2, @Nullable AlgoPoint p3) {
        if (p2 == null)
            return null;

        AlgoPoint cos = new AlgoPoint();
        float x = 0.0f, y = 0.0f;//length x, length y
        try{
            if (p1 == null) {
                x = p3.x - p2.x;
                y = p3.y - p2.y;
                cos.setAsHead((p3.y - p2.y) / (p3.x - p2.x) > 0);
            } else if (p3 == null) {
                x = p2.x - p1.x;
                y = p2.y - p1.y;
                cos.setAsHead((p2.y - p1.y) / (p2.x - p1.x) > 0);
            } else {
                x = (p1.x + p2.x) / 2 - p3.x;
                y = (p1.y + p2.y) / 2 - p3.y;
                cos.setAsHead((p3.y - y) / (p3.x - x) > 0);
            }

            double bevel = Math.hypot(x, y);
            /*cos.setX(Double.isNaN(x / bevel) ? 1f : (float) (x / bevel));
            cos.setY(Double.isNaN(y / bevel) ? 1f : (float) (y / bevel));*/
            /*cos.set(Double.isNaN(x / bevel) ? 1f : (float) (x / bevel),
                    Double.isNaN(y / bevel) ? 1f : (float) (y / bevel));*/
            cos.set(cos.isHead() ? (float)(-y / bevel) : (float) (y / bevel),
                    (float) (x / bevel));//perpendicular
        }catch (Exception ex){
            ex.printStackTrace();//TODO isNaNException???
            Log.i("isNaN or infinity", ex + "");
            cos.set(1f, 1f);
        }

        return cos;
    }

    public AlgoPoint copy() {
        AlgoPoint copyOne = new AlgoPoint(x, y);
        copyOne.setAsHead(this.isHead());
        copyOne.setCount(this.count);

        return copyOne;
    }

    public void set(AlgoPoint point) {
        this.x = point.x;
        this.y = point.y;
        this.head = point.isHead();
        this.count = point.getCount();
    }

    public String toString() {
        DecimalFormat df = new DecimalFormat("#.00000");
        String xString = df.format(x);
        String yString = df.format(y);
        return "(" + xString + "," + yString + ")";
    }

    public void toPoint(String string) {
        string = string.substring(1, string.length() - 1);

        x = Float.parseFloat(string.substring(0, string.indexOf(',')));
        y = Float.parseFloat(string.substring(string.indexOf(',') + 1));
    }
}
