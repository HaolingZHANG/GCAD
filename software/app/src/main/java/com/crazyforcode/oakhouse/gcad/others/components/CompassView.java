package com.crazyforcode.oakhouse.gcad.others.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.R;

import java.util.LinkedList;

public class CompassView extends SurfaceView implements SensorEventListener {

    private static SensorManager compass;
    private static Sensor magneticSensor;
    private static Sensor accelerometerSensor;

    private static float currentDegree;
    private static AlgoPoint levelCenter = new AlgoPoint(75, 75);

    private Paint paintCircleOut;
    private Paint paintCircleIn;
    private Paint paintLine;
    private Paint paintIndicator;
    private Paint paintRing;
    private Paint paintLevel;
    private Paint paintPointer;

    private static int width;
    private static int height;
    private static int shadowWidth = 0;
    private static float sensitivity = 1;

    private static int adjustmentDegree = 0;

    private static boolean hasMagnetometer;

    private float[] accelerometerValues = new float[3];
    private float[] magneticValues = new float[3];

    private LinkedList<Float> queueDegree = new LinkedList<>();
    private LinkedList<AlgoPoint> queueLevel = new LinkedList<>();


    public CompassView(Context context) {
        super(context);
        init(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        this.setClickable(false);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
        this.setWillNotDraw(false);

        //??????????????????
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        width = (int) context.getResources().getDimension(R.dimen.compass_width);
        height = (int) context.getResources().getDimension(R.dimen.compass_width);
        //shadowWidth = (int) context.getResources().getDimension(R.dimen.compass_shadow_width);

//        ??????????????????
        paintCircleOut = new Paint();
        paintCircleOut.setColor(Color.parseColor("#02A8F3"));
        paintCircleOut.setStyle(Paint.Style.STROKE);
        paintCircleOut.setAntiAlias(true);
        paintCircleOut.setStrokeWidth(4);
        paintCircleOut.setShadowLayer(8, 4, 4, Color.parseColor("#014b6e"));
        /*{
            float[] direction = new float[]{ 1, 1, 3 };   // ?????????????????????
            float light = 0.5f;     //?????????????????????
            float specular = 20;     // ????????????????????????
            float blur = 15.0f;      //????????????
            EmbossMaskFilter emboss=new EmbossMaskFilter(direction,light,specular,blur);
            paintCircleOut.setMaskFilter(emboss);
        }*/
//        ??????????????????
        paintCircleIn = new Paint();
        paintCircleIn.setColor(Color.parseColor("#02A8F3"));
        paintCircleIn.setAntiAlias(true);
        paintCircleIn.setStyle(Paint.Style.FILL_AND_STROKE);
        paintCircleIn.setStrokeWidth(5);
//        ?????????????????????
        paintLine = new Paint();
        paintLine.setColor(Color.WHITE);
        paintLine.setStyle(Paint.Style.FILL);
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(2);
//        ??????????????????
        paintIndicator = new Paint();
        paintIndicator.setColor(Color.WHITE);
        paintIndicator.setStyle(Paint.Style.FILL);
        paintIndicator.setStrokeCap(Paint.Cap.ROUND);
        paintIndicator.setAntiAlias(true);
        paintIndicator.setStrokeWidth(4);
//        ?????????????????????
        paintRing = new Paint();
        paintRing.setColor(Color.WHITE);
        paintRing.setStyle(Paint.Style.STROKE);
        paintRing.setAntiAlias(true);
        paintRing.setStrokeWidth(2);
//        ???????????????????????????
        paintLevel = new Paint();
        paintLevel.setColor(Color.WHITE);
        paintLevel.setStyle(Paint.Style.STROKE);
        paintLevel.setAntiAlias(true);
        paintLevel.setStrokeWidth(2);
//        ???????????????????????????
        paintPointer = new Paint();
        paintPointer.setColor(Color.parseColor("#02A8F3"));
        paintPointer.setStyle(Paint.Style.FILL);
        paintPointer.setAntiAlias(true);

//        sensorManager?????????????????????
        compass = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
//        ???????????????
        magneticSensor = compass.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

//        ??????????????????
        accelerometerSensor = compass.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        ???????????????????????????????????????
        compass.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        compass.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);


        //?????????????????????
        adjustmentDegree = context.getSharedPreferences("config", Context.MODE_PRIVATE).getInt("CompassAdjust", 0);
        setSensitivityForCompass(context.getSharedPreferences("config", Context.MODE_PRIVATE).getInt("CompassSensitivity", 5));
    }

