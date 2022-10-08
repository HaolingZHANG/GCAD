package com.crazyforcode.oakhouse.gcad.kernel.tools.iconOperation;


import android.content.Context;

import java.util.ArrayList;

public class IconOperation {

    public ArrayList<ArrayList<Icon>> icons = new ArrayList<>(6);
    private int[] signsLength = {15, 9, 18, 9, 27, 6};
    private static final int SIGNS_TYPE_NUM = 6;

    public void initIcons(Context context) {
        //Icon myIcon = null;
        for (int i = 0, sign = 0; i < SIGNS_TYPE_NUM; i++) {
            icons.add(new ArrayList<Icon>(signsLength[i]));
            for (int j = 1; j <= signsLength[i]; j++) {
                sign = (i+1)*100+j;
                icons.get(i).add( new Icon(context.getResources()
                        .getIdentifier("s" + sign, "drawable", context.getPackageName())//"com.crazyforcode.oakhouse.gcad:drawable/icons/s"+sign,null,null)
                        , sign
                        , context.getResources()
                        .getIdentifier("s" + sign, "string", context.getPackageName())));//R.string.s
            }
        }
    }

    public Icon searchIcon(int sign) {
        for (int i = 0; i < icons.size(); i++) {
            for (int j = 0; j < icons.get(i).size(); j++) {
                if (icons.get(i).get(j).getOperateCode() == sign)
                    return icons.get(i).get(j);
            }
        }

        return null;
    }
}
