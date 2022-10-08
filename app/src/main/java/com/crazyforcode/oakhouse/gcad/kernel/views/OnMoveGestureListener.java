package com.crazyforcode.oakhouse.gcad.kernel.views;

public interface OnMoveGestureListener {
    boolean onMoveBegin(TouchGestureDetector detector);

    boolean onMove(TouchGestureDetector detector);

    void onMoveEnd(TouchGestureDetector detector);
}