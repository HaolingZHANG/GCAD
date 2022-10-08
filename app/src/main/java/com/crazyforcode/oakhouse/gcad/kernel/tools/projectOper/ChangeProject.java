package com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ChangeProject {

    public static boolean changeFileName(String oldName, String newName, Context context) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String rootPath = preferences.getString("RootPath", "");

            File project = new File(rootPath + "/" + oldName);

            saveInfos(new File(project.getPath() + "/Data/Info.txt"), newName);

            return project.renameTo(new File(rootPath + "/" + newName));
        } catch (IOException e) {
            Log.i("更改文件名", "保存出错");
            return false;
        }
    }

    private static void saveInfos(File file, String newName) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String temp;
        String s = "";
        while ((temp = bufferedReader.readLine()) != null)
            s += temp + "\n";

        String[] infos = s.split("\n");

        infos[0] = newName;
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.write((infos[0] + "\n").getBytes());
        randomAccessFile.write((infos[1] + "\n").getBytes());
        randomAccessFile.write((infos[2] + "\n").getBytes());
        randomAccessFile.write((infos[3] + "\n").getBytes());
        randomAccessFile.write((infos[4] + "\n").getBytes());
        randomAccessFile.close();
    }

    public static void changeScale(File file, double scale) {
        try {
             FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String temp;
            String s = "";
            while ((temp = bufferedReader.readLine()) != null)
                s += temp + "\n";

            String[] infos = s.split("\n");

            infos[2] = String.valueOf(scale);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.write((infos[0] + "\n").getBytes());
            randomAccessFile.write((infos[1] + "\n").getBytes());
            randomAccessFile.write((infos[2] + "\n").getBytes());
            randomAccessFile.write((infos[3] + "\n").getBytes());
            randomAccessFile.write((infos[4] + "\n").getBytes());
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("更改比例尺", "保存出错");
        }
    }
}
