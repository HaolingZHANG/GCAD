package com.crazyforcode.oakhouse.gcad.kernel.views;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

public class TouchGestureDetector extends BaseGestureDetector {

    //用于记录最终结果，并返回
    private PointF mExternalPointer = new PointF();

    private OnMoveGestureListener mListener;

    public TouchGestureDetector(Context context, OnMoveGestureListener listener){
        super(context);
        mListener = listener;
    }

    @Override
    protected void handleInProgressEvent(MotionEvent event) {
        int actionCode = event.getAction() & MotionEvent.ACTION_MASK;

        switch (actionCode) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mListener.onMoveEnd(this);
                resetState();
                break;
            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);
                boolean update = mListener.onMove(this);
                if (update) {
                    mPreMotionEvent.recycle();
                    mPreMotionEvent = MotionEvent.obtain(event);
                }
                break;
        }
    }

    @Override
    protected void handleStartProgressEvent(MotionEvent event) {
        int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                resetState();//防止没有接收到CANCEL or UP ,保险起见
                mPreMotionEvent = MotionEvent.obtain(event);
                updateStateByEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mGestureInProgress = mListener.onMoveBegin(this);
                break;
        }

    }

    protected void updateStateByEvent(MotionEvent event) {
        final MotionEvent prev = mPreMotionEvent;

        PointF mPrePointer = calculateFocalPointer(prev);
        PointF mCurrentPointer = calculateFocalPointer(event);

        boolean mSkipThisMoveEvent = prev.getPointerCount() != event.getPointerCount();

        mExternalPointer.x = mSkipThisMoveEvent ? 0 : mCurrentPointer.x - mPrePointer.x;
        mExternalPointer.y = mSkipThisMoveEvent ? 0 : mCurrentPointer.y - mPrePointer.y;

    }

    /**
     * 根据event计算多指中心点
     */
    private PointF calculateFocalPointer(MotionEvent event) {
        final int count = event.getPointerCount();
        float x = 0, y = 0;

        for (int i = 0; i < count; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= count;
        y /= count;

        return new PointF(x, y);
    }

    public float getMoveX() {
        return mExternalPointer.x;
    }

    public float getMoveY() {
        return mExternalPointer.y;
    }
}
