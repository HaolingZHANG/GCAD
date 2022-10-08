package com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MThreadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ProjectInfo {

    private Bitmap map;
    private String projectName;
    private int dpi;
    private double scale;
    private int count;
    private String date;

    private static ArrayList<ProjectInfo> projectInfos = null;

    public ProjectInfo(Bitmap map, String projectName, int dpi, double scale, int count, String date) {
        this.map = map;
        this.projectName = projectName;
        this.dpi = dpi;
        this.scale = scale;
        this.count = count;
        this.date = date;
    }

    public Bitmap getMap() {
        return map;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public int getDpi() {
        return dpi;
    }

    public int getCount() {
        return this.count;
    }

    public String getDate() {
        return this.date;
    }

    public double getScale() {
        return scale;
    }

    public static ProjectInfo getCurrentProject(String projectName) {
        for(int i = 0; i < projectInfos.size(); i++)
            if(projectInfos.get(i).getProjectName().equals(projectName))
                return projectInfos.get(i);

        return null;
    }

    public static ArrayList<ProjectInfo> getProject(Context context) {
        if(projectInfos == null) {
            projectInfos = new ArrayList<>();
            SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String rootPath = preferences.getString("RootPath", "");

            File file = new File(rootPath);
            if (file.exists() && file.isDirectory())
                if (file.list().length > 0)
                    for (String path : file.list()) {
                        String pathInEachProject = rootPath + "/" + path;
                        File thumbnail = new File(pathInEachProject + "/Map/Thumbnail.gcadP");
                        if(thumbnail.exists()) {
                            String[] info = getInfo(new File(pathInEachProject + "/Data/Info.txt"));

                            if (info != null)
                                projectInfos.add(new ProjectInfo(BitmapFactory.decodeFile(thumbnail.getPath(), null), info[0], Integer.parseInt(info[1]),
                                        Double.parseDouble(info[2]), Integer.parseInt(info[3]), info[4]));
                        } else {
                            String p = pathInEachProject.substring(pathInEachProject.lastIndexOf("/") + 1, pathInEachProject.length());
                            Log.i("清除未成功建立的工程", p);
                            deleteProject(p, context);
                        }
                    }
        }
        return projectInfos;
    }

//    public static ArrayList<ProjectInfo> getProjectsByRunnable(Context context) {
//        try {
//            if(projectInfos == null)
//                setProjects(context);
//        } catch(NullPointerException ignored) {
//
//        }
//        return projectInfos;
//    }

    private static void setProjects(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String rootPath = preferences.getString("RootPath", "");

        MHandler mHandler = new MHandler(context);

        LoadProjectsRunnable loadProjectsRunnable = new LoadProjectsRunnable(mHandler, rootPath);

        MThreadPool.addTask(loadProjectsRunnable);
        MThreadPool.start();
    }

    public static void setProjectInfos(ArrayList<ProjectInfo> infos) {
        projectInfos = infos;
    }

    private static String[] getInfo(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String temp;
            String s = "";

            while ((temp = bufferedReader.readLine()) != null)
                s += temp + "\n";

            return s.split("\n");
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean isSameName(String newName) {
        for(int i = 0; i < projectInfos.size(); i++)
            if(projectInfos.get(i).getProjectName().equals(newName))
                return false;
        return true;
    }

    public static void changeProjectName(String oldName, String newName, Context context) {
        for(int i = 0; i < projectInfos.size(); i++)
            if(projectInfos.get(i).getProjectName().equals(oldName))
                projectInfos.get(i).setProjectName(newName);

        ChangeProject.changeFileName(oldName, newName, context);
    }

    public static void deleteProject(String projectName, Context context) {
        for(int i = 0; i < projectInfos.size(); i++)
            if(projectInfos.get(i).getProjectName().equals(projectName))
                projectInfos.remove(i);

        DeleteProject.recursionDeleteFile(projectName, context);
    }
}
