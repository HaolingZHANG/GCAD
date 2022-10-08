package com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper;

import android.util.Log;

import com.crazyforcode.oakhouse.gcad.draw.legendPaint.AreaObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.LineObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.SpotObject;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/04/29.
 */
public class DrawObjectFactory {

    //TODO every id in svg file
    private enum  SVGIds{ORCHARD("_x34_13.0_Orchard_x2C__one_direction", 104),
        CULAND("_x34_15.0_Cultivated_land", 105),
        EARTHBANK("_x31_06.2_Earth_bank__x5F_thicker_x5F__4", 106);

        private String id;
        private int sign;
        SVGIds(String id, int sign){
            this.id = id;
            this.sign = sign;
        }

        private String getId(){
            return id;
        }

        private int getSign(){
            return sign;
        }
    }

    public static void createDrawObject(int sign) {
        createDrawObject(sign, 0);
    }

    /**check does sign below to area or line or point*/
    public static DrawObject createDrawObject(int sign, int drawType) {
        DrawObject instance = null;

        Log.i("DragetObjecctType", "" + DrawObjects.getObjectType(sign));
        switch (DrawObjects.getObjectType(sign)) {
            case DrawObjects.AREA: Log.i("Enter Area","");
                KernelLayout.isDrawing = true;
                instance = createAreaObject(sign, drawType);break;
            case DrawObjects.LINE: Log.i("Enter line","");
                KernelLayout.isDrawing = true;
                instance = createLineObject(sign, drawType);break;
            case DrawObjects.SPOT: Log.i("Enter point","that's correct");
                KernelLayout.isDrawing = true;
                instance = createSpotObject(sign);break;
            default: Log.i("没有匹配的sign",""+DrawObjects.getObjectType(sign));
                KernelLayout.isDrawing = false;//画笔选择异常，无画笔可用不'开始'绘
        }
        //chooseAreaObject(313, true);

        return instance;
    }

    /**drawObjects映射中每个键指向的是每种sign的对象集合arraylist
     * 一个开头就是一个对象，即path只会在开头有一次moveTo()
     * 搜索时可以找层、找arraylist的最大最小坐标*/
    public static LineObject createLineObject(int sign, int drawType) {
        return new LineObject(sign, drawType);
    }

    public static LineObject createLineObject(int sign) {
        return createLineObject(sign, DrawObjects.DRAW_TYPE_STRAIGHT);
    }

    public static SpotObject createSpotObject(int sign) {
        return new SpotObject(sign);
    }

    public static DrawObject createAreaObject(int sign, int drawType) {
        return new AreaObject(sign, drawType);
    }

    public static DrawObject createAreaObject(int sign) {
        return createAreaObject(sign, DrawObjects.DRAW_TYPE_STRAIGHT);
    }


    /**for svg input*/
    public static DrawObject getDrawObjectById(String id, int drawType) {
        for (SVGIds svgId : Arrays.asList(SVGIds.values())) {
            if (svgId.getId().equals(id)) {
                return createAreaObject(svgId.getSign(), drawType);
            }
        }

        Log.i("drawOFactory error!!!", id+"cannot match!");
        return null;
    }
}
