package com.crazyforcode.oakhouse.gcad.test.FileTest;

import android.content.Context;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.ProjectInfo;

import java.util.ArrayList;

public class ProjectInfoPrint {

    public static void printAllProjectInfo(Context context) {
        ArrayList<ProjectInfo> infos = ProjectInfo.getProject(context);
        int count = 0;
        for(ProjectInfo info : infos) {
            Log.i("个数", (count++) + "");
            Log.i("Name", info.getProjectName());
            Log.i("Scale", info.getScale() + "");
            Log.i("Count", info.getCount() + "");
            Log.i("Dpi", info.getDpi() + "");
            Log.i("Date", info.getDate());
        }
    }
}
