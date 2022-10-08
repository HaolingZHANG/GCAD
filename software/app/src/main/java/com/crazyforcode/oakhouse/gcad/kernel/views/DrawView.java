package com.crazyforcode.oakhouse.gcad.kernel.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.draw.assist.CurveControl;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.AreaObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.EdgeObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.LineObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.SpotObject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjectFactory;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.SVGCreator;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.others.components.EditPicker;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class DrawView extends SurfaceView
        /*implements SurfaceHolder.Callback*/ {

    //  绘制信息
    private static HashMap<Integer, ArrayList<DrawObject>> drawObjects = new HashMap<>();
    private static TreeMap<Integer, Bitmap> fillBmp = null;
    public Bitmap[] backgrounds;
    public boolean[] levelVisible;
    //private int layer;
    private static DrawObject painterCurrent = null;
    private static Path pathMain;
    private static Path pathCache;
    public static AlgoPoint editPoint = null;

    private SurfaceHolder surfaceHolder;

    //  平移放缩信息
    private int state = NONE;
    //public static double scale = 1;

    //  初始化信息确定
    private AlgoPoint lastCenter;
    private double lastDistance;
    private final static int NONE = 0;
    private final static int CHANGE = 1;
    public final static int DRAW = 2;//=(drawing / editing(editing = dealbegun / not))
    public final static int DRAW_EXTERNAL = 3;
    public final static int PEOPLE_POSITION = 4;

    private boolean initChange = true;

    //  当前信息
    private AlgoPoint currentCenter;
    private double currentDistance;
    private AlgoPoint pointForMap;

    public DrawView(Context context/*, int layer*/) {
        super(context);
        init(/*layer*/);
    }

    public DrawView(Context context, AttributeSet attrs/*, int layer*/) {
        super(context, attrs);
        init(/*layer*/);
    }


    private void init(/*int layer*/) {
        //this.layer = layer;
        backgrounds = new Bitmap[KernelLayout.LEVEL_NUM];
        levelVisible = new boolean[KernelLayout.LEVEL_NUM];

        //surfaceHolder = this.getHolder();
        //surfaceHolder.addCallback(this);

        this.setClickable(true);
        //this.setVisibility(GONE);
        this.setVisibility(VISIBLE);

//        设置透明
//        this.setZOrderOnTop(true);
//        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
//        设置硬件加速（增加内存消耗）→ 关闭硬件加速
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        设置可以使用onDraw()方法
        this.setWillNotDraw(false);

        pathCache = new Path();
        editPoint = new AlgoPoint();
    }

    public void initBackground(int layer) {
        double mapWidth = SaveTotalMap.getOriginMap().getWidth();
        double mapHeight = SaveTotalMap.getOriginMap().getHeight();
        Bitmap.Config mapConfig = SaveTotalMap.getOriginMap().getConfig();

        if(mapWidth != 0 && mapHeight != 0) {
            backgrounds[layer] = Bitmap.createBitmap((int) mapWidth, (int) mapHeight, mapConfig);

            Matrix matrix = new Matrix();
            matrix.postScale((float) KernelLayout.mapView.getCompressionScale(),
                    (float) KernelLayout.mapView.getCompressionScale());

            backgrounds[layer] = Bitmap.createBitmap(backgrounds[layer], 0, 0,
                    backgrounds[layer].getWidth(), backgrounds[layer].getHeight(), matrix, true);

//            面板测试
//            Canvas canvas = new Canvas(background);
//
//            switch(layer) {
//                case 0:canvas.drawColor(Color.GREEN);break;
//                case 1:canvas.drawColor(Color.YELLOW);break;
//                case 2:canvas.drawColor(Color.BLUE);break;
//                case 3:canvas.drawColor(Color.BLACK);break;
//                case 4:canvas.drawColor(Color.RED);break;
//            }
        }
    }

    public @Nullable Bitmap getBackgroundBitmap(int layer) {
        return backgrounds[layer];
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        KernelLayout.mapView.setCurrentMapPosition();
        switch(event.getAction()) {
            case MotionEvent.ACTION_UP:
                if(state == DRAW && KernelLayout.isDrawing && painterCurrent != null) {//TODO isDrawing调整，与state功能不同
//                    currentpointScreen = new AlgoPoint(x, y);

                    //scale = KernelLayout.mapView.getScale();

                    pointForMap = KernelLayout.toMapLocation(new AlgoPoint(event.getX(), event.getY()));
                    if (painterCurrent.getSize() <= 0)
                        pointForMap.setAsHead(true);

                    Log.i("相对加载初始图左上顶点的点位", "X = " + pointForMap.x
                            + " ,  Y = "+ pointForMap.y);
                    Log.i("相对屏幕位置", "X = " + event.getX()
                            + " ,  Y = " + event.getY());

                    if (painterCurrent != null) {
                        if (!(painterCurrent instanceof SpotObject)) {//line/area
                            Log.i("add point", "ACTION_UP");
                            if (((EdgeObject) painterCurrent).curve != null) {//曲线
                                if (painterCurrent.getSize() <= 0) {
                                    ((EdgeObject) painterCurrent).curve.setAsSecond(true);

                                    ((EdgeObject) painterCurrent).curve.addOneSideBezier(
                                            pointForMap.x, pointForMap.y
                                            , CurveControl.Direction.FROM);
                                    ((EdgeObject) painterCurrent).curve.addOneSideBezier(
                                            pointForMap.x, pointForMap.y
                                            , CurveControl.Direction.TO);
                                    pathMain.moveTo(event.getX(), event.getY());
                                    pathCache.set(pathMain);
                                } else {
                                    pathMain.set(pathCache);
                                    float x2 = painterCurrent.peekPoint().x,
                                            y2 = painterCurrent.peekPoint().y,
                                            //len = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2)),
                                            dx = (event.getX() - x2) / 4,//cubic控制杆设为拉出来的新路径的1/4
                                            dy = (event.getY() - y2) / 4;
                                    float x2forMap = (x2 - KernelLayout.mapView.getMapPosition().x)
                                            / KernelLayout.mapView.getScale(),
                                            y2forMap = (y2 - KernelLayout.mapView.getMapPosition().x)
                                                    / KernelLayout.mapView.getScale(),
                                            dxforMap = dx / KernelLayout.mapView.getScale(),
                                            dyforMap = dy / KernelLayout.mapView.getScale();

                                    ((EdgeObject) painterCurrent).curve
                                            .editOneSideBezier(x2forMap + dxforMap,
                                                    y2forMap + dyforMap, CurveControl.Direction.FROM);//from side
                                    ((EdgeObject) painterCurrent).curve
                                            .editOneSideBezier(x2forMap - dxforMap,
                                                    y2forMap - dyforMap, CurveControl.Direction.TO);//to side
                                    ((EdgeObject) painterCurrent).curve
                                            .addOneSideBezier(pointForMap.x - dxforMap,
                                                    pointForMap.y - dyforMap, CurveControl.Direction.TO);//to side
                                    ((EdgeObject) painterCurrent).curve
                                            .addOneSideBezier(pointForMap.x + dxforMap,
                                                    pointForMap.y + dyforMap, CurveControl.Direction.FROM);//from side

                                    if (!((EdgeObject) painterCurrent).curve.isSecond()) {//不是第二个点才做
                                        /*AlgoPoint bisector =
                                                AlgoPoint.bisector(
                                                        painterCurrent.getPoint(painterCurrent.getSize() - 2),
                                                        painterCurrent.peekPoint(),
                                                        pointForMap);

                                        painterCurrent.curve
                                                .editOneSideBezier(painterCurrent.peekPoint().x
                                                                + (pointForMap.x
                                                                - painterCurrent.peekPoint().x)
                                                                *bisector.y/4,
                                                        painterCurrent.peekPoint().y
                                                                + (pointForMap.y
                                                                - painterCurrent.peekPoint().y)
                                                                *bisector.x/4, CurveControl.FROM);//from side
                                        painterCurrent.curve
                                                .editOneSideBezier(painterCurrent.peekPoint().x
                                                                - (painterCurrent.peekPoint().x
                                                                - painterCurrent.getPoint(painterCurrent.getSize() - 2).x)
                                                                *bisector.y/4,
                                                        painterCurrent.peekPoint().y
                                                                - (painterCurrent.peekPoint().y
                                                                - painterCurrent.getPoint(painterCurrent.getSize() - 2).y)
                                                                *bisector.x/4, CurveControl.TO);//to side
                                        painterCurrent.curve
                                                .addOneSideBezier(pointForMap.x
                                                                - (pointForMap.x
                                                                - painterCurrent.peekPoint().x)/4,
                                                        pointForMap.y
                                                                - (pointForMap.y
                                                                - painterCurrent.peekPoint().y)/4, CurveControl.TO);//to side
                                        painterCurrent.curve
                                                .addOneSideBezier(pointForMap.x + (pointForMap.x
                                                                - painterCurrent.peekPoint().x)/4,
                                                        pointForMap.y + (pointForMap.y
                                                                - painterCurrent.peekPoint().y)/4, CurveControl.FROM);//from side

                                        pathMain.cubicTo((float) painterCurrent.curve.bezierF
                                                        .get(painterCurrent.getSize() - 2).x,
                                                (float) painterCurrent.curve.bezierF
                                                        .get(painterCurrent.getSize() - 2).y,
                                                (float) ((EdgeObject) painterCurrent).curve.bezierT
                                                        .get(painterCurrent.getSize() - 1).x,
                                                (float) painterCurrent.curve.bezierT
                                                        .get(painterCurrent.getSize() - 1).y,
                                                (float) (KernelLayout.toScreenLocation(
                                                        painterCurrent.peekPoint().x, KernelLayout.COOX)),
                                                (float) (KernelLayout.toScreenLocation(
                                                        painterCurrent.peekPoint().y, KernelLayout.COOY)));*/
                                        pathMain.cubicTo((float) ((EdgeObject) painterCurrent).curve.getControlPoint(
                                                        painterCurrent.getSize() - 2, CurveControl.Direction.FROM).x,
                                                (float) ((EdgeObject) painterCurrent).curve.getControlPoint(
                                                        painterCurrent.getSize() - 2, CurveControl.Direction.FROM).y,
                                                x2 - dx, y2 - dy, x2, y2);
                                    } else {
                                        ((EdgeObject) painterCurrent).curve.setAsSecond(false);

                                        /*double x2forScreen = (painterCurrent.peekPoint().x
                                                    - KernelLayout.mapView.getMapPosition().x) / scale,
                                                y2forScreen = (painterCurrent.peekPoint().y
                                                        - KernelLayout.mapView.getMapPosition().x) / scale,
                                                dxforMap = (event.getX() - x2forScreen
                                                        - KernelLayout.mapView.getMapPosition().x) * scale,
                                                dyforMap = (event.getY() - y2forScreen
                                                        - KernelLayout.mapView.getMapPosition().x) * scale;*/
                                        /*double dxforMap = (pointForMap.x - painterCurrent.peekPoint().x)/4,
                                                dyforMap = (pointForMap.y - painterCurrent.peekPoint().y)/4;

                                        painterCurrent.curve.editOneSideBezier(
                                                painterCurrent.peekPoint().x + dxforMap,
                                                painterCurrent.peekPoint().y + dyforMap,
                                                CurveControl.FROM);//from side
                                        painterCurrent.curve.editOneSideBezier(
                                                painterCurrent.peekPoint().x - dxforMap,
                                                painterCurrent.peekPoint().y - dyforMap,
                                                CurveControl.TO);//to side
                                        painterCurrent.curve.addOneSideBezier(
                                                pointForMap.x - dxforMap,
                                                pointForMap.y - dyforMap, CurveControl.TO);//to side
                                        painterCurrent.curve.addOneSideBezier(
                                                pointForMap.x + dxforMap,
                                                pointForMap.y + dyforMap, CurveControl.FROM);//from side*/

                                    }
                                    pathCache.set(pathMain);
                                    pathMain.quadTo(x2 + dx, y2 + dy, event.getX(), event.getY());
                                    /*pathMain.quadTo((float) painterCurrent.curve.bezierF
                                                    .get(painterCurrent.getSize() - 1).x,
                                            (float) painterCurrent.curve.bezierF
                                                    .get(painterCurrent.getSize() - 1).y,
                                            event.getX(), event.getY());*/
                                }
                            } else {//直线
                                if (painterCurrent.getSize() <= 0) {
                                    pathMain.moveTo(event.getX(), event.getY());
                                    pathCache.set(pathMain);
                                    //pathAuxiliary.moveTo(event.getX(), event.getY());
                                    //painterCurrent.addPoint(new AlgoPoint(-1.0, -1.0));
                                    //painterCurrent.addPoint(pointForMap);
                                } else {
                                    //painterCurrent.addPoint(pointForMap);
                                    pathCache.set(pathMain);
                                    pathMain.lineTo(event.getX(), event.getY());
                                    //pathAuxiliary.lineTo(event.getX(), event.getY());
                                /*int index = ((LineObject)painterCurrent).bezierL.size()-1;
                                pathAuxiliary.rCubicTo((float)(((LineObject)painterCurrent).bezierL.get(index).x),
                                        (float)(((LineObject)painterCurrent).bezierL.get(index).y),
                                        (float)(((LineObject)painterCurrent).bezierR.get(index).x),
                                        (float)(((LineObject)painterCurrent).bezierL.get(index).y),
                                        event.getX(), event.getY());
                                Log.i("rcubic", "over");//曲线绘制，未写完, old type*/
                                }
                            }//直线/曲线
                        } else {//点直接绘
                            // draw();//pathMain.moveTo(event.getX(), event.getY());//pointObject dont have path
                        }

                        painterCurrent.addPoint(pointForMap);
                    }
                    state = NONE;
//                    movingDraw(-5, -5);
                    invalidate();
                }else if (state == DRAW && KernelLayout.isEditing) {
                    if (!MainActivity.editPicker.dealBegun()) {
                        if (DrawObjects.locateFocusObj(event.getX(), event.getY())) {
                            paintToBack();
                            invalidate();
                        }
                    } else {
                        //TODO 需要UP的edit操作
                        if (MainActivity.editPicker.getEditType() == EditPicker.EditType.EDIT_POINT){
                            editPoint = new AlgoPoint();//un bind with DrawObject
                        } else if (MainActivity.editPicker.getEditType() == EditPicker.EditType.EDIT_OBJECT) {
                            DrawObjects.moveGlobal(painterCurrent,
                                    event.getX() - editPoint.x,
                                    event.getY() - editPoint.y);
                            painterCurrent.getPathMain().offset((float) (event.getX() - editPoint.x),
                                    (float) (event.getY() - editPoint.y));
                            //editPoint.setX(event.getX());
                            //editPoint.setY(event.getY());
                            editPoint.set(event.getX(), event.getY());
                            editPoint.setAsHead(true);//reset
                        }else if (MainActivity.editPicker.getEditType() == EditPicker.EditType.ROTATE) {
                            /*if (editPoint.isHead() ||
                                    (Math.abs(editPoint.x - event.getX())
                                            < AlgoPoint.CLICK_TOLERANCE
                                            && Math.abs(editPoint.y - event.getY())
                                            < AlgoPoint.CLICK_TOLERANCE)) {
                                editPoint.setX(event.getX());
                                editPoint.setY(event.getY());
                                editPoint.setAsHead(false);
                            }else */
                            if (!editPoint.isHead()) {
                                //DrawObjects.end.setX(event.getX());
                                //DrawObjects.end.setY(event.getY());
                                DrawObjects.end.set(event.getX(), event.getY());
                                DrawObjects.rotate(painterCurrent, editPoint);
                                //DrawObjects.begin.setX(DrawObjects.end.x);
                                //DrawObjects.begin.setY(DrawObjects.end.y);//hide control line
                                DrawObjects.begin.set(DrawObjects.end.x, DrawObjects.end.y);
                            }else {
                                //editPoint.setX(event.getX());
                                //editPoint.setY(event.getY());
                                editPoint.set(event.getX(), event.getY());
                                editPoint.setAsHead(false);
                            }
                            invalidate();
                        }else if (MainActivity.editPicker.getEditType() == EditPicker.EditType.CUT) {
                            AlgoPoint second = KernelLayout.toMapLocation(
                                    new AlgoPoint(event.getX(), event.getY()));
                            editPoint.set(KernelLayout.toMapLocation(
                                            editPoint.x, KernelLayout.COOX),
                                    KernelLayout.toMapLocation(
                                            editPoint.y, KernelLayout.COOY));
                            DrawObjects.cutLine(painterCurrent, editPoint, second);

                            editPoint.setAsHead(true);
                            pathCache.reset();
                        }else if (MainActivity.editPicker.getEditType()
                                == EditPicker.EditType.CUT_HOLE) {
                            if (DrawObjects.inRegion(painterCurrent, event.getX(), event.getY())) {
                                AlgoPoint forMap = new AlgoPoint(
                                        KernelLayout.toMapLocation(event.getX(), KernelLayout.COOX),
                                        KernelLayout.toMapLocation(event.getY(), KernelLayout.COOY));

                                if (editPoint.isHead()) {//TODO curve
                                    pathMain.moveTo(event.getX(), event.getY());

                                    editPoint.setAsHead(false);
                                    forMap.setAsHead(true);
                                }else {
                                    pathMain.lineTo(event.getX(), event.getY());
                                }

                                painterCurrent.addPoint(forMap);//editPoint.copy()
                            }
                        }

                        //editPoint = null;
                    }

                }else/**watch&CHANGE mode, drag map*/ {
                    KernelLayout.mapView.changeByDrawViewOver(currentCenter);
                    /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                        KernelLayout.drawViews[i].invalidate();*/
                    KernelLayout.drawView.invalidate();

                    KernelLayout.mapView.invalidate();
                    lastDistance = 0;
                    KernelLayout.savedMatrix.set(KernelLayout.currentMatrix);
                    initChange = true;
                    KernelLayout.isTransform = false;
                    state = NONE;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                KernelLayout.mapView.changeByDrawViewOver(currentCenter);
                /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                    KernelLayout.drawViews[i].invalidate();*/
                KernelLayout.drawView.invalidate();

                KernelLayout.mapView.invalidate();
                lastDistance = 0;
                KernelLayout.savedMatrix.set(KernelLayout.currentMatrix);
                initChange = true;
                KernelLayout.isTransform = false;
                state = NONE;
                break;
            case MotionEvent.ACTION_DOWN:
                state = DRAW;
                KernelLayout.isTransform = false;
                if (KernelLayout.isEditing) {
                    if (MainActivity.editPicker.getEditType()
                            == EditPicker.EditType.EDIT_POINT) {
                        AlgoPoint clickForMap = KernelLayout.toMapLocation(
                                new AlgoPoint(event.getX(), event.getY()));
                        for (int i = 0; i < painterCurrent.getSize(); i++) {
                            /**优先控制曲线点，再控制本体*/
                            if (painterCurrent instanceof EdgeObject
                                    && ((EdgeObject) painterCurrent).curve != null) {
                                if (((EdgeObject) painterCurrent).curve
                                        .getControlPoint(i, CurveControl.Direction.FROM).anear(clickForMap)) {
                                    /*editPoint.set(((EdgeObject) painterCurrent).curve
                                            .getControlPoint(i, CurveControl.Direction.FROM));*/
                                    editPoint = ((EdgeObject) painterCurrent).curve
                                            .getControlPoint(i, CurveControl.Direction.FROM);
                                    break;
                                }

                                if (((EdgeObject) painterCurrent).curve
                                        .getControlPoint(i, CurveControl.Direction.TO).anear(clickForMap)) {
                                    /*editPoint.set(((EdgeObject) painterCurrent).curve
                                            .getControlPoint(i, CurveControl.Direction.TO));*/
                                    editPoint = ((EdgeObject) painterCurrent).curve
                                            .getControlPoint(i, CurveControl.Direction.TO);
                                    break;
                                }
                            }

                            if (painterCurrent.getPoint(i).anear(clickForMap)) {
                                editPoint = painterCurrent.getPoint(i);//BIND, but editPoint.set(painterCurrent.getPoint(i));
//                                editPoint.setX(clickForMap.x);
//                                editPoint.setY(clickForMap.y);
//                                DrawObjects.refreshPath(painterCurrent, false);
//                                invalidate();
                                break;
                            }

                        }
                    }else if (MainActivity.editPicker.getEditType()
                            == EditPicker.EditType.EDIT_OBJECT
                            ||MainActivity.editPicker.getEditType()
                            == EditPicker.EditType.CUT) {
                        /*editPoint.setAsHead(false);
                        editPoint.setX(event.getX());
                        editPoint.setY(event.getY());
                        DrawObjects.end.setX(editPoint.x);
                        DrawObjects.end.setY(editPoint.y);*/
                        editPoint.set(event.getX(), event.getY());
                        DrawObjects.end.set(editPoint.x, editPoint.y);
                                /*(event.getX() - KernelLayout.mapView.getMapPosition().x) * scale,
                                (event.getY() - KernelLayout.mapView.getMapPosition().y) * scale);*/
                    }else if (MainActivity.editPicker.getEditType()
                            == EditPicker.EditType.ROTATE) {
                        if(editPoint.isHead()) {
                            //editPoint.setX(event.getX());
                            //editPoint.setY(event.getY());
                            editPoint.set(event.getX(), event.getY());
                            //editPoint.setAsHead(false);
                        }else {
                            //DrawObjects.begin.setX(event.getX());
                            //DrawObjects.begin.setY(event.getY());
                            DrawObjects.begin.set(event.getX(), event.getY());
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                state = CHANGE;
                KernelLayout.isTransform = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() >= 2) {
                    boolean changedDraw = false, changedEdit = false;
                    if (KernelLayout.isDrawing) {
                        KernelLayout.isDrawing = false;
                        changedDraw = true;
                    }
                    if (KernelLayout.isEditing) {
                        KernelLayout.isEditing = false;
                        changedEdit = true;
                    }

                    AlgoPoint figOne = new AlgoPoint(event.getX(0), event.getY(0));
                    AlgoPoint figTwo = new AlgoPoint(event.getX(1), event.getY(1));

                    currentDistance = Math.hypot(figOne.x - figTwo.x,
                            figOne.y - figTwo.y);

                    currentCenter = new AlgoPoint((figOne.x + figTwo.x) / 2,
                            (figOne.y + figTwo.y) / 2);

                    if(initChange) {
                        lastDistance = currentDistance;
                        lastCenter = currentCenter.copy();//new AlgoPoint(currentCenter);
                        state = CHANGE;
                        initChange = false;
                    }

                    double scaleXY = currentDistance / lastDistance;

                    double moveX = currentCenter.x
                            - lastCenter.x;
                    double moveY = currentCenter.y
                            - lastCenter.y;

                    KernelLayout.currentMatrix.set(KernelLayout.savedMatrix);

                    if((Math.abs(moveX) > 10 || Math.abs(moveY) > 10)) {
                        KernelLayout.currentMatrix.postTranslate((float) moveX, (float) moveY);

                        /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                            KernelLayout.drawViews[i].invalidate();

                        KernelLayout.mapView.invalidate();*/
                        postCanvasChange();
                    }

                    if(KernelLayout.mapView.allowChangeScale()) {
                        if(currentDistance - lastDistance > 10) {
                            KernelLayout.currentMatrix.postScale((float) scaleXY,(float) scaleXY,
                                    (float)currentCenter.x, (float)currentCenter.y);

                            KernelLayout.mapView.setTransformScale(scaleXY);

                            /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                                KernelLayout.drawViews[i].invalidate();

                            KernelLayout.mapView.invalidate();*/
                            postCanvasChange();
                        } else if(currentDistance - lastDistance < -10) {
                            KernelLayout.currentMatrix.postScale((float) scaleXY,(float) scaleXY,
                                    (float)currentCenter.x, (float)currentCenter.y);

                            KernelLayout.mapView.setTransformScale(scaleXY);

                            /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
                                KernelLayout.drawViews[i].invalidate();

                            KernelLayout.mapView.invalidate();*/
                            postCanvasChange();
                        }
                        MainActivity.changeScale();
                    }
                    /*if (pathMain != null) {
                        //scale = KernelLayout.mapView.getScale();
                        DrawObjects.refreshPath((EdgeObject) painterCurrent);//, DrawObjects.FORSCREEN
                        painterCurrent.zoomStyle(DrawObjects.CanvasTarget.FORSCREEN.value());
                    }已模块化为方法postCanvasChange()*/

                    if (changedDraw)
                        KernelLayout.isDrawing = true;
                    if (changedEdit)
                        KernelLayout.isEditing = true;
                }else /**figCount=1*/{
                    if (KernelLayout.isDrawing) {
                        if(state == DRAW && painterCurrent != null) {
                            if (!(painterCurrent instanceof SpotObject)) {

//                            //显示开始停止按钮
//                            MainActivity.drawStart.setVisibility(View.VISIBLE);
//                            MainActivity.drawPause.setVisibility(View.VISIBLE);

                                if (painterCurrent.getSize() <= 0) {
                                    pathMain.moveTo(event.getX(), event.getY());
                                    //pathCache.set(pathMain);
                                }/*else if (painterCurrent != null && painterCurrent.curve.isSecond()) {
                            pathMain.set(pathCache);
                           pathMain.lineTo(event.getX(), event.getY());
                        }*/ else if (((EdgeObject) painterCurrent).curve != null && !((EdgeObject) painterCurrent).curve.isSecond()) {//曲线
                                    pathMain.set(pathCache);
                                    int size = painterCurrent.getSize();
                                    float x1 = event.getX(),
                                            y1 = event.getY(),
                                            x2 = (float) painterCurrent.getPoint(size - 1).x,
                                            y2 = (float) painterCurrent.getPoint(size - 1).y,
                                            //len = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2)),
                                            dx = (x1 - x2) / 4,//cubic控制杆设为拉出来的新路径的1/4
                                            dy = (y1 - y2) / 4;

                                    pathMain.cubicTo((float) ((EdgeObject) painterCurrent).curve
                                                    .getControlPoint(size - 2, CurveControl.Direction.FROM).x,
                                            (float) ((EdgeObject) painterCurrent).curve
                                                    .getControlPoint(size - 2, CurveControl.Direction.FROM).y,
                                            x2 - dx, y2 - dy, x2, y2);

                                    pathMain.quadTo(x2 + dx, y2 + dy, x1, y1);
                                } else {//直线
                                    pathMain.set(pathCache);
                                    pathMain.lineTo(event.getX(), event.getY());
                                }

                                //movingX = event.getX();
                                //movingY = event.getY();
                                //isMovingDraw = true;
                                //movingDraw(movingX, movingY);//individually invoke moving draw
                                invalidate();
                                //                    if(((LineObject)painterCurrent).editBezier(event.getX(), event.getY()))
                                //                        draw();
                                //                    else{
                                //                        AlgoPoint movingPoint = new AlgoPoint(event.getX(), event.getY());
                                //                        painterCurrent.points.add(movingPoint);
                                //                        movingDraw();
                                //                        painterCurrent.points.remove(movingPoint);
                                //                    }
                            } else if (painterCurrent instanceof SpotObject) {
                                //movingDraw(event.getX(), event.getY());
                                invalidate();
                            }
                        }
                    //surfaceHolder.lockCanvas().restore();
                    }else if (KernelLayout.isEditing) {/**MOVE*/
                        Log.i("eidt move", MainActivity.editPicker.getEditType() + "");
                        if (MainActivity.editPicker.getEditType() != null) {
                            if (MainActivity.editPicker.getEditType()
                                    == EditPicker.EditType.EDIT_POINT
                                    && !editPoint.isHead()) {
                                editPoint.set(KernelLayout.toMapLocation(event.getX(), KernelLayout.COOX),
                                        KernelLayout.toMapLocation(event.getY(), KernelLayout.COOY));
                                try {
                                    DrawObjects.refreshPath((EdgeObject) painterCurrent);//, DrawObjects.FORSCREEN
                                }catch (ClassCastException ex) {

                                }

                                Log.i("moving edit", "editPoint:" + editPoint.x + "::" + editPoint.y);
                                invalidate();
                            } else if (MainActivity.editPicker.getEditType()
                                    == EditPicker.EditType.EDIT_OBJECT
                                    && !editPoint.isHead()) {
                                //relative offset
                                /*DrawObjects.moveGlobal(painterCurrent,
                                        event.getX() - editPoint.x,
                                        event.getY() - editPoint.y);
                                DrawObjects.refreshPath(painterCurrent, DrawObjects.FORSCREEN);*/
                                /*painterCurrent.getPathMain().offset((float) (event.getX() - editPoint.x),
                                        (float) (event.getY() - editPoint.y));*/
                                DrawObjects.end.set(event.getX(), event.getY());
                                invalidate();
                            } else if (MainActivity.editPicker.getEditType()
                                    == EditPicker.EditType.ROTATE) {
                                if (editPoint.isHead() ||
                                        (!editPoint.isHead()
                                                && Math.abs(editPoint.x - event.getX())
                                                < AlgoPoint.CLICK_TOLERANCE
                                                && Math.abs(editPoint.y - event.getY())
                                                < AlgoPoint.CLICK_TOLERANCE)) {
                                    editPoint.set(event.getX(), event.getY());
                                    //editPoint.setAsHead(false);
                                }else {
                                    DrawObjects.end.set(event.getX(), event.getY());
                                }
                                invalidate();
                            }else if (MainActivity.editPicker.getEditType()
                                    == EditPicker.EditType.CUT) {
                                /*pathCache.reset();
                                pathCache.moveTo((float) editPoint.x, (float) editPoint.y);
                                pathCache.lineTo(event.getX(), event.getY());*/
                                DrawObjects.end.set(event.getX(), event.getY());
                                invalidate();
                            }
                        }
                    }else if (painterCurrent != null){
                        Log.i("暂停绘制后再开", painterCurrent.toString());
                        //显示开始停止按钮
                        MainActivity.drawStart.setVisibility(View.GONE);
                        MainActivity.drawPause.setVisibility(View.VISIBLE);

                        KernelLayout.isDrawing = true;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            KernelLayout.mapView.ensurePositionAndScale();

//            绘制当前DrawView背景图片
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//先清空move状态的笔
            //canvas.drawBitmap(background, KernelLayout.currentMatrix, null);
            for (int i = KernelLayout.LEVEL_NUM - 1; i >= 0; i--) {//循环正倒序决定上下层
                Log.i("backgrounds["+i+"]", backgrounds[i] + "");
                if (levelVisible[i] && backgrounds[i] != null)
                    canvas.drawBitmap(backgrounds[i], KernelLayout.currentMatrix, null);
            }

//            若需要显示特殊层次，在最顶层绘制特殊层次图片
            /*if(KernelLayout.showSpecial
                    && KernelLayout.drawViews[4].getVisibility() == GONE
                    && layer == KernelLayout.getLevel() && layer != 4)//TODO what this mean? in special draw
                canvas.drawBitmap(KernelLayout.drawViews[4].getSpecialLayer(),
                        KernelLayout.currentMatrix, null);*/
            if (KernelLayout.showSpecial
                    && levelVisible[4]
                    && KernelLayout.getLevel() == 4)
                canvas.drawBitmap(getSpecialLayer(), KernelLayout.currentMatrix, null);

//            painterCurrent.drawSelf(canvas);

//            if (movingDraw(canvas, movingX, movingY))//canvas given by system
//                return;

            Log.i("onDraw方法", painterCurrent.toString());
            Log.i("isEditing?", KernelLayout.isEditing + "");
            Log.i("isDrawing?", KernelLayout.isDrawing + "");
            Log.i("mmTrans",  KernelLayout.getMmTrans()+"");
            Log.i("scale", KernelLayout.mapView.getScale()+"");
            Log.i("editPoint", "begin:" + editPoint.isHead() + "; x:" + editPoint.x + "; y:" + editPoint.y);
            Log.i("drawObjectslength", drawObjects.size() + "");
            if (painterCurrent instanceof AreaObject)
                painterCurrent.drawSelf(canvas);
            else //point/line
                painterCurrent.drawSelf(canvas, null, null);
            /*canvas.drawPath(pathMain, painterCurrent.getPaintMain());
            if (painterCurrent.getPaintAuxiliary() != null)
                canvas.drawPath(pathMain, painterCurrent.getPaintAuxiliary());*/

/*            if (painterCurrent.getSize() > 0) {
                canvas.drawRect((float) (KernelLayout.toScreenLocation(painterCurrent.getPoint(0).x, KernelLayout.COOX) - 5),
                        (float) (KernelLayout.toScreenLocation(painterCurrent.getPoint(0).y, KernelLayout.COOY) - 5),
                        (float) (KernelLayout.toScreenLocation(painterCurrent.getPoint(0).x, KernelLayout.COOX) + 5),
                        (float) (KernelLayout.toScreenLocation(painterCurrent.getPoint(0).y, KernelLayout.COOY) + 5),
                        DrawObjects.getDrawingPaint());
            }*/
            for(int i = 0; i < painterCurrent.getSize(); i++) {
                Log.i("for map:origin:" + painterCurrent.getPoint(i).x, "x:::y" + painterCurrent.getPoint(i).y);
                float pointScreenX = KernelLayout.toScreenLocation(
                        painterCurrent.getPoint(i).x, KernelLayout.COOX),
                        pointScreenY = KernelLayout.toScreenLocation(
                                painterCurrent.getPoint(i).y, KernelLayout.COOY);
                /*float lastScrX = (float) KernelLayout.toScreenLocation(
                        painterCurrent.getPoint(i - 1).x, KernelLayout.COOX),
                        lastScrY = (float) KernelLayout.toScreenLocation(
                                painterCurrent.getPoint(i - 1).y, KernelLayout.COOY);
                Log.i("forScreen:after trans"+pointScreenX, "x:::y"+pointScreenY);*/
                canvas.drawRect(pointScreenX - 5, pointScreenY - 5,
                        pointScreenX + 5, pointScreenY + 5,
                        DrawObjects.getDrawingPaint());
                /*DrawObjects.getDrawingPaint().setColor(Color.RED);
                canvas.drawLine(lastScrX, lastScrY, pointScreenX, pointScreenY, DrawObjects.getDrawingPaint());
                DrawObjects.getDrawingPaint().setColor(Color.BLACK);*/
            }

            if (KernelLayout.isEditing) {
                for(int i = 0; i < painterCurrent.getSize(); i++) {
                    float pointScreenX = KernelLayout.toScreenLocation(
                            painterCurrent.getPoint(i).x, KernelLayout.COOX),
                            pointScreenY = KernelLayout.toScreenLocation(
                                    painterCurrent.getPoint(i).y, KernelLayout.COOY);

                    /**draw a edit aim star*/
                    canvas.drawCircle(pointScreenX, pointScreenY,
                            6f, DrawObjects.getEditingPaint());
                    canvas.drawCircle(pointScreenX, pointScreenY,
                            20f, DrawObjects.getEditingPaint());
                    canvas.drawLine(pointScreenX - 8f, pointScreenY,
                            pointScreenX - 25f, pointScreenY,
                            DrawObjects.getEditingPaint());
                    canvas.drawLine(pointScreenX + 8f, pointScreenY,
                            pointScreenX + 25f, pointScreenY,
                            DrawObjects.getEditingPaint());
                    canvas.drawLine(pointScreenX, pointScreenY - 8f,
                            pointScreenX, pointScreenY - 25f,
                            DrawObjects.getEditingPaint());
                    canvas.drawLine(pointScreenX, pointScreenY + 8f,
                            pointScreenX, pointScreenY + 25f,
                            DrawObjects.getEditingPaint());

                    if (MainActivity.editPicker.getEditType()
                            == EditPicker.EditType.EDIT_POINT
                            || ((EdgeObject) painterCurrent).curve != null) {
                        CurveControl.drawCurControl(canvas,
                                pointScreenX, pointScreenY,
                                ((EdgeObject) painterCurrent).curve.getControlPoint(i, CurveControl.Direction.FROM));
                        CurveControl.drawCurControl(canvas,
                                pointScreenX, pointScreenY,
                                ((EdgeObject) painterCurrent).curve.getControlPoint(i, CurveControl.Direction.TO));
                    }
                }

                if (MainActivity.editPicker.getEditType() == EditPicker.EditType.ROTATE
                        && !editPoint.isHead()) {
                    DrawObjects.getEditingPaint().setColor(
                            getContext().getResources().getColor(R.color.theme_warm));
                    canvas.drawCircle((float) editPoint.x,
                            (float) editPoint.y,
                            20f, DrawObjects.getEditingPaint());
                    canvas.drawLine((float) editPoint.x - 40f, (float) editPoint.y - 40f,
                            (float) editPoint.x + 40f, (float) editPoint.y + 40f,
                            DrawObjects.getEditingPaint());
                    DrawObjects.getEditingPaint().setColor(
                            getContext().getResources().getColor(R.color.theme_thin_warm));

                    canvas.drawLine((float) DrawObjects.begin.x, (float) DrawObjects.begin.y,
                            (float) DrawObjects.end.x, (float) DrawObjects.end.y,
                            DrawObjects.getDrawingPaint());
                }else if (MainActivity.editPicker.getEditType() == EditPicker.EditType.EDIT_OBJECT
                        && !editPoint.isHead()) {
                    canvas.drawLine((float) editPoint.x, (float) editPoint.y,
                            (float) DrawObjects.end.x, (float) DrawObjects.end.y,
                            DrawObjects.getDrawingPaint());
                }else if (MainActivity.editPicker.getEditType() == EditPicker.EditType.CUT
                        && !editPoint.isHead()) {
                    canvas.drawLine(editPoint.x, editPoint.y,
                            DrawObjects.end.x, DrawObjects.end.y, DrawObjects.getDrawingPaint());
                }
                /*canvas.drawRect((float)pointScreen.x - 5,
                        (float)pointScreen.y - 5,
                        (float)pointScreen.x + 5,
                        (float)pointScreen.y + 5, drawingPaint);*/
            }

        } catch(NullPointerException e) {

        }
    }

    private static void postCanvasChange() {
        if (pathMain != null) {
            //scale = KernelLayout.mapView.getScale();
            DrawObjects.refreshPath((EdgeObject) painterCurrent);//, DrawObjects.FORSCREEN
            painterCurrent.zoomStyle(DrawObjects.CanvasTarget.FORSCREEN.value());
        }

        /*for(int i = 0; i < KernelLayout.drawViews.length; i++)
            KernelLayout.drawViews[i].invalidate();*/
        KernelLayout.drawView.invalidate();

        KernelLayout.mapView.invalidate();
    }

    /**change each point in path in painter to now points+scale+matrix*/
    private static void refreshPath() {
        Path path = painterCurrent.getPathMain();
        CurveControl curve = null;
        int i = 0,
            pointCount = painterCurrent.getSize();
        float pathX, pathY,
                curveFromX, curveFromY,
                curveToX, curveToY;


        KernelLayout.mapView.ensurePositionAndScale();
        path.reset();
        while(i < pointCount) {
            pathX = (float) KernelLayout.toScreenLocation(
                    painterCurrent.getPoint(i).x, KernelLayout.COOX);
            pathY = (float) KernelLayout.toScreenLocation(
                    painterCurrent.getPoint(i).y, KernelLayout.COOY);
            if (((EdgeObject) painterCurrent).curve == null) {//直线
                if (i == 0) {
                    path.moveTo(pathX, pathY);
                }else {
                    path.lineTo(pathX, pathY);
                }
            }else {
                curve = ((EdgeObject) painterCurrent).curve;
                if (i <= 0) {
                    path.moveTo(pathX, pathY);
                }else {
                    curveFromX = (float) KernelLayout.toScreenLocation(
                            curve.bezierF.get(i - 1).x, KernelLayout.COOX);
                    curveFromY = (float) KernelLayout.toScreenLocation(
                            curve.bezierF.get(i-1).y, KernelLayout.COOY);
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

    /**path对象整合成图*/
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    public Bitmap paintToBack(Bitmap background) {
        //Canvas canvas = null;
        //Bitmap newBack = null;
        if (painterCurrent == null)
            return background;


        //Paint paint = new Paint();//test
        Log.i("+++++++paintToBack", painterCurrent+"");
        //try {
            painterCurrent.zoomStyle(DrawObjects.CanvasTarget.FORMAP.value());
            //painterCurrent.zoomStyle(DrawObjects.CanvasTarget.FORSCREEN.value());
            Paint mainCopy = new Paint(),
                    auxiliCopy = null;
            mainCopy.set(painterCurrent.getPaintMain());
            //mainCopy.setStrokeWidth(10);
            //mainCopy.setColor(Color.GREEN);
            if (painterCurrent.getPaintAuxiliary() != null) {
                auxiliCopy = new Paint();
                auxiliCopy.set(painterCurrent.getPaintAuxiliary());
                //auxiliCopy.setStrokeWidth(10);
                //auxiliCopy.setColor(Color.BLUE);
            }
//        switch(this.layer) {
//            case 0: paint.setColor(Color.GREEN);break;
//            case 1: paint.setColor(Color.YELLOW);break;
//            case 2: paint.setColor(Color.BLUE);break;
//            case 3: paint.setColor(Color.BLACK);break;
//            case 4: paint.setColor(Color.RED);
//        }


            Canvas canvas = new Canvas(background);
            //newBack = Bitmap.createBitmap(background.getWidth(), background.getHeight(), background.getConfig());
            //canvas = new Canvas(newBack);
            Log.i("DrawView level visible:", "0:"+levelVisible[0]);
            Log.i("level1", levelVisible[1]+"");
            Log.i("level2", "" + levelVisible[2]);
            Log.i("level3", ""+ levelVisible[3]);
            Log.i("level4", ""+levelVisible[4]);

            canvas.drawBitmap(background, null, canvas.getClipBounds(), null);
        //canvas.save();
            //canvas.setMatrix(KernelLayout.currentMatrix);

            Log.i("paint to back info over", "+++++++++++++++++++++++++++++++++++");

//                设置为上图重叠下图的模式
            PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
            //paint.setXfermode(mode);//test
            mainCopy.setXfermode(mode);
            if (auxiliCopy != null)
                auxiliCopy.setXfermode(mode);

//        canvas.drawRect((float) pointForMap.x - 10, (float) pointForMap.y - 10,
//                (float) pointForMap.x + 10, (float) pointForMap.y + 10, paint);
//            if (!(painterCurrent instanceof SpotObject))
//                DrawObjects.refreshPath(painterCurrent, DrawObjects.FORSCREEN);
            //canvas.setMatrix(KernelLayout.currentMatrix);NO FUNCTION
            painterCurrent.drawSelf(canvas, mainCopy, auxiliCopy, DrawObjects.CanvasTarget.FORMAP);
            //painterCurrent.drawSelf(canvas, mainCopy, auxiliCopy, DrawObjects.CanvasTarget.FORSCREEN);

        //} finally {//TODO try what? finally for what?
            //canvas.restore();
            return background;
            //return background;
        //}
    }
    public Bitmap paintToBack() {
        if (KernelLayout.getLevel() == KernelLayout.ONLY_MAP)
            return null;//TODO 地图底图？？？

        Bitmap blankback = Bitmap.createBitmap(
                backgrounds[KernelLayout.getLevel()].getWidth(),
                backgrounds[KernelLayout.getLevel()].getHeight(),
                backgrounds[KernelLayout.getLevel()].getConfig());
        try {
            Paint mainCopy = new Paint(),
                    auxiliCopy = new Paint();

            Canvas canvas = new Canvas(blankback);
            ArrayList<DrawObject> thisLevelObjs = this.asListPainter();

//                设置为上图重叠下图的模式
            PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
            //paint.setXfermode(mode);//test
            mainCopy.setXfermode(mode);
            if (auxiliCopy != null)
                auxiliCopy.setXfermode(mode);

            for (int i = 0; i < thisLevelObjs.size(); i ++) {
                if (thisLevelObjs.get(i) == painterCurrent)
                    continue;

                thisLevelObjs.get(i).zoomStyle(DrawObjects.CanvasTarget.FORMAP.value());
                mainCopy.set(thisLevelObjs.get(i).getPaintMain());
                //mainCopy.setStrokeWidth(10);
                //mainCopy.setColor(Color.GREEN);
                if (thisLevelObjs.get(i).getPaintAuxiliary() != null) {
                    auxiliCopy.set(thisLevelObjs.get(i).getPaintAuxiliary());
                    //auxiliCopy.setStrokeWidth(10);
                    //auxiliCopy.setColor(Color.BLUE);
                }
                thisLevelObjs.get(i).drawSelf(canvas, mainCopy, auxiliCopy, DrawObjects.CanvasTarget.FORMAP);
            }

        } finally {
            this.backgrounds[KernelLayout.getLevel()] = blankback;
            return blankback;//TODO maybe no mean
        }
    }

    public boolean join(int with) throws ClassCastException{
        boolean success = false;

        if (with == EditPicker.WITH_SELF)
            success = DrawObjects.joinSelf((LineObject) painterCurrent);
        else if (with == EditPicker.WITH_OTHER)
            success = DrawObjects.joinOther((LineObject) painterCurrent);

        if (success)
            invalidate();

        return success;
    }

    public static void reverseLine() throws ClassCastException{
        ((LineObject) painterCurrent).setReverse();
        painterCurrent.zoomStyle(DrawObjects.CanvasTarget.FORSCREEN.value());
        KernelLayout.drawView.invalidate();
    }

    public Bitmap getSpecialLayer() {
        /*if(layer == 4)
            return this.background;
        return null;*/

        return backgrounds[4];
    }

//    public void startDrawInThisView() {
//        if (painterCurrent == null) {
//            //call the choose painter panel
//        }
//    }

    public void finishDraw(int topLevel) {
        //this.background = paintToBack(background);
        //int topLevel = KernelLayout.getLevel();
        if (backgrounds[topLevel] == null)
            initBackground(topLevel);

        backgrounds[topLevel] = paintToBack(backgrounds[topLevel]);
        cleanPainter();
        invalidate();
    }

    private static int countPainter() {

        return 0;
    }

    public ArrayList<DrawObject> asListPainter() {
        return asListPainter(KernelLayout.getLevel());
    }

    public ArrayList<DrawObject> asListPainter(int layer) {
        ArrayList<DrawObject> singleLevelObjects = new ArrayList<>();
//        int kindCount = 0;
//        switch (this.layer+1) {
//            case 1:kindCount = 15;break;
//            case 2:kindCount = 9;break;
//            case 3:kindCount = 18;break;
//            case 4:kindCount = 9;break;
//            case 5:kindCount = 27;break;
//            case 6:kindCount = 6;break;
//            default:
//        }

        //TODO 待改！待改！待改！
        //int layer = KernelLayout.getLevel();
        if (layer == 1) {
            for (int signi = 101; signi <= 115; signi++) {
                if (drawObjects.get(signi) != null)
                    for (DrawObject dobj : drawObjects.get(signi))
                        singleLevelObjects.add(dobj);
            }
            for (int signi = 401; signi <= 409; signi++) {
                if (drawObjects.get(signi) != null)
                    for (DrawObject dobj : drawObjects.get(signi))
                        singleLevelObjects.add(dobj);
            }
        }else if (layer == 2) {
            for (int signi = 201; signi <= 209; signi++) {
                if (drawObjects.get(signi) != null)
                    for (DrawObject dobj : drawObjects.get(signi))
                        singleLevelObjects.add(dobj);
            }
        }else if (layer == 3) {
            for (int signi = 501; signi <= 527; signi++) {
                if (drawObjects.get(signi) != null)
                    for (DrawObject dobj : drawObjects.get(signi))
                        singleLevelObjects.add(dobj);
            }
        }else if (layer == 0) {
            for (int signi = 301; signi <= 318; signi++) {
                if (drawObjects.get(signi) != null)
                    for (DrawObject dobj : drawObjects.get(signi))
                        singleLevelObjects.add(dobj);
            }
        }else if (layer == 4) {
            for (int signi = 601; signi <= 606; signi++) {
                if (drawObjects.get(signi) != null)
                    for (DrawObject dobj : drawObjects.get(signi))
                        singleLevelObjects.add(dobj);
            }
        }

        Log.i("singleLevelObjects'size", singleLevelObjects.size() + "");
        return singleLevelObjects;
    }

    public static void cleanPainter() {
        /*DrawView topView = KernelLayout.drawViews[KernelLayout.getLevel()];
        if (topView.background == null)
            topView.initBackground();
        else
            topView.background = topView.paintToBack(topView.background);
        topView.postInvalidate();*///TODO 线程异步maybe
        if (painterCurrent == null)
            return;

        if (painterCurrent.getSize() > 0) {
            int sign = painterCurrent.getSign();
            //avoid replicated reference
            if (drawObjects.get(sign) != null)
                drawObjects.get(sign)
                    .remove(painterCurrent);
            if (EditPicker.EditType.DELETE
                    != MainActivity.editPicker.getEditType())
                drawObjects.get(sign)
                        .add(painterCurrent);
        }

        painterCurrent = null;
        pathMain =  null;
        pathCache.reset();
        //drawObjects.clear();
    }

    public static void shapeshiftPainter(int sign, int drawType) {
        if (drawObjects.get(sign) == null)
            drawObjects.put(sign, new ArrayList<DrawObject>());

        if (DrawObjects.isPoint(sign)) {
            painterCurrent.setSign(sign);

            return;
        }

        if (DrawObjects.isLine(sign)
                == DrawObjects.isLine(painterCurrent.getSign())) {
            painterCurrent.setSign(sign);
        } else {
            DrawObject shiftPainter = null;
            if (DrawObjects.isLine(sign)) {
                shiftPainter = new LineObject(sign, drawType);

            } else if (DrawObjects.isArea(sign)) {
                shiftPainter = new AreaObject(sign, drawType);
            }else {
                throw new IllegalArgumentException(
                        "shape shift painter mthod got a wrong sign arg");
            }
            int index = 0;
            while (index < painterCurrent.getSize()) {
                shiftPainter.addPoint(painterCurrent.getPoint(index++));
            }
            shiftPainter.getPathMain().set(pathMain);
            painterCurrent = shiftPainter;
        }



        if (drawType == DrawObjects.DRAW_TYPE_STRAIGHT) {
            ((EdgeObject) painterCurrent).curve = null;
        }else {
            //((EdgeObject) painterCurrent).curve = new CurveControl();整合到init method
            ((EdgeObject) painterCurrent).initCurve();
        }

        //KernelLayout.drawViews[KernelLayout.getLevel()].invalidate();
        KernelLayout.drawView.invalidate();
    }

    public void deleteBackground(int layer) {
        if(backgrounds[layer] != null)
            backgrounds[layer].recycle();
    }

    public void deleteBackground() {
        deleteBackground(KernelLayout.getLevel());
    }

    public static HashMap<Integer, ArrayList<DrawObject>> getAllDrawObjects() {
        return drawObjects;
    }

    public static void setAllDrawObjects(HashMap<Integer, ArrayList<DrawObject>> objs) {
        drawObjects = objs;
    }

    public void clearAllDrawObjects() {
        drawObjects.clear();
    }

    public void setFillBmp(TreeMap<Integer, Bitmap> fillBmp){
        this.fillBmp = fillBmp;
    }

    public static boolean isPainterLoad() {
        return painterCurrent != null;
    }
    public static boolean isLoadOne(DrawObject painter) {return painterCurrent == painter;}
    public static boolean classCheck(Class sonType) {
        return painterCurrent.getClass().equals(sonType);
    }

    public static void chooseDrawObject(DrawObject drawObject) {//TODO 调用前clean
        //cleanPainter();
        painterCurrent = drawObject;
        pathMain = drawObject.getPathMain();
    }

    public static void chooseDrawObject(int sign) {
        chooseDrawObject(sign, 0);
    }

    /**check does sign below to area or line or point*/
    public static void chooseDrawObject(int sign, int drawType) {
        Log.i("远方传来一个sign", sign+"");
        Log.i("远方传来一个curl", drawType + "");
        //cleanPainter();
        KernelLayout.isEditing = false;

        if(drawObjects.get(sign) == null){
            drawObjects.put(sign, new ArrayList<DrawObject>());
        }

        Log.i("DragetObjecctType",""+DrawObjects.getObjectType(sign));
        switch (DrawObjects.getObjectType(sign)) {
            case DrawObjects.AREA: Log.i("Enter Area","");
                KernelLayout.isDrawing = true;
                painterCurrent = DrawObjectFactory.createAreaObject(sign, drawType);
                bindPattern(sign);
                break;
            case DrawObjects.LINE: Log.i("Enter line","");
                KernelLayout.isDrawing = true;
                painterCurrent = DrawObjectFactory.createLineObject(sign, drawType);
                break;
            case DrawObjects.SPOT: Log.i("Enter point","that's correct");
                KernelLayout.isDrawing = true;
                painterCurrent = DrawObjectFactory.createSpotObject(sign);break;
            default: Log.i("没有匹配的sign",""+DrawObjects.getObjectType(sign));
                KernelLayout.isDrawing = false;//画笔选择异常，无画笔可用不'开始'绘
        }

        pathMain = painterCurrent.getPathMain();
        //chooseAreaObject(313, true);
    }

    /**drawObjects映射中每个键指向的是每种sign的对象集合arraylist
     * 一个开头就是一个对象，即path只会在开头有一次moveTo()
     * 搜索时可以找层、找arraylist的最大最小坐标*/
/*    public static void chooseLineObject(int sign, int drawType) {
        if(drawObjects.get(sign) == null) {
            drawObjects.put(sign, new ArrayList<DrawObject>());//TODO ArrayList(length);图标最大个数低于16的设置这个
        }

        painterCurrent = new LineObject(sign, drawType);
        //drawObjects.get(sign).add(painterCurrent);
        pathMain = painterCurrent.getPathMain();
        //pathAuxiliary = painterCurrent.getPathAuxiliary();
        //paintAuxiliary = painterCurrent.getPaintAuxiliary();
        //paintMain = painterCurrent.getPaintMain();
    }

    public static void chooseLineObject(int sign) {
        chooseLineObject(sign, DrawObjects.DRAW_TYPE_STRAIGHT);
    }

    public static void choosePointObject(int sign) {
        if(drawObjects.get(sign) == null){
            drawObjects.put(sign, new ArrayList<DrawObject>());
        }

        painterCurrent = new SpotObject(sign);
        //drawObjects.get(sign).add(painterCurrent);
        //paintMain = painterCurrent.getPaintMain();
    }

    public static void chooseAreaObject(int sign, int drawType) {
        *//*if((painterCurrent = drawObjects.get(sign)) == null){
            painterCurrent = new AreaObject(sign, true);
            drawObjects.put(sign, painterCurrent);
        }*//*
        if (drawObjects.get(sign) == null) {
            drawObjects.put(sign, new ArrayList<DrawObject>());
        }

        painterCurrent = new AreaObject(sign, drawType);
        //drawObjects.get(sign).add(painterCurrent);
        pathMain = painterCurrent.getPathMain();
        //paintMain = painterCurrent.getPaintMain();
        //paintAuxiliary = painterCurrent.getPaintAuxiliary();
        //pathAuxiliary = painterCurrent.getPathAuxiliary();

        if (fillBmp == null) {
            //int weight = DrawObjects.getWeight(sign);
            if (KernelLayout.drawView != null)
                loadPattern(KernelLayout.drawView.getContext());

        }


        if(fillBmp.containsKey(Integer.valueOf(sign))){//ceilingEntry
            ((AreaObject)painterCurrent).fillDrawable = new ShapeDrawable(
                    new PathShape(pathMain,50,50));
            ((AreaObject)painterCurrent).fillDrawable.getPaint().setColor(Color.RED);//for test
            Matrix matrix = new Matrix();
            //matrix.setScale(0.1f, 0.1f);
            Bitmap pieceBmp = Bitmap.createBitmap(
                    fillBmp.get(Integer.valueOf(sign)),0,0,
                    fillBmp.get(Integer.valueOf(sign)).getWidth(),
                    fillBmp.get(Integer.valueOf(sign)).getHeight(), matrix, true);
            if (sign == 407 || sign == 605) {
                *//*pieceBmp.setConfig(Bitmap.Config.ARGB_8888);*//*
                *//*ByteArrayOutputStream dataByte = new ByteArrayOutputStream();
                pieceBmp.compress(Bitmap.CompressFormat.PNG, 100, dataByte);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                //opts.inSampleSize = OPT_INSAMPLE_SIZE;
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                pieceBmp = BitmapFactory.decodeByteArray(dataByte.toByteArray(), 0, dataByte.size(), opts);*//*
            }
            Shader pieceShader = new BitmapShader(pieceBmp, Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT);
            if (sign == 407 || sign == 605)
                ((AreaObject)painterCurrent).fillDrawable.getPaint().
                        setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            ((AreaObject)painterCurrent).fillDrawable.getPaint().setShader(pieceShader);
            ((AreaObject)painterCurrent).setPaintMain(((AreaObject) painterCurrent).fillDrawable.getPaint());//测试？paint应该如何与shader与shapeDrawable绑定
            ((AreaObject)painterCurrent).fillDrawable.setBounds(0,0,1080,1920);//绘制时动态设置 TODO 单个绘制对象的像素范围
        }
    }

    public static void chooseAreaObject(int sign) {
        chooseAreaObject(sign, DrawObjects.DRAW_TYPE_STRAIGHT);
    }*/

    /*private boolean movingDraw(float x, float y) {//按住屏幕移动过程显示的线(无风格--only for line(maybe &area
       //按住屏幕移动过程显示的线(无风格--only for line(maybe &area
        //invalidate();
//        backDraw();
//        非点对象绘制样条线
        if (surfaceHolder.getSurface().isValid()) {//legal check
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(background, KernelLayout.currentMatrix, drawingPaint);

            if (!(painterCurrent instanceof SpotObject)) {
                canvas.drawPath(pathMain, drawingPaint);
            }

            //光标十字叉
            canvas.drawLine(x - 10, y, x + 10, y, drawingPaint);
            canvas.drawLine(x, y - 10, x, y + 10, drawingPaint);
        *//*int size = painterCurrent.points.size();
        if(size!=0) {
            float x2 = (float) painterCurrent.points.get(size - 1).x, x1 = (float) painterCurrent.points.get(size - 2).x,
                    y2 = (float) painterCurrent.points.get(size - 1).y, y1 = (float) painterCurrent.points.get(size - 2).y;
            float offsetX = (float) (0.2 * (x2 - x1) / Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
            float offsetY = (float) (0.2 * (y2 - y1) / Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
            canvas.drawLine(x1 - offsetX, y1 - offsetY, x2 - offsetX, y2 - offsetY, paintMain);
            canvas.drawLine(x1 + offsetX, y1 + offsetY, x2 - offsetX, y2 - offsetY, paintMain);
        }*//*

            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        if (painterCurrent instanceof SpotObject)
            invalidate();

        return true;
    }*/

    public static void bindPattern(int sign) {
        if (fillBmp == null) {
            //int weight = DrawObjects.getWeight(sign);
            if (KernelLayout.drawView != null)
                loadPattern(KernelLayout.drawView.getContext());

        }


        if(fillBmp.containsKey(Integer.valueOf(sign))){//ceilingEntry
            ((AreaObject)painterCurrent).fillDrawable = new ShapeDrawable(
                    new PathShape(pathMain,50,50));
            ((AreaObject)painterCurrent).fillDrawable.getPaint().setColor(Color.RED);//for test
            Matrix matrix = new Matrix();
            //matrix.setScale(0.1f, 0.1f);
            Bitmap pieceBmp = Bitmap.createBitmap(
                    fillBmp.get(Integer.valueOf(sign)),0,0,
                    fillBmp.get(Integer.valueOf(sign)).getWidth(),
                    fillBmp.get(Integer.valueOf(sign)).getHeight(), matrix, true);
            if (sign == 407 || sign == 605) {
                /*pieceBmp.setConfig(Bitmap.Config.ARGB_8888);*/
                /*ByteArrayOutputStream dataByte = new ByteArrayOutputStream();
                pieceBmp.compress(Bitmap.CompressFormat.PNG, 100, dataByte);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                //opts.inSampleSize = OPT_INSAMPLE_SIZE;
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                pieceBmp = BitmapFactory.decodeByteArray(dataByte.toByteArray(), 0, dataByte.size(), opts);*/
            }
            Shader pieceShader = new BitmapShader(pieceBmp, Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT);
            if (sign == 407 || sign == 605)
                ((AreaObject)painterCurrent).fillDrawable.getPaint().
                        setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            ((AreaObject)painterCurrent).fillDrawable.getPaint().setShader(pieceShader);
            ((AreaObject)painterCurrent).setPaintMain(((AreaObject) painterCurrent).fillDrawable.getPaint());//测试？paint应该如何与shader与shapeDrawable绑定
            ((AreaObject)painterCurrent).fillDrawable.setBounds(0,0,1080,1920);//绘制时动态设置 TODO 单个绘制对象的像素范围
        }
    }

    public static void loadPattern(Context context) {
        /*TreeMap<Integer, Bitmap> */fillBmp = new TreeMap<Integer, Bitmap>();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        /*203，302，304，307，309，312，313，314，315，408，(605*朝北)*/
        Bitmap temp;
        /*填充类型图例个数12*/
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup203v08);
        fillBmp.put(Integer.valueOf(203), temp);
        temp =BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup302);
        fillBmp.put(Integer.valueOf(302), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup304);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(304), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup307);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(307), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup309);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(309), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup312);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(312), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup313);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(313), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup314);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(314), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup315);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(315), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup408);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(408), temp);

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup407red, options);//石块地
        fillBmp.put(Integer.valueOf(407), temp);
        temp = BitmapFactory.
                decodeResource(context.getResources(), R.drawable.dup605);//id动态调用不可,id无规律
        fillBmp.put(Integer.valueOf(605), temp);
        //Log.i("放入fillBmp", R.string.SANDY + ":" + Integer.valueOf(R.string.SANDY).intValue());
        //Log.i("value of 408",""+Integer.valueOf(408));

        return ;
    }


}