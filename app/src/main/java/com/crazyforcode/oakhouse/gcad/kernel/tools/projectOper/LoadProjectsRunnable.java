package com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadProjectsRunnable extends MRunnable {

    private ArrayList<ProjectInfo> projectInfos = new ArrayList<>();
    private String rootPath;

    public LoadProjectsRunnable(MHandler handler, String rootPath) {
        super(handler);
        this.rootPath = rootPath;
    }

    @Override
    public void running() {
        Message message;
        try {
            File file = new File(rootPath);
            if (file.exists() && file.isDirectory())
                if (file.list().length > 0)
                    for (String path : file.list()) {
                        String pathInEachProject = rootPath + "/" + path;
                        File thumbnail = new File(pathInEachProject + "/Map/FinalMap0.jpg");
                        String[] info = getInfo(new File(pathInEachProject + "/Data/Info.txt"));

                        if (info != null) {
                            if (thumbnail.exists())
                                projectInfos.add(new ProjectInfo(getThumbnail(thumbnail), info[0], Integer.parseInt(info[1]),
                                        Double.parseDouble(info[2]), Integer.parseInt(info[3]), info[4]));
                            else
                                projectInfos.add(new ProjectInfo(getThumbnail(new File(pathInEachProject + "/Map/Thumbnail.gcadP")), info[0], Integer.parseInt(info[1]),
                                        Double.parseDouble(info[2]), Integer.parseInt(info[3]), info[4]));
                        }
                    }

            ProjectInfo.setProjectInfos(projectInfos);

            message = getMHandler().obtainMessage(MHandler.LOAD_PROJECTS, MHandler.SUCCESS, 0);

            getMHandler().sendMessageAtTime(message, 0);
        } catch(NullPointerException e) {
            message = getMHandler().obtainMessage(MHandler.LOAD_PROJECTS, MHandler.FAILURE, 0);

            getMHandler().sendMessageAtTime(message, 0);
        }
    }

    public static Bitmap getThumbnail(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        options.inJustDecodeBounds = false;

        int compress = (int)(options.outHeight / (float)200);
        if(compress <= 0)
            compress = 1;

        options.inSampleSize = compress;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    public static String[] getInfo(File file) {
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
}
