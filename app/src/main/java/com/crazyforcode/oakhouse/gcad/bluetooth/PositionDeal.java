package com.crazyforcode.oakhouse.gcad.bluetooth;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

public class PositionDeal {

    private static AlgoPoint positionCM = null;
    private static float altitude = 0.0f;

    public static void initPositionCM(AlgoPoint start) {
        positionCM = start;
    }

    public static boolean isInit() {
        return positionCM!= null;
    }

    private static void setNewPositionCM(AlgoPoint start) {
        positionCM = start;
    }

    public static void changeToCurrentPosition(float[] data) {
        float x = positionCM.x + data[0];
        float y = positionCM.y + data[1];
        altitude += data[2];
        MainActivity.changeAltitude(altitude);

        positionCM = new AlgoPoint(x, y);
    }

    public static AlgoPoint getPositionInMap() {
        //TODO 相对地图的位置
        return null;
    }
}
