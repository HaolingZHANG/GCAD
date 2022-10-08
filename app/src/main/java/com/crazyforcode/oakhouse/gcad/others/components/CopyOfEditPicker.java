/*
package com.crazyforcode.oakhouse.gcad.Others.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.crazyforcode.oakhouse.gcad.Kernel.Tools.Toast.TextToast;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.Window.MainActivity;

public class CopyOfEditPicker extends LinearLayout implements View.OnClickListener {

    public final static int NONE = 0;

    public final static int EDIT_OBJECT = 1;
    public final static int EDIT_POINT = 2;
    public final static int JOIN = 3;
    public final static int MERGE = 4;
    public final static int ROTATE_OBJECT = 5;

    public final static int DELETE = 6;
    public final static int CHANGE_SYMBOL = 7;
    public final static int REVERSE_LINE_OBJECT = 8;
    public final static int CUT = 9;
    public final static int CUT_HOLE = 10;

    private static int editType = NONE;
    private static boolean show = false;
    private Context context;

    private static Button editSwitch;
    private static Button[] editOperations = new Button[10];

    public CopyOfEditPicker(Context context) {
        super(context);
        initView(context);
    }

    public CopyOfEditPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.edit_picker, this);

        this.context = context;

        editSwitch = (Button) findViewById(R.id.edit_switch);
        editSwitch.setOnClickListener(this);

        editOperations[0] = (Button) findViewById(R.id.edit0);
        editOperations[1] = (Button) findViewById(R.id.edit1);
        editOperations[2] = (Button) findViewById(R.id.edit2);
        editOperations[3] = (Button) findViewById(R.id.edit3);
        editOperations[4] = (Button) findViewById(R.id.edit4);

        editOperations[5] = (Button) findViewById(R.id.edit5);
        editOperations[6] = (Button) findViewById(R.id.edit6);
        editOperations[7] = (Button) findViewById(R.id.edit7);
        editOperations[8] = (Button) findViewById(R.id.edit8);
        editOperations[9] = (Button) findViewById(R.id.edit9);

        for (Button editOperation : editOperations)
            editOperation.setOnClickListener(this);
    }

    public static boolean isShow() {
        return show;
    }

    public static void setHide() {
        show = false;
        if(editType != NONE) {
            darkEdit(editType);
            editSwitch.setBackgroundResource(R.drawable.e_switch_none);
            editType = NONE;
        }
    }

    public static void setShow() {
        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.END | Gravity.TOP;
        lp.setMargins(0, (MainActivity.editPicker.getHeight() / 8 * 6), -(MainActivity.editPicker.getWidth() * 4 / 5), 0);

        MainActivity.editPicker.setLayoutParams(lp);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.edit_switch:
                if(KernelLayout.isEditing) {
                    if(show)
                        MainActivity.hideEditPicker();
                    else
                        MainActivity.showEditPicker();

                    show = !show;
                }
                break;
            case R.id.edit0:
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == EDIT_OBJECT) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[0].setBackgroundResource(R.drawable.e0_light);
                editType = EDIT_OBJECT;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("对象整体操作", context);
                break;
            case R.id.edit1:
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == EDIT_POINT) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[1].setBackgroundResource(R.drawable.e1_light);
                editType = EDIT_POINT;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("对象点位操作", context);
                break;
            case R.id.edit2://瞬时
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == JOIN) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[2].setBackgroundResource(R.drawable.e2_light);
                editType = JOIN;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("连接对象首尾", context);
                break;
            case R.id.edit3://瞬时
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == MERGE) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[3].setBackgroundResource(R.drawable.e3_light);
                editType = MERGE;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("合并相同对象", context);
                break;
            case R.id.edit4:
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == ROTATE_OBJECT) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[4].setBackgroundResource(R.drawable.e4_light);
                editType = ROTATE_OBJECT;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("旋转当前对象", context);
                break;
            case R.id.edit5://瞬时
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == DELETE) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[5].setBackgroundResource(R.drawable.e5_light);
                editType = DELETE;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("删除当前对象", context);
                break;
            case R.id.edit6:
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == CHANGE_SYMBOL) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[6].setBackgroundResource(R.drawable.e6_light);
                editType = CHANGE_SYMBOL;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("替换当前符号", context);
                break;
            case R.id.edit7://瞬时
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == REVERSE_LINE_OBJECT) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[7].setBackgroundResource(R.drawable.e7_light);
                editType = REVERSE_LINE_OBJECT;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("翻转当前对象", context);
                break;
            case R.id.edit8:
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == CUT) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[8].setBackgroundResource(R.drawable.e8_light);
                editType = CUT;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("线性对象截断", context);
                break;
            case R.id.edit9:
                if(editType != NONE)
                    darkEdit(editType);
                if(editType == CUT_HOLE) {
                    editType = NONE;
                    KernelLayout.stopEdit();
                    editSwitch.setBackgroundResource(R.drawable.e_switch_none);
                    break;
                }
                editOperations[9].setBackgroundResource(R.drawable.e9_light);
                editType = CUT_HOLE;
                KernelLayout.startEdit(editType);
                editSwitch.setBackgroundResource(R.drawable.e_switch_selected);
                if(MainActivity.getNovice())
                    TextToast.showTextToast("面状对象开洞", context);
        }
    }

    private static void darkEdit(int id) {
        switch(id) {
            case EDIT_OBJECT:
                editOperations[0].setBackgroundResource(R.drawable.e0_dark);
                break;
            case EDIT_POINT:
                editOperations[1].setBackgroundResource(R.drawable.e1_dark);
                break;
            case JOIN:
                editOperations[2].setBackgroundResource(R.drawable.e2_dark);
                break;
            case MERGE:
                editOperations[3].setBackgroundResource(R.drawable.e3_dark);
                break;
            case ROTATE_OBJECT:
                editOperations[4].setBackgroundResource(R.drawable.e4_dark);
                break;
            case DELETE:
                editOperations[5].setBackgroundResource(R.drawable.e5_dark);
                break;
            case CHANGE_SYMBOL:
                editOperations[6].setBackgroundResource(R.drawable.e6_dark);
                break;
            case REVERSE_LINE_OBJECT:
                editOperations[7].setBackgroundResource(R.drawable.e7_dark);
                break;
            case CUT:
                editOperations[8].setBackgroundResource(R.drawable.e8_dark);
                break;
            case CUT_HOLE:
                editOperations[9].setBackgroundResource(R.drawable.e9_dark);
        }
    }

    public boolean dealBegun() {//has deal begun already
        return editType == NONE;
    }
}
*/
