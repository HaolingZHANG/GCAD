package com.crazyforcode.oakhouse.gcad.draw.legendPaint;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.SumPathEffect;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.draw.assist.CurveControl;

import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.ObtainTotalMapRunnable;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;

import java.util.ArrayList;

public class LineObject extends EdgeObject {
/*    //    Line Object
    private Path pathMain;

    private Paint paintMain;
    private Paint paintAuxiliary;*/

    //private double mmSize;
    private byte reverse = 1;//1 or -1

    public LineObject(int sign, int drawType) {
        initRes();
        points = new ArrayList<AlgoPoint>();
        pathMain = new Path();
        paintMain = new Paint();
        paintAuxiliary = new Paint();//TODO 不一定有此实例
        if (drawType == DrawObjects.DRAW_TYPE_CURL)
            curve = new CurveControl();
        setSign(sign);
    }

    @Override
    public void setSign(int sign) {
        this.sign = sign;

        setStyle();
        zoomStyle(DrawObjects.CanvasTarget.FORSCREEN.value());
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void setStyle() {
        switch (this.sign){
            case 101://等高线
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 102://计曲线
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 104://辅助等高线
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 105://土涯土砍
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintAuxiliary.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintMain.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                break;
            case 106://小土墙、土恆
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintMain.setAlpha(255);
                paintAuxiliary.setAlpha(255);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintAuxiliary.setStyle(Paint.Style.STROKE);break;
            case 107://冲沟
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintMain.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintAuxiliary.setStyle(Paint.Style.FILL_AND_STROKE);
                //paintMain.setStrokeCap(Paint.Cap.SQUARE);
                break;
            case 108://小冲沟
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                break;
            case 204:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_SIMPLE_WAVE_BLUE));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 205:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_SIMPLE_WAVE_BLUE));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 401:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_LINE_BLACK));
                paintMain.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                break;
            case 402:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_RLINT_BLACK));
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_LINE_BLACK));
                paintMain.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                break;
            case 501:
                paintMain.setStyle(Paint.Style.STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ROAD_YELLOW));
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_PLOUGH_BLACK));
                break;
            case 502:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paintMain.setStyle(Paint.Style.STROKE);
                paintMain.setAntiAlias(true);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.MAIN_RLINT_BLACK));
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setAntiAlias(true);
            break;
            case 503:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 504:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 505://bridge
                paintMain.setColor(context.getResources().getColor(R.color.AUXILIARY_CONSTRUCTION_BLACK));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 506://tunnel one side
                paintMain.setColor(context.getResources().getColor(R.color.AUXILIARY_LINE_BLACK));
                break;
            case 507://railway
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_CONSTRUCTION_WHITE));
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.AUXILIARY_PLOUGH_BLACK));
                paintMain.setStyle(Paint.Style.STROKE);
                break;
            case 508://track
                paintMain.setStyle(Paint.Style.STROKE);
                break;//
            case 509:
                paintMain.setStyle(Paint.Style.STROKE);
                //paintMain.setStrokeWidth((float) (0.14f * mmTrans * KernelLayout.mapView.getScale()));
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintAuxiliary.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                break;
            case 510:
                paintMain.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                break;
            case 511:
                paintMain.setStyle(Paint.Style.STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                break;
            case 512:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintMain.setAlpha(255);
                paintAuxiliary.setAlpha(255);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                break;
            case 513://single side
            case 521://double side
            case 522://double side
                paintMain.setStyle(Paint.Style.STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                break;
            case 514://single side
                paintMain.setStyle(Paint.Style.STROKE);
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                break;
            case 520:
                paintMain.setColor(context.getResources().getColor(R.color.MAIN_LADDER_BLACK));
                paintMain.setStrokeJoin(Paint.Join.BEVEL);
                paintMain.setStyle(Paint.Style.STROKE);
                paintAuxiliary.setStrokeJoin(Paint.Join.BEVEL);
                paintAuxiliary.setColor(context.getResources().getColor(R.color.AUXILIARY_CONSTRUCTION_BLACK));
                paintAuxiliary.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    @Override
    public void zoomStyle(boolean isForMap) {//reset stork/circle/effect width
        Log.i("mmTrans", KernelLayout.getMmTrans() + "");//TODO 待删，mmTrans非法判断
        mmTrans = (Double.isInfinite(KernelLayout.getMmTrans()) || KernelLayout.getMmTrans() < 0?
                1f : (float) KernelLayout.getMmTrans())  * ObtainTotalMapRunnable.getSize();

        switch (this.sign){
            case 101://等高线
                paintMain.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans));
                break;
            case 102://计曲线
                paintMain.setStrokeWidth((float) ((isForMap ? 0.35f : 0.35f / KernelLayout.mapView.getScale()) * mmTrans));
                break;
            case 104://辅助等高线
                paintMain.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans));
                paintMain.setPathEffect(new DashPathEffect(
                        new float[]{(float) ((isForMap ? 1.87 : 1.87f / KernelLayout.mapView.getScale()) * mmTrans)
                                , (float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans)}, 0.35f * mmTrans));
                break;
            case 105://土涯土砍
                paintMain.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans));
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.75f : 0.75f / KernelLayout.mapView.getScale())*mmTrans));
                //paintAuxiliary.setPathEffect(new DashPathEffect(new float[]{0.21f * mmTrans, 0.75f * mmTrans}, 0));
                //Rect竖着插在横线上
            {
                Path effectPath = new Path();
                effectPath.addRect(0,
                        (float) ((reverse - 1)/2*(isForMap ? 0.75f : 0.75f / KernelLayout.mapView.getScale())*mmTrans),
                        (float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale())*mmTrans),
                        (float) ((reverse + 1)/2*(isForMap ? 0.75f : 0.75f / KernelLayout.mapView.getScale())*mmTrans),
                        Path.Direction.CCW);//0.75+0.21
                paintAuxiliary.setPathEffect(
                        new PathDashPathEffect(
                                effectPath, (float) ((isForMap ? 0.96f : 0.96f / KernelLayout.mapView.getScale()) * mmTrans), 0,
                                PathDashPathEffect.Style.MORPH));
            }
            //pathMain.addRect(0,(reverse - 1)/2*0.75f*mmTrans,  0.21f*mmTrans, reverse*0.75f*mmTrans, Path.Direction.CCW);//0.75+0.21
            //paintAuxiliary.setPathEffect(new PathDashPathEffect(pathMain, 0.96f*mmTrans, 0, PathDashPathEffect.Style.MORPH));
            break;
            case 106://小土墙、土恆
            {
                Path effectPath = new Path();
                effectPath.addCircle(0, 0, (float) ((isForMap ? 0.3f : 0.3f/ KernelLayout.mapView.getScale())*mmTrans), Path.Direction.CCW);
                PathEffect eff = new PathDashPathEffect(effectPath, (float) ((isForMap ? 3.75f : 3.75f / KernelLayout.mapView.getScale())*mmTrans), 0,
                        PathDashPathEffect.Style.ROTATE);//?
                paintMain.setPathEffect(eff);
            }
            //pathMain.addCircle(0, 0, 0.3f*mmTrans, Path.Direction.CCW);
            //PathEffect eff = new PathDashPathEffect(pathMain, 3.75f*mmTrans, 0, PathDashPathEffect.Style.ROTATE);//?
            //paintMain.setPathEffect(eff);
            paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans));
            break;
            case 107://冲沟
                if (isForMap)
                    paintMain.setStrokeWidth(0.37f * mmTrans);
                else
                    paintMain.setStrokeWidth((float) ((0.37f / KernelLayout.mapView.getScale()) * mmTrans));
                //paintMain.setStrokeCap(Paint.Cap.SQUARE);
                break;
            case 108://小冲沟
            {
                Path effectPath = new Path();
                effectPath.addCircle(0, 0, (float) ((isForMap ? 0.19f : 0.19f / KernelLayout.mapView.getScale())*mmTrans), Path.Direction.CCW);//0.37/2
                PathEffect eff8 = new PathDashPathEffect(effectPath, (float) ((isForMap ? 0.97f : 0.97f / KernelLayout.mapView.getScale())*mmTrans), 0,
                        PathDashPathEffect.Style.MORPH);//0.6+0.37
                paintMain.setPathEffect(eff8);
            }
            //pathMain.addCircle(0, 0, 0.19f*mmTrans, Path.Direction.CCW);//0.37/2
            //PathEffect eff8 = new PathDashPathEffect(pathMain, 0.97f*mmTrans, 0, PathDashPathEffect.Style.MORPH);//0.6+0.37
            //paintMain.setPathEffect(eff8);
            break;
            case 204:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale())*mmTrans));
                break;
            case 205:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans));
                paintMain.setPathEffect(new DashPathEffect(
                        new float[]{(float) ((isForMap ? 1.87f : 1.87f / KernelLayout.mapView.getScale()) * mmTrans)
                                , (float) ((isForMap ? 0.37f : 0.37f / KernelLayout.mapView.getScale()) * mmTrans)}, 0));
                break;
            case 401:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.5f : 0.5f / KernelLayout.mapView.getScale()) * mmTrans));
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.18f : 0.18f / KernelLayout.mapView.getScale()) * mmTrans));
                //paintAuxiliary.setPathEffect(new DashPathEffect(new float[]{0.21f * mmTrans, 0.75f * mmTrans}, 0));
            {
                Path effectPath = new Path();
                effectPath.addRect(0,
                        (float) ((reverse - 1) / 2 * (isForMap ? 0.55f : 0.55f / KernelLayout.mapView.getScale()) * mmTrans),
                        (float) ((isForMap ? 0.18f : 0.18f / KernelLayout.mapView.getScale()) * mmTrans),
                        (float) ((reverse + 1) / 2 * (isForMap ? 0.55f : 0.55f / KernelLayout.mapView.getScale()) * mmTrans), Path.Direction.CCW);//0.75+0.21
                paintAuxiliary.setPathEffect(
                        new PathDashPathEffect(
                                effectPath, (float) ((isForMap ? 0.93f : 0.93f / KernelLayout.mapView.getScale()) * mmTrans), 0,
                                PathDashPathEffect.Style.MORPH));
            }
            //pathMain.addRect((reverse - 1) / 2 * 0.5f * mmTrans, 0, 0.18f * mmTrans, 0.55f * mmTrans, Path.Direction.CCW);//0.75+0.21
            ///paintAuxiliary.setPathEffect(new PathDashPathEffect(pathMain, 0.93f*mmTrans, 0, PathDashPathEffect.Style.MORPH));
            break;
            case 402:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans));
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.18f : 0.18f / KernelLayout.mapView.getScale())*mmTrans));
                //paintAuxiliary.setPathEffect(new DashPathEffect(new float[]{0.21f * mmTrans, 0.75f * mmTrans}, 0));
            {
                Path effectPath = new Path();
                effectPath.addRect(0,
                        (float) ((reverse - 1)/2*(isForMap ? 0.5f : 0.5f / KernelLayout.mapView.getScale())*mmTrans),
                        (float) ((isForMap ? 0.18f : 0.18f / KernelLayout.mapView.getScale())*mmTrans),
                        (float) ((reverse + 1)/2*(isForMap ? 0.5f : 0.5f / KernelLayout.mapView.getScale())*mmTrans), Path.Direction.CCW);//0.75+0.21
                paintAuxiliary.setPathEffect(new PathDashPathEffect(
                        effectPath, (float) ((isForMap ? 0.93f : 0.93f / KernelLayout.mapView.getScale()) * mmTrans), 0,
                        PathDashPathEffect.Style.MORPH));
            }
            //pathMain.addRect((reverse - 1)/2*0.5f*mmTrans, 0, 0.18f*mmTrans, 0.5f*mmTrans, Path.Direction.CCW);//0.75+0.21
            //paintAuxiliary.setPathEffect(new PathDashPathEffect(pathMain, 0.93f*mmTrans, 0, PathDashPathEffect.Style.MORPH));
            break;
            case 501:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans));//0.35-0.07*2
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.35f : 0.35f / KernelLayout.mapView.getScale()) * mmTrans));
                break;
            case 502:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.35f : 0.35f / KernelLayout.mapView.getScale()) * mmTrans));
                paintMain.setPathEffect(new DashPathEffect(
                        new float[]{(float) ((isForMap ? 2f : 2f / KernelLayout.mapView.getScale()) * mmTrans)
                                , (float) ((isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale()) * mmTrans)}, 0));
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.49f : 0.49f / KernelLayout.mapView.getScale()) * mmTrans));//0.35+0.07*2
                paintAuxiliary.setPathEffect(new DashPathEffect(
                        new float[]{(float) ((isForMap ? 2f : 2f / KernelLayout.mapView.getScale()) * mmTrans)
                                , (float) ((isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale()) * mmTrans)}, 0));
                break;
            case 503:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.18f : 0.18f / KernelLayout.mapView.getScale()) * mmTrans));
                paintMain.setPathEffect(new DashPathEffect(
                        new float[]{(float) ((isForMap ? 1f : 1f / KernelLayout.mapView.getScale()) * mmTrans),
                                (float) ((isForMap ? 0.18f : 0.18f / KernelLayout.mapView.getScale()) * mmTrans)},
                        (float) ((isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale()) * mmTrans)));
                break;
            case 504:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.14f : 0.14f / KernelLayout.mapView.getScale()) * mmTrans));
                paintMain.setPathEffect(new DashPathEffect(
                        new float[]{(float) ((isForMap ? 3f : 3f / KernelLayout.mapView.getScale()) * mmTrans),
                                (float) ((isForMap ? 0.14f : 0.14f / KernelLayout.mapView.getScale()) * mmTrans)},
                        (float) ((isForMap ? 0.5f : 0.5f / KernelLayout.mapView.getScale()) * mmTrans)));
                break;
            case 505://bridge
                paintMain.setStrokeWidth((float) ((isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale()) * mmTrans));//main for bridge two side
            {
                Path pathL = new Path(),
                        pathR = new Path();
                pathL.addRect(0, (float) ((isForMap ? 0.45f : 0.45f / KernelLayout.mapView.getScale()) * mmTrans),
                        isForMap ? 0.2f : (float) (0.2f / KernelLayout.mapView.getScale()),
                        (float) ((isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale()) * mmTrans), Path.Direction.CCW);//0.75+0.21
                pathR.addRect(0, (float) (-(isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale()) * mmTrans),
                        isForMap ? 0.2f : (float) (0.2f / KernelLayout.mapView.getScale()),
                        (float) (-(isForMap ? 0.45f : 0.45f / KernelLayout.mapView.getScale()) * mmTrans), Path.Direction.CCW);
                paintMain.setPathEffect(new SumPathEffect(
                        new PathDashPathEffect(pathL, isForMap ? 0.2f : (float) (0.2f / KernelLayout.mapView.getScale()), 0, PathDashPathEffect.Style.ROTATE),
                        new PathDashPathEffect(pathR, isForMap ? 0.2f : (float) (0.2f / KernelLayout.mapView.getScale()), 0, PathDashPathEffect.Style.ROTATE)
                ));
            }
            break;
            case 506://tunnel one side
            {
                Path effectPath = new Path();
                effectPath.addCircle(0, 0, (float) ((isForMap ? 0.13f : 0.13f / KernelLayout.mapView.getScale()) * mmTrans), Path.Direction.CCW);//0.25/2
                PathEffect eff506 = new PathDashPathEffect(
                        effectPath, (float) ((isForMap ? 0.75f : 0.75f / KernelLayout.mapView.getScale())*mmTrans), 0,
                        PathDashPathEffect.Style.MORPH);//0.5+0.25
                paintMain.setPathEffect(eff506);
            }
            //pathMain.addCircle(0, 0, 0.13f * mmTrans, Path.Direction.CCW);//0.25/2
            //PathEffect eff506 = new PathDashPathEffect(pathMain, 0.75f*mmTrans, 0, PathDashPathEffect.Style.MORPH);//0.5+0.25
            //paintMain.setPathEffect(eff506);
            break;
            case 507://railway
                //paintAuxiliary.setPathEffect(new DashPathEffect(new float[]{1.5f, 1.0f}, 0));
            {
                Path effetcPath = new Path();
                effetcPath.addRect(0, (float) ((isForMap ? -0.175f : -0.175f / KernelLayout.mapView.getScale()) * mmTrans),
                        1.0f * mmTrans, (float) ((isForMap ? 0.175f : 0.175f / KernelLayout.mapView.getScale()) * mmTrans), Path.Direction.CCW);//0.75+0.21
                paintAuxiliary.setPathEffect(new PathDashPathEffect(
                        effetcPath, (float) ((isForMap ? 2.5f : 2.5f / KernelLayout.mapView.getScale()) * mmTrans), 0,
                        PathDashPathEffect.Style.MORPH));
            }
            //pathMain.addRect(0, (float) (-0.175f * mmTrans / KernelLayout.mapView.getScale()),
            //        1.0f * mmTrans, (float) (0.175f * mmTrans / KernelLayout.mapView.getScale()), Path.Direction.CCW);//0.75+0.21
            //paintAuxiliary.setPathEffect(new PathDashPathEffect(pathMain, 2.5f * mmTrans, 0, PathDashPathEffect.Style.MORPH));
            paintAuxiliary.setStrokeWidth((float) (0.35f * mmTrans / KernelLayout.mapView.getScale()));
            paintMain.setStrokeWidth((float) (0.55f*mmTrans / KernelLayout.mapView.getScale()));//0.35 + 0.1*2
            //paintMain.getStrokeWidth()
            break;
            case 508://track
                //paintMain.setStrokeWidth((float) (0.1f * mmTrans * KernelLayout.mapView.getScale()));
            {
                Path pathL = new Path(),
                        pathR = new Path();
                pathL.addRect(0, (float) ((isForMap ? 0.275f : 0.275f / KernelLayout.mapView.getScale()) * mmTrans),
                        0.2f, (float) ((isForMap ? 0.175f : 0.175f / KernelLayout.mapView.getScale()) * mmTrans),
                        Path.Direction.CCW);//0.75+0.21
                pathR.addRect(0, (float) ((isForMap ? -0.175f : -0.175f / KernelLayout.mapView.getScale()) * mmTrans),
                        0.2f,  (float) ((isForMap ? -0.275f : -0.275f / KernelLayout.mapView.getScale()) * mmTrans),
                        Path.Direction.CCW);
                paintMain.setPathEffect(new SumPathEffect(
                        new PathDashPathEffect(pathL, 0.2f, 0, PathDashPathEffect.Style.ROTATE),
                        new PathDashPathEffect(pathR, 0.2f, 0, PathDashPathEffect.Style.ROTATE)
                ));
            }
            break;//
            case 509:
                //paintMain.setStrokeWidth((float) (0.14f * mmTrans * KernelLayout.mapView.getScale()));
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.75f : 0.75f / KernelLayout.mapView.getScale())*mmTrans));
                //paintAuxiliary.setPathEffect(new DashPathEffect(new float[]{0.21f * mmTrans, 0.75f * mmTrans}, 0));
            {
                Path effectPath = new Path();
                effectPath.addRect(0, (float) ((isForMap ? 0.74f/2 : 0.74f/2 / KernelLayout.mapView.getScale()) * mmTrans),
                        (float) ((isForMap ? 0.14f : 0.14f / KernelLayout.mapView.getScale()) * mmTrans),
                        (float) ((isForMap ? -0.74f/2 : -0.74f/2 / KernelLayout.mapView.getScale()) * mmTrans),
                        Path.Direction.CCW);//2.5+0.14/2
                paintAuxiliary.setPathEffect(new PathDashPathEffect(
                        effectPath, (float) ((isForMap ? 2.57f : 2.57f / KernelLayout.mapView.getScale()) * mmTrans), 0,
                        PathDashPathEffect.Style.MORPH));
            }
            //pathMain.addRect(0, 0.74f/2 * mmTrans,0.14f * mmTrans,  -0.74f/2 * mmTrans,  Path.Direction.CCW);//2.5+0.14/2
            //paintAuxiliary.setPathEffect(new PathDashPathEffect(pathMain, 2.57f * mmTrans, 0, PathDashPathEffect.Style.MORPH));
            break;
            case 510:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.55f : 0.55f / KernelLayout.mapView.getScale()) * mmTrans));
            {
                Path pathL = new Path(),
                        pathR = new Path();
                pathL.addRect(0, (float) ((isForMap ? 0.89f : 0.89f / KernelLayout.mapView.getScale()) * mmTrans),
                        2f, (float) ((isForMap ? 0.75f : 0.75f / KernelLayout.mapView.getScale()) * mmTrans),
                        Path.Direction.CCW);//0.75+0.21
                pathR.addRect(0, (float) ((isForMap ? -0.75f : -0.75f / KernelLayout.mapView.getScale()) * mmTrans),
                        2f, (float) ((isForMap ? -0.89f : -0.89f / KernelLayout.mapView.getScale()) * mmTrans),
                        Path.Direction.CCW);

                //pathMain.addRect(0.275f * mmTrans, 0, 0.175f * mmTrans, 5f * mmTrans, Path.Direction.CCW);//0.75+0.21
                //pathMain.addRect(-0.175f * mmTrans, 0, -0.275f * mmTrans, 5f * mmTrans, Path.Direction.CCW);
                    /*paintAuxiliary.setPathEffect(new SumPathEffect(
                            new PathDashPathEffect(pathMain, 0.1f, 0, PathDashPathEffect.Style.MORPH),
                            new PathDashPathEffect(pathMain, 0.1f, 0, PathDashPathEffect.Style.MORPH)));*/
                paintMain.setPathEffect(new SumPathEffect(
                        new PathDashPathEffect(pathL, 2f, 0, PathDashPathEffect.Style.ROTATE),
                        new PathDashPathEffect(pathR, 2f, 0, PathDashPathEffect.Style.ROTATE)));
            }
            paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.14f : 0.14f / KernelLayout.mapView.getScale()) * mmTrans));
            break;
            case 511:
                paintMain.setStrokeWidth((float) ((isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale())*mmTrans * KernelLayout.mapView.getScale()));
                break;
            case 512:
            {
                Path effectPath = new Path();
                effectPath.addCircle(0, 0, (float) ((isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans), Path.Direction.CCW);
                PathEffect eff512 = new PathDashPathEffect(effectPath, 3.75f*mmTrans, 0, PathDashPathEffect.Style.MORPH);//?
                paintMain.setPathEffect(eff512);
            }
            //pathMain.addCircle(0, 0, 0.6f*mmTrans, Path.Direction.CCW);
            //PathEffect eff512 = new PathDashPathEffect(pathMain, 3.75f*mmTrans, 0, PathDashPathEffect.Style.MORPH);//?
            //paintMain.setPathEffect(eff512);
            paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale()) * mmTrans));
            break;
            case 513://single side, twins
            /*{
                Path shape = new Path();*//*had exchange x&y*//*
                shape.rMoveTo(0, 0);
                shape.rLineTo(-(isForMap ? 0.16f : 0.16f / KernelLayout.mapView.getScale()) * mmTrans, 0);

                shape.rLineTo((isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                shape.rLineTo((isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                shape.rLineTo(-(isForMap ? 0.375f-0.12f : (0.375f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f : (0.65f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                shape.rMoveTo((isForMap ? 0.9f : 0.9f / KernelLayout.mapView.getScale()) * mmTrans, 0);
                shape.rLineTo(-(isForMap ? 0.16f : 0.16f / KernelLayout.mapView.getScale()) * mmTrans, 0);

                shape.rLineTo((isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                shape.rLineTo((isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                shape.rLineTo(-(isForMap ? 0.375f-0.12f*reverse : (0.375f-0.12f*reverse) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f*reverse : (0.65f-0.07f*reverse) / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                Path effect = new Path();
                effect.addPath(shape);

                float advance = (isForMap ? 3.75f : 3.75f / KernelLayout.mapView.getScale()) * mmTrans;
                paintAuxiliary.setPathEffect(new PathDashPathEffect(effect, advance, 0, PathDashPathEffect.Style.MORPH));
            }*/
            this.fitTeeth(false, true, isForMap);
            paintMain.setStrokeWidth((float) ((isForMap ? 0.4f : 0.4f / KernelLayout.mapView.getScale()) * mmTrans));
            paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.14f : 0.14f / KernelLayout.mapView.getScale()) * mmTrans));
            break;
            case 521://double side,twins
            /*{
                Path shape = new Path();*//*had exchange x&y*//*
                shape.rMoveTo(-(isForMap ? 0.16f : 0.16f / KernelLayout.mapView.getScale()) * mmTrans, 0);

                shape.rLineTo((isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo((isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo(-(isForMap ? 0.375f-0.12f : (0.375f-0.12f) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f : (0.65f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo((isForMap ? 0.375f-0.12f : (0.375f-0.12f) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f : (0.65f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans);
                shape.rLineTo(-(isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans);
                shape.rLineTo(-(isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rMoveTo(-(isForMap ? 0.9f + 0.16f : (0.9f + 0.16f) / KernelLayout.mapView.getScale()) * mmTrans, 0);

                shape.rLineTo((isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo((isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo(-(isForMap ? 0.375f-0.12f : (0.375f-0.12f) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f : (0.65f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo((isForMap ? 0.375f-0.12f : (0.375f-0.12f) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f : (0.65f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans);
                shape.rLineTo(-(isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans);
                shape.rLineTo(-(isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans);

                Path effect = new Path();
                effect.addPath(shape);

                float advance = (isForMap ? 3.75f : 3.75f / KernelLayout.mapView.getScale()) * mmTrans;
                paintAuxiliary.setPathEffect(new PathDashPathEffect(effect, advance, 0, PathDashPathEffect.Style.MORPH));
            }*/
            this.fitTeeth(true, true, isForMap);
            paintMain.setStrokeWidth((float) ((isForMap ? 0.4f : 0.4f / KernelLayout.mapView.getScale()) * mmTrans));
            paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.14f : 0.14f / KernelLayout.mapView.getScale()) * mmTrans));
            break;
            case 522://double side, single teeth
            /*{
                Path shape = new Path();*//*had exchange x&y*//*
                shape.rMoveTo(-(isForMap ? 0.16f : 0.16f / KernelLayout.mapView.getScale()) * mmTrans, 0);

                shape.rLineTo((isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo((isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo(-(isForMap ? 0.375f-0.12f : (0.375f-0.12f) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f : (0.65f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans);

                shape.rLineTo((isForMap ? 0.375f-0.12f : (0.375f-0.12f) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f : (0.65f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans);
                shape.rLineTo(-(isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans);
                shape.rLineTo(-(isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans);

                Path effect = new Path();
                effect.addPath(shape);

                float advance = (isForMap ? 3.75f : 3.75f / KernelLayout.mapView.getScale()) * mmTrans;
                paintAuxiliary.setPathEffect(new PathDashPathEffect(effect, advance, 0, PathDashPathEffect.Style.MORPH));
            }*/
                this.fitTeeth(true, false, isForMap);
                paintMain.setStrokeWidth((float) ((isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale()) * mmTrans));
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.14f : 0.14f / KernelLayout.mapView.getScale()) * mmTrans));
                break;
            case 514://single side
            /*{
                *//**0.16=0.14/sin60, 0.07=0.14*cos60, 0.12=0.14*sin60
                 * 0.65=0.75*sin60, 0.375=0.75*cos60*//*
                Path shape = new Path();*//*had exchange x&y*//*
                shape.rMoveTo(0, 0);
                shape.rLineTo(-(isForMap ? 0.16f : 0.16f / KernelLayout.mapView.getScale()) * mmTrans, 0);

                shape.rLineTo((isForMap ? 0.375f : 0.375f / KernelLayout.mapView.getScale()) * mmTrans,
                        -(isForMap ? 0.65f : 0.65f / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                shape.rLineTo((isForMap ? 0.12f : 0.12f / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.07f : 0.07f / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                shape.rLineTo(-(isForMap ? (0.375f-0.12f) : (0.375f-0.12f) / KernelLayout.mapView.getScale()) * mmTrans,
                        (isForMap ? 0.65f-0.07f : (0.65f-0.07f) / KernelLayout.mapView.getScale()) * mmTrans * reverse);

                Path effect = new Path();
                effect.addPath(shape);

                float advance = (isForMap ? 3.75f : 3.75f / KernelLayout.mapView.getScale()) * mmTrans;
                paintAuxiliary.setPathEffect(new PathDashPathEffect(effect, advance, 0, PathDashPathEffect.Style.MORPH));
            }*/
                this.fitTeeth(false, false, isForMap);
                paintMain.setStrokeWidth((float) ((isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())*mmTrans));
                paintAuxiliary.setStrokeWidth((float) ((isForMap ? 0.14f : 0.14f / KernelLayout.mapView.getScale()) * mmTrans));
                break;
            case 520:
            {
                Path effectPath = new Path();
                effectPath.addRect(0, (float) ((isForMap ? -0.9f : -0.9f / KernelLayout.mapView.getScale()) * mmTrans),
                        (float) ((isForMap ? 0.4f : 0.4f / KernelLayout.mapView.getScale()) * mmTrans),
                        (float) ((isForMap ? 0.9f : 0.9f / KernelLayout.mapView.getScale()) * mmTrans), Path.Direction.CCW);
                paintMain.setPathEffect(new PathDashPathEffect(effectPath,
                        (float) ((isForMap ? 0.5f : 0.5f / KernelLayout.mapView.getScale()) * mmTrans), 0,
                        PathDashPathEffect.Style.MORPH));
            }
                /*pathMain.addRect(0, (float) (-0.9f * mmTrans / KernelLayout.mapView.getScale()),
                        (float) (0.4f * mmTrans / KernelLayout.mapView.getScale()),
                        (float) (0.9f * mmTrans / KernelLayout.mapView.getScale()), Path.Direction.CCW);
                paintMain.setPathEffect(new PathDashPathEffect(pathMain,
                        (float) (0.5f * mmTrans / KernelLayout.mapView.getScale()), 0, PathDashPathEffect.Style.MORPH));*/
            paintAuxiliary.setStrokeWidth((float) ((isForMap ? 2f : 2f / KernelLayout.mapView.getScale()) * mmTrans));
            break;
        }
    }

    public void setReverse() {
        reverse = (byte) -reverse;
    }

    @Override
    public int getSign() {
        return this.sign;
    }

    @Override
    public int getWeight() {
        return weight;
    }

