package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

public class DrawTypeRadio extends LinearLayout implements View.OnClickListener {

    private int type = NONE;
    private int sign = -1;

    public static final int NONE = 0;
    public static final int POINT = 1;
    public static final int STRAIGHT = 1;
    public static final int CURVE = 2;
    public static final int FREE = 3;

    public DrawTypeRadio(Context context) {
        super(context);
        initView(context);
    }

    public DrawTypeRadio(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.drawtype_radio, this);

        findViewById(R.id.straight).setOnClickListener(this);
        findViewById(R.id.curve).setOnClickListener(this);
        findViewById(R.id.free).setOnClickListener(this);

        if(MainActivity.useExternal)
            showFree();
        else
            hideFree();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.straight:
                clearLast();
                v.setBackgroundResource(R.drawable.d0_light);
                type = STRAIGHT;
                if(MainActivity.getNovice())
                    TextToast.showTextToast("直线绘制", getContext());
                break;
            case R.id.curve:
                clearLast();
                v.setBackgroundResource(R.drawable.d1_light);
                type = CURVE;
                if(MainActivity.getNovice())
                    TextToast.showTextToast("曲线绘制", getContext());
                break;
            case R.id.free:
                if (MainActivity.isUseExternal()) {
                    clearLast();
                    v.setBackgroundResource(R.drawable.d2_light);
                    type = FREE;
                    if(MainActivity.getNovice())
                        TextToast.showTextToast("自由线绘制", getContext());
                } else
                    TextToast.showTextToast("未开启外设不可使用", getContext());
        }
    }

    public void showFree() {
        findViewById(R.id.free).setVisibility(VISIBLE);
        findViewById(R.id.space).setVisibility(VISIBLE);
    }

    public void hideFree() {
        findViewById(R.id.free).setVisibility(GONE);
        findViewById(R.id.space).setVisibility(GONE);
    }

    public void clearLast() {
        if(type != NONE)
            switch (type) {
                case STRAIGHT:
                    findViewById(R.id.straight).setBackgroundResource(R.drawable.d0_dark);
                    break;
                case CURVE:
                    findViewById(R.id.curve).setBackgroundResource(R.drawable.d1_dark);
                    break;
                case FREE:
                    findViewById(R.id.free).setBackgroundResource(R.drawable.d2_dark);

            }
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public int getType() {
        if(type != NONE)
            return type;

        if(sign == -1)
            return NONE;

        if(DrawObjects.isPoint(sign))
            return POINT;

        return STRAIGHT;
    }

    public void clear() {
        type = NONE;
        sign = -1;
    }
}
