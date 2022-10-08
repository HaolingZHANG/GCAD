package com.crazyforcode.oakhouse.gcad.kernel.tools.toast;


import android.content.Context;
import android.widget.Toast;

public class TextToast {
    private static String oldDescription;

    protected static Toast toast = null;

    private static long oneTime = 0;

    public synchronized static void showTextToast(final String description, final Context context) {
        if(toast == null) {
            toast = Toast.makeText(context, description, Toast.LENGTH_SHORT);

            toast.show();

            oneTime = System.currentTimeMillis();
        } else {
            long twoTime = System.currentTimeMillis();

            if(description.equals(oldDescription)) {
                if ((twoTime - oneTime) > Toast.LENGTH_LONG)
                    toast.show();
            } else {
                oldDescription = description;
                toast.setText(description);
                toast.show();
            }
            oneTime = twoTime;
        }
    }
}
