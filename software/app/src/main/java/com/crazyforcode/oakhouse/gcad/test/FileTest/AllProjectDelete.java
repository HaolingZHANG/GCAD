package com.crazyforcode.oakhouse.gcad.test.FileTest;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class AllProjectDelete {

    public static void deleteAdd(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String rootPath = preferences.getString("RootPath", "");
        recursionDeleteFile(new File(rootPath));
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
