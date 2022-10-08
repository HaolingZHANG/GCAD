package com.crazyforcode.oakhouse.gcad.others.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.crazyforcode.oakhouse.gcad.kernel.tools.bufferSet.ImageCache;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.iconOperation.Icon;
import com.crazyforcode.oakhouse.gcad.kernel.tools.iconOperation.IconOperation;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

/**
 * Created by Monomania on 12/8/15.
 * gerResource Identifier
 * xml fragment's property name define realized class
 * LayoutInflater where different from is it would create a new instance as xml describe
 * and findviewbyId() just obtain UI component from xml
 */
public class LegendTileFragment extends Fragment {
    private int weight,
                resourceFragment,
                objectType = DrawObjects.NONE;
    private LegendClickListener listener;
    private OnSignChangeListener callbackListener;
    public static IconOperation icons;
    private ViewGroup vg;
    private ImageCache imageCache;

    @Override
    public void onPause() {
        super.onPause();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (savedInstanceState != null)
            objectType = savedInstanceState.getInt("objectType");*/
        Log.i("fragment 接受Bundle", ""+getArguments().getInt("objectType"));

        int loopCount = 0;

        imageCache = new ImageCache(getContext(), "IconCache");
        if (icons == null) {
            icons = new IconOperation();
            icons.initIcons(getContext());
        }

        listener = new LegendClickListener();
        weight = getArguments().getInt("weight");
        objectType = getArguments().getInt("objectType");
        resourceFragment = getContext().getResources()
                .getIdentifier("fragment_legend" + weight + "_picker", "layout", getActivity().getPackageName());
        Log.i("packageName", getActivity().getPackageName()+"");

        int vgID = getContext().getResources()
                .getIdentifier("legend" + weight + "view", "id", getActivity().getPackageName());
        //ViewGroup vg = (ViewGroup) getActivity().findViewById(containerViewID);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);//↓R.layout.fragment_legend1_picker
        LinearLayout groupPollingAddress = (LinearLayout) inflater.inflate(resourceFragment, null);
        vg = (ViewGroup) groupPollingAddress.findViewById(vgID);
        switch (weight) {
            case 1:loopCount = 15;break;
            case 2:loopCount = 9;break;
            case 3:loopCount = 18;break;
            case 4:loopCount = 9;break;
            case 5:loopCount = 27;break;
            case 6:loopCount = 6;break;
            default:
        }

        for (int i = 1; i <= loopCount; i++) {
            //getActivity().findViewById/layoutinflater
            /*int imgViewID = getContext().getResources()
                    .getIdentifier("legend" + (weight*100+i) + "ima"
                            , "id", getActivity().getPackageName());


            ImageView imageView = (ImageView) groupPollingAddress.findViewById(imgViewID);*/

            /*ImageView imageView = (ImageView) (getActivity().findViewById(getContext().getResources()
                    .getIdentifier("legend" + (weight*100+i) + "ima"
                            , "id", getActivity().getPackageName())));*/

            //imageView.setBackgroundResource(icons.icons.get(weight - 1).get(i - 1).getLegendRes());
            //imageView.setOnClickListener(listener);
//            String imageKey = String.valueOf(icons.icons.get(weight - 1).get(i - 1).getLegendRes());
//            Bitmap bitmapForThisIcon = imageCache.getBitmapFromMemCache(imageKey);
//
//            if(bitmapForThisIcon != null) {
//                Drawable drawable =new BitmapDrawable(bitmapForThisIcon);
//                vg.getChildAt(i - 1).setBackground(drawable);
//            } else {
                vg.getChildAt(i - 1).setBackgroundResource(icons.icons.get(weight - 1).get(i - 1).getLegendRes());
//                imageCache.addBitmapToCache(String.valueOf(icons.icons.get(weight - 1).get(i - 1).getLegendRes()),
//                        drawableToBitmap(vg.getChildAt(i - 1).getBackground()));
//            }
            if (objectType == DrawObjects.NONE
                    || (objectType == DrawObjects.SPOT &&
                    DrawObjects.getObjectType(icons.icons
                    .get(weight - 1).get(i - 1).getOperateCode()) == objectType)
                    || (objectType == DrawObjects.EDGE
                     && (DrawObjects.getObjectType(icons.icons
                     .get(weight - 1).get(i - 1).getOperateCode()) == DrawObjects.LINE
                     ||DrawObjects.getObjectType(icons.icons
                     .get(weight - 1).get(i - 1).getOperateCode()) == DrawObjects.AREA)))
                vg.getChildAt(i - 1).setOnClickListener(listener);
            else
                ((ImageView) vg.getChildAt(i - 1)).setImageResource(R.drawable.cant_select);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
        //View view = inflater.inflate(resourceFragment, container, false);
/*        ViewGroup vg = (ViewGroup) view.findViewById(R.id.legend1view);
        int childCount = vg.getChildCount();//same as loop count
        for (int i = 0; i < childCount; i++) {
            vg.getChildAt(i).setBackgroundResource(icons.icons.get(weight - 1).get(i).getLegendRes());
            vg.getChildAt(i).setOnClickListener(listener);
        }*/
        //View view = new LinearLayout(getContext()??);
//        ((LinearLayout)view).removeView(??);
        //((LinearLayout) view).addView(vg);
        if (vg.getParent() != null)
            ((ViewGroup)vg.getParent()).removeAllViewsInLayout();

        return vg;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callbackListener = (OnSignChangeListener)context;
        }catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement Callback Listener");
        }
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        callbackListener.onSignChanged(-1);
//        if (listener.lastView != null)
//            ((ImageView) listener.lastView).setImageDrawable(null);
//    }

    public interface OnSignChangeListener {
        public void onSignChanged(int sign);
    }

    private class LegendClickListener implements View.OnClickListener {
        int choosedSign = -1;
        View lastView = null;
        Icon icon = null;

        @Override
        public void onClick(View view) {
            Log.i("onClick", view.toString());

            if (lastView != null)
                ((ImageView) lastView).setImageDrawable(null);

            choosedSign = Integer.parseInt(view.getContentDescription().toString());
            ((ImageView) view).setImageResource(R.drawable.selected3g);
            lastView = view;

            /**按照sign找icon对象,
             * 设置DrawView的chooseDrawObject,
             * GONE掉legendPicker,X dont need
             * 弹出提示toast*/
            callbackListener.onSignChanged(choosedSign);
            icon = icons.searchIcon(choosedSign);

            /*if(DrawObjects.isPoint(choosedSign)) {
                LegendPicker.typeChooser.setVisibility(View.GONE);
                LegendPicker.typeChooser.clearLast();
            }
            else
                LegendPicker.typeChooser.setVisibility(View.VISIBLE);

            LegendPicker.typeChooser.setSign(choosedSign);*/

            if(MainActivity.getNovice())
                TextToast.showTextToast("已选择图例："
                        + getResources().getString(icon.getNoteRes()), getContext());//icon.getNote();
        }
    }

//    private Bitmap drawableToBitmap(Drawable drawable) {
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        } else if (drawable instanceof NinePatchDrawable) {
//            Bitmap bitmap = Bitmap
//                    .createBitmap(
//                            drawable.getIntrinsicWidth(),
//                            drawable.getIntrinsicHeight(),
//                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                                    : Bitmap.Config.RGB_565);
//            Canvas canvas = new Canvas(bitmap);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                    drawable.getIntrinsicHeight());
//            drawable.draw(canvas);
//            return bitmap;
//        } else {
//            return null;
//        }
//    }
}
