package com.crazyforcode.oakhouse.gcad.kernel.tools.toast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.crazyforcode.oakhouse.gcad.bluetooth.SetBlueTooth;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.DeleteProject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.LoadProject;
import com.crazyforcode.oakhouse.gcad.others.components.CompassView;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;

import java.lang.reflect.Method;

public class UserPromptBox {

    public static final int EXIT = 0;
    public static final int NETWORK_OPEN = 1;
    public static final int NEW_PROJECT_WORING = 2;

    private static int boxType;
    private static Context currentContext;
    private static AlertDialog.Builder dialog = null;

    public static void init(Activity activity, String title,
                            String prompt, String rightString, String cancelString, int type) {

        boxType = type;

        currentContext = activity.getApplicationContext();

        dialog = new AlertDialog.Builder(activity);

        dialog.setTitle(title);

        dialog.setMessage(prompt);

        dialog.setPositiveButton(rightString, new RightDriver());

        dialog.setNegativeButton(cancelString, new CancelDriver());
    }

    public static void show() {
        dialog.show();

        dialog = null;
    }

    static class RightDriver implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(boxType == EXIT) {
//                释放所有资源
                SaveTotalMap.deleteWhenQuit();
                KernelLayout.deleteWhenQuit();
                CompassView.deleteWhenQuit();
                SetBlueTooth.deleteWhenQuit();

                currentContext = null;
                dialog.cancel();
                System.gc();

//                正常退出App
                System.exit(0);
            } else if(boxType == NETWORK_OPEN) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager)currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);

                Method setMobileDataEnable;

                try {
                    setMobileDataEnable = connectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
                    setMobileDataEnable.invoke(connectivityManager, true);
                } catch (Exception ignored) {

                }
                currentContext = null;
                dialog.cancel();
            } else if(boxType == NEW_PROJECT_WORING) {
                String name = LoadProject.getProjectName();
                DeleteProject.recursionDeleteFile(name, currentContext);
                currentContext = null;
                dialog.cancel();
//                正常退出App
                System.exit(0);
            }
        }
    }

    static class CancelDriver implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(boxType == EXIT) {
                currentContext = null;
                dialog.cancel();
            } else if(boxType == NETWORK_OPEN) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager)currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if(!networkInfo.isConnectedOrConnecting()) {
                    TextToast.showTextToast("为成功开启数据流量，\n" +
                            "多人协作模式开启失败！", currentContext);
                }
                currentContext = null;
                dialog.cancel();
            } else if(boxType == NEW_PROJECT_WORING) {
                currentContext = null;
                dialog.cancel();
            }
        }
    }
}

