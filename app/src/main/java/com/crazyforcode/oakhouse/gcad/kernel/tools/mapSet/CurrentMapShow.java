package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;

public class CurrentMapShow {

    private static final int BIG = 1;
    private static final int SMALL = 2;

    private static int currentSize = BIG;

    private static boolean[][] showPieces = new boolean[9][9];

    public static void judgeChange(AlgoPoint mapPosition, double scale) {
        mapSizeChange(needSizeChange(scale), mapPosition);

        mapPositionChange(needPositionChange(), mapPosition);
    }

//    以scale = 0.05为界
    private static int needSizeChange(double scale) {

        if(scale > 0.05 && currentSize != BIG)
            return 1;

        if(scale <= 0.05 && currentSize != SMALL)
            return 2;

        return 0;
    }

//    1为上，2为下，3为左，4为右
    private static int needPositionChange() {
        if(currentSize == BIG)
            return 0;
        else {

        }

        return 0;
    }

    private static void mapSizeChange(int type, AlgoPoint mapPosition) {

    }

    private static void mapPositionChange(int type, AlgoPoint mapPosition) {

    }
}
