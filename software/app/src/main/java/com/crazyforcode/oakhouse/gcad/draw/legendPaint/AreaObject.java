package com.crazyforcode.oakhouse.gcad.draw.legendPaint;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.draw.assist.CurveControl;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.ObtainTotalMapRunnable;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;

import com.crazyforcode.oakhouse.gcad.R;

import java.util.ArrayList;

public class AreaObject extends EdgeObject {
/*    //    Area Object
    private Path pathMain;

    private Paint paintMain;//Main/In
    private Paint paintAuxiliary;//Au/Out*/
    public ShapeDrawable fillDrawable;

    private double mmSize;

    public AreaObject(int sign, int drawType) {
        initRes();
        points = new ArrayList<AlgoPoint>();

        pathMain = new Path();
        paintMain = new Paint();
        paintAuxiliary = new Paint();
        pathMain.setFillType(Path.FillType.EVEN_ODD);
        if (drawType == DrawObjects.DRAW_TYPE_CURL)
            curve = new CurveControl();
        setSign(sign);
    }

    @Override
    public void setSign(int sign) {
        this.sign=sign;

        setStyle();
        zoomStyle(DrawObjects.CanvasTarget.FORSCREEN.value());
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void setStyle() {
        switch (this.sign){
            case 201:
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_WAVE_BLACK));
                paintMain.setStyle(Paint.Style.FILL);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_IMPASSABILITY_WAVE_BLUE));
                break;
            case 202:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_PASSABILITY_WAVE_BLUE));
                break;
            case 301:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CLEAR_LAND_GREEN));
                break;
            case 303:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_COMPLEX_CLEAR_LAND_GREEN));
                break;
            case 305:
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_PLOUGH_BLACK));
                paintMain.setStyle(Paint.Style.FILL);
                paintMain.setColor(context.getResources().getColor(R.color.AUXILIARY_CONSTRUCTION_WHITE));
                break;
            case 306:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_JOGGING_WOODS_GREEN));
                break;
            case 308:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_SLOW_WOODS_GREEN));
                break;
            case 310:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_DIFFICULT_CURRENT_WOODS_GREEN));
                break;
            case 311:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CANNOT_WOODS_GREEN));
                break;
            case 315:
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_PLOUGH_BLACK));
                break;
            case 403:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setStrokeJoin(Paint.Join.ROUND);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_RLINT_BLACK));
                break;
            case 407:
                //paintMain.setColor(context.getResources().getColor(R.color.MAIN_RLINT_BLACK));
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                /*Path effPath = new Path();
                effPath.addArc(new RectF(0.0f, 0.0f, 6f, 7.2f), 0, 45);//长.72 宽0.6，直角(委曲求全)三角形
                PathEffect randomEff = new DiscretePathEffect(3.0f,5.0f);
                PathEffect shapeEff = new PathDashPathEffect(effPath, 12, 0.96f*2, PathDashPathEffect.Style.ROTATE);
                PathEffect mixEff = new ComposePathEffect(randomEff, shapeEff);
                paintMain.setPathEffect(mixEff);
                //paintAuxiliary.setAlpha(0);*/
                break;
            case 409:
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_STONE_PLATFROM_BLACK));
                break;
            case 408:
            case 605:
                paintAuxiliary.setColor(Color.GREEN);//test 临时改
                paintMain.setColor(Color.RED);//test 临时改
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setAlpha(255);//TODO 0:no limbo
                paintMain.setAlpha(255);
                paintMain.setStyle(Paint.Style.FILL);
                break;
            case 501:
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_CONSTRUCTION_BLACK));
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ROAD_YELLOW));
                paintMain.setAlpha(255);//0-255
                paintMain.setStrokeWidth(0.35f);
                paintAuxiliary.setAntiAlias(true);
                paintAuxiliary.setStrokeWidth(0.49f);
                paintAuxiliary.setStyle(Paint.Style.STROKE);break;
            case 502:
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_CONSTRUCTION_BLACK));
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ROAD_YELLOW));
                paintAuxiliary.setAntiAlias(true);
                paintAuxiliary.setPathEffect(new DashPathEffect(new float[]{2f, 0.25f}, 0));//0.49=0.35+0.07*2
                paintAuxiliary.setStrokeWidth(0.49f);
                paintMain.setStrokeWidth(0.35f);break;
            case 515:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CANNOT_CURRENT_CONSTRUCTION_BLACK));
                paintMain.setStyle(Paint.Style.FILL);
                paintMain.setStrokeCap(Paint.Cap.SQUARE);
                paintMain.setStrokeCap(Paint.Cap.SQUARE);
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_CONSTRUCTION_BLACK));
                break;
            case 516:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CAN_CURRENT_CONSTRUCTION_BLACK));
                paintMain.setStyle(Paint.Style.FILL);
                paintMain.setStrokeCap(Paint.Cap.SQUARE);
                paintAuxiliary.setStrokeCap(Paint.Cap.SQUARE);
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_CONSTRUCTION_BLACK));
                break;
            case 518:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_PAVING_HARD_LAND_BLACK));
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
            case 519:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_PESTRICTED_ZONE_BLACK));
                paintMain.setStyle(Paint.Style.FILL_AND_STROKE);
                break;

        }
    }

    @Override
    public void zoomStyle(boolean isForMap) {
        mmTrans = (Double.isInfinite(KernelLayout.getMmTrans()) || KernelLayout.getMmTrans() < 0?
                1f : (float) KernelLayout.getMmTrans())  * ObtainTotalMapRunnable.getSize();

        switch (this.sign) {
            case 201:
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale()) * mmTrans));
                break;
            /*case 406:
                pathMain.addArc(new RectF(0, 0,
                        (float) (6f * mmTrans / DrawView.scale),
                        (float) (7.2f * mmTrans / DrawView.scale)), 0, (float) (45 * mmTrans / DrawView.scale));//长.72 宽0.6，直角(委曲求全)三角形
                PathEffect randomEff = new DiscretePathEffect((float) (3.0f * mmTrans / DrawView.scale), (float) (5.0f / DrawView.scale));
                PathEffect shapeEff = new PathDashPathEffect(pathMain,
                        (float) (12/DrawView.scale), (float) (0.96f*2 * mmTrans/DrawView.scale), PathDashPathEffect.Style.ROTATE);
                PathEffect mixEff = new ComposePathEffect(randomEff, shapeEff);
                paintMain.setPathEffect(mixEff);
                break;*///本来想用画来实现的石块地
            case 305:
                paintAuxiliary.setStrokeWidth((float) (0.5f * mmTrans / KernelLayout.mapView.getScale()));
                break;
            case 315:
            case 408:
            case 605:
                break;
            case 501:
                paintMain.setStrokeWidth((float)
                        ((isForMap ? 0.35f : 0.35f / KernelLayout.mapView.getScale()) * mmTrans));
                paintAuxiliary.setStrokeWidth((float)
                        ((isForMap ? 0.49f : 0.49f / KernelLayout.mapView.getScale()) * mmTrans));break;
            case 502:
                paintAuxiliary.setPathEffect(
                        new DashPathEffect(
                                new float[]{(float) ((isForMap ? 2f
                                        : 2f / KernelLayout.mapView.getScale()) * mmTrans)
                                        , (float) ((isForMap ? 0.25f
                                        : 0.25f / KernelLayout.mapView.getScale()) * mmTrans)}, 0));//0.49=0.35+0.07*2
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.49f
                        : 0.49f / KernelLayout.mapView.getScale()) * mmTrans));
                paintMain.setStrokeWidth((float) ((isForMap ? 0.35f
                        : 0.35f / KernelLayout.mapView.getScale()) * mmTrans));break;
            case 515:
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.14f
                        : 0.14f / KernelLayout.mapView.getScale()) * mmTrans));break;
        }
    }

    @Override
    public int getSign() {
        return sign;
    }

    @Override
    public int getWeight() {
        return weight;
    }

