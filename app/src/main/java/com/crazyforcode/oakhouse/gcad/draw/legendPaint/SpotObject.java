package com.crazyforcode.oakhouse.gcad.draw.legendPaint;



import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.ObtainTotalMapRunnable;
import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;
import com.crazyforcode.oakhouse.gcad.R;

import java.util.ArrayList;

public class SpotObject extends DrawObject {
//    //    Point Object
//    public static final boolean FORMAP = true,
//                                FORSCREEN = false;

    private Paint paint;

    public SpotObject(int sign) {
        initRes();
        points = new ArrayList<AlgoPoint>();
        paint = new Paint();

        setSign(sign);
    }

    //public void setPosition(MyPoint position)
    //{
    //    this.position = position;
    //}

//    @SuppressLint("ResourceAsColor")
//    public void Draw()
//    {
//        if(position != null) {
//            if (sign == 110) {
//                path = new Path[2];
//                path[0] = new Path();
//                path[1] = new Path();
//                paint = new Paint();
//                paint.setColor(context.getResources().getColor(R.color.landformYellow);
//                path[0].moveTo((float) (position.x - 0.6 * mmSize), (float) (position.y - 0.6 * mmSize));
//                path[0].lineTo((float)(1.2 * mmSize), (float)(1.2 * mmSize));
//                path[1].moveTo((float) (position.x - 0.6 * mmSize), (float) (position.y + 0.6 * mmSize));
//                path[1].lineTo((float)(1.2 * mmSize), (float)(-1.2 * mmSize));
//            }
//        }
//    }


    @Override
    public void setSign(int sign) {
        this.sign = sign;

        setStyle();
        zoomStyle(DrawObjects.CanvasTarget.FORMAP.value());
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void setStyle() {
        switch (this.sign){
            case 109:
            case 110:
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setColor(context.getResources()
                        .getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                break;
            case 111:
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                break;
            case 112:
            case 113:
            case 114:
                paint.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
            case 115:
                paint.setColor(context.getResources().getColor(R.color.MAIN_CONTOUR_LINE_YELLOW));
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 206:
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_SIMPLE_WAVE_BLUE));
                break;
            case 207:
            case 208:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_SIMPLE_WAVE_BLUE));
                break;
            case 209:
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_SIMPLE_WAVE_BLUE));
                break;
            case 316:
                paint.setColor(context.getResources().getColor(R.color.MAIN_DIFFICULT_CURRENT_WOODS_GREEN));
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 317:
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_DIFFICULT_CURRENT_WOODS_GREEN));
                break;
            case 318:
                paint.setColor(context.getResources().getColor(R.color.MAIN_DIFFICULT_CURRENT_WOODS_GREEN));
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 404://盐坑山洞
            case 405:
            case 406://巨石大石
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                break;
            case 517:
            case 523:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                break;
            case 524:
            case 525:
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_ARTIFICIAL_CHARACTERISTIC_OBJECT_BLACK));
                break;
            case 526://石碑
            case 527://特殊人造物
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 603:
            case 605:
            case 606:
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(context.getResources().getColor(R.color.MAIN_TECHNOLOGY_SIGN_PURPLE));

        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void zoomStyle(boolean isForMap) {
        mmTrans = (Double.isInfinite(KernelLayout.getMmTrans()) || KernelLayout.getMmTrans() < 0?
                1f : (float) KernelLayout.getMmTrans())  * ObtainTotalMapRunnable.getSize();

        switch (this.sign){
            case 109:
            case 110:
                paint.setStrokeWidth((float) ((isForMap ? 0.21f : 0.21f / KernelLayout.mapView.getScale())*mmTrans));
                break;
            case 111:
            case 115:
                paint.setStrokeWidth((float) ((isForMap ? 0.25 : 0.25f / KernelLayout.mapView.getScale())*mmTrans));
                break;
                //case 112:只有填充没有边break;
            case 205:
            case 206:
            case 209:paint.setStrokeWidth((float) (0.25f * mmTrans / KernelLayout.mapView.getScale()));
                break;
            case 523:paint.setStrokeWidth((float) (0.25f * mmTrans / KernelLayout.mapView.getScale()));
                break;
            case 524:
            case 525:paint.setStrokeWidth((float) (0.22f * mmTrans / KernelLayout.mapView.getScale()));
                break;
            case 526:
            case 527:paint.setStrokeWidth((float) (0.22f * mmTrans / KernelLayout.mapView.getScale()));//真实直径0.98-0.22/2
                break;
            case 603:
                paint.setStrokeWidth((float) (0.35f * mmTrans / KernelLayout.mapView.getScale()));
                break;
            case 605:
                paint.setStrokeWidth((float) (0.1f * mmTrans / KernelLayout.mapView.getScale()));
                break;
            case 606:
                paint.setStrokeWidth((float) (0.35f * mmTrans / KernelLayout.mapView.getScale()));
                break;
        }
    }

    @Override
    public int getSign() {
        return sign;
    }

    @Override
    public int getWeight()
    {
        return weight;
    }

