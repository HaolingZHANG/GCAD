package com.crazyforcode.oakhouse.gcad.others.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("DrawAllocation")
public class BorderTextView extends TextView {

    private int srokeWidth = 1;

    public BorderTextView(Context context) {
        super(context);
        this.setAllCaps(false);
    }

    public BorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setAllCaps(false);
    }

    public void setSrokeWidth(int srokeWidth) {
        this.srokeWidth = srokeWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
//        将边框设为黑色
        paint.setColor(android.graphics.Color.BLACK);
//        画TextView的4个边
        canvas.drawLine(0, 0, this.getWidth() - srokeWidth, 0, paint);
        canvas.drawLine(0, 0, 0, this.getHeight() - srokeWidth, paint);
        canvas.drawLine(this.getWidth() - srokeWidth, 0,
                this.getWidth() - srokeWidth, this.getHeight() - srokeWidth, paint);
        canvas.drawLine(0, this.getHeight() - srokeWidth,
                this.getWidth() - srokeWidth, this.getHeight() - srokeWidth, paint);
        super.onDraw(canvas);
    }
}
