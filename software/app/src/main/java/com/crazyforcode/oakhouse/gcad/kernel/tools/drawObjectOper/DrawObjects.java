package com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.draw.assist.CurveControl;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.AreaObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.EdgeObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.LineObject;
import com.crazyforcode.oakhouse.gcad.kernel.views.DrawView;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;
import com.crazyforcode.oakhouse.gcad.others.components.LayerPicker;
import com.crazyforcode.oakhouse.gcad.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawObjects {

    public static final int NONE = 0;
    public static final int SPOT = 1;
    public static final int EDGE = 2;
    public static final int LINE = 3;
    public static final int AREA = 4;
    //public enum Shape{NONE, SPOT,EDGE, LINE, AREA}

    public static final int DRAW_TYPE_STRAIGHT = 4;
    public static final int DRAW_TYPE_CURL = 5;
    public static final int DRAW_TYPE_FREELINE = 6;

    private static final Paint drawingPaint = new Paint();
    private static final Paint editingPaint = new Paint();

    public static final AlgoPoint begin = new AlgoPoint(-1, -1),
            end = new AlgoPoint(-1, -1);

    /**当绘制当前对象时，画布应用了Matrix缩放比，所以要将间距线宽除以缩放值。
     * 绘制当底图上时，底图是抽取的整张Bitmap，没有缩放。*/
    public enum CanvasTarget {FORMAP (true), FORSCREEN(false);

         CanvasTarget(boolean isForMap) {
             this.isForMap = isForMap;
        }

        private final boolean isForMap;

        public boolean value(){
            return isForMap;
        }
    }

    static {
        initDrawingPaint();
        initEditingPaint();
    }

    public static void initDrawingPaint() {
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setColor(Color.BLACK);
        drawingPaint.setStrokeWidth(2f);//default
        drawingPaint.setAntiAlias(true);
        drawingPaint.setShadowLayer(3, 0, 0, Color.WHITE);
    }

    public static void initEditingPaint() {
        editingPaint.setStyle(Paint.Style.STROKE);
        editingPaint.setColor(
                KernelLayout.mapView.getContext().getResources().getColor(R.color.theme_thin_warm));
        editingPaint.setStrokeWidth(5f);
        editingPaint.setStrokeCap(Paint.Cap.ROUND);
        editingPaint.setAlpha(200);
        editingPaint.setAntiAlias(true);
    }

    public static Paint getDrawingPaint() {
        if (drawingPaint == null)
            initDrawingPaint();

        return drawingPaint;
    }

    public static Paint getEditingPaint() {
        if (editingPaint == null)
            initEditingPaint();

        return editingPaint;
    }

    public static int getObjectType(int sign) {

        if(isLine(sign))
            return LINE;
        else if(isArea(sign))
            return AREA;
        else if(isPoint(sign))
            return SPOT;
        else
            return NONE;

    }

    public static boolean isPoint(int sign) {
        if(sign == 103 || (sign >= 109 && sign <= 115))
            return true;
        else if(sign >= 206 && sign <= 209)
            return true;
        else if(sign == 316 || sign == 318)
            return true;
        else if(sign >= 404 && sign <= 406)
            return true;
        else if(sign == 517 || (sign >= 523 && sign <= 526))
            return true;
        else if(sign == 601 || sign == 606 || sign == 607)
            return true;

        return false;
    }

    public static boolean isLine(int sign) {
        if(sign == 101 || sign == 102 || (sign >= 104 && sign <= 108))
            return true;
        else if(sign == 204 || sign == 205)
            return true;
        else if(sign == 401 || sign ==402)
            return true;
        else if((sign >= 501 && sign <= 514) || (sign >= 520 && sign <= 522))
            return true;
        else if(sign >= 602 && sign <= 604)
            return true;

        return false;
    }

    public static boolean isArea(int sign) {
        if(sign >= 201 && sign <= 203)
            return true;
        else if(sign >= 301 && sign <= 315)
            return true;
        else if(sign == 403 || (sign >= 407 && sign <= 409))
            return true;
        else if(sign == 515 || sign == 516 || sign == 518 || sign == 519)
            return true;
        else if(sign == 605)
            return true;

        return false;
    }

    public static void makeCurrentDrawOnTop(int sign) {

        int level = getWeight(sign);

        LayerPicker.setLayerWhenDraw(level);
    }

    public static int getWeight(int sign) {

        int weight  = sign / 100;

        if(weight  == 1 || weight  == 4)
            return 1;
        else if(weight  == 2)
            return 2;
        else if(weight  == 5)
            return 3;
        else if(weight  == 3)
            return 0;
        else
            return 4;
    }

    /**change each point in path in painter to now points+scale+matrix*/
    public static void refreshPath(EdgeObject painterCurrent) {
        Path path = painterCurrent.getPathMain();
        if (path == null)
            return;

        CurveControl curve = null;
        int i = 0;
        float pathX, pathY,
                curveFromX, curveFromY,
                curveToX, curveToY;

//        KernelLayout.mapView.ensurePositionAndScale();
        path.reset();
        while(i < painterCurrent.getSize()) {
            /*if (isForMap) {
                pathX = (float) painterCurrent.getPoint(i).x;
                pathY = (float) painterCurrent.getPoint(i).y;
            }else */
                pathX = (float) KernelLayout.toScreenLocation(
                        painterCurrent.getPoint(i).x, KernelLayout.COOX);
                pathY = (float) KernelLayout.toScreenLocation(
                        painterCurrent.getPoint(i).y, KernelLayout.COOY);

            if (painterCurrent.curve == null) {//直线

                if (painterCurrent.getPoint(i).isHead()) {
                    path.moveTo(pathX, pathY);
                }else {
                    path.lineTo(pathX, pathY);
                }
            }else {
                curve = painterCurrent.curve;
                if (painterCurrent.getPoint(i).isHead()) {
                    path.moveTo(pathX, pathY);
                }else {
                    /*if (isForMap) {
                        curveFromX = (float) curve.bezierF.get(i - 1).x;
                        curveFromY = (float) curve.bezierF.get(i - 1).y;
                        curveToX = (float) curve.bezierT.get(i).x;
                        curveToY = (float) curve.bezierT.get(i).y;
                    }else */
//                        curveFromX = (float) (curve.bezierF.get(i - 1).x * DrawView.scale
//                                + KernelLayout.mapView.getMapPosition().x);
//                        curveFromY = (float) (curve.bezierF.get(i - 1).y * DrawView.scale
//                                + KernelLayout.mapView.getMapPosition().y);
//                        curveToX = (float) (curve.bezierT.get(i).x * DrawView.scale
//                                + KernelLayout.mapView.getMapPosition().x);
//                        curveToY = (float) (curve.bezierT.get(i).y * DrawView.scale
//                                + KernelLayout.mapView.getMapPosition().y);
                        curveFromX = (float) KernelLayout.toScreenLocation(
                                curve.bezierF.get(i - 1).x, KernelLayout.COOX);
                        curveFromY = (float) KernelLayout.toScreenLocation(
                                curve.bezierF.get(i - 1).y, KernelLayout.COOY);
                        curveToX = (float) KernelLayout.toScreenLocation(
                                curve.bezierT.get(i).x, KernelLayout.COOX);
                        curveToY = (float) KernelLayout.toScreenLocation(
                                curve.bezierT.get(i).y, KernelLayout.COOY);

                    path.cubicTo(curveFromX,curveFromY, curveToX, curveToY, pathX, pathY);
                }
            }
            i++;
        }

        //for pattern fill bmp
//        if (fillBmp!=null && fillBmp.containsKey(painterCurrent.getSign())) {
//            Bitmap bmp = fillBmp.get(painterCurrent.getSign());
//            Matrix matrix = new Matrix();
//            matrix.setScale((float) scale, (float) scale);
//            Bitmap zoomedBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//            fillBmp.put(painterCurrent.getSign(), zoomedBmp);
//            Shader pieceShader = new BitmapShader(zoomedBmp, Shader.TileMode.REPEAT,
//                    Shader.TileMode.REPEAT);
//            ((AreaObject)painterCurrent).fillDrawable.getPaint().setShader(pieceShader);
//            ((AreaObject)painterCurrent).setPaintMain(((AreaObject) painterCurrent).fillDrawable.getPaint());//测试？paint应该如何与shader与shapeDrawable绑定
//            ((AreaObject)painterCurrent).fillDrawable.setBounds(0, 0, 1080, 1920);//绘制时动态设置 TODO 单个绘制对象的像素范围

//        }
    }

    //make top drawview focus on obj which user actually click on
    /**return is success*/
    public static boolean locateFocusObj(float eventX, float eventY) {
        ArrayList<DrawObject> list
                = KernelLayout.drawView.asListPainter();
        /*Log.i("drawViews["+0+"]", KernelLayout.drawViews[0].asListPainter()+"");
        Log.i("drawViews["+1+"]", KernelLayout.drawViews[1].asListPainter()+"");
        Log.i("drawViews["+2+"]", KernelLayout.drawViews[2].asListPainter()+"");
        Log.i("drawViews["+3+"]", KernelLayout.drawViews[3].asListPainter()+"");
        Log.i("drawViews["+4+"]", KernelLayout.drawViews[4].asListPainter()+"");*/

        //Trans to map , Cuz all point in list is for map
        eventX = (float) KernelLayout.toMapLocation(eventX, KernelLayout.COOX);
        eventY = (float) KernelLayout.toMapLocation(eventY, KernelLayout.COOY);

        Log.i("locate in", eventX+"::"+eventY);
        for (DrawObject drawObject : list) {
            Log.i("BoundsleftMAP", drawObject.getBound(DrawObject.BOUNDS_LEFT)+"");
            Log.i("BoundsRIGHTMAP", drawObject.getBound(DrawObject.BOUNDS_RIGHT)+"");
            Log.i("BoundsTOPMAP", drawObject.getBound(DrawObject.BOUNDS_TOP)+"");
            Log.i("BoundsBOTTOMMAP", drawObject.getBound(DrawObject.BOUNDS_BOTTOM)+"");
            Log.i("Boundsleft", KernelLayout.toScreenLocation(
                    drawObject.getBound(DrawObject.BOUNDS_LEFT), KernelLayout.COOX)+"");
            Log.i("BoundsRIGHT", KernelLayout.toScreenLocation(
                    drawObject.getBound(DrawObject.BOUNDS_RIGHT), KernelLayout.COOX)+"");
            Log.i("BoundsTOP", KernelLayout.toScreenLocation(
                    drawObject.getBound(DrawObject.BOUNDS_TOP), KernelLayout.COOY)+"");
            Log.i("BoundsBOTTOM", KernelLayout.toScreenLocation(
                    drawObject.getBound(DrawObject.BOUNDS_BOTTOM), KernelLayout.COOY)+"");
            if (eventX < KernelLayout.toScreenLocation(
                    drawObject.getBound(DrawObject.BOUNDS_LEFT), KernelLayout.COOX)
                    || eventX > KernelLayout.toScreenLocation(
                    drawObject.getBound(DrawObject.BOUNDS_RIGHT), KernelLayout.COOX)
                    || eventY < KernelLayout.toScreenLocation(
                    drawObject.getBound(DrawObject.BOUNDS_TOP), KernelLayout.COOY)
                    || eventY > KernelLayout.toScreenLocation(
                    drawObject.getBound(DrawObject.BOUNDS_BOTTOM), KernelLayout.COOY))
                continue;//click out of bound, must cant be

            if (drawObject instanceof AreaObject) {
                if (pnPoly(drawObject, eventX, eventY)) {
                    if (!DrawView.isLoadOne(drawObject)) {
                        DrawView.chooseDrawObject(drawObject);
                        //KernelLayout.drawViews[KernelLayout.getLevel()].paintToBack();
                        return true;
                    }
                }
                //TODO 进行锁定
            } else if (drawObject instanceof LineObject) {
                for (int i = 0; i +1 < drawObject.getSize(); i++) {//lost the last point
                    Log.i("index-" + i + "-d ", "x:" + drawObject.getPoint(i).x + ";;;y:" + drawObject.getPoint(i).y);

                    if (onLine(drawObject.getPoint(i), drawObject.getPoint(i + 1), eventX, eventY)) {
                        if (!DrawView.isLoadOne(drawObject)) {
                            DrawView.chooseDrawObject(drawObject);
                            //KernelLayout.drawViews[KernelLayout.getLevel()].paintToBack();
                            return true;
                        }
                    }
                }
            }else/**for pointObject*/ {
                int size = drawObject.getSize();
                for (int i = 0; i < size; i++) {
                    if (onPoint(drawObject.getPoint(i), eventX, eventY)) {
                        if (!DrawView.isLoadOne(drawObject)) {
                                DrawView.chooseDrawObject(drawObject);
                                return true;
                        }
                    }
                }
            }//area/line/point end
        }//for end

        return false;
    }

    /**PnPoly算法 计算点是否在不规则多边形内, 计算真实地图坐标*/
    public static boolean pnPoly (DrawObject drawObject, float eventX, float eventY) {
        int size = drawObject.getSize();
        boolean c = false;

        for (int i = 0; i < size - 1; i++) {
            if ( ( (drawObject.getPoint(i).y>eventY) != (drawObject.getPoint(i + 1).y>eventY) ) &&
                    (eventX < (eventY-drawObject.getPoint(i).y)
                            * (drawObject.getPoint(i + 1).x - drawObject.getPoint(i).x)
                            / (drawObject.getPoint(i + 1).y-drawObject.getPoint(i).y)
                            + drawObject.getPoint(i).x) )
                c = !c;//TODO eventX == 则在边上(LineObject)

            //顶点处理,从第二个点开始，第一个点/循环的最后一点 循环结束后再算
            //夹角包含水平线算一个交点，不包含算两个(即不算)
            else if (i > 0 && (drawObject.getPoint(i).y == eventY) &&
                    ( (drawObject.getPoint(i - 1).y > eventY)
                            == (drawObject.getPoint(i + 1).y < eventY) ))
                c = !c;
        }
        if ((drawObject.getPoint(size - 1).y == eventY) &&
                ( (drawObject.getPoint(size - 2).y > eventY)
                        == (drawObject.getPoint(0).y < eventY) ))
            c = !c;

        return c;
    }

    /**计算当前路径所在屏幕上的坐标*/
    public static boolean inRegion(DrawObject drawObject, float eventX, float eventY) {
        //构造一个区域对象，左闭右开的。
        RectF r=new RectF();
        Region region = new Region();
        //计算控制点的边界
        drawObject.getPathMain().computeBounds(r, true);
        //设置区域路径和剪辑描述的区域
        region.setPath(drawObject.getPathMain(), new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        //判断触摸点是否在封闭的path内 在返回true 不在返回false
        return region.contains((int)eventX, (int)eventY);
    }

    /**计算真实(地图)坐标*/
    public static boolean onLine(AlgoPoint point, AlgoPoint endPoint, float eventX, float eventY) {
        Log.i("event's x:" + eventX, "event's y:" + eventY);
        Log.i("index--d ", "x:" + point.x + ";;;y:" + point.y);
        if (Math.abs(eventX - point.x) < AlgoPoint.CLICK_TOLERANCE
                && Math.abs(eventY - point.y) < AlgoPoint.CLICK_TOLERANCE)
            return true;

        //(y - y2)/(y2 - y1) = (x - x2)/(x2 - x1)
        if ((((endPoint.y - point.y)
                / (endPoint.x - point.x))
                - ((point.y - eventY)
                / (point.x - eventX)) < 0.4f)
                || (((endPoint.y - point.y)
                / (endPoint.x - point.x))
                - ((eventY - point.y)
                / (eventX - point.x)) < 0.4f) )//三角函数的误差值
            return true;

        return false;
    }

    /**计算真实(地图)坐标                                                                                                         */
    public static boolean onPoint(AlgoPoint point, float eventX, float eventY) {
            if (Math.abs(eventX - point.x) < 2
                    && Math.abs(eventY - point.y) < 2)
                return true;

        return false;
    }


    /**operation in edit panel*/
    public static boolean joinSelf(LineObject painter) {
        if (painter.getPoint(0)
                .anear(painter.getPoint(painter.getSize() - 1))) {
            painter.popPoint();
            painter.getPathMain().close();
            return true;
        }
        return false;
    }

    public static boolean joinOther(LineObject painter) {
        boolean otherIsC = false;
        for (DrawObject other : DrawView.getAllDrawObjects().get(painter.getSign())) {
            otherIsC = ((LineObject) other).curve != null;

            /***本体头与别人的头*/
            if (other.getPoint(0).anear(painter.getPoint(0))) {
                if (otherIsC && painter.curve == null) {
                    painter.initCurve();
                }

                while (other.getSize() > 0) {
                    painter.addPoint(0, other.removePoint(0));
                    if (painter.curve != null) {
                        if (otherIsC) {
                            AlgoPoint otherCon = ((LineObject) other).curve.removeControlPoint(0, CurveControl.Direction.FROM);
                            painter.curve.insOneSideBez(otherCon.x, otherCon.y, CurveControl.Direction.FROM, 0);
                            otherCon = ((LineObject) other).curve.removeControlPoint(0, CurveControl.Direction.TO);
                            painter.curve.insOneSideBez(otherCon.x, otherCon.y, CurveControl.Direction.TO, 0);
                        }else {
                            painter.curve.insOneSideBez(painter.getPoint(0).x, painter.getPoint(0).y
                                    , CurveControl.Direction.FROM, 0);
                            painter.curve.insOneSideBez(painter.getPoint(0).x, painter.getPoint(0).y
                                    , CurveControl.Direction.TO, 0);
                        }
                    }
                }
                return true;
            }

            /***本体尾和别人的头*/
            if (other.getPoint(0).anear(painter.peekPoint())) {
                if (otherIsC && painter.curve == null) {
                    painter.initCurve();
                }

                while (other.getSize() > 0) {
                    painter.addPoint(other.removePoint(0));
                    if (painter.curve != null) {
                        if (otherIsC) {
                            AlgoPoint otherCon = ((LineObject) other).curve.removeControlPoint(0, CurveControl.Direction.FROM);
                            painter.curve.addOneSideBezier(otherCon.x, otherCon.y, CurveControl.Direction.FROM);
                            otherCon = ((LineObject) other).curve.removeControlPoint(0, CurveControl.Direction.TO);
                            painter.curve.addOneSideBezier(otherCon.x, otherCon.y, CurveControl.Direction.TO);
                        }else {
                            painter.curve.addOneSideBezier(painter.getPoint(0).x, painter.getPoint(0).y
                                    , CurveControl.Direction.FROM);
                            painter.curve.addOneSideBezier(painter.getPoint(0).x, painter.getPoint(0).y
                                    , CurveControl.Direction.TO);
                        }
                    }
                }
                return true;
            }

            /***本体头与别人的尾*/
            if (other.peekPoint().anear(painter.getPoint(0))) {
                if (otherIsC && painter.curve == null) {
                    painter.initCurve();
                }
                
                while (other.getSize() > 0) {
                    painter.addPoint(0, other.popPoint());

                    if (painter.curve != null) {
                        if (otherIsC) {
                            int size = ((LineObject) other).curve.getSize();//must = other.getSize()
                            AlgoPoint otherCon = ((LineObject) other).curve.removeControlPoint(size, CurveControl.Direction.FROM);
                            painter.curve.insOneSideBez(otherCon.x, otherCon.y, CurveControl.Direction.FROM, 0);
                            otherCon = ((LineObject) other).curve.removeControlPoint(size, CurveControl.Direction.TO);
                            painter.curve.insOneSideBez(otherCon.x, otherCon.y, CurveControl.Direction.TO, 0);
                        }else {
                            painter.curve.insOneSideBez(painter.getPoint(0).x, painter.getPoint(0).y
                                    , CurveControl.Direction.FROM, 0);
                            painter.curve.insOneSideBez(painter.getPoint(0).x, painter.getPoint(0).y
                                    , CurveControl.Direction.TO, 0);
                        }
                    }
                }
                return true;
            }

            /***本体尾与别人的尾*/
            if (other.peekPoint().anear(painter.peekPoint())) {
                if (otherIsC && painter.curve == null) {
                    painter.initCurve();
                }

                while (other.getSize() > 0) {
                    painter.addPoint(other.popPoint());

                    if (painter.curve != null) {
                        if (otherIsC) {
                            int size = ((LineObject) other).curve.getSize();
                            AlgoPoint otherCon = ((LineObject) other).curve.removeControlPoint(size, CurveControl.Direction.FROM);
                            painter.curve.addOneSideBezier(otherCon.x, otherCon.y, CurveControl.Direction.FROM);
                            otherCon = ((LineObject) other).curve.removeControlPoint(size, CurveControl.Direction.TO);
                            painter.curve.addOneSideBezier(otherCon.x, otherCon.y, CurveControl.Direction.TO);
                        }else {
                            painter.curve.addOneSideBezier(painter.getPoint(0).x, painter.getPoint(0).y
                                    , CurveControl.Direction.FROM);
                            painter.curve.addOneSideBezier(painter.getPoint(0).x, painter.getPoint(0).y
                                    , CurveControl.Direction.TO);
                        }
                    }
                }
                return true;
            }
        }

        return false;
    }

    public static void moveGlobal(DrawObject drawObject, float offsetX, float offsetY) {
        for (int i = 0; i < drawObject.getSize(); i++) {
            AlgoPoint point = drawObject.getPoint(i);
            //point.set(point.x + offsetX, point.y + offsetY);
            point.offset(offsetX, offsetY);
        }
    }

    /**center begin end coodinate is for screen*/
    public static void rotate(DrawObject drawObject, AlgoPoint center) {
    //TODO transform, 注释掉的：手算每个点的相对轴的旋转终点， 每点移动坐标。

        double a = Math.sqrt(Math.pow(begin.x - end.x, 2)
                + Math.pow(begin.y - end.y, 2));
        double b = Math.sqrt(Math.pow(begin.x - center.x, 2)
                + Math.pow(begin.y - center.y, 2));
        double c = Math.sqrt(Math.pow(end.x - center.x, 2)
                + Math.pow(end.y - center.y, 2));
        double cosA = Math.acos((b*b + c*c - a*a) / 2*b*c);//余玄定理, 弧度
        //tan a0 x=1~-1/-1~1, angel=0~ pi , >0&x>0
        double cosa0 = 0;
        float centerMapX = KernelLayout.toMapLocation(center.x, KernelLayout.COOX),
                centerMapY = KernelLayout.toMapLocation(center.y, KernelLayout.COOY);
        //(y-y0)/(y0-y1) = (x-x0) / (x0 - x1)
        boolean isCW = (end.y - begin.y) / (begin.y - center.y) >
                (end.x - begin.x) / (begin.x - center.x);

        /**change data point*/
        for (int i = 0; i < drawObject.getSize(); i++) {
            AlgoPoint point = drawObject.getPoint(i);
            double length = Math.hypot(point.x - centerMapX
                    , point.y - centerMapY);
            cosa0 = Math.acos(point.y - centerMapY)
                    / Math.sqrt(Math.pow(point.x - centerMapX, 2)
                    + Math.pow(point.y - centerMapY, 2));

            //cos变化与y无关，只与x有关
            if ((isCW && point.y < 0) || (!isCW && point.y > 0)) {
                point.set(centerMapX + (float) (length * Math.cos(cosA + cosa0)),
                        centerMapY + (float) (length * Math.sin(cosA + cosa0)));
            }else {
                point.set(centerMapX + (float) (length * Math.cos(cosA - cosa0)),
                        centerMapY + (float) (length * Math.sin(cosA - cosa0)));
            }
        }

        /**change screen path*/
        Matrix rotate = new Matrix();

        if (isCW) {
            rotate.postRotate((float) Math.toDegrees(cosA),
                    centerMapX, centerMapY);
        }else {
            rotate.postRotate((float) -Math.toDegrees(cosA),
                    centerMapX, centerMapY);
        }
        drawObject.getPathMain().transform(rotate);
    }

    /**first & second coodinate is for map*/
    public static void cutLine(DrawObject drawObject, AlgoPoint first, AlgoPoint second) {
        for (int i = 0; i + 1 < drawObject.getSize(); i++) {
            if (onLine(drawObject.getPoint(i), drawObject.getPoint(i + 1),
                     first.x, first.y)) {
                if (onLine(drawObject.getPoint(i), drawObject.getPoint(i + 1),
                        second.x, second.y)) {
                    double firstDistan = Math.sqrt(Math.pow(first.x - drawObject.getPoint(i).x, 2)
                            + Math.pow(first.y - drawObject.getPoint(i).y, 2));
                    double secondDistan = Math.sqrt(Math.pow(second.x - drawObject.getPoint(i).x, 2)
                            + Math.pow(second.y - drawObject.getPoint(i).y, 2));

                    if (firstDistan < secondDistan) {
                        second.setAsHead(true);
                        drawObject.addPoint(i + 1, second);
                        drawObject.addPoint(i + 1, first);
                    }else {
                        first.setAsHead(true);
                        drawObject.addPoint(i + 1, first);
                        drawObject.addPoint(i + 1, second);
                    }

                }else {
                    drawObject.addPoint(i + 1, first);
                }
            }
        }
    }

}