//    @Override
//    public boolean isHead() {
//        return false;
//    }
//
//    @Override
//    public void setAsHead(boolean isHead) {
//
//    }

    @Override
    public Path getPathMain() {
        return null;
    }

    @Override
    public Paint getPaintMain() {
        return paint;
    }

    @Override
    public Paint getPaintAuxiliary() {
        return null;
    }

    @Override
    public void drawSelf(Canvas canvas/*, AlgoPoint screenXY*/) {
        drawSelf(canvas, this.getPaintMain(), this.getPaintAuxiliary());
    }

    @Override
    public void drawSelf(Canvas canvas, Paint mainCopy, Paint auxiliCopy) {
        if (!(getSize() > 0)) {
            return;
        }

        if (mainCopy == null) {
            this.paint.setShadowLayer(3, 0, 0, Color.WHITE);
            for (int i = 0; i < this.getSize(); i++) {
                canvas.drawLine(KernelLayout.toScreenLocation(getPoint(i).x - 10, KernelLayout.COOX),
                        KernelLayout.toScreenLocation(getPoint(i).y - 10, KernelLayout.COOY),
                        KernelLayout.toScreenLocation(getPoint(i).x + 10, KernelLayout.COOX),
                        KernelLayout.toScreenLocation(getPoint(i).y + 10, KernelLayout.COOY),
                        paint);
                canvas.drawLine(KernelLayout.toScreenLocation(getPoint(i).x + 10, KernelLayout.COOX),
                        KernelLayout.toScreenLocation(getPoint(i).y - 10, KernelLayout.COOY),
                        KernelLayout.toScreenLocation(getPoint(i).x - 10, KernelLayout.COOX),
                        KernelLayout.toScreenLocation(getPoint(i).y + 10, KernelLayout.COOY),
                        paint);
            }
            this.paint.clearShadowLayer();
        }else {
            drawSelf(canvas, mainCopy, auxiliCopy, DrawObjects.CanvasTarget.FORSCREEN);
        }

    }

    @Override
    public void drawSelf (Canvas canvas, Paint mainCopy, Paint auxiliCopy, DrawObjects.CanvasTarget target) {
        double x=0, y=0;
        boolean isForMap = target == DrawObjects.CanvasTarget.FORMAP;
        switch (sign) {
            case 109:
                for (int i = 0; i < points.size(); i++) {//2.25f, 1.6f
                    if (isForMap) {
                        x =  points.get(i).x;
                        y =  points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);

                    }
                    RectF rect = new RectF((float) (x - (isForMap ? 1.125f : 1.125f / KernelLayout.mapView.getScale())*mmTrans),
                            (float) (y - (isForMap ? 0.8f : 0.8f/ KernelLayout.mapView.getScale()) * mmTrans ),
                            (float) (x + (isForMap ? 1.125f : 1.125f / KernelLayout.mapView.getScale())*mmTrans),
                            (float) (y + (isForMap ? 0.8f : 0.8f/ KernelLayout.mapView.getScale())*mmTrans));
                    canvas.drawArc(rect, 95, 175, false, mainCopy);
                    /*rect.set((float) (x - (isForMap ? 0.85f : 0.85f / KernelLayout.mapView.getScale())*mmTrans),
                            (float) (y - (isForMap ? 0.8f : 0.8f/ KernelLayout.mapView.getScale())*mmTrans),
                            (float) (x + (isForMap ? 1.3f : 1.3f / KernelLayout.mapView.getScale())*mmTrans),
                            (float) (y + (isForMap ? 0.8f : 0.8f/ KernelLayout.mapView.getScale())*mmTrans));*/
                    canvas.drawArc(rect, 265, 185, false, mainCopy);
                    canvas.drawLine((float) (x - (isForMap ? 1.125f : 1.125f/ KernelLayout.mapView.getScale()) * mmTrans ), (float) y,
                            (float) (x - (isForMap ? 0.35f : 0.35f/ KernelLayout.mapView.getScale()) * mmTrans), (float) y, mainCopy);
                    canvas.drawLine((float) (x + (isForMap ? 1.125f : 1.125f / KernelLayout.mapView.getScale()) * mmTrans), (float) y,
                            (float) (x + (isForMap ? 0.35f : 0.35f/ KernelLayout.mapView.getScale()) * mmTrans ), (float) y, mainCopy);
                }
                break;
            case 110:
                for (int i = 0; i < points.size(); i++) {
                    if (isForMap) {
                        x =  points.get(i).x;
                        y =  points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }
                    canvas.drawArc(new RectF((float) (x - (isForMap ? 1.13f : 1.13f/KernelLayout.mapView.getScale()) * mmTrans),
                                    (float) (y - (isForMap ? 0.8f : 0.8f/KernelLayout.mapView.getScale()) * mmTrans),
                                    (float) (x + (isForMap ? 1.13f : 1.13f/KernelLayout.mapView.getScale()) * mmTrans),
                                    (float) (y + (isForMap ? 0.8f : 0.8f/KernelLayout.mapView.getScale()) * mmTrans)),
                            0, 360, false, mainCopy);
                }
                break;
            case 111:
                drawSemiCircle(canvas, mainCopy, isForMap);
                break;
            case 112:
                drawTip(canvas, mainCopy, isForMap);
                break;
            case 113:
                drawDisc(canvas, mainCopy, isForMap);
                break;
            case 114:
                Path elli = new Path();
                for (int i = 0; i < points.size(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }

                    elli.moveTo((float) (x + (isForMap ? 0.43f : 0.43f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y - (isForMap ? 0.43f : 0.43f / KernelLayout.mapView.getScale()) * mmTrans));//0.43=1.2/√2/2
                    elli.cubicTo((float) x, (float) (y - (isForMap ? 0.645f : 0.645f/KernelLayout.mapView.getScale()) * mmTrans),//0.86cutof1/3
                            (float) (x - (isForMap ? 0.645f : 0.645f/KernelLayout.mapView.getScale()) * mmTrans), (float) y,
                            (float) (x - (isForMap? 0.43f : 0.43f/KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap? 0.43f : 0.43f/KernelLayout.mapView.getScale()) * mmTrans));
                    elli.cubicTo((float) x, (float) (y + (isForMap ? 0.645 : 0.645f/KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x + (isForMap ? 0.645f : 0.645f/KernelLayout.mapView.getScale())*mmTrans), (float) y,
                            (float) (x + (isForMap? 0.43f : 0.43f/KernelLayout.mapView.getScale())*mmTrans),
                            (float) (y - (isForMap? 0.43f : 0.43f/KernelLayout.mapView.getScale())*mmTrans));
                }
                canvas.drawPath(elli, mainCopy);
                break;
            case 115:
                drawCross(canvas, mainCopy, isForMap);
                break;
            case 206:
                drawCircle(canvas, mainCopy, isForMap);
                break;
            case 207:
                drawSemiCircle(canvas, mainCopy, isForMap);
                break;
            case 208:
                drawTip(canvas, mainCopy, isForMap);
                break;
            case 209:
                drawCross(canvas, mainCopy, isForMap);
                break;
            case 316:
                drawDisc(canvas, mainCopy, isForMap);
                break;
            case 317:
                drawCircle(canvas, mainCopy, isForMap);
                break;
            case 318:
                drawCross(canvas, mainCopy, isForMap);
                break;
            case 404:drawTip(canvas, mainCopy, isForMap);
                break;
            case 405:
                for (int i = 0; i < points.size(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }

                    canvas.drawCircle((float) x, (float) y,
                            (isForMap ? 0.9f : 0.9f/KernelLayout.mapView.getScale()) * mmTrans / 2, mainCopy);
                }
            case 406:
                drawDisc(canvas, mainCopy, isForMap);
                break;
            case 517:
                for (int i = 0; i < this.getSize(); i++){
                    AlgoPoint point = points.get(i);
                    canvas.drawRect((point.x - (isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale()) * mmTrans),
                            (point.y - (isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale()) * mmTrans),
                            (point.x + (isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale()) * mmTrans),
                            (point.y + (isForMap ? 0.25f : 0.25f / KernelLayout.mapView.getScale()) * mmTrans), mainCopy);
                }
                break;
            case 523:
                for (int i = 0; i < this.getSize(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }
                    canvas.drawCircle((float) x, (float) y,
                            (float) ((isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans), mainCopy);//o
                    canvas.drawLine((float) (x - (isForMap ? 0.81f : 0.81f / KernelLayout.mapView.getScale()) * mmTrans),//0.21+0.6
                            (float) y,
                            (float) (x + (isForMap ? 0.81f : 0.81f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) y, mainCopy);//----
                    canvas.drawLine((float) x,
                            (float) (y - (isForMap ? 0.81f : 0.81f / KernelLayout.mapView.getScale())),
                            (float) x,
                            (float) (y + (isForMap ? 0.81f : 0.81f / KernelLayout.mapView.getScale())), mainCopy);//||||
                }
                break;
            case 524:
                for (int i = 0; i < this.getSize(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }

                    canvas.drawLine((float) (x - (isForMap ? 0.75f : 0.75f/KernelLayout.mapView.getScale())*mmTrans),
                            (float) y,
                            (float) (x + (isForMap ? 0.75f : 0.75f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) y, mainCopy);
                    canvas.drawLine((float) x, (float) y,
                            (float) x,
                            (float) (y + (isForMap ? 1.5f : 1.5f / KernelLayout.mapView.getScale())*mmTrans), mainCopy);
                }
                break;
            case 525:
                for (int i = 0; i < this.getSize(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }
                    canvas.drawLine((float) x, (float) y,
                            (float) (x + (isForMap ? 0.74f : 0.74f / KernelLayout.mapView.getScale())*mmTrans),
                            (float) (y + (isForMap ? 0.43f : 0.43f / KernelLayout.mapView.getScale())*mmTrans), mainCopy);
                    canvas.drawLine((float) x, (float) y,
                            (float) (x - (isForMap ? 0.74f : 0.74f / KernelLayout.mapView.getScale())*mmTrans),
                            (float) (y + (isForMap ? 0.43f : 0.43f / KernelLayout.mapView.getScale())*mmTrans), mainCopy);
                    canvas.drawLine((float) x, (float) y,
                            (float) x,
                            (float) (y + (isForMap ? 1.5f : 1.5f / KernelLayout.mapView.getScale())*mmTrans), mainCopy);
                }
                break;
            case 526:
                for (int i =0; i < this.getSize(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }

                    //radius*2 = 0.98-0.22/2//////0.09+0.11=0.2
                    canvas.drawCircle((float) x, (float) y,
                            (float) ((isForMap ? 0.43f : 0.43f / KernelLayout.mapView.getScale()) * mmTrans), mainCopy);
                    canvas.drawCircle((float) x, (float) y,
                            (float) (0.09f * mmTrans / KernelLayout.mapView.getScale()), mainCopy);
                }
                break;
            case 527:
                for (int i = 0; i < this.getSize(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }

                    canvas.drawLine((float) (x - (isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y - (isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x + (isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans), mainCopy);
                    canvas.drawLine((float) (x + (isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y - (isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x - (isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans), mainCopy);
                }
                break;
            case 603://TODO rotate?
                for (int i = 0; i < this.getSize(); i++) {
                    //0.9+5*2, 3/2+6
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }

                    canvas.drawArc(new RectF((float) (x - 6f / KernelLayout.mapView.getScale() * mmTrans),
                            (float) (y - (isForMap ? 0.9f : 10.9f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x + (isForMap ? 6f : 6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y - (isForMap ? 0.9f : 0.9f / KernelLayout.mapView.getScale()) * mmTrans)), 75, 30, false, mainCopy);//up
                    canvas.drawArc(new RectF((float) (x - (isForMap ? 6f : 6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 10.9f : 10.9f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x + (isForMap ? 6f : 6f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 0.9f : 0.9f / KernelLayout.mapView.getScale()) * mmTrans)), 255, 30, false, mainCopy);//down
                }
                break;
            case 605://face north
                for (int i = 0; i < this.getSize(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }

                    canvas.drawLine((float) (x - (isForMap ? 0.15f : 0.15f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) y,
                            (float) (x + (isForMap ? 0.15f : 0.15f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) y, mainCopy);
                    canvas.drawLine((float) x, (float) (y - (isForMap ? 0.15f : 0.15f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) x, (float) (y + (isForMap ? 0.15f : 0.15f / KernelLayout.mapView.getScale()) * mmTrans), mainCopy);
                }
                break;
            case 606://face north
                //big circle:1.1/3.05, small circle:0.25/0.76,wall:y3.03 = 2.6+1.1/2-0.12
                for (int i = 0; i < this.getSize(); i++) {
                    if (isForMap) {
                        x = points.get(i).x;
                        y = points.get(i).y;
                    }else {
                        x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                        y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
                    }

                    canvas.drawLine((float) (x - (isForMap ? 1.52f : 1.52f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y - (isForMap ? 3.03f : 3.03f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x - (isForMap ? 0.38f : 0.38f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 3.03f : 3.03 / KernelLayout.mapView.getScale()) * mmTrans), mainCopy);//3.05/2
                    canvas.drawLine((float) (x + 1.52f * mmTrans / KernelLayout.mapView.getScale()),
                            (float) (y - (isForMap ? 3.03f : 3.03f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x + (isForMap ? 0.38f : 0.38f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 3.03f : 3.03f / KernelLayout.mapView.getScale()) * mmTrans), mainCopy);
                    canvas.drawOval(new RectF((float) (x - (isForMap ? 1.52f : 1.52f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y - (isForMap ? 3.58f : 3.58f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x + (isForMap ? 1.52f : 1.52f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y - (isForMap ? 2.48f : 2.48f / KernelLayout.mapView.getScale()) * mmTrans)), mainCopy);
                    canvas.drawArc(new RectF((float) (x - (isForMap ? 0.38f : 0.38f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 2.905f : 2.905f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x + (isForMap ? 0.38f : 0.38f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 3.155f : 3.155f / KernelLayout.mapView.getScale()) * mmTrans)), 0, 180, false, mainCopy);
                }

        }
    }

    /**fill circle*/
    private void drawDisc(Canvas canvas, Paint mainCopy, boolean isForMap) {
        float x, y;

        for (int i = 0; i < points.size(); i++) {
            if (isForMap) {
                x = points.get(i).x;
                y = points.get(i).y;
            }else {
                x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
            }

            canvas.drawCircle(x, y,
                    (isForMap ? 0.75f : 0.75f / KernelLayout.mapView.getScale()) * mmTrans / 2, mainCopy);
        }
    }

    private void drawCircle(Canvas canvas, Paint mainCopy, boolean isForMap) {
        float x = 0, y = 0;

        for (int i = 0; i < points.size(); i++) {
            if (isForMap) {
                x = points.get(i).x;
                y = points.get(i).y;
            }else {
                x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
            }
            canvas.drawCircle(x, y,
                    (isForMap ? 0.7f : 0.7f / KernelLayout.mapView.getScale()) *mmTrans / 2, mainCopy);//0.95-0.25
        }
    }

    private void drawSemiCircle(Canvas canvas, Paint mainCopy, boolean isForMap) {
        double x = 0.0, y = 0.0;

        for (int i = 0; i < points.size(); i++) {
            if (isForMap) {
                x = points.get(i).x;
                y = points.get(i).y;
            }else {
                x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
            }
            canvas.drawArc(new RectF((float) (x - (isForMap ? 0.7f : 0.7f /KernelLayout.mapView.getScale()) *mmTrans),
                            (float) (y - (isForMap ? 0.7f : 0.7f / KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (x + (isForMap ? 0.7f : 0.7f /KernelLayout.mapView.getScale()) * mmTrans),
                            (float) (y + (isForMap ? 0.7f : 0.7f / KernelLayout.mapView.getScale()) * mmTrans)),
                    0, 180, false, mainCopy);//0.95-0.25
        }
    }

    private void drawTip(Canvas canvas, Paint mainCopy, boolean isForMap) {
        Path limbo = new Path();
        double x = 0, y = 0;

        for (int i = 0; i < points.size(); i++) {
            if (isForMap) {
                x = points.get(i).x;
                y = points.get(i).y;
            }else {
                x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
            }

            limbo.moveTo((float) (x - (isForMap ? 0.41f : 0.41f / KernelLayout.mapView.getScale()) * mmTrans),
                    (float) (y - (isForMap ? 0.625f : 0.625f / KernelLayout.mapView.getScale()) * mmTrans));//0.41=0.82/2
            limbo.lineTo((float) (x - (isForMap ? 0.16f : 0.16f / KernelLayout.mapView.getScale()) * mmTrans),
                    (float) (y - (isForMap ? 0.625f : 0.625f / KernelLayout.mapView.getScale()) * mmTrans));//0.16=0.41-0.25
            limbo.lineTo((float) x, (float) (y + (isForMap ? 0.18f : 0.18f / KernelLayout.mapView.getScale()) * mmTrans));//0.18 = 1.25/2 - 0.25*√3
            limbo.lineTo((float) (x + (isForMap? 0.16f : 0.16f / KernelLayout.mapView.getScale()) *mmTrans),
                    (float) (y - (isForMap ? 0.625f : 0.625f /KernelLayout.mapView.getScale()) *mmTrans));
            limbo.lineTo((float) (x + (isForMap ? 0.41f : 0.41f/KernelLayout.mapView.getScale()) *mmTrans),
                    (float) (y - (isForMap ? 0.625f : 0.625f / KernelLayout.mapView.getScale()) *mmTrans));
            limbo.lineTo((float) x, (float) (y + (isForMap ? 0.625f : 0.625f / KernelLayout.mapView.getScale()) * mmTrans));
        }
        canvas.drawPath(limbo, mainCopy);
    }

    private void drawCross(Canvas canvas, Paint mainCopy, boolean isForMap) {
        //Path pathX = new Path();
        double x = 0, y = 0;
        
        for (int i = 0; i < points.size(); i++) {
            if (isForMap) {
                x =  points.get(i).x;
                y =  points.get(i).y;
            }else {
                x = KernelLayout.toScreenLocation(points.get(i).x, KernelLayout.COOX);
                y = KernelLayout.toScreenLocation(points.get(i).y, KernelLayout.COOY);
            }

            canvas.drawLine((float) (x - (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans),
                    (float) (y - (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans),
                    (float) (x + (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                    (float) (y + (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                    mainCopy);
            canvas.drawLine((float) (x + (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                    (float) (y - (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                    (float) (x - (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans),
                    (float) (y + (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans),
                    mainCopy);
            /*pathX.moveTo((float) (x - (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans),
                    (float) (y - (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans));
            pathX.lineTo((float) (x + (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                    (float) (y + (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans));
            pathX.moveTo((float) (x + (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans),
                    (float) (y - (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale()) * mmTrans));
            pathX.lineTo((float) (x - (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans),
                    (float) (y + (isForMap? 0.6f : 0.6f / KernelLayout.mapView.getScale())*mmTrans));*/
        }
        //canvas.drawPath(pathX, mainCopy);
    }
}
