package com.crazyforcode.oakhouse.gcad.test.ThreadTest;


import android.util.Log;

public class CurrentThread {

    public static void printCurrentCount() {
        Log.i("当前线程数", Thread.currentThread().getStackTrace().length + "");
    }

    public static void printStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        for (int i = 0; i < stackTraceElements.length; ++i) {
            Log.i("thread count", (i + 1) + "");
            Log.i("FileName", stackTraceElements[i].getFileName());
            Log.i("ClassName", stackTraceElements[i].getClassName());
            Log.i("MethodName", stackTraceElements[i].getMethodName());
            Log.i("LineNumber", stackTraceElements[i].getLineNumber() + "");
        }
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public static void printIsMainThread() {
        Log.i("当前正在运行的是否是主线程", Thread.currentThread().equals("main") + "");
    }
}
