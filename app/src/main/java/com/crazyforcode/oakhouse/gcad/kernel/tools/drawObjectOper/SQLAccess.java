package com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MThreadPool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLAccess {


    private static SQLService service;

    private static SQLiteDatabase database;

    public static boolean initSQLService(Context context, String projectPath, boolean init) {
        String dpPath = projectPath + "/Data/Draw.db";

        if(init) {
            service = new SQLService(context, dpPath);
            database = service.getWritableDatabase();
            return true;
        } else {
            File file = new File(dpPath);
            if(!file.exists())
                return false;

            service = new SQLService(context, dpPath);
            database = service.getWritableDatabase();
            return true;
        }
    }

    public static void saveDrawObject(HashMap<Integer, ArrayList<DrawObject>> objects, Context context) {
        MHandler mHandler = new MHandler(context);
        SQLToFileRunnable sqlToFileRunnable = new SQLToFileRunnable(mHandler, objects);
        MThreadPool.addTask(sqlToFileRunnable);
        MThreadPool.start();
    }

    public static void obtainDrawObjects(Context context) {
        MHandler mHandler = new MHandler(context);
        SQLFromFileRunnable sqlFromFileRunnable = new SQLFromFileRunnable(mHandler);
        MThreadPool.addTask(sqlFromFileRunnable);
        MThreadPool.start();
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static SQLService getService() {
        return service;
    }

    public static void closeService() {
        service.close();
    }
}
