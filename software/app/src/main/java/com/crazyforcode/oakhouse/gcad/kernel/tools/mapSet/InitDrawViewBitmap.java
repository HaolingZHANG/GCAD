package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;

import java.util.ArrayList;

public class InitDrawViewBitmap {

    public static boolean initDrawView(ArrayList<DrawObject> objects, Bitmap background) {
        Canvas canvas = new Canvas(background);

        //TODO 绘制 background

        return true;
    }
}
