package com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.SQLAccess;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.MapInfo;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MThreadPool;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.ObtainTotalMapRunnable;

import java.io.File;

public class LoadProject {

    private static String name;

    public static boolean loadProject(String projectName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String projectPath = preferences.getString("RootPath", "") + "/" + projectName;

        name = projectName;

        return loadDraw(projectPath, context) && loadTemplate(projectPath, context);
    }

    private static boolean loadTemplate(String projectPath, Context context) {
        File file = new File(projectPath + "/Map/OriginTotalMap.gcadP");
        if(file.exists()) {
            Uri uri = Uri.fromFile(file);

            MHandler mHandler = new MHandler(context);
            ObtainTotalMapRunnable obtainTotalMapRunnable = new ObtainTotalMapRunnable(mHandler,
                    uri, MHandler.INPUT_MAP_AGAIN);
            MThreadPool.addTask(obtainTotalMapRunnable);
            MThreadPool.start();

            return true;
        }
        return false;
    }

    private static boolean loadDraw(String projectPath, Context context) {
        SQLAccess.initSQLService(context, projectPath, false);
        return true;
    }

    public static String getProjectName() {
        return name;
    }

    public static void setDpi() {
        ProjectInfo project = ProjectInfo.getCurrentProject(name);
        if (project != null)
            MapInfo.setMapDPI(project.getDpi());
    }
}
