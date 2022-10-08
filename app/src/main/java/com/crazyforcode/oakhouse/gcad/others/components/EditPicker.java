package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.crazyforcode.oakhouse.gcad.draw.legendPaint.AreaObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.LineObject;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.SpotObject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.kernel.views.DrawView;
import com.crazyforcode.oakhouse.gcad.others.surfaces.LegendPicker;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import static com.crazyforcode.oakhouse.gcad.others.components.EditPicker.EditType.*;

public class EditPicker extends LinearLayout {
    public enum EditType implements View.OnClickListener {
        NONE(-1, R.id.edit_switch, 0, null){
            @Override
            public void onClick(View view) {
                if(KernelLayout.isEditing) {
                    if(show)
                        MainActivity.hideEditPicker();
                    else
                        MainActivity.showEditPicker();

                    show = !show;
                }
            }
        },//index -1 never could be invoke, or invoke startEdit
        EDIT_OBJECT(0, R.id.edit0, R.drawable.e0_dark, Sustainability.HOLDON) {
            @Override
            public void onClick(View view) {
                if(editType == EDIT_OBJECT) {
                    this.stopEdit();
                    return;
                }

                if (this.startEdit()) {
                    editOperations[code()].setBackgroundResource(R.drawable.e0_light);
                }else {
                    this.stopEdit();
                }

                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_0)
                                    , context);
            }
        },
        EDIT_POINT(1, R.id.edit1, R.drawable.e1_dark, Sustainability.HOLDON) {
            @Override
            public void onClick(View view) {

                if(editType == EDIT_POINT) {
                    editType = NONE;
                    this.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    return;
                }
                if (this.startEdit()) {
                    editOperations[code()].setBackgroundResource(R.drawable.e1_light);
                    editType = EDIT_POINT;
                }else {
                    this.stopEdit();
                }
                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_1)
                                    , context);
            }
        },
        JOIN(2, R.id.edit2, R.drawable.e2_dark, Sustainability.ONESHOT) {//瞬发
            @Override
            public void onClick(View view) {
//              cleanType();
                if(editType == JOIN) {
                    this.stopEdit();
                    return;
                }

                if(MainActivity.getNovice())
                    TextToast.showTextToast(
                            context.getResources().getString(R.string.EDIT_TYPE_NAME_2)
                            , context);

                if (this.startEdit()) {
                    //editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                    editOperations[code()].setBackgroundResource(R.drawable.e2_light);
                    editType = JOIN;
                    try {
                        if (KernelLayout.drawView
                                .join(EditPicker.WITH_SELF))
                            TextToast.showTextToast("首尾连接终", context);
                        else
                            TextToast.showTextToast("首尾相距过远", context);
                    } catch (ClassCastException ex) {
                        TextToast.showTextToast("非线性对象！", context);
                    }/*catch (NullPointerException exx) {
                    TextToast.showTextToast("未选择对象", context);
                }*/
                }

                this.stopEdit();

            }
        },
        MERGE(3, R.id.edit3, R.drawable.e3_dark, Sustainability.ONESHOT) {//瞬时
            @Override
            public void onClick(View view) {

                if(editType == MERGE) {
                    this.stopEdit();
                    return;
                }
                if (this.startEdit()) {
                    editOperations[3].setBackgroundResource(R.drawable.e3_light);
                    editType = MERGE;
                    try {
                        if (KernelLayout.drawView
                                .join(EditPicker.WITH_OTHER))
                            TextToast.showTextToast("合并对象已整合显示", context);
                        else
                            TextToast.showTextToast("没有足够近的同类", context);
                    }catch (ClassCastException ex) {
                        TextToast.showTextToast("非线性对象！", context);
                    }
                }

                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_3)
                                    , context);

                this.stopEdit();
            }
        }, 
        ROTATE(4, R.id.edit4, R.drawable.e4_dark, Sustainability.HOLDON) {
            @Override
            public void onClick(View view) {

                if(editType == ROTATE) {
                    this.stopEdit();
                    return;
                }
                startEdit();
                editOperations[4].setBackgroundResource(R.drawable.e4_light);

                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_4)
                                    , context);
            }
        },
        DELETE(5, R.id.edit5, R.drawable.e5_dark, Sustainability.ONESHOT) { //瞬时
            @Override
            public void onClick(View view) {

                if(editType == DELETE) {
                    this.stopEdit();
                    return;
                }

                if (this.startEdit()) {
                    editOperations[5].setBackgroundResource(R.drawable.e5_light);
                    //TODO 删除点对象时只删一个点?
                    DrawView.cleanPainter();
                    KernelLayout.drawView.invalidate();
                }

                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_5)
                            , context);

                this.stopEdit();
            }

        },
        CHANGE_SYMBOL(6, R.id.edit6, R.drawable.e6_dark, Sustainability.ONESHOT) {
            @Override
            public void onClick(View view) {

                if(editType == CHANGE_SYMBOL) {
                    this.stopEdit();
                    return;
                }
                if (startEdit()) {
                    editOperations[6].setBackgroundResource(R.drawable.e6_light);

                    Intent intent = new Intent(context, LegendPicker.class);
                    Bundle data = new Bundle();
                    //线面互换，点换点
                    data.putInt("objectType",
                            DrawView.classCheck(SpotObject.class) ?
                                    DrawObjects.SPOT : DrawObjects.EDGE);
                    intent.putExtras(data);
                    MainActivity.staticActivity.startActivityForResult(
                            intent, MainActivity.CHOOSE_SYMBOL);
                }

                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_6)
                            , context);

                this.stopEdit();
            }
        },
        REVERSE_LINE(7, R.id.edit7, R.drawable.e7_dark, Sustainability.ONESHOT) {//瞬时
            @Override
            public void onClick(View view) {
                if(editType == REVERSE_LINE) {
                    this.stopEdit();
                    return;
                }

                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_7)
                                    , context);

                if (this.startEdit()) {
                    editOperations[7].setBackgroundResource(R.drawable.e7_light);
                    try {
                        DrawView.reverseLine();
                    }catch (ClassCastException ex) {
                        TextToast.showTextToast("非线型对象", context);
                    }
                }

                this.stopEdit();
            }
        },
        CUT(8, R.id.edit8, R.drawable.e8_dark, Sustainability.HOLDON) {
            @Override
            public void onClick(View view) {

                if(editType == CUT) {
                    DrawView.editPoint = null;
                    this.stopEdit();
                    return;
                }


                if (this.startEdit()) {
                    if (!DrawView.classCheck(LineObject.class)) {
                        TextToast.showTextToast("非线型对象", context);
                        this.stopEdit();
                        return;
                    }
                    editOperations[8].setBackgroundResource(R.drawable.e8_light);

                }
                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_8)
                                    , context);
            }
        },
        CUT_HOLE(9, R.id.edit9, R.drawable.e9_dark, Sustainability.HOLDON) {
            @Override
            public void onClick(View view) {

                if(editType == CUT_HOLE) {
                    this.stopEdit();
                    return;
                }


                if (this.startEdit()) {
                    if (!DrawView.classCheck(AreaObject.class)) {
                        TextToast.showTextToast("非面状对象", context);
                        this.stopEdit();
                        return;
                    }
                    editOperations[9].setBackgroundResource(R.drawable.e9_light);
                    DrawView.editPoint.setAsHead(true);
                }
                if(MainActivity.getNovice())
                    TextToast
                            .showTextToast(context.getResources().getString(R.string.EDIT_TYPE_NAME_9)
                                    , context);
            }
        };
        
        final static int TYPE_COUNT = 10;
        private final int code;
        private final int id;
        private final int darkDrawable;
        private final Sustainability sustain;
        private enum Sustainability {
            HOLDON{
                @Override
                protected boolean manageEdit() {
                    //KernelLayout.isDrawing = false;
                    //KernelLayout.isEditing = true;
                    if (!DrawView.isPainterLoad()) {
                        TextToast.showTextToast("未选择对象", context);
                        return false;
                    }
                    DrawView.editPoint.setAsHead(true);

                    return true;

                }
            },ONESHOT {
                @Override
                protected boolean manageEdit() {
                    //TODO 瞬时类修改，即点击“编辑方式”即完成编辑的类型。需要对流程的处理
                    if (!DrawView.isPainterLoad()) {
                        return false;
                    }

                    return true;
                }
            };

            abstract protected boolean manageEdit();
        }
        
        EditType(int code, int id, int darkDrawable, Sustainability sustain) {
            this.code = code;
            this.id = id;
            this.darkDrawable = darkDrawable;
            this.sustain = sustain;
        }
        int code() {return code;}
        int id() {return id;}

