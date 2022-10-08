package com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.SQLAccess;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.MapInfo;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SaveProject {

    @SuppressLint("SimpleDateFormat")
    public static void saveProjectInfoToFile(String projectPath) {
        File info = null;
        try {
            info = new File(projectPath + "/Data/Info.txt");
            String projectName = projectPath.substring(projectPath.lastIndexOf("/") + 1, projectPath.length());
            if (!info.exists())
                Log.i("信息文件创建成功", info.createNewFile() + "");

            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));

            RandomAccessFile randomAccessFile = new RandomAccessFile(info, "rw");
            randomAccessFile.write((projectName + "\n").getBytes());
            randomAccessFile.write((MapInfo.getMapDPI() + "\n").getBytes());
            randomAccessFile.write((SaveTotalMap.getMapScale() + "\n").getBytes());
            randomAccessFile.write("0\n".getBytes());
            randomAccessFile.write((date + "\n").getBytes());
            randomAccessFile.close();
        } catch (IOException e) {
            Log.i("信息文件书写失败", info.getPath());
        }
    }

    public static void saveProjectInSql(HashMap<Integer, ArrayList<DrawObject>> objects, Context context) {
        SQLAccess.saveDrawObject(objects, context);
    }
}
