package com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper;

import android.os.Message;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.draw.assist.CurveControl;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.EdgeObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.SpotObject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MRunnable;
import com.crazyforcode.oakhouse.gcad.test.FileTest.SQLPrint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SQLToFileRunnable extends MRunnable {

    private HashMap<Integer, ArrayList<DrawObject>> objects;

    private static boolean success = false;

    private static int countForPiece0 = 0;
    private static int countForPiece1 = 0;
    private static int countForPiece2 = 0;
    private static int countForPiece3 = 0;
    private static int countForPiece4 = 0;


    public SQLToFileRunnable(MHandler handler, HashMap<Integer, ArrayList<DrawObject>> objects) {
        super(handler);
        this.objects = objects;
    }

    @Override
    public void runBefore() {
        int oldVersion = SQLAccess.getService().getDbVersion();

        SQLAccess.getService().onUpgrade(SQLAccess.getDatabase(), oldVersion, oldVersion + 1);
        SQLAccess.getDatabase().beginTransaction();

        success = false;
    }

    @Override
    public void running() {
        Message message;
        try {
            /**
             * 显式调用map.entrySet()的集合迭代器
             * 比for each map.entrySet()，用临时变量保存map.entrySet()
             * 快1.5倍
             * */
            Iterator<Map.Entry<Integer, ArrayList<DrawObject>>> iterator = objects.entrySet().iterator();
            if(iterator.hasNext()) {
                do {
                    Map.Entry<Integer, ArrayList<DrawObject>> entry = iterator.next();

                    for(DrawObject obj : entry.getValue()) {
                        switch (DrawObjects.getWeight(obj.getSign())) {
                            case 0:
                                SQLAccess.getService().execSQL(SQLAccess.getDatabase(),
                                        objectToSql(obj, countForPiece0++));
                                break;
                            case 1:
                                SQLAccess.getService().execSQL(SQLAccess.getDatabase(),
                                        objectToSql(obj, countForPiece1++));
                                break;
                            case 2:
                                SQLAccess.getService().execSQL(SQLAccess.getDatabase(),
                                        objectToSql(obj, countForPiece2++));
                                break;
                            case 3:
                                SQLAccess.getService().execSQL(SQLAccess.getDatabase(),
                                        objectToSql(obj, countForPiece3++));
                                break;
                            case 4:
                                SQLAccess.getService().execSQL(SQLAccess.getDatabase(),
                                        objectToSql(obj, countForPiece4++));
                        }
                    }
                } while (iterator.hasNext());
            }
            SQLAccess.getDatabase().setTransactionSuccessful();

            success = true;

            message = getMHandler().obtainMessage(MHandler.SAVE_SQL_TO_FILE, MHandler.SUCCESS, 0);
            getMHandler().sendMessageAtTime(message, 0);
        } catch (Exception e) {
            success =  false;
            message = getMHandler().obtainMessage(MHandler.SAVE_SQL_TO_FILE, MHandler.FAILURE, 0);
            getMHandler().sendMessageAtTime(message, 0);
        } finally {
            objects = null;
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

    private static String objectToSql(DrawObject object, int count) {
        StringBuilder sql = new StringBuilder();

        sql.append("insert into DrawObjects");
        sql.append(DrawObjects.getWeight(object.getSign()));

        ArrayList<AlgoPoint> points = new ArrayList<>();

        for(int i = 0; i < object.getSize(); i++)
            points.add(object.getPoint(i));

        if(object instanceof SpotObject) {
            sql.append(" ( id, sign, type, points ) values ( ");
            sql.append(count);
            sql.append(", ");
            sql.append(object.getSign());
            sql.append(", ");
            sql.append(DrawObjects.DRAW_TYPE_STRAIGHT);
            sql.append(", ");
            sql.append("'");
            sql.append(arraysToString(points));
            sql.append("'");
            sql.append(")");
        } else {
            CurveControl curveControl = ((EdgeObject)object).curve;

            sql.append(" ( id, sign, type, points, curveF, curveT ) values ( ");
            sql.append(count);
            sql.append(", ");
            sql.append(object.getSign());
            sql.append(", ");
            if(curveControl != null) {
                sql.append(DrawObjects.DRAW_TYPE_CURL);
                sql.append(", ");
                sql.append("'");
                sql.append(arraysToString(points));
                sql.append("'");
                sql.append(", ");
                sql.append("'");
                sql.append(arraysToString(curveControl.bezierF));
                sql.append("'");
                sql.append(", ");
                sql.append("'");
                sql.append(arraysToString(curveControl.bezierT));
                sql.append("'");
            } else {
                sql.append(DrawObjects.DRAW_TYPE_STRAIGHT);
                sql.append(", ");
                sql.append("'");
                sql.append(arraysToString(points));
                sql.append("'");
                sql.append(", ");
                sql.append("'NULL', 'NULL'");
            }
            sql.append(")");
        }
        //Print
        SQLPrint.printObjectToSql(sql.toString());

        return sql.toString();
    }

    private static String arraysToString(ArrayList<AlgoPoint> points) {
        StringBuilder stringBuilder = new StringBuilder();

        if(points == null || points.size() == 0)
            return "NULL";

        for(AlgoPoint point : points) {
            stringBuilder.append(point.toString());
            stringBuilder.append(";");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
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
