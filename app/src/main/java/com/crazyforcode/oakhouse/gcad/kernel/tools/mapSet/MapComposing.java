package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class MapComposing {

    public static Bitmap conformHorizontal(Bitmap left, Bitmap right) {
        if(left == null || right == null)
            return null;

        int leftWidth = left.getWidth();
        int leftHeight = left.getHeight();
        int rightWidth = right.getWidth();

        Bitmap finalMap = Bitmap.createBitmap(leftWidth + rightWidth, leftHeight, left.getConfig());
        Canvas canvas = new Canvas(finalMap);

        canvas.drawBitmap(left, 0, 0, null);
        canvas.drawBitmap(right, leftWidth, 0, null);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return finalMap;
    }

    public static Bitmap conformVertical(Bitmap top, Bitmap bottom) {
        if(top == null || bottom == null)
            return null;

        int topWidth = top.getWidth();
        int topHeight = top.getHeight();
        int bottomHeight = bottom.getHeight();

        Bitmap finalMap = Bitmap.createBitmap(topWidth, topHeight + bottomHeight, top.getConfig());
        Canvas canvas = new Canvas(finalMap);

        canvas.drawBitmap(top, 0, 0, null);
        canvas.drawBitmap(bottom, 0, bottomHeight, null);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return finalMap;
    }
}