//    @Override
//    public boolean isHead() {
//        return isHead;
//    }
//
//    @Override
//    public void setAsHead(boolean isHead) {
//        this.isHead = isHead;
//    }


    @Override
    public void drawSelf(Canvas canvas/*, AlgoPoint screenXY*/) {
        drawSelf(canvas, this.paintMain, this.paintAuxiliary, DrawObjects.CanvasTarget.FORSCREEN);
    }

    @Override
    public void drawSelf(Canvas canvas, Paint mainCopy, Paint auxiliCopy) {
        drawSelf(canvas, mainCopy, auxiliCopy, DrawObjects.CanvasTarget.FORSCREEN);//TODO 调整结构
    }

    @Override
    public void drawSelf(Canvas canvas, Paint mainCopy, Paint auxiliCopy, DrawObjects.CanvasTarget target) {
        Log.i(sign + "", "drawSelf");
        //zoomStyle();
        boolean isForMap = target == DrawObjects.CanvasTarget.FORMAP;
        switch (sign) {
            case 201:
            case 305:
            case 505:
            case 507:
                if (isForMap) {
                    Path mapPath = this.getMapPath();
                    canvas.drawPath(mapPath, mainCopy);
                    canvas.drawPath(mapPath, auxiliCopy);
                }else {
                    canvas.drawPath(pathMain, mainCopy);
                    canvas.drawPath(pathMain, auxiliCopy);
                }
                break;
            case 202:
            case 301:
            case 303:
            case 506:
            case 508:
            case 510:
            case 511:
            case 518:
            case 519:
                if (isForMap)
                    canvas.drawPath(this.getMapPath(), mainCopy);
                else
                    canvas.drawPath(pathMain, mainCopy);
                break;
            case 203:
            case 302:
            case 304:
            case 307:
            case 309:
            case 312:
            case 313:
            case 314:
            case 315:
            case 408://pattern 众
                canvas.drawPath(pathMain, auxiliCopy);
                canvas.drawPath(pathMain, mainCopy);
                //canvas.drawPath(pathAuxiliary,((AreaObject)painterCurrent).fillDrawable.getPaint());
                fillDrawable.draw(canvas);
                //canvas.drawPath(pathAuxiliary,paintAuxiliary);
                //((AreaObject)painterCurrent).fillDrawable.setBounds(0,0,480,640);
                break;
            case 407:
            case 605://alpha channel pattern
                canvas.drawPath(pathMain, paintAuxiliary);
                canvas.drawPath(pathMain, paintMain);
                fillDrawable.draw(canvas);
                break;
        }
    }
/*

    @Override
    public Path getPathMain() {
        return pathMain;
    }

    @Override
    public Paint getPaintMain() {
        return paintMain;
    }

    @Override
    public Paint getPaintAuxiliary() {
        return paintAuxiliary;
    }
*/


    //Main放图案，Auxiliary放边界(是图案的情况)
    public void setPaintMain (Paint paintMain) {
        this.paintMain = paintMain;
    }
}
