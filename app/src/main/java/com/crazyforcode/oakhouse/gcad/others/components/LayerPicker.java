package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

public class LayerPicker extends RelativeLayout implements View.OnClickListener {

    private static ImageView layerImgs[];
    private static boolean layerIsChoosed[];
    private static int[] imgsLightRes = {R.mipmap.layer_picker_vegetation_light,
                                    R.mipmap.layer_picker_terrain_light,
                                    R.mipmap.layer_picker_water_light,
                                    R.mipmap.layer_picker_construction_light};
    private static int[] imgsDarkRes = {R.mipmap.layer_picker_vegetation_dark,
                                    R.mipmap.layer_picker_terrain_dark,
                                    R.mipmap.layer_picker_water_dark,
                                    R.mipmap.layer_picker_construction_dark};

    public LayerPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public LayerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LayerPicker(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
//        0:植被、1:地质、2:水体、3:建筑物、4:特殊标记
        View.inflate(context, R.layout.layer_picker, this);

        layerImgs = new ImageView[5];
        layerIsChoosed = new boolean[5];//TODO 初始化图层是否选中
        layerImgs[4] = (ImageView)findViewById(R.id.map_img);/**map*/
        layerImgs[0] = (ImageView)findViewById(R.id.vegetation_img);
        layerImgs[1] = (ImageView)findViewById(R.id.geologic_img);
        layerImgs[2] = (ImageView)findViewById(R.id.water_img);
        layerImgs[3] = (ImageView)findViewById(R.id.struct_img);

        for(int i = 0; i < 5; i++)
            layerImgs[i].setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.map_img:
                if (layerIsChoosed[4]) {
                    layerImgs[4].setImageResource(R.mipmap.layer_picker_template_dark);
                    KernelLayout.hideMap();
                    layerIsChoosed[4] = false;
                } else {
                    layerImgs[4].setImageResource(R.mipmap.layer_picker_template_light);
                    KernelLayout.showMap();
                    layerIsChoosed[4] = true;
                }
                break;
            case R.id.vegetation_img:adjustLayer(0);break;
            case R.id.geologic_img:adjustLayer(1);break;
            case R.id.water_img:adjustLayer(2);break;
            case R.id.struct_img:adjustLayer(3);break;
            default:
        }

        findTopLevel();
    }

    private void adjustLayer(int layer) {
        if(KernelLayout.isDrawing) {
            TextToast.showTextToast("不可在绘制时切换面板", getContext());
        } else {
            if (layerIsChoosed[layer]) {
                layerImgs[layer].setImageResource(imgsDarkRes[layer]);
                //KernelLayout.drawViews[layer].setVisibility(GONE);
                KernelLayout.drawView.levelVisible[layer] = false;
                KernelLayout.drawView.invalidate();//TODO invalidate(Rect)
                layerIsChoosed[layer] = false;
            } else {
                layerImgs[layer].setImageResource(imgsLightRes[layer]);
                //KernelLayout.drawViews[layer].setVisibility(VISIBLE);
                KernelLayout.drawView.levelVisible[layer] = true;
                KernelLayout.drawView.invalidate();
                layerIsChoosed[layer] = true;
                //TODO 如果当前顶层面板没有sign中的元素，就清空当前元素图标
                if (KernelLayout.getLevel() < 3) {
                    MainActivity mainActivity = (MainActivity) MainActivity.staticActivity;
                    mainActivity.setNoDraw();
                }
            }
        }
    }

    private void findTopLevel() {
//        0:植被、1:地质、2:水体、3:建筑物、4:特殊标记
        int top = KernelLayout.ONLY_MAP;

        for(int i = 0; i < layerIsChoosed.length; i++)
            if(layerIsChoosed[i])
                top = i;

        KernelLayout.topLevelChange(top);
    }

    public static void saveDrawBack() {
        //        0:植被、1:地质、2:水体、3:建筑物、4:特殊标记
//        置换顶层图层前保存前图层的画为静态图
        Log.i("saveDrawBack", KernelLayout.getLevel()+"topLevel");
        int topLayer = KernelLayout.getLevel();
        if (topLayer >= 0) {
           /* DrawView topView = KernelLayout.drawViews[KernelLayout.getLevel()];

            if (topView.background == null)
                topView.initBackground();
            else
                topView.background = topView.paintToBack(topView.background);*/

            if (KernelLayout.drawView.backgrounds[topLayer] == null)
                KernelLayout.drawView.initBackground(topLayer);
            else
                KernelLayout.drawView.paintToBack(KernelLayout.drawView.backgrounds[topLayer]);
        }
    }

    public static void setLayerWhenDraw(int level) {
//        0:植被、1:地质、2:水体、3:建筑物、4:特殊标记
//        置换顶层图层前保存前图层的画为静态图
       /* if (KernelLayout.getLevel() >= 0) {
            DrawView topView = KernelLayout.drawViews[KernelLayout.getLevel()];

            if (topView.background == null)
                topView.initBackground();
            else
                topView.background = topView.paintToBack(topView.background);
            //topView.postInvalidate();

        }*/
        //saveDrawBack();

        Log.i("setLayerWhenDraw", "level"+level);
        if(level != 4)
            showDrawView(level);
        else {
//            绘制特殊面板
            //KernelLayout.drawViews[4].setVisibility(VISIBLE);
            KernelLayout.drawView.levelVisible[4] = true;
            KernelLayout.drawView.invalidate();//TODO invalidata(Rect);
            MainActivity.setShowSpecialLayer();
        }

        for(int i = level + 1; i < 4; i++)
            hideDrawView(i);

        KernelLayout.topLevelChange(level);
    }

    private static void showDrawView(int layer) {
        Log.i("showDrawView", "is it? layer"+layer);
        if(!layerIsChoosed[layer]) {
            /*switch(layer) {
                case 0:layerImgs[0].setImageResource(R.mipmap.layer_picker_vegetation_light);break;
                case 1:layerImgs[1].setImageResource(R.mipmap.layer_picker_terrain_light);break;
                case 2:layerImgs[2].setImageResource(R.mipmap.layer_picker_water_light);break;
                case 3:layerImgs[3].setImageResource(R.mipmap.layer_picker_construction_light);break;
                default:
            }*/
            layerImgs[layer].setImageResource(imgsLightRes[layer]);
            //KernelLayout.drawViews[layer].setVisibility(VISIBLE);
            KernelLayout.drawView.levelVisible[layer] = true;
            KernelLayout.drawView.invalidate();//TODO invalidate(Rect)
            layerIsChoosed[layer] = true;
        }
    }

    private static void hideDrawView(int layer) {
        if(layerIsChoosed[layer]) {
            /*switch(layer) {
                case 0:layerImgs[0].setImageResource(R.mipmap.layer_picker_vegetation_dark);break;
                case 1:layerImgs[1].setImageResource(R.mipmap.layer_picker_terrain_dark);break;
                case 2:layerImgs[2].setImageResource(R.mipmap.layer_picker_water_dark);break;
                case 3:layerImgs[3].setImageResource(R.mipmap.layer_picker_construction_dark);
            }*/
            layerImgs[layer].setImageResource(imgsDarkRes[layer]);
            //KernelLayout.drawViews[layer].setVisibility(GONE);
            KernelLayout.drawView.levelVisible[layer] = false;
            KernelLayout.drawView.invalidate();//TODO invalidate(Rect)
            layerIsChoosed[layer] = false;
        }
    }

    public static void changeMap() {
        layerImgs[4].setImageResource(R.mipmap.layer_picker_template_dark);
        KernelLayout.hideMap();
        layerIsChoosed[4] = false;
    }
}