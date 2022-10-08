package com.crazyforcode.oakhouse.gcad.kernel.tools.toast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.widget.EditText;

import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.MapInfo;
import com.crazyforcode.oakhouse.gcad.window.AlignActivity;

public class InputBox {

    private static Context currentContext;
    private static AlertDialog.Builder dialog = null;
    private static EditText inputServer;

    public static void init(Activity activity, String title, String rightString, String cancelString) {

        currentContext = activity.getApplicationContext();

        dialog = new AlertDialog.Builder(activity);

        inputServer = new EditText(currentContext);
        inputServer.setTextColor(Color.BLACK);
        inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputServer.setText(String.valueOf(MapInfo.getMapDPI()));

        dialog.setTitle(title);
        dialog.setView(inputServer);
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
            try {
                if(inputServer.getText() != null) {
                    MapInfo.setMapDPI(Integer.parseInt(String.valueOf(inputServer.getText())));
                    AlignActivity.showMap();
                    currentContext = null;
                    dialog.cancel();
                }
            } catch (NumberFormatException e) {
                TextToast.showTextToast("您输入的dpi有误", currentContext);
            }
        }

    }

    static class CancelDriver implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            currentContext = null;
            dialog.cancel();
        }
    }
}
