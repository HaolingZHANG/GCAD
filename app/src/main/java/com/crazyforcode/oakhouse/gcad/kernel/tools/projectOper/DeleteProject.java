package com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class DeleteProject {

    public static boolean recursionDeleteFile(String projectName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String rootPath = preferences.getString("RootPath", "");
        ChangeProject.changeFileName(projectName, "_____", context);
        recursionDeleteFile(new File(rootPath + "/_____"));
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void recursionDeleteFile(File file) {
        if(file.isFile()) {
            file.delete();
            return;
        }
        if(file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if(childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for(File childFile : childFiles)
                recursionDeleteFile(childFile);

            file.delete();
        }
    }
}