//        abstract protected void startEdit() {
//            KernelLayout.isDrawing = false;
//            KernelLayout.isEditing = true;
//            EditPicker.editType = this;
//        }
        protected boolean startEdit() {
            editType.stopEdit();

            EditPicker.editType = this;
            editSwitch.setBackgroundResource(R.drawable.e_switch_selected);

            return sustain.manageEdit();
        }

        protected void stopEdit() {
            if (this == NONE)
                return;

            Log.i("stopEidt", "调用者："+editType.name());
            editOperations[code].setBackgroundResource(darkDrawable);
            EditPicker.editType = NONE;
            editSwitch.setBackgroundResource(R.drawable.e_switch_none);
            DrawView.editPoint.setAsHead(true);
        }
    }

//    public final static int NONE = 0;
//
//    public final static int EDIT_OBJECT = 1;
//    public final static int EDIT_POINT = 2;
//    public final static int JOIN = 3;
//    public final static int MERGE = 4;
//    public final static int ROTATE_OBJECT = 5;
//
//    public final static int DELETE = 6;
//    public final static int CHANGE_SYMBOL = 7;
//    public final static int REVERSE_LINE_OBJECT = 8;
//    public final static int CUT = 9;
//    public final static int CUT_HOLE = 10;
    public final static int WITH_SELF = 0;
    public final static int WITH_OTHER = 1;

    private static EditType editType = NONE;
    private static boolean show = false;
    private static Context context;

    private static Button editSwitch;
    private static Button[] editOperations = new Button[TYPE_COUNT];

    public EditPicker(Context context) {
        super(context);
        initView(context);
    }

    public EditPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.edit_picker, this);

        this.context = context;

//        editSwitch = (Button) findViewById(NONE.id);
//        editSwitch.setOnClickListener(EditType.NONE);

        for (EditType type : values())
            if(type.code() >= 0) {
                editOperations[type.code] = (Button) findViewById(type.id);
                editOperations[type.code].setOnClickListener(type);
            } else {
                editSwitch = (Button) findViewById(NONE.id);
                editSwitch.setOnClickListener(NONE);
            }
//        for (Button editOperation : editOperations)
//            editOperation.setOnClickListener(this);
    }

    public boolean isShow() {
        return show;
    }

    public void setHide() {
        show = false;
        editType.stopEdit();
    }

    public static void setShow() {
        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.END | Gravity.TOP;
        lp.setMargins(0, (MainActivity.editPicker.getHeight() / 8 * 6),
                -(MainActivity.editPicker.getWidth() * 4 / 5), 0);

        MainActivity.editPicker.setLayoutParams(lp);
    }

    public boolean dealBegun() {//has deal begun already
        return editType != NONE;
    }

    public EditType getEditType() {
        return editType;
    }
}
