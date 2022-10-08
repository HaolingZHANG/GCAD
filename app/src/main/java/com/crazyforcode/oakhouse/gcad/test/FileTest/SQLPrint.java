package com.crazyforcode.oakhouse.gcad.test.FileTest;

import android.util.Log;

public class SQLPrint {

    public static void printObjectToSql(String sql) {
        Log.i("sql operation", "-----------------------------------------------");
        Log.i("sql to file", "sql");
        Log.i("sql", sql);
        Log.i("sql operation", "-----------------------------------------------");
    }

    public static void printSqlToObject(int sign, int type, String points, String curveF, String curveT) {
        Log.i("sql operation", "-----------------------------------------------");
        Log.i("sql from file", "object");
        Log.i("object", "attribute");
        Log.i("sign", sign + "");
        Log.i("type", type + "");
        Log.i("points", points);
        Log.i("curveF", curveF);
        Log.i("curveT", curveT);
        Log.i("sql operation", "-----------------------------------------------");
    }
}
