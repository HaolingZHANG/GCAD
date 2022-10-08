package com.crazyforcode.oakhouse.gcad.others.components;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.LinkedList;

public class CompassDirection extends SurfaceView implements SensorEventListener {

    private static int originDegree;
    private static int adjustDegree = 0;

    private LinkedList<Float> queueDegree = new LinkedList<>();

    private float[] accelerometerValues = new float[3];
    private float[] magneticValues = new float[3];

    public CompassDirection(Context context) {
        super(context);
        init();
    }

    public CompassDirection(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setBackgroundColor(Color.WHITE);
        this.setWillNotDraw(false);

//        sensorManager用于管理传感器
        SensorManager compass = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);

//        地磁传感器
        Sensor magneticSensor = compass.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        加速度传感器
        Sensor accelerometerSensor = compass.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        分别给两种传感器注册监听器
        compass.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
        compass.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void setAdjustDegree(int degree) {
        adjustDegree =  -degree;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int degree = originDegree + adjustDegree;
        if(degree < -180)
            degree = 360 + degree;
        else if(degree > 180)
            degree = 180 - degree;


        Paint paint = new Paint();
        paint.setColor(Color.argb(255, 2, 168, 243));
        paint.setAntiAlias(true);

        if(degree > -1 && degree < 1)
            paint.setColor(Color.argb(255, 255, 76, 0));

        canvas.drawLine(this.getWidth() / 2, 35, this.getWidth() / 2 - 100, 135, paint);
        canvas.drawLine(this.getWidth() / 2, 35, this.getWidth() / 2 + 100, 135, paint);
        canvas.drawLine(this.getWidth() / 2 - 80, this.getHeight() / 2,
                this.getWidth() / 2 + 80, this.getHeight() / 2, paint);
        canvas.drawLine(this.getWidth() / 2, 35,
                this.getWidth() / 2, this.getHeight() - 10, paint);

        paint.setColor(Color.argb(255, 2, 168, 243));
        paint.setTextSize(30);

        canvas.drawText("当前方向", this.getWidth() / 2 - 60, this.getHeight() / 2 - 5, paint);


        if(degree >= 1 && degree < 180) {
            if(degree >= 10 && degree < 100)
                canvas.drawText(String.valueOf(degree), this.getWidth() / 2 - 14, 30, paint);
            else if(degree >= 100)
                canvas.drawText(String.valueOf(degree), this.getWidth() / 2 - 23, 30, paint);
            else
                canvas.drawText(String.valueOf(degree), this.getWidth() / 2 - 7, 30, paint);

            canvas.drawText("偏西", this.getWidth() / 2 - 30, this.getHeight() / 2 + 35, paint);
        } else if(degree > - 180 && degree <= -1) {
            degree = -degree;
            if(degree >= 10 && degree < 100)
                canvas.drawText(String.valueOf(degree), this.getWidth() / 2 - 14, 30, paint);
            else if(degree >= 100)
                canvas.drawText(String.valueOf(degree), this.getWidth() / 2 - 23, 30, paint);
            else
                canvas.drawText(String.valueOf(degree), this.getWidth() / 2 - 7, 30, paint);

            canvas.drawText("偏东", this.getWidth() / 2 - 30, this.getHeight() / 2 + 35, paint);
        } else if(degree > -1 && degree < 1){
            canvas.drawText(String.valueOf(degree), this.getWidth() / 2 - 7, 0, paint);

            canvas.drawText("正方向", this.getWidth() / 2 - 45, this.getHeight() / 2 + 35, paint);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerValues = event.values.clone();
        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magneticValues = event.values.clone();

        float[] R = new float[9];
        float[] values = new float[3];

//            得到包含旋转矩阵的R数组
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
//            计算手机的旋转数据，并将参数存入values数组
        SensorManager.getOrientation(R, values);

//            将弧度转换为角度
        float degree = -(float) Math.toDegrees(values[0]);

        if(queueDegree.size() == 10)
            queueDegree.removeFirst();

        queueDegree.add(degree);

        float avgDegree = 0;

        for(int i = 0; i < queueDegree.size(); i++)
            avgDegree += queueDegree.get(i);

        avgDegree /= queueDegree.size();

        if(Math.abs(avgDegree - originDegree) > (0.15 / (float)(0.05))) {
            originDegree = (int)avgDegree;
            invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