    public void setSensitivityForCompass(int sensitivityIn) {
        sensitivity = (float)((float)sensitivityIn / 5.0);

        if(sensitivity > 1.2) {
            compass.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
            compass.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else if(sensitivity > 0.8) {
            compass.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
            compass.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            compass.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
            compass.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public int getSensitivityForCompass() {
        return (int)(sensitivity * 5);
    }

    public static void setAdjustmentDegree(int degree) {
        adjustmentDegree = degree;
    }

    public static int getAdjustmentDegree() {
        return adjustmentDegree;
    }

    public static boolean isLevel() {
        return Math.hypot((levelCenter.x - width / 2),(levelCenter.y - height / 2)) < 15;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            if(Math.abs((currentDegree + adjustmentDegree) % 360) <= (1.5 / sensitivity)) {
                paintCircleOut.setColor(Color.parseColor("#02A8F3"));
                paintCircleIn.setColor(Color.WHITE);
                paintLine.setColor(Color.parseColor("#02A8F3"));
                paintIndicator.setColor(Color.parseColor("#02A8F3"));
                paintRing.setColor(Color.parseColor("#02A8F3"));
                paintLevel.setColor(Color.parseColor("#02A8F3"));
                paintPointer.setColor(Color.parseColor("#02A8F3"));
                if(!isLevel()) {
                    paintRing.setColor(Color.parseColor("#FF4C00"));
                    paintLevel.setColor(Color.parseColor("#FF4C00"));
                }
            } else {
                paintCircleOut.setColor(Color.parseColor("#02A8F3"));
                paintCircleIn.setColor(Color.parseColor("#02A8F3"));
                paintLine.setColor(Color.WHITE);
                paintIndicator.setColor(Color.WHITE);
                paintRing.setColor(Color.parseColor("#02A8F3"));
                paintLevel.setColor(Color.WHITE);
                paintPointer.setColor(Color.WHITE);
                if(!isLevel()) {
                    paintRing.setColor(Color.parseColor("#FF4C00"));
                    paintLevel.setColor(Color.parseColor("#FF4C00"));
                }

            }
            canvas.save();
            canvas.translate(16, 16);

            canvas.drawCircle(width / 2, height / 2, width / 2 - 2, paintCircleOut);
            canvas.drawCircle(width / 2, height / 2, width / 2 - 5, paintCircleIn);

//            ???????????????
            canvas.drawCircle(width / 2, height / 2, 15, paintLevel);
//            ?????????????????????
            canvas.drawCircle((float)levelCenter.x, (float)levelCenter.y, 5, paintLevel);

//            ???????????????????????????
            @SuppressLint("DrawAllocation")
            Path path = new Path();
            path.moveTo(70, 0);
            path.lineTo(80, 0);
            path.lineTo(75, 5);
//            ????????????????????????????????????
            path.close();
            canvas.drawPath(path, paintPointer);

            for(int i = 0; i < 16; i++) {

                canvas.save();
                if(i % 4 == 0) {
                    paintLine.setStrokeWidth(4);
                    canvas.rotate((float)22.5 * i + (currentDegree + adjustmentDegree), width / 2, height / 2);
                    canvas.drawLine(width / 2, height / 2 - 70,
                            width / 2, height / 2 - 70 + 15, paintLine);
                } else {
                    paintLine.setStrokeWidth(2);
                    canvas.rotate((float)22.5 * i + (currentDegree + adjustmentDegree), width / 2, height / 2);
                    canvas.drawLine(width / 2, height / 2 - 70,
                            width / 2, height / 2 - 70 + 7, paintLine);
                }

                canvas.restore();
            }

//            ??????degree???????????????canvas?????????????????????????????????
            canvas.rotate((currentDegree + adjustmentDegree) + 45, width / 2, height / 2);

//            ????????????
            canvas.drawLine(width / 2, height / 2, width / 8 + 20, height / 8 + 20, paintIndicator);

            canvas.restore();//for translate
        } catch(NullPointerException e) {
//            sensorManager?????????????????????
            compass = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);

            if(sensitivity > 1.2) {
                compass.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
                compass.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else if(sensitivity > 0.8) {
                compass.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
                compass.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
            } else {
                compass.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
                compass.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    private boolean isContain(int x, int y) {
        return Math.hypot(width / 2 - x, height / 2 - y) <= 65;
    }

    public static void deleteWhenQuit() {
        compass = null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerValues = event.values.clone();
        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = event.values.clone();
        }
        compute();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void compute() {
        float[] R = new float[9];
        float[] values = new float[3];

//            ???????????????????????????R??????
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
//            ????????????????????????????????????????????????values??????
        SensorManager.getOrientation(R, values);

//            ????????????????????????
        float degree = -(float) Math.toDegrees(values[0]);

        float pitch = values[1];
        float roll = values[2];

//            ????????????,?????????????????????????????????
        AlgoPoint temp = new AlgoPoint();

//            ??????x
        if(Math.abs(roll) <= Math.PI / 4.0)
            temp.setX(75 * (1 - roll));
        else if(roll > Math.PI / 4.0)
            temp.setX(5);
        else
            temp.setX(130);

//            ??????y
        if(Math.abs(pitch) <= Math.PI / 4.0)
            temp.setY(75 * (1 + pitch));
        else if(pitch > Math.PI / 4.0)
            temp.setY(130);
        else
            temp.setY(5);

        boolean change = false;
//            ???????????????,??????????????????????????????
        if(isContain((int)temp.x, (int)temp.y)) {
            if(Math.sqrt(Math.pow(levelCenter.x - temp.x, 2)
                    + Math.pow(levelCenter.y - temp.y, 2)) > 3) {
                if(queueLevel.size() == 10)
                    queueLevel.removeFirst();

                queueLevel.add(temp);

                AlgoPoint avgCenter = new AlgoPoint();

                for(int i = 0; i < queueLevel.size(); i++)
                    avgCenter.set(avgCenter.x + queueLevel.get(i).x,
                            avgCenter.y + queueLevel.get(i).y);

                avgCenter.set(avgCenter.x / queueLevel.size(),
                        avgCenter.y / queueLevel.size());

                levelCenter.set(avgCenter.x, avgCenter.y);
                change = true;
            }
        }


        if(queueDegree.size() == 25)
            queueDegree.removeFirst();

        queueDegree.add(degree);

        float avgDegree = 0;

        for(int i = 0; i < queueDegree.size(); i++)
            avgDegree += queueDegree.get(i);

        avgDegree /= queueDegree.size();

        if(Math.abs(avgDegree - currentDegree) > (0.15 / sensitivity) || change) {
            currentDegree = avgDegree;
            invalidate();
        }
    }
}
