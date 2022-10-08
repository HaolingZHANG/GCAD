package com.crazyforcode.oakhouse.gcad.kernel.tools.toast;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ImageTextToast {

    public static void showToast(Bitmap bitmap, Context context) {
        Toast toast = Toast.makeText(context, "已加载", Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(context);
        imageCodeProject.setImageBitmap(bitmap);
        imageCodeProject.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        toastView.addView(imageCodeProject, 100, 100);
        toastView.setGravity(Gravity.CENTER);

        toast.show();
    }
}