/*    @Override
    public void newMMSize(double mmSize) {
        this.mmSize = mmSize;
        //Draw();
    }*/



    @Override
    public void drawSelf(Canvas canvas/*, AlgoPoint screenXY*/) {

        drawSelf(canvas, this.paintMain, this.paintAuxiliary);
    }

    @Override
    public void drawSelf(Canvas canvas, Paint mainCopy, Paint auxiliCopy) {
        if (!(getSize() > 0)) {
            return;
        }

        if (mainCopy == null) {
            //canvas.drawPath(pathMain, DrawObjects.getDrawingPaint());
            paintMain.setShadowLayer(5, 0, 0, Color.WHITE);
            if (paintAuxiliary != null)
                paintAuxiliary.setShadowLayer(5, 0, 0, Color.WHITE);

            drawSelf(canvas, this.paintMain, this.paintAuxiliary, DrawObjects.CanvasTarget.FORSCREEN);

            paintMain.clearShadowLayer();
            paintAuxiliary.clearShadowLayer();
            Log.i("使用了地球上投", "效果拔群！drawingpaint:"+DrawObjects.getDrawingPaint());
        }else {
            drawSelf(canvas, mainCopy, auxiliCopy, DrawObjects.CanvasTarget.FORSCREEN);
            Log.i("使用了十万伏特", "效果一般。");
        }

    }

    @Override
    public void drawSelf(Canvas canvas, Paint mainCopy, Paint auxiliCopy, DrawObjects.CanvasTarget target)
            throws NullPointerException{
        boolean isForMap = target == DrawObjects.CanvasTarget.FORMAP;
        Log.i("target", target + "");
        Log.i("isFormap", isForMap + "");

        //this.zoomStyle(isForMap); before copy as mainCopy, zoom it
        switch (this.sign) {
            case 101:
            case 102:
            case 104:
                if (isForMap)
                    canvas.drawPath(this.getMapPath(), mainCopy);
                else
                    canvas.drawPath(pathMain, mainCopy);
                break;
            case 105:
            case 106:
                if (isForMap) {
                    Path mapPath = this.getMapPath();
                    canvas.drawPath(mapPath, mainCopy);
                    canvas.drawPath(mapPath, auxiliCopy);
                } else {
                    canvas.drawPath(pathMain, mainCopy);
                    canvas.drawPath(pathMain, auxiliCopy);
                }
                break;
            case 107:
                Path shape = new Path();
                float headXScreen = 0, headYScreen = 0,
                        tailXScreen = 0, tailYScreen = 0;
                if (this.getSize() < 1) {
                    break;
                }else if (this.getSize() == 1) {//one ◆水平 TODO 旋转？
                    AlgoPoint onlyOne = points.get(0);
                    
                    if (isForMap) {
                        shape.moveTo(onlyOne.x, (onlyOne.y - 0.37f * mmTrans / 2));
                        shape.lineTo((onlyOne.x + 0.75f * mmTrans), onlyOne.y);
                        shape.lineTo(onlyOne.x, (onlyOne.y + 0.37f * mmTrans / 2));
                        shape.lineTo((onlyOne.x - 0.75f*mmTrans), onlyOne.y);
                    }else {
                        headXScreen = KernelLayout.toScreenLocation(
                                onlyOne.x, KernelLayout.COOX);
                        headYScreen = KernelLayout.toScreenLocation(
                                onlyOne.x, KernelLayout.COOY);
                        shape.moveTo(headXScreen, headYScreen - 0.37f * mmTrans / 2);
                        shape.lineTo(headXScreen + 0.75f * mmTrans, headYScreen);
                        shape.lineTo(headXScreen, headYScreen + 0.37f * mmTrans / 2);
                        shape.lineTo(headXScreen - 0.75f*mmTrans, headYScreen);
                    }
                    
                    //canvas.drawPath(pathMain, auxiliCopy);
                }else if (curve != null) {
                    AlgoPoint head = points.get(0),
                            headBez = curve.bezierT.get(0),
                            tail = points.get(this.getSize()-1),
                            tailBez = curve.bezierF.get(curve.bezierF.size() - 1);
                    double longestside = Math.sqrt(
                            Math.pow(headBez.x - head.x, 2)
                                    + Math.pow(headBez.y - head.y,2)),
                            ratiox = (headBez.x - head.x) / longestside,
                            ratioy = (headBez.y - head.y) / longestside;
                    
                    if (isForMap) {
                        shape.moveTo((float) (head.x + 0.37f * mmTrans / 2 * ratioy),
                                (float) (head.y - 0.37f * mmTrans / 2 * ratiox));
                        shape.lineTo((float) (head.x - 0.75f * mmTrans * ratiox),
                                (float) (head.y - 0.75 * mmTrans * ratioy));
                        shape.lineTo((float) (head.x - 0.37f * mmTrans / 2 * ratioy),
                                (float) (head.y + 0.37f * mmTrans / 2 * ratiox));
                    } else {
                        headXScreen = KernelLayout.toScreenLocation(head.x, KernelLayout.COOX);
                        headYScreen = KernelLayout.toScreenLocation(head.y, KernelLayout.COOY);
                        shape.moveTo((float) (headXScreen + 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratioy),
                                (float) (headYScreen - 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratiox));
                        shape.lineTo((float) (headXScreen - 0.75f * mmTrans / KernelLayout.mapView.getScale() * ratiox),
                                (float) (headYScreen - 0.75 * mmTrans / KernelLayout.mapView.getScale() * ratioy));
                        shape.lineTo((float) (headXScreen - 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratioy),
                                (float) (headYScreen + 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratiox));
                    }


                    longestside = Math.sqrt(
                            Math.pow(tailBez.x - tail.x, 2)
                                    + Math.pow(tailBez.y - tail.y, 2));
                    ratiox = (tailBez.x - tail.x) / longestside;
                    ratioy = (tailBez.y - tail.y) / longestside;
                    if (isForMap) {
                        shape.moveTo((float) (tail.x - 0.37f * mmTrans / 2 * ratioy),
                                (float) (tail.y + 0.37f * mmTrans / 2 * ratiox));
                        shape.lineTo((float) (tail.x + 0.75f * mmTrans * ratiox),
                                (float) (tail.y + 0.75 * mmTrans * ratioy));
                        shape.lineTo((float) (tail.x + 0.37f*mmTrans / 2 * ratioy),
                                (float)(tail.y - 0.37f*mmTrans / 2 * ratiox));
                    }else {
                        tailXScreen = KernelLayout.toScreenLocation(tail.x, KernelLayout.COOX);
                        tailYScreen = KernelLayout.toScreenLocation(tail.y, KernelLayout.COOY);
                        shape.moveTo((float) (tailXScreen - 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratioy),
                                (float) (tailYScreen + 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratiox));
                        shape.lineTo((float) (tailXScreen + 0.75f * mmTrans / KernelLayout.mapView.getScale() * ratiox),
                                (float) (tailYScreen + 0.75 * mmTrans / KernelLayout.mapView.getScale() * ratioy));
                        shape.lineTo((float) (tailXScreen + 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratioy),
                                (float) (tailYScreen - 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratiox));
                    }

                }else {//直线
                    AlgoPoint head = points.get(0), seco = points.get(1),
                            tail = points.get(this.getSize()-1), notail = points.get(this.getSize() - 2);
                    double longestside = Math.sqrt(
                            Math.pow(head.x - seco.x, 2)
                                    + Math.pow(head.y - seco.y,2)),
                            ratiox = (seco.x - head.x) / longestside,
                            ratioy = (seco.y - head.y) / longestside;
                    if (isForMap) {
                        shape.moveTo((float) (head.x + 0.37f * mmTrans / 2 * ratioy),
                                (float) (head.y - 0.37f * mmTrans / 2 * ratiox));
                        shape.lineTo((float) (head.x - 0.75f * mmTrans * ratiox),
                                (float) (head.y - 0.75 * mmTrans * ratioy));
                        shape.lineTo((float) (head.x - 0.37f * mmTrans / 2 * ratioy),
                                (float) (head.y + 0.37f * mmTrans / 2 * ratiox));
                    }else {
                        headXScreen = KernelLayout.toScreenLocation(head.x, KernelLayout.COOX);
                        headYScreen = KernelLayout.toScreenLocation(head.y, KernelLayout.COOY);
                        shape.moveTo((float) (headXScreen + 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratioy),
                                (float) (headYScreen - 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratiox));
                        shape.lineTo((float) (headXScreen - 0.75f * mmTrans / KernelLayout.mapView.getScale() * ratiox),
                                (float) (headYScreen - 0.75 * mmTrans / KernelLayout.mapView.getScale() * ratioy));
                        shape.lineTo((float) (headXScreen - 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratioy),
                                (float) (headYScreen + 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratiox));
                    }


                    longestside = Math.hypot(tail.x - notail.x, tail.y - notail.y);
                    ratiox = (tail.x - notail.x) / longestside;
                    ratioy = (tail.y - notail.y) / longestside;
                    if (isForMap) {
                        shape.moveTo((float) (tail.x + 0.37f * mmTrans / 2 * ratioy),
                                (float) (tail.y - 0.37f * mmTrans / 2 * ratiox));
                        shape.lineTo((float) (tail.x + 0.75f * mmTrans * ratiox),
                                (float) (tail.y + 0.75 * mmTrans * ratioy));
                        shape.lineTo((float) (tail.x - 0.37f*mmTrans/2*ratioy),
                                (float)(tail.y + 0.37f*mmTrans/2*ratiox));
                    }else {
                        tailXScreen = KernelLayout.toScreenLocation(tail.x, KernelLayout.COOX);
                        tailYScreen = KernelLayout.toScreenLocation(tail.y, KernelLayout.COOY);
                        shape.moveTo((float) (tailXScreen + 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratioy),
                                (float) (tailYScreen - 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratiox));
                        shape.lineTo((float) (tailXScreen + 0.75f * mmTrans / KernelLayout.mapView.getScale() * ratiox),
                                (float) (tailYScreen + 0.75 * mmTrans / KernelLayout.mapView.getScale() * ratioy));
                        shape.lineTo((float) (tailXScreen - 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratioy),
                                (float) (tailYScreen + 0.37f * mmTrans / KernelLayout.mapView.getScale() / 2 * ratiox));
                    }

                }
                canvas.drawPath(shape, auxiliCopy);
                if (isForMap)
                    canvas.drawPath(this.getMapPath(), mainCopy);
                else
                    canvas.drawPath(isForMap? this.getMapPath() : pathMain, mainCopy);
                break;
            case 108:
            case 204:
            case 205:
                canvas.drawPath(isForMap ? this.getMapPath() : pathMain, mainCopy);
                break;
            case 401:
            case 402:
                {
                    Path mapPath = this.getMapPath();
                    canvas.drawPath(isForMap ? mapPath : pathMain, mainCopy);
                    canvas.drawPath(isForMap ? mapPath : pathMain, auxiliCopy);
                }

                break;
            case 501:
            case 502:
                {
                    Path mapPath = this.getMapPath();
                    canvas.drawPath(isForMap ? mapPath : pathMain, auxiliCopy);//must auxi first
                    canvas.drawPath(isForMap ? mapPath : pathMain, mainCopy);
                }
                break;
            case 503:
            case 504:
                canvas.drawPath(isForMap ? this.getMapPath() : pathMain, mainCopy);
                break;
            case 505:
                //if (true/**TODO 小于2，直接删除此桥，对象，集合中*/);改：桥可拐弯

                /*double x1bridgeHead = points.get(0).x * DrawView.scale,
                        y1bridgeHead = points.get(0).y * DrawView.scale,
                        x2bridgeHead = points.get(1).x * DrawView.scale,
                        y2bridgeHead = points.get(1).y * DrawView.scale,
                        slopebridgeHead = Math.sqrt((x2bridgeHead - x1bridgeHead) * (x2bridgeHead - x1bridgeHead)
                                + (y2bridgeHead - y1bridgeHead) * (y2bridgeHead - y1bridgeHead)),
                        cosXHead = (x2bridgeHead - x1bridgeHead) / slopebridgeHead,
                        cosYHead = (y2bridgeHead - y1bridgeHead) / slopebridgeHead,
                        x1bridgeTail = points.get(this.getSize() - 1).x * DrawView.scale,
                        y1bridgeTail = points.get(this.getSize() - 1).y * DrawView.scale,
                        x2bridgeTail = points.get(this.getSize() - 2).x * DrawView.scale,
                        y2bridgeTail = points.get(this.getSize() - 2).y * DrawView.scale,
                        slopebridgeTail = Math.sqrt((x2bridgeTail - x1bridgeTail) * (x2bridgeTail - x1bridgeTail)
                                + (y2bridgeTail - y1bridgeTail) * (y2bridgeTail - y1bridgeTail)),
                        cosXTail = (x2bridgeTail - x1bridgeTail) / slopebridgeTail,
                        cosYTail = (y2bridgeTail - y1bridgeTail) / slopebridgeTail;//桥头到桥尾的线段斜率

                //bridge head
                canvas.drawLine((float) (x1bridgeHead), (float) (y1bridgeHead),
                        (float) (x1bridgeHead + reverse*0.45*mmTrans*DrawView.scale/cosXHead),
                        (float) (y1bridgeHead + reverse*0.45*mmTrans*DrawView.scale/cosYHead),
                        paintMain);
                //bridge tail
                canvas.drawLine((float) (x2bridgeTail), (float) (y2bridgeTail),
                        (float) (x2bridgeTail + 0.45*mmTrans*DrawView.scale/cosXTail),
                        (float) (y2bridgeTail + 0.45*mmTrans*DrawView.scale/cosYTail),
                        paintMain);*/

                //Path path505L = new Path(), path505R = new Path();

                try{
                    AlgoPoint bis505 = null;
                    float offset505 = 0.2f * mmTrans;//one side, forMap, real length
                    float pierWidth = 0.035f * mmTrans;
                    AlgoPoint headBis = AlgoPoint.bisector(null, getPoint(0), getPoint(1));
                    AlgoPoint tailBis = AlgoPoint.bisector(getPoint(getSize() - 2), peekPoint(), null);
                    if (isForMap) {
                        canvas.drawLine((getPoint(0).x - offset505 * (-headBis.y)),
                                (getPoint(0).y - offset505 * (-headBis.x)),
                                (getPoint(0).x - (offset505 + pierWidth) * (-headBis.y)),
                                (getPoint(0).y - (offset505 + pierWidth) * (-headBis.x)),
                                mainCopy);
                        canvas.drawLine((getPoint(0).x + offset505 * (-headBis.y)),
                                (getPoint(0).y + offset505 * (-headBis.x)),
                                (getPoint(0).x + (offset505 + pierWidth) * (-headBis.y)),
                                (getPoint(0).y + (offset505 + pierWidth) * (-headBis.x)),
                                mainCopy);

                        canvas.drawLine((peekPoint().x - offset505 * (-tailBis.y)),
                                (peekPoint().y - offset505 * (-tailBis.x)),
                                (peekPoint().x - (offset505 + pierWidth) * (-tailBis.y)),
                                (peekPoint().y - (offset505 + pierWidth) * (-tailBis.x)),
                                mainCopy);
                        canvas.drawLine((peekPoint().x + offset505 * (-tailBis.y)),
                                (peekPoint().y + offset505 * (-tailBis.x)),
                                (peekPoint().x + (offset505 + pierWidth) * (-tailBis.y)),
                                (peekPoint().y + (offset505 + pierWidth) * (-tailBis.x)),
                                mainCopy);
                    } else {
                        AlgoPoint pointScreen505 = KernelLayout.toScreenLocation(getPoint(0));
                        float offset505Scr = (offset505 / KernelLayout.mapView.getScale());
                        float pierWidthScr = (pierWidth / KernelLayout.mapView.getScale());

                        canvas.drawLine((pointScreen505.x - offset505Scr * (-headBis.y)),
                                (pointScreen505.y - offset505Scr * (-headBis.x)),
                                (pointScreen505.x - (offset505Scr + pierWidthScr) * (-headBis.y)),
                                (pointScreen505.y - (offset505Scr + pierWidthScr) * (-headBis.x)),
                                mainCopy);
                        canvas.drawLine((pointScreen505.x + offset505Scr * (-headBis.y)),
                                (pointScreen505.y + offset505Scr * (-headBis.x)),
                                (pointScreen505.x + (offset505Scr + pierWidthScr) * (-headBis.y)),
                                (pointScreen505.y + (offset505Scr + pierWidthScr) * (-headBis.x)),
                                mainCopy);

                        canvas.drawLine((pointScreen505.x - offset505Scr * (-tailBis.y)),
                                (pointScreen505.y - offset505Scr * (-tailBis.x)),
                                (pointScreen505.x - (offset505Scr + pierWidthScr) * (-tailBis.y)),
                                (pointScreen505.y - (offset505Scr + pierWidthScr) * (-tailBis.x)),
                                mainCopy);
                        canvas.drawLine((pointScreen505.x + offset505Scr * (-tailBis.y)),
                                (pointScreen505.y + offset505Scr * (-tailBis.x)),
                                (pointScreen505.x + (offset505Scr + pierWidthScr) * (-tailBis.y)),
                                (pointScreen505.y + (offset505Scr + pierWidthScr) * (-tailBis.x)),
                                mainCopy);
                    }
                }catch (IndexOutOfBoundsException ex) {
                    //小于2的桥不绘制
                }
                /*for (int i = 1; i + 1 < getSize(); i++) {
                    bis505 = AlgoPoint.bisector(getPoint(i - 1), getPoint(i), getPoint(i + 1));

                    if (isForMap) {
                        if (i == 1) {
                            AlgoPoint headBis = AlgoPoint.bisector(null, getPoint(0), getPoint(1));
                            path505L.moveTo((float) (getPoint(0).x - offset505 * (-headBis.y)),
                                    (float) (getPoint(0).y - offset505 * (-headBis.x)));
                            path505R.moveTo((float) (getPoint(0).x + offset505 * (-headBis.y)),
                                    (float) (getPoint(0).y + offset505 * (-headBis.x)));

                            canvas.drawLine((float) (getPoint(0).x - offset505 * (-headBis.y)),
                                    (float) (getPoint(0).y - offset505 * (-headBis.x)),
                                    (float) (getPoint(0).x - (offset505 + pierWidth) * (-headBis.y)),
                                    (float) (getPoint(0).y - (offset505 + pierWidth) * (-headBis.x)),
                                    mainCopy);
                            canvas.drawLine((float) (getPoint(0).x + offset505 * (-headBis.y)),
                                    (float) (getPoint(0).y + offset505 * (-headBis.x)),
                                    (float) (getPoint(0).x + (offset505 + pierWidth) * (-headBis.y)),
                                    (float) (getPoint(0).y + (offset505 + pierWidth) * (-headBis.x)),
                                    mainCopy);
                        }

                        path505L.lineTo((float) (getPoint(i).x - offset505 * bis505.x),
                                (float) (getPoint(i).y - offset505 * bis505.y));
                        path505R.lineTo((float) (getPoint(i).x + offset505 * bis505.x),
                                (float) (getPoint(i).y + offset505 * bis505.y));

                        if (i + 1 == getSize() - 1) {
                            AlgoPoint tailBis = AlgoPoint.bisector(getPoint(i), getPoint(i + 1), null);
                            path505L.lineTo((float) (getPoint(i + 1).x - offset505 * (-tailBis.y)),
                                    (float) (getPoint(i + 1).y - offset505 * (-tailBis.x)));
                            path505R.lineTo((float) (getPoint(i + 1).x + offset505 * (-tailBis.y)),
                                    (float) (getPoint(i + 1).y + offset505 * (-tailBis.x)));

                            canvas.drawLine((float) (peekPoint().x - offset505 * (-tailBis.y)),
                                    (float) (peekPoint().y - offset505 * (-tailBis.x)),
                                    (float) (peekPoint().x - (offset505 + pierWidth) * (-tailBis.y)),
                                    (float) (peekPoint().y - (offset505 + pierWidth) * (-tailBis.x)),
                                    mainCopy);
                            canvas.drawLine((float) (peekPoint().x + offset505 * (-tailBis.y)),
                                    (float) (peekPoint().y + offset505 * (-tailBis.x)),
                                    (float) (peekPoint().x + (offset505 + pierWidth) * (-tailBis.y)),
                                    (float) (peekPoint().y + (offset505 + pierWidth) * (-tailBis.x)),
                                    mainCopy);
                        }
                    }else *//*for screen*//* {
                        if (i == 1) {
                            AlgoPoint headBis = AlgoPoint.bisector(null, getPoint(0), getPoint(1));
                            path505L.moveTo((float) (pointScreen505.x - offset505Scr * (-headBis.y)),
                                    (float) (pointScreen505.y - offset505Scr * (-headBis.x)));
                            path505R.moveTo((float) (pointScreen505.x + offset505Scr * (-headBis.y)),
                                    (float) (pointScreen505.y + offset505Scr * (-headBis.x)));

                            canvas.drawLine((float) (pointScreen505.x - offset505Scr * (-headBis.y)),
                                    (float) (pointScreen505.y - offset505Scr * (-headBis.x)),
                                    (float) (pointScreen505.x - (offset505Scr + pierWidthScr) * (-headBis.y)),
                                    (float) (pointScreen505.y - (offset505Scr + pierWidthScr) * (-headBis.x)),
                                    mainCopy);
                            canvas.drawLine((float) (pointScreen505.x + offset505Scr * (-headBis.y)),
                                    (float) (pointScreen505.y + offset505Scr * (-headBis.x)),
                                    (float) (pointScreen505.x + (offset505Scr + pierWidthScr) * (-headBis.y)),
                                    (float) (pointScreen505.y + (offset505Scr + pierWidthScr) * (-headBis.x)),
                                    mainCopy);
                        }

                        pointScreen505.setX(KernelLayout.toScreenLocation(getPoint(i).x, KernelLayout.COOX));
                        pointScreen505.setY(KernelLayout.toScreenLocation(getPoint(i).y, KernelLayout.COOY));
                        path505L.lineTo((float) (pointScreen505.x - offset505Scr * bis505.x),
                                (float) (pointScreen505.y - offset505Scr * bis505.y));
                        path505R.lineTo((float) (pointScreen505.x - offset505Scr * bis505.x),
                                (float) (pointScreen505.y - offset505Scr * bis505.y));

                        if (i + 1 == getSize() - 1) {
                            AlgoPoint tailBis = AlgoPoint.bisector(getPoint(i), getPoint(i + 1), null);
                            pointScreen505.setX(KernelLayout.toScreenLocation(peekPoint().x, KernelLayout.COOX));
                            pointScreen505.setY(KernelLayout.toScreenLocation(peekPoint().y, KernelLayout.COOY));

                            path505L.lineTo((float) (pointScreen505.x - offset505Scr * (-tailBis.y)),
                                    (float) (pointScreen505.y - offset505Scr * (-tailBis.x)));
                            path505R.lineTo((float) (pointScreen505.x + offset505Scr * (-tailBis.y)),
                                    (float) (pointScreen505.y + offset505Scr * (-tailBis.x)));

                            canvas.drawLine((float) (pointScreen505.x - offset505Scr * (-tailBis.y)),
                                    (float) (pointScreen505.y - offset505Scr * (-tailBis.x)),
                                    (float) (pointScreen505.x - (offset505Scr + pierWidthScr) * (-tailBis.y)),
                                    (float) (pointScreen505.y - (offset505Scr + pierWidthScr) * (-tailBis.x)),
                                    mainCopy);
                            canvas.drawLine((float) (pointScreen505.x + offset505Scr * (-tailBis.y)),
                                    (float) (pointScreen505.y + offset505Scr * (-tailBis.x)),
                                    (float) (pointScreen505.x + (offset505Scr + pierWidthScr) * (-tailBis.y)),
                                    (float) (pointScreen505.y + (offset505Scr + pierWidthScr) * (-tailBis.x)),
                                    mainCopy);
                            break;
                        }
                    }
                }

                canvas.drawPath(path505L, mainCopy);
                canvas.drawPath(path505R, mainCopy);*/
                canvas.drawPath(isForMap ? getMapPath() : getPathMain(), mainCopy);
                break;
                /*for (int i = 0; ; i++) {
                    if (i == 0) {
                        bisector = AlgoPoint.bisector(null, getPoint(i), getPoint(i + 1));
                        canvas.drawLine((float) (getPoint(i).x + 0.2f * bisector.x),
                                (float) (getPoint(i).y
                                        + (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                                * mmTrans * bisector.y),
                                (float) (getPoint(i).x
                                        + (isForMap ? 0.67f : 0.67f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        + (isForMap ? 0.67f : 0.67f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y), mainCopy);//0.67=0.2+0.47
                        pathL.moveTo((float) (getPoint(i).x
                                        + (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        + (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y));
                        canvas.drawLine((float) (getPoint(i).x
                                        - (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        - (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y),
                                (float) (getPoint(i).x
                                        - (isForMap ? 0.67f : 0.67f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        - (isForMap ? 0.67f : 0.67f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y), mainCopy);
                        pathR.moveTo((float) (getPoint(i).x
                                        - (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        - (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y));
                    } else if (i < this.getSize()) {
                        bisector = AlgoPoint.bisector(getPoint(i -1), getPoint(i), getPoint(i+1));
                        pathL.lineTo((float) (getPoint(i).x
                                        + (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        + (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y));
                        pathR.lineTo((float) (getPoint(i).x
                                        - (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        - (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y));
                    } else {
                        bisector = AlgoPoint.bisector(getPoint(i - 1), getPoint(i), null);
                        pathL.lineTo((float) (getPoint(i).x
                                        + (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        + (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y));
                        pathR.lineTo((float) (getPoint(i).x
                                        - (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.x),
                                (float) (getPoint(i).y
                                        - (isForMap ? 0.2f : 0.2f / KernelLayout.mapView.getScale())
                                        * mmTrans * bisector.y));
                        break;
                    }
                }*/

                /*int size = this.getSize();
                float x2 = (float) points.get(size - 1).x, x1 = (float) points.get(size - 2).x,
                        y2 = (float) points.get(size - 1).y, y1 = (float) points.get(size - 2).y;
            float offsetX= (float) (0.2*(x2-x1)/Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
            float offsetY= (float) (0.2*(y2-y1)/Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
            canvas.drawLine(x1 - offsetX, y1 - offsetY, x2 - offsetX, y2 - offsetY, paintMain);
            canvas.drawLine(x1+offsetX, y1+offsetY, x2-offsetX, y2-offsetY, paintMain);

                //up draw
                canvas.drawRect(new RectF((float) (x2 + 0.2 + 0.1), (float) (y2 + 0.125)//left,top
                        , (float) (x2 + 0.2 + 0.1 + 0.47), (float) (y2 - 0.125))//right,buttom
                        , paintAuxiliary);//RectF use float
                canvas.drawRect(new RectF((float) (x2 - 0.2 - 0.1), (float) (y2 + 0.125)//left,top
                        , (float) (x2 - 0.2 - 0.1 - 0.47), (float) (y2 - 0.125))//right,buttom
                        , paintAuxiliary);

            pathAuxiliary.addRect(new RectF((float) (x2 + 0.2 + 0.1), (float) (y2 + 0.125)//left,top
                    , (float) (x2 + 0.2 + 0.1 + 0.47), (float) (y2 - 0.125))//right,buttom
                    , Path.Direction.CW);
            pathAuxiliary.addRect(new RectF((float)(x2 - 0.2 - 0.1), (float)(y2 + 0.125)//left,top
                    , (float)(x2 - 0.2 - 0.1-0.47), (float)(y2 - 0.125))//right,buttom
                    , Path.Direction.CW);
            */
                /*if (!isHead()) {
                    double x1bridge = points.get(this.getSize() - 1).x * DrawView.scale,
                            y1bridge = points.get(this.getSize() - 1).y * DrawView.scale,
                            x2bridge = screenXY.x,
                            y2bridge = screenXY.y,
                            slopebridge = Math.sqrt((x2bridge - x1bridge) * (x2bridge - x1bridge)
                                    + (y2bridge - y1bridge) * (y2bridge - y1bridge)),
                            cosX = (x2bridge - x1bridge) / slopebridge,
                            cosY = (y2bridge - y1bridge) / slopebridge;//桥头到桥尾的线段斜率

                    //bridge head
                    canvas.drawLine((float) (x1bridge + 0.2*mmTrans*DrawView.scale/cosX),
                            (float) (y1bridge + 0.2*mmTrans*DrawView.scale/cosY),
                            (float) (x1bridge + 0.65*mmTrans*DrawView.scale/cosX),
                            (float) (y1bridge + 0.65*mmTrans*DrawView.scale/cosY),
                            paintMain);
                    canvas.drawLine((float) (x1bridge - 0.2*mmTrans*DrawView.scale/cosX),
                            (float) (y1bridge - 0.2*mmTrans*DrawView.scale/cosY),
                            (float) (x1bridge - 0.65*mmTrans*DrawView.scale/cosX),
                            (float) (y1bridge - 0.65*mmTrans*DrawView.scale/cosY),
                            paintMain);
                    //bridge body
                    canvas.drawLine((float) (x1bridge + 0.2*mmTrans*DrawView.scale/cosX),
                            (float) (y1bridge + 0.2*mmTrans*DrawView.scale/cosY),
                            (float) (x2bridge + 0.2*mmTrans*DrawView.scale/cosX),
                            (float) (y2bridge + 0.2*mmTrans*DrawView.scale/cosY),
                            paintMain);
                    canvas.drawLine((float) (x1bridge - 0.2*mmTrans*DrawView.scale/cosX),
                            (float) (y1bridge - 0.2*mmTrans*DrawView.scale/cosY),
                            (float) (x2bridge - 0.2*mmTrans*DrawView.scale/cosX),
                            (float) (y2bridge - 0.2*mmTrans*DrawView.scale/cosY),
                            paintMain);
                    //bridge tail
                    canvas.drawLine((float) (x2bridge + 0.2*mmTrans*DrawView.scale/cosX),
                            (float) (y2bridge + 0.2*mmTrans*DrawView.scale/cosY),
                            (float) (x2bridge + 0.65*mmTrans*DrawView.scale/cosX),
                            (float) (y2bridge + 0.65*mmTrans*DrawView.scale/cosY),
                            paintMain);
                    canvas.drawLine((float) (x2bridge - 0.2*mmTrans*DrawView.scale/cosX),
                            (float) (y2bridge - 0.2*mmTrans*DrawView.scale/cosY),
                            (float) (x2bridge - 0.65*mmTrans*DrawView.scale/cosX),
                            (float) (y2bridge - 0.65*mmTrans*DrawView.scale/cosY),
                            paintMain);
                }
                setAsHead(!isHead());//桥无转折一点开始一点结束, no path*/
            case 506://隧道:单边绘制
                canvas.drawPath(isForMap ? getMapPath() : pathMain, mainCopy);
                break;
            case 507:
            {
                Path mapPath = this.getMapPath();
                canvas.drawPath(isForMap ? mapPath : pathMain, mainCopy);
                canvas.drawPath(isForMap ? mapPath : pathMain, auxiliCopy);//order
            }
                break;
            case 508:
                /*AlgoPoint bis508 = null;
                AlgoPoint pointScreen508 = KernelLayout.toScreenLocation(getPoint(0));
                Path pathL508 = new Path(), pathR508 = new Path();
                float offset508 = 0.175f * mmTrans;//one side, forMap, real length
                float offset508Scr = (float) (offset508 / KernelLayout.mapView.getScale());

                for (int i = 1; i + 1 < this.getSize(); i++) {//
                    bis508 = AlgoPoint.bisector(this.getPoint(i - 1), this.getPoint(i), this.getPoint(i + 1));
                    if (isForMap) {
                        if (i == 1) {
                            AlgoPoint headBis = AlgoPoint.bisector(getPoint(i - 1), getPoint(i), null);
                            pathL508.moveTo((float) (this.getPoint(i - 1).x - offset508 * -headBis.y),
                                    (float) (this.getPoint(i - 1).y - offset508 * -headBis.x));
                            pathR508.moveTo((float) (this.getPoint(i - 1).x + offset508 * -headBis.y),
                                    (float) (this.getPoint(i- 1).y + offset508 * -headBis.x));
                        }

                        pathL508.lineTo((float) (this.getPoint(i).x - offset508 * bis508.x),
                                (float) (this.getPoint(i).y - offset508 * bis508.y));
                        pathR508.lineTo((float) (this.getPoint(i).x + offset508 * bis508.x),
                                (float) (this.getPoint(i).y + offset508 * bis508.y));

                        if (i + 1 == getSize() - 1) {
                            AlgoPoint tailBis = AlgoPoint.bisector(null, getPoint(i), getPoint(i + 1));
                            pathL508.lineTo((float) (this.getPoint(i + 1).x - offset508 * -tailBis.y),
                                    (float) (this.getPoint(i + 1).y - offset508 * -tailBis.x));
                            pathR508.lineTo((float) (this.getPoint(i + 1).x + offset508 * -tailBis.y),
                                    (float) (this.getPoint(i + 1).y + offset508 * -tailBis.x));
                        }

                    }else*//*trans to srceen*//* {
                        if (i == 1) {
                            AlgoPoint headBis = AlgoPoint.bisector(getPoint(i - 1), getPoint(i), null);
                            pathL508.moveTo((float) (pointScreen508.x - offset508Scr * -headBis.y),
                                    (float) (pointScreen508.y - offset508Scr * -headBis.x));
                            pathR508.moveTo((float) (pointScreen508.x + offset508Scr * -headBis.y),
                                    (float) (pointScreen508.y + offset508Scr * -headBis.x));
                        }

                        pointScreen508.setX(KernelLayout.toScreenLocation(this.getPoint(i).x, KernelLayout.COOX));
                        pointScreen508.setY(KernelLayout.toScreenLocation(this.getPoint(i).y, KernelLayout.COOY));
                        pathL508.lineTo((float) (pointScreen508.x - offset508Scr * bis508.x),
                                (float) (pointScreen508.y - offset508Scr * bis508.y));
                        pathR508.lineTo((float) (pointScreen508.x + offset508Scr * bis508.x),
                                (float) (pointScreen508.y + offset508Scr * bis508.y));

                        if (i + 1 == getSize() - 1) {
                            AlgoPoint tailBis = AlgoPoint.bisector(null, getPoint(i), getPoint(i + 1));
                            pointScreen508.setX(KernelLayout.toScreenLocation(
                                    this.getPoint(i + 1).x, KernelLayout.COOX));
                            pointScreen508.setY(KernelLayout.toScreenLocation(
                                    this.getPoint(i + 1).y, KernelLayout.COOY));

                            pathL508.lineTo((float) (pointScreen508.x - offset508Scr * -tailBis.y),
                                    (float) (pointScreen508.y - offset508Scr * -tailBis.x));
                            pathR508.lineTo((float) (pointScreen508.x + offset508Scr * -tailBis.y),
                                    (float) (pointScreen508.y + offset508Scr * -tailBis.x));
                            break;
                        }
                    }
                }

                //only two point , (one point dont consider
                if (bis508 == null) {
                    try {
                        AlgoPoint singleBis = AlgoPoint.bisector(getPoint(0), getPoint(1), null);

                        pathL508.moveTo((float) ((isForMap ? this.getPoint(0).x
                                        : KernelLayout.toScreenLocation(this.getPoint(0).x, KernelLayout.COOX))
                                        - offset508 * -singleBis.y),
                                (float) ((isForMap ? this.getPoint(0).x
                                        : KernelLayout.toScreenLocation(this.getPoint(0).y, KernelLayout.COOX))
                                        - offset508 * -singleBis.x));
                        pathR508.moveTo((float) ((isForMap ? this.getPoint(0).x
                                        : KernelLayout.toScreenLocation(this.getPoint(0).x, KernelLayout.COOX))
                                        + offset508 * -singleBis.y),
                                (float) ((isForMap ? this.getPoint(0).x
                                        : KernelLayout.toScreenLocation(this.getPoint(0).y, KernelLayout.COOX))
                                        + offset508 * -singleBis.x));

                        pathL508.lineTo((float) ((isForMap ? this.getPoint(1).x
                                        : KernelLayout.toScreenLocation(this.getPoint(1).x, KernelLayout.COOX))
                                        - offset508 * -singleBis.y),
                                (float) ((isForMap ? this.getPoint(1).y
                                        : KernelLayout.toScreenLocation(this.getPoint(1).y, KernelLayout.COOX))
                                        - offset508 * -singleBis.x));
                        pathR508.lineTo((float) ((isForMap ? this.getPoint(1).x
                                        : KernelLayout.toScreenLocation(this.getPoint(1).x, KernelLayout.COOX))
                                        + offset508 * -singleBis.y),
                                (float) ((isForMap ? this.getPoint(1).y
                                        : KernelLayout.toScreenLocation(this.getPoint(1).y, KernelLayout.COOX))
                                        + offset508 * -singleBis.x));
                    }catch (ArrayIndexOutOfBoundsException ex) {
                        ex.printStackTrace();//"508：单点线对象不绘制"
                    }
                }

                canvas.drawPath(pathL508, mainCopy);
                canvas.drawPath(pathR508, mainCopy);*/
                canvas.drawPath(isForMap ? this.getMapPath() : getPathMain(), mainCopy);
                break;
            case 509:
                {
                    Path mapPath = this.getMapPath();
                    canvas.drawPath(isForMap ? mapPath:pathMain, mainCopy);
                    canvas.drawPath(isForMap ? mapPath : pathMain, auxiliCopy);
                }

                break;
            case 510:
                AlgoPoint bis510 = null;
                AlgoPoint pointScreen510 = KernelLayout.toScreenLocation(getPoint(0));
                //Path pathL510 = new Path(), pathR510 = new Path();
                float offset510 = 0.75f * mmTrans;
                float width510 = 0.97f * mmTrans;
                float offset510Scr = (offset510 / KernelLayout.mapView.getScale());
                float width510Scr = (width510 / KernelLayout.mapView.getScale());

                for (int i = 1; i + 1 < this.getSize(); i++) {
                    bis510 = AlgoPoint.bisector(this.getPoint(i - 1), this.getPoint(i), getPoint(i + 1));

                    if (isForMap) {
                        /*if (i == 1) {
                            AlgoPoint headBis = AlgoPoint.bisector(this.getPoint(i - 1), this.getPoint(i), null);

                            pathL510.moveTo((float) (this.getPoint(i - 1).x + offset510
                                            * -headBis.y * (headBis.isHead() ? -1 : 1)),
                                    (float) (this.getPoint(i - 1).y + offset510 * -headBis.x));
                            pathR510.moveTo((float) (this.getPoint(i - 1).x + offset510 * headBis.y),
                                    (float) (this.getPoint(i - 1).y + offset510
                                            * headBis.x * (headBis.isHead() ? -1 : 1)));
                        }*/
                        if (bis510.isHead()) {//对角线fx系数为正，垂直线k则为负，一端--，一端++
                            canvas.drawLine((this.getPoint(i).x - width510 * bis510.x),
                                    (this.getPoint(i).y - width510 * bis510.y),
                                    (this.getPoint(i).x + width510 * bis510.x),
                                    (this.getPoint(i).y + width510 * bis510.y), auxiliCopy);
                        }else {//对角线fx系数为负，垂直线k则为正，一端+-，一端+-(无左右)
                            canvas.drawLine((this.getPoint(i).x + width510 * bis510.x),
                                    (this.getPoint(i).y - width510 * bis510.y),
                                    (this.getPoint(i).x - width510 * bis510.x),
                                    (this.getPoint(i).y + width510 * bis510.y), auxiliCopy);
                        }

                        /*pathL510.lineTo((float) (this.getPoint(i).x + offset510 * bis510.x
                                        * ((getPoint(i + 1).x + getPoint(i - 1).x)/ 2 < getPoint(i).x ? -1 : 1)),
                                (float) (this.getPoint(i).y + offset510 * bis510.y
                                        * ((getPoint(i + 1).y + getPoint(i - 1).y)/2 < getPoint(i).y ? -1 : 1)));
                        pathR510.lineTo((float) (this.getPoint(i).x + offset510 * bis510.x
                                        * ((getPoint(i + 1).x + getPoint(i - 1).x)/ 2 < getPoint(i).x ? 1 : -1)),
                                (float) (this.getPoint(i).y + offset510 * bis510.y
                                        * ((getPoint(i + 1).y + getPoint(i - 1).y)/ 2 < getPoint(i).y ? 1 : -1)));*/

                        /*if (i + 1 == getSize() - 1) {
                            AlgoPoint tailBis = AlgoPoint.bisector(null, this.getPoint(i), this.getPoint(i + 1));
                            pathL510.lineTo((float) (this.getPoint(i + 1).x + offset510
                                            * (-tailBis.y) * (tailBis.isHead() ? -1 : 1)),
                                    (float) (this.getPoint(i + 1).y + offset510 * (-tailBis.x)));
                            pathR510.lineTo((float) (this.getPoint(i + 1).x + offset510 * tailBis.y),
                                    (float) (this.getPoint(i + 1).y + offset510
                                            * tailBis.x * (tailBis.isHead() ? -1 : 1)));
                            break;
                        }*/
                    }else/*trans to srceen*/ {
                        /*if (i == 1) {
                            AlgoPoint headBis = AlgoPoint.bisector(this.getPoint(i - 1), this.getPoint(i), null);
                            pathL510.moveTo((float) (pointScreen510.x + offset510Scr
                                            * -headBis.y * (headBis.isHead() ? -1 : 1)),
                                    (float) (pointScreen510.y + offset510Scr * (-headBis.x)));
                            pathR510.moveTo((float) (pointScreen510.x + offset510Scr * headBis.y),
                                    (float) (pointScreen510.y + offset510Scr
                                            * headBis.x * (headBis.isHead() ? -1 : 1)));
                        }*/

                        pointScreen510.set(KernelLayout.toScreenLocation(
                                this.getPoint(i).x, KernelLayout.COOX),
                                KernelLayout.toScreenLocation(
                                this.getPoint(i).y, KernelLayout.COOY));

                        if (bis510.isHead()) {
                            canvas.drawLine((pointScreen510.x + width510Scr * bis510.y),
                                    (pointScreen510.y - width510Scr * bis510.x),
                                    (pointScreen510.x - width510Scr * bis510.y),
                                    (pointScreen510.y + width510Scr * bis510.x), auxiliCopy);
                        }else {
                            canvas.drawLine((pointScreen510.x + width510Scr * bis510.y),
                                    (pointScreen510.y + width510Scr * bis510.x),
                                    (pointScreen510.x - width510Scr * bis510.y),
                                    (pointScreen510.y - width510Scr * bis510.x), auxiliCopy);
                        }

                        /*pathL510.lineTo((float) (pointScreen510.x - offset510Scr * bis510.x
                                * ((getPoint(i + 1).x + getPoint(i - 1).x)/ 2 < getPoint(i).x ? -1 : 1)),
                                (float) (pointScreen510.y - offset510Scr * bis510.y
                                        * ((getPoint(i + 1).y + getPoint(i - 1).y)/2 < getPoint(i).y ? -1 : 1)));
                        pathR510.lineTo((float) (pointScreen510.x + offset510Scr * bis510.x
                                        * ((getPoint(i + 1).x + getPoint(i - 1).x)/ 2 < getPoint(i).x ? 1 : -1)),
                                (float) (pointScreen510.y + offset510Scr * bis510.y
                                        * ((getPoint(i + 1).y + getPoint(i - 1).y)/ 2 < getPoint(i).y ? 1 : -1)));*/

                        /*if (i + 1 == getSize() - 1) {
                            pointScreen510.setX(KernelLayout.toScreenLocation(
                                    this.getPoint(i + 1).x, KernelLayout.COOX));
                            pointScreen510.setY(KernelLayout.toScreenLocation(
                                    this.getPoint(i + 1).y, KernelLayout.COOY));

                            AlgoPoint tailBis = AlgoPoint.bisector(getPoint(i), getPoint(i + 1), null);
                            pathL510.lineTo((float) (pointScreen510.x - offset510Scr
                                            * (-tailBis.y) * (tailBis.isHead() ? -1 : 1)),
                                    (float) (pointScreen510.y + offset510Scr * (-tailBis.x)));
                            pathR510.lineTo((float) (pointScreen510.x + offset510Scr * tailBis.y),
                                    (float) (pointScreen510.y + offset510Scr
                                            * tailBis.x * (tailBis.isHead() ? -1 : 1)));
                            break;
                        }*/
                    }
                }

                //canvas.drawPath(pathL510, mainCopy);
                //canvas.drawPath(pathR510, mainCopy);
                canvas.drawPath(isForMap ? this.getMapPath() : pathMain, mainCopy);
                break;
            case 511:
                canvas.drawPath(isForMap ? this.getMapPath() : pathMain, mainCopy);
                break;
            case 512:
            /*{
                if (isForMap) {
                    Path mapPath = this.getMapPath();
                    canvas.drawPath(mapPath, mainCopy);
                    canvas.drawPath(mapPath, auxiliCopy);
                }else {
                    canvas.drawPath(pathMain, mainCopy);
                    canvas.drawPath(pathMain, auxiliCopy);
                }
            }
                break;*/
            case 513: /*{
                AlgoPoint bis513 = null;
                float offset1 = 4.65f,
                        offset2 = 4.275f,
                        offset3 = 3.75f,
                        offset4 = 3.375f,
                        offset5 = 0.65f;//0.75/2*√3
                if (!isForMap) {
                    offset1 = offset1 / KernelLayout.mapView.getScale() * mmTrans;
                    offset2 = offset2 / KernelLayout.mapView.getScale() * mmTrans;
                    offset3 = offset3 / KernelLayout.mapView.getScale() * mmTrans;
                    offset4 = offset4 / KernelLayout.mapView.getScale() * mmTrans;
                    offset5 = offset5 / KernelLayout.mapView.getScale() * mmTrans;
                }

                for (int i = 0, j = 1; i + 1 < this.getSize(); i++, j = 1) {
                    AlgoPoint p1 = this.getPoint(i),
                            p2 = this.getPoint(i + 1);
                    Log.i("p1, from points", p1.x + "x, y" + p1.y);
                    if (!isForMap) {
                        p1 = KernelLayout.toScreenLocation(p1);
                        p2 = KernelLayout.toScreenLocation(p2);
                    }
                    bis513 = AlgoPoint.bisector(null, p1, p2); //bisector verticality with the line self
                    float bevelX = (float) ((p2.x - p1.x) / Math.hypot(p2.x - p1.x, p2.y - p1.y)),
                            bevelY = (float) ((p2.y - p1.y) / Math.hypot(p2.x - p1.x, p2.y - p1.y));

                    //3.75+0.9, 3.75-(0.75/2x√3=0.65)
                    //while not surpass end point
                    while (((p1.x + offset1 * bevelX * j <= p2.x) == (p1.x <= p2.x))
                            && ((p1.y + offset1 * bevelY * j <= p2.y) == (p1.y <= p2.y))) {
                        Log.i("hello world", "j:" + j);//TODO test
                        Log.i("offset1", offset1 + "");
                        canvas.drawLine((p1.x + offset3 * bevelX * j),
                                (p1.y + offset3 * bevelY * j),
                                (p1.x + offset4 * bevelX * j + reverse * offset5 * bis513.x),
                                (p1.y + offset4 * bevelY * j + reverse * offset5 * bis513.y), auxiliCopy);
                        canvas.drawLine((p1.x + offset1 * bevelX * j),
                                (p1.y + offset1 * bevelY * j),
                                (p1.x + offset2 * bevelX * j + reverse * offset5 * bis513.x),
                                (p1.y + offset2 * bevelY * j + reverse * offset5 * bis513.y), auxiliCopy);
                        j++;
                    }
                }
            }
                canvas.drawPath(isForMap ? this.getMapPath() : pathMain, mainCopy);
                break;*/
            case 514: /*{
                AlgoPoint bis514 = null;
                float offset1 = 3.75f,
                        offset2 = 3.1f,
                        offset3 = 0.65f;//0.75/2*√3
                if (!isForMap) {
                    offset1 = offset1 / KernelLayout.mapView.getScale() * mmTrans;
                    offset2 = offset2 / KernelLayout.mapView.getScale() * mmTrans;
                    offset3 = offset3 / KernelLayout.mapView.getScale() * mmTrans;
                }

                for (int i = 0, j = 1; i + 1 < this.getSize(); i++, j = 1) {
                    AlgoPoint p1 = this.getPoint(i),
                            p2 = this.getPoint(i + 1);
                    bis514 = AlgoPoint.bisector(null, p1, p2); //bisector verticality with the line self
                    float bevelX = (float) ((p2.x - p1.x) / Math.hypot(p2.x - p1.x, p2.y - p1.y)),
                            bevelY = (float) ((p2.y - p1.y) / Math.hypot(p2.x - p1.x, p2.y - p1.y));
                    //3.75+0.9, 3.75-(0.75/2x√3=0.65)
                    //while not surpass end point
                    *//**bevel = y/x, negative-bevel = -x/y*//*
                    while (((p1.x + offset1 * bevelX * j <= p2.x) == (p1.x <= p2.x))
                            && ((p1.y + offset1 * bevelY * j <= p2.y) == (p1.y <= p2.y))) {
                        Log.i("hello world", "j:" + j);//test loop
                        canvas.drawLine((p1.x + offset1 * bevelX * j),
                                (p1.y + offset1 * bevelY * j),
                                (p1.x + offset2 * bevelX * j + reverse * offset3 * bis514.x),
                                (p1.y + offset2 * bevelY * j + reverse * offset3 * bis514.y), auxiliCopy);
                        j++;
                    }
                }
            }*/
                /*if (isForMap) {
                    Path mapP = this.getMapPath();
                    canvas.drawPath(mapP, auxiliCopy);
                    canvas.drawPath(mapP, mainCopy);
                }else{
                    canvas.drawPath(this.pathMain, auxiliCopy);
                    canvas.drawPath(this.pathMain, mainCopy);
                }
                break;*/
            case 520:
            /*{
                if (isForMap) {
                    Path mapPath = this.getMapPath();
                    canvas.drawPath(mapPath, auxiliCopy);
                    canvas.drawPath(mapPath, mainCopy);
                }else {
                    canvas.drawPath(pathMain, auxiliCopy);
                    canvas.drawPath(pathMain, mainCopy);//order no change
                }
            }
                break;*/
            case 521: /*{
                AlgoPoint bis521 = null;
                float offset1 = 4.65f,
                        offset2 = 4f,
                        offset3 = 3.75f,
                        offset4 = 3.1f,
                        offset5 = 0.65f;//0.75/2*√3
                if (!isForMap) {
                    offset1 = offset1 / KernelLayout.mapView.getScale() * mmTrans;
                    offset2 = offset2 / KernelLayout.mapView.getScale() * mmTrans;
                    offset3 = offset3 / KernelLayout.mapView.getScale() * mmTrans;
                    offset4 = offset4 / KernelLayout.mapView.getScale() * mmTrans;
                    offset5 = offset5 / KernelLayout.mapView.getScale() * mmTrans;
                }

                for (int i = 0, j = 1; i + 1 < this.getSize(); i++, j = 1) {
                    AlgoPoint p1 = this.getPoint(i),
                            p2 = this.getPoint(i + 1);
                    if (!isForMap) {
                        p1 = KernelLayout.toScreenLocation(p1);
                        p2 = KernelLayout.toScreenLocation(p2);
                    }
                    bis521 = AlgoPoint.bisector(null, p1, p2); //bisector verticality with the line self
                    float bevelX = (float) ((p2.x - p1.x) / Math.hypot(p2.x - p1.x, p2.y - p1.y)),
                            bevelY = (float) ((p2.y - p1.y) / Math.hypot(p2.x - p1.x, p2.y - p1.y));
                    //3.75+0.9, 3.75-(0.75/2x√3=0.65)
                    //while not surpass end point
                    while (((p1.x + offset3 * bevelX * j <= p2.x) == (p1.x <= p2.x))
                            && ((p1.y + offset3 * bevelY * j <= p2.y) == (p1.y <= p2.y))) {
                        canvas.drawLine((p1.x + offset3 * bevelX * j),
                                (p1.y + offset3 * bevelY * j),
                                (p1.x + offset4 * bevelX * j + offset5 * bis521.x),
                                (p1.y + offset4 * bevelY * j + offset5 * bis521.y), auxiliCopy);
                        canvas.drawLine((p1.x + offset3 * bevelX * j),
                                (p1.y + offset3 * bevelY * j),
                                (p1.x + offset4 * bevelX * j - offset5 * bis521.x),
                                (p1.y + offset4 * bevelY * j - offset5 * bis521.y), auxiliCopy);
                        canvas.drawLine((p1.x + offset1 * bevelX * j),
                                (p1.y + offset1 * bevelY * j),
                                (p1.x + offset2 * bevelX * j + offset5 * bis521.x),
                                (p1.y + offset2 * bevelY * j + offset5 * bis521.y), auxiliCopy);
                        canvas.drawLine((p1.x + offset1 * bevelX * j),
                                (p1.y + offset1 * bevelY * j),
                                (p1.x + offset2 * bevelX * j - offset5 * bis521.x),
                                (p1.y + offset2 * bevelY * j - offset5 * bis521.y), auxiliCopy);
                        j++;
                    }
                }
            }*/
                /*if (isForMap) {
                    Path mapP = this.getMapPath();
                    canvas.drawPath(mapP, mainCopy);
                    canvas.drawPath(mapP, auxiliCopy);
                }else {
                    canvas.drawPath(pathMain, auxiliCopy);
                    canvas.drawPath(pathMain, mainCopy);
                }
                break;*/
            case 522: /*{
                AlgoPoint bis522 = null;
                float offset1 = 3.75f,
                        offset2 = 3.1f,
                        offset3 = 0.65f;//0.75/2*√3
                if (!isForMap) {
                    offset1 = offset1 / KernelLayout.mapView.getScale() * mmTrans;
                    offset2 = offset2 / KernelLayout.mapView.getScale() * mmTrans;
                    offset3 = offset3 / KernelLayout.mapView.getScale() * mmTrans;
                }

                for (int i = 0, j = 1; i + 1 < this.getSize(); i++, j = 1) {
                    AlgoPoint p1 = this.getPoint(i),
                            p2 = this.getPoint(i + 1);
                    if (!isForMap) {
                        p1 = KernelLayout.toScreenLocation(p1);
                        p2 = KernelLayout.toScreenLocation(p2);
                    }
                    bis522 = AlgoPoint.bisector(null, p1, p2); //bisector verticality with the line self
                    float bevelX = (float) ((p2.x - p1.x) / Math.hypot(p2.x - p1.x, p2.y - p1.y)),
                            bevelY = (float) ((p2.y - p1.y) / Math.hypot(p2.x - p1.x, p2.y - p1.y));
                    //3.75+0.9, 3.75-(0.75/2x√3=0.65)
                    //while not surpass end point
                    while (((p1.x + offset1 * bevelX * j <= p2.x) == (p1.x <= p2.x))
                            && ((p1.y + offset1 * bevelY * j <= p2.y) == (p1.y <= p2.y))) {
                        canvas.drawLine((p1.x + offset1 * bevelX * j),
                                (p1.y + offset1 * bevelY * j),
                                (p1.x + offset2 * bevelX * j + offset3 * bis522.x),
                                (p1.y + offset2 * bevelY * j + offset3 * bis522.y), auxiliCopy);
                        canvas.drawLine((p1.x + offset1 * bevelX * j),
                                (p1.y + offset1 * bevelY * j),
                                (p1.x + offset2 * bevelX * j - offset3 * bis522.x),
                                (p1.y + offset2 * bevelY * j - offset3 * bis522.y), auxiliCopy);
                        j++;
                    }
                }
            }*/
                if (isForMap) {
                    Path mapP = this.getMapPath();
                    canvas.drawPath(mapP, mainCopy);
                    canvas.drawPath(mapP, auxiliCopy);
                }else {
                    canvas.drawPath(pathMain, mainCopy);
                    canvas.drawPath(pathMain, auxiliCopy);
                }
                break;
        }
    }

    /**only be invoke for zoom sign 513,514,521,522*/
    private void fitTeeth(boolean isDoubleSide, boolean isDoubleTeeth, boolean isForMap) {
        Path shape = new Path();/*had exchange x&y*/
        float dimen3 = 0.9f;
        if (!isForMap) {
            dimen3 = dimen3 / KernelLayout.mapView.getScale() * mmTrans;
        }

        buildShape(shape, isDoubleSide, isForMap);
        if (isDoubleTeeth) {
            shape.rMoveTo(dimen3, 0);
            buildShape(shape, isDoubleSide, isForMap);
        }

        Path effect = new Path();
        effect.addPath(shape);

        float advance = (isForMap ? 3.75f : 3.75f / KernelLayout.mapView.getScale()) * mmTrans;
        paintAuxiliary.setPathEffect(new PathDashPathEffect(effect, advance, 0, PathDashPathEffect.Style.MORPH));
    }

    private void buildShape(Path shape, boolean isDoubleSide, boolean isForMap) {
/**0.16=0.14/sin60, 0.07=0.14*cos60, 0.12=0.14*sin60
 * 0.65=0.75*sin60, 0.375=0.75*cos60*/
        float dimen1 = 0.16f,
                dimen1cos = 0.12f,
                dimen1sin = 0.07f;
        float dimen2 = 0.75f,
                dimen2cos = 0.65f,
                dimen2sin = 0.375f;


        if (!isForMap) {
            dimen1 = dimen1 / KernelLayout.mapView.getScale() * mmTrans;
            dimen1cos = dimen1cos / KernelLayout.mapView.getScale() * mmTrans;
            dimen1sin = dimen1sin / KernelLayout.mapView.getScale() * mmTrans;
            dimen2 = dimen2 / KernelLayout.mapView.getScale() * mmTrans;
            dimen2cos = dimen2cos / KernelLayout.mapView.getScale() * mmTrans;
            dimen2sin = dimen2sin / KernelLayout.mapView.getScale() * mmTrans;
        }

        if (isDoubleSide) {
            shape.rMoveTo(-dimen1, 0);
            shape.rLineTo(dimen2sin, -dimen2cos);
            shape.rLineTo(dimen1cos, dimen1sin);
            shape.rLineTo(-(dimen2sin - dimen1cos), dimen2cos - dimen1sin);
            shape.rLineTo(dimen2sin - dimen1cos, dimen2cos - dimen1sin);
            shape.rLineTo(-dimen1cos, dimen1sin);
            shape.rLineTo(-dimen2sin, -dimen2cos);
        }else {
            shape.rMoveTo(0, 0);
            shape.rLineTo(-dimen1, 0);
            shape.rLineTo(dimen2sin, -dimen2cos * reverse);
            shape.rLineTo(dimen1cos, dimen1sin * reverse);
            shape.rLineTo(-(dimen2sin - dimen1 + dimen1cos), (dimen2cos - dimen1sin * reverse) * reverse);
        }
    }

/*    *//**p1 ot p2 is one line, p2 ot p3 is the other, return x/bevel,y/bevel*//*
    private AlgoPoint bisector(AlgoPoint p1, AlgoPoint p2, AlgoPoint p3) {
        if (p2 == null)
            return null;

        AlgoPoint cos = new AlgoPoint();
        double x = 0, y = 0;
        if (p1 == null) {
            x = p3.x - p2.x;
            y = p3.y - p2.y;
        }else if (p3 == null) {
            x = p2.x - p1.x;
            y = p2.y - p1.y;
        }else {
            x = (p1.x + p2.x)/2 - p3.x;
            y = (p1.y + p2.y)/2 - p3.y;
        }

        double bevel = Math.sqrt(x * x + y * y);
        cos.setX(x / bevel);
        cos.setY(y / bevel);
        return cos;
    }*/
}
