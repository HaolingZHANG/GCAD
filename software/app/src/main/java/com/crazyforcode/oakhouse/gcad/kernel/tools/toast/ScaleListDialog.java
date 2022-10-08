package com.crazyforcode.oakhouse.gcad.kernel.tools.toast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.ChangeProject;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import java.io.File;

public class ScaleListDialog {

    private static AlertDialog.Builder dialog = null;
    private static String[] scales;

    public static void init(Activity activity) {
        Context currentContext = activity.getApplicationContext();
        dialog = new AlertDialog.Builder(activity);

        dialog.setTitle("选择比例尺");

        scales = currentContext.getResources().getStringArray(R.array.final_scales);
        RadioOnClickListener radioOnClickListener = new RadioOnClickListener(scaleCount(scales));
        dialog.setSingleChoiceItems(scales, radioOnClickListener.getIndex(), radioOnClickListener);
    }

    public static void show() {
        dialog.show();
        dialog = null;
    }

    private static int scaleCount(String[] scales) {
        for(int i = 0; i < scales.length; i++)
            if(Integer.parseInt(scales[i]) == (int)SaveTotalMap.getMapScale())
                return i;

        return 0;
    }

    static class RadioOnClickListener implements DialogInterface.OnClickListener {

        private int index;

        public RadioOnClickListener(int index){
            setIndex(index);
        }

        public void setIndex(int index){
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void onClick(DialogInterface dialog, int whichButton){
            double scale = Double.parseDouble(scales[whichButton]);
            SaveTotalMap.setMapScale(scale);
            ChangeProject.changeScale(new File(MainActivity.getProjectPath() + "/Data/Info.txt"), scale);
            dialog.dismiss();
        }
    }
}
