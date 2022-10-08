package com.crazyforcode.oakhouse.gcad.kernel.tools.makePaintToMap;

import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;

import java.util.ArrayList;
import java.util.TreeSet;

public class FindCurrentRepaintDrawObjects {

    private static ArrayList<DrawObject> temp = null;

    public static ArrayList<DrawObject> getRepaintDrawObjects(TreeSet<DrawObject> currentPaints) {

        temp = new ArrayList<>(currentPaints);

//        查找比起轻量的所有DrawObject并对其覆盖
        return temp;
    }

    private static void searchLightWeightObjects() {

    }
}
