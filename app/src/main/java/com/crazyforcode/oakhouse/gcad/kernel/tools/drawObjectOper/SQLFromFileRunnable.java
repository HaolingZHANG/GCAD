package com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper;

import android.database.Cursor;
import android.os.Message;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.draw.assist.CurveControl;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.AreaObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.EdgeObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.LineObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.SpotObject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MRunnable;
import com.crazyforcode.oakhouse.gcad.test.FileTest.SQLPrint;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLFromFileRunnable extends MRunnable {

    private static boolean success = false;
    private static HashMap<Integer, ArrayList<DrawObject>> drawObjects = new HashMap<>();

    public SQLFromFileRunnable(MHandler handler) {
        super(handler);
    }

    @Override
    public void runBefore() {
        SQLAccess.getDatabase().beginTransaction();
        success = false;
    }

    @Override
    public void running() {
        Message message;
        try {
            for (int i = 0; i < 5; i++) {
                Cursor cursor = SQLAccess.getService().query(SQLAccess.getDatabase(), "SELECT * from  DrawObjects" + i + ";");
                ArrayList<DrawObject> objects = sqlToObject(cursor);

                for(int j = 0; j < objects.size(); j++) {
                    DrawObject current = objects.get(j);

                    if(drawObjects.get(current.getSign()) == null)
                        drawObjects.put(current.getSign(), new ArrayList<DrawObject>());

                    drawObjects.get(current.getSign()).add(current);
                }
            }

            message = getMHandler().obtainMessage(MHandler.GET_SQL_FROM_FILE, MHandler.SUCCESS, 0);
            getMHandler().sendMessageAtTime(message, 0);
        } catch (Exception e) {
            success = false;
            message = getMHandler().obtainMessage(MHandler.GET_SQL_FROM_FILE, MHandler.FAILURE, 0);
            getMHandler().sendMessageAtTime(message, 0);
        } finally {
            SQLAccess.getDatabase().endTransaction();
        }
    }

    @Override
    public void runAfter() {
        SQLAccess.closeService();
    }

    public static boolean isSuccess() {
        return success;
    }

    public static HashMap<Integer, ArrayList<DrawObject>> getDrawObjects() {
        return drawObjects;
    }

    public static void clearObjects() {
        drawObjects = null;
    }

    @SuppressWarnings("ConstantConditions")
    private static ArrayList<DrawObject> sqlToObject(Cursor cursor) {
        ArrayList<DrawObject> objects = new ArrayList<>();
        while(cursor.moveToNext()) {
            DrawObject object;

            int sign = cursor.getInt(cursor.getColumnIndex("sign"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            ArrayList<AlgoPoint> points = stringToArrays(cursor.getString(cursor.getColumnIndex("points")));

            if(DrawObjects.isPoint(sign))
                object = new SpotObject(sign);
            else if(DrawObjects.isArea(sign))
                object = new AreaObject(sign, type);
            else
                object = new LineObject(sign, type);

            for(int i = 0; i < points.size(); i++)
                object.addPoint(points.get(i));

            if(!DrawObjects.isPoint(sign) && type == DrawObjects.DRAW_TYPE_CURL) {
                ArrayList<AlgoPoint> curveF = stringToArrays(cursor.getString(cursor.getColumnIndex("curveF")));
                ArrayList<AlgoPoint> curveT = stringToArrays(cursor.getString(cursor.getColumnIndex("curveT")));

                CurveControl curveControl = new CurveControl();

                for(int j = 0; j < curveF.size(); j++) {
                    curveControl.addOneSideBezier(curveF.get(j).x, curveF.get(j).y, CurveControl.Direction.FROM);
                    curveControl.addOneSideBezier(curveT.get(j).x, curveT.get(j).y, CurveControl.Direction.TO);
                }
                ((EdgeObject)object).curve = curveControl;
            }

            //Print
            SQLPrint.printSqlToObject(sign, type, cursor.getString(cursor.getColumnIndex("points")),
                    cursor.getString(cursor.getColumnIndex("curveF")),
                    cursor.getString(cursor.getColumnIndex("curveT")));

            objects.add(object);
        }
        return objects;
    }

    private static ArrayList<AlgoPoint> stringToArrays(String string) {
        ArrayList<AlgoPoint> points = new ArrayList<>();

        if(string.equals("NULL"))
            return points;

        String[] strings = string.split(";");

        for(String stringForPoint : strings) {
            AlgoPoint point = new AlgoPoint();
            point.toPoint(stringForPoint);
            points.add(point);
        }
        return points;
    }
}
