package com.crazyforcode.oakhouse.gcad.test;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

public class PhoneTest {

    public static void printHardware(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for(Sensor sensor : sensorList)
            Log.i("sensor name", sensor.getName());
    }
}
