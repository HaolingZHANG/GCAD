package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.crazyforcode.oakhouse.gcad.kernel.tools.iconOperation.IconOperation;
import com.crazyforcode.oakhouse.gcad.R;

/**
 * Created by Master Jedi on 2016/1/9.
 */
public class LegendTileView extends ViewGroup {
    IconOperation icons = null;

    public LegendTileView(Context context) {
        this(context, null);
    }

    public LegendTileView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public LegendTileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        View child = getChildAt(0);
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        int width = getMeasuredWidth(),
            childdimen = getResources().getDimensionPixelOffset(R.dimen.legend_picker_img_width);
        int colNum = 5;/*写死一排五个*//*(int) Math.floor((double) width
                / (childdimen + lp.rightMargin + lp.leftMargin));=0*/
        int lc=0, tc=0, rc=0, bc=0;

        Log.i("onLayout_getwidth",""+width);
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            lp = (MarginLayoutParams) child.getLayoutParams();

            lc = (i%colNum)*(childdimen + lp.rightMargin + lp.leftMargin);
            tc = (i/colNum)*(childdimen + lp.topMargin);
            rc = lc + childdimen;
            bc = tc + childdimen;

//            Log.i("lc", lc+"");
//            Log.i("tc", tc+"");
//            Log.i("rc", rc+"");
//            Log.i("bc", bc+"");
            child.layout(lc, tc, rc, bc);
            //child.setBackgroundResource(R.drawable.selected);
        }
        Log.i("onLayout", childCount+"");
    }

    /**compute the container's demension size upon the inside view
     * we don't need it there*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec),
//                sizeHeight = MeasureSpec.getSize(heightMeasureSpec),
//                modeWidth = MeasureSpec.getMode(widthMeasureSpec),
//                modeHeight = MeasureSpec.getMode(heightMeasureSpec);
//
//        View child = getChildAt(0);
//        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
//        measureChild(child, widthMeasureSpec, heightMeasureSpec);
//
//        int lineWidth = 0, lineHeight = 0, childCount = getChildCount(),
//                childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin,
//                childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
//
//        for (int i = 1; i < childCount; i++) {
//            if (lineWidth + childWidth > sizeWidth) {
//                lineWidth = childWidth;//begin a new row
//                lineHeight += childHeight;//add to now height
//            }else {
//                lineWidth += childWidth;
//            }
//
//            if (i == childCount - 1) {
//                lineWidth = Math.max(lineWidth, lineWidth);
//            }
//
//            child = getChildAt(i);
//        }

        setMeasuredDimension(getResources().getDimensionPixelOffset(R.dimen.legend_picker_img_width) * 5
                , MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        int childCount = getChildCount();
        View child = null;

        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);

            if (((ImageView)child).getDrawable() != null)
                ((ImageView)child).setImageDrawable(null);
            //child.setBackgroundResource(R.drawable.selected);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT);
    }
}
