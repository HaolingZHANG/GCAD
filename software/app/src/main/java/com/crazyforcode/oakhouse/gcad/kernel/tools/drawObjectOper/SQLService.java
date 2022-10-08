package com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class SQLService extends SQLiteOpenHelper {

    private File dbFile;

    private static final int DB_VERSION = 1;

    public SQLService(Context context, String dbPath) {
        super(context, dbPath, null, DB_VERSION);
        this.dbFile = new File(dbPath);
        onCreate();
    }

    public void onCreate() {
        SQLiteDatabase db = getWritableDatabase();
        for(int i = 0; i < 5; i++) {
            String sqlOne = "CREATE TABLE IF NOT EXISTS DrawObjects" + i;
            String sqlTwo = "(id integer primary key autoincrement," +
                    " sign integer not null," +
                    " type integer not null," +
                    " points text not null," +
                    " curveF text," +
                    " curveT text)";
            db.execSQL(sqlOne + sqlTwo);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("创建DrawView的表", "共计5个");

        for(int i = 0; i < 5; i++) {
            String sqlOne = "CREATE TABLE IF NOT EXISTS DrawObjects" + i;
            String sqlTwo = "(id integer primary key autoincrement," +
                    " sign integer not null," +
                    " type integer not null," +
                    " points text not null," +
                    " curveF text," +
                    " curveT text)";
            execSQL(db, sqlOne + sqlTwo);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i = 0; i < 5; i++)
            execSQL(db, "DROP TABLE IF EXISTS DrawObjects" + i);
        onCreate(db);
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        boolean isFileCreateSuccess = false;

        if(!dbFile.exists())
            try {
                isFileCreateSuccess = dbFile.createNewFile();
                onCreate(SQLiteDatabase.openOrCreateDatabase(dbFile.getPath(), null));
                Log.i("数据库文件", "创建成功");
            } catch (IOException ignored) {
                Log.i("数据库文件", "创建不成功");
            }
        else
            isFileCreateSuccess = true;

        if(isFileCreateSuccess)
            return SQLiteDatabase.openOrCreateDatabase(dbFile.getPath(), null);
        else
            return null;
    }

    public void execSQL(SQLiteDatabase db, String sql) {
        db.execSQL(sql);
    }

    public Cursor query(SQLiteDatabase db, String sql) {
        return db.rawQuery(sql, null);
    }

    public int getDbVersion() {
        return DB_VERSION;
    }

    public void close() {
        if(dbFile.exists())
            SQLiteDatabase.openOrCreateDatabase(dbFile, null).close();
    }
}
