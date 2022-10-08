package com.crazyforcode.oakhouse.gcad.others.components;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

/**
 * Created by Master_Jedi on 2016/02/29.
 */
public class DrawTypePickerLayout extends LinearLayout {
    int drawType = -1;
    private int pickingSign = -1;
    Animation animation, animationR;
    private final boolean IS_REVERSE = true;
    private final long ANIM_DURING = 200l;

    /**dont create this individually! only use for a part of lengend picker*/
    @SuppressWarnings("ResourceType")
    public DrawTypePickerLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        drawType = DrawObjects.DRAW_TYPE_STRAIGHT;

        animation = new ScaleAnimation(1, 0.2f, 1, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationR = new ScaleAnimation(0.2f, 1, 0.2f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(40);
        animationR.setDuration(40);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                animeStart(IS_REVERSE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        setGravity(Gravity.LEFT);

        final ImageView drawTypeIma = new ImageView(context, attrs);
        drawTypeIma.setImageResource(R.drawable.d0_light_g);
        drawTypeIma.setAdjustViewBounds(true);
        addView(drawTypeIma);
        final TextView drawTypeName = new TextView(context, attrs);
        LinearLayout.LayoutParams params =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,28, 0, 0);
        drawTypeName.setLayoutParams(params);
        drawTypeName.setGravity(Gravity.CENTER_VERTICAL);
        drawTypeName.setText(R.string.draw_type_0);
        drawTypeName.setTextSize(16);
        drawTypeName.setPadding(6, 12, 0, 0);//TODO 粉色间距
        drawTypeName.setTextDirection(LAYOUT_DIRECTION_LOCALE);
        addView(drawTypeName);

        drawTypeName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DrawObjects.isPoint(pickingSign)) {
                    animeStart(!IS_REVERSE);

                    if (drawType == DrawObjects.DRAW_TYPE_STRAIGHT) {
                        drawTypeIma.setImageResource(R.drawable.d1_light_g);
                        drawTypeName.setText(R.string.draw_type_1);
                        setDrawType(DrawObjects.DRAW_TYPE_CURL);
                    }else if (drawType == DrawObjects.DRAW_TYPE_CURL && MainActivity.isUseExternal()) {
                        drawTypeIma.setImageResource(R.drawable.d2_light_g);
                        drawTypeName.setText(R.string.draw_type_2);
                        setDrawType(DrawObjects.DRAW_TYPE_FREELINE);
                    }else {
                        drawTypeIma.setImageResource(R.drawable.d0_light_g);
                        drawTypeName.setText(R.string.draw_type_0);
                        setDrawType(DrawObjects.DRAW_TYPE_STRAIGHT);
                    }

                }
            }
        });

        drawTypeIma.setOnClickListener(new OnClickListener() {
            ImageView typeIma2, typeIma3;
            Animation t2, t3, t2R, t3R;
            AnimationSet pos, neg;
            final static private boolean OPEN = false, BACK = true;
            boolean state = OPEN;

            @Override
            public void onClick(View view) {
                if (state == OPEN) {
                    if (typeIma2 == null) {
                        init();
                    }else {
                        drawTypeIma.setImageResource(drawType == DrawObjects.DRAW_TYPE_STRAIGHT ?
                        R.drawable.d0_light_g : R.drawable.d0_dark);
                    }
                    bindAnimeToIma(OPEN);
                } else {
                    if (view.equals(drawTypeIma)) {
                        setDrawType(DrawObjects.DRAW_TYPE_STRAIGHT);
                    }else if (view.equals(typeIma2)) {
                        setDrawType(DrawObjects.DRAW_TYPE_CURL);
                    }else {
                        if (MainActivity.isUseExternal()) {
                            setDrawType(DrawObjects.DRAW_TYPE_FREELINE);
                        }else {
                            TextToast.showTextToast("未开启外设", context);
                            return;
                        }
                    }

                    bindAnimeToIma(BACK);
                }

            }

            private void init() {
                //init & add imageView
                LinearLayout.LayoutParams paramsOther =
                        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsOther.setMargins(getParentWidth() / 3 - getParentHeight(), 0, 0, 0);
                typeIma2 = new ImageView(context, attrs);
                typeIma2.setLayoutParams(paramsOther);
                typeIma2.setAdjustViewBounds(true);
                typeIma2.setOnClickListener(this);
                addView(typeIma2);
                typeIma3 = new ImageView(context, attrs);
                typeIma3.setLayoutParams(paramsOther);
                typeIma3.setAdjustViewBounds(true);
                typeIma3.setOnClickListener(this);
                addView(typeIma3);
                loadImage();

                //init anime
                //AnimationSet animeSet = new AnimationSet(true);
                pos = new AnimationSet(true);
                neg = new AnimationSet(true);
                t2 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -getParentHeight(),
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
                t3 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -getParentHeight() * 2,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
                t2R = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, -getParentHeight(),
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
                t3R = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, -getParentHeight() * 2,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
//                t2.setDuration(200);
//                t2R.setDuration(200);
//                t3.setDuration(200);
//                t3R.setDuration(200);
                //Animator an = AnimatorInflater.loadAnimator(R.animator.)
                //animeSet.addAnimation(t2);
                //animeSet.addAnimation(t2R);
                //animeSet.addAnimation(t3);
                //animeSet.addAnimation(t3R);
                pos.addAnimation(t2);
                pos.addAnimation(t3);
                neg.addAnimation(t2R);
                neg.addAnimation(t3R);
                //animeSet.setDuration(ANIM_DURING);
                pos.setDuration(ANIM_DURING);
                neg.setDuration(ANIM_DURING);
                t3.setStartOffset(ANIM_DURING);
                t3R.setStartOffset(ANIM_DURING);
                pos.setInterpolator(new AccelerateInterpolator(1.5f));
                neg.setInterpolator(new AccelerateInterpolator(1.5f));
                pos.setFillAfter(true);
                neg.setFillAfter(true);
                //animeSet.setInterpolator(new AccelerateInterpolator(1.5f));
                //animeSet.setFillAfter(true);

                t2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        drawTypeName.setVisibility(GONE);

                        typeIma2.setVisibility(VISIBLE);
                        typeIma3.setVisibility(VISIBLE);
                        //t3.startNow();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //t3.startNow();
                        state = BACK;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                t2R.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //t3R.startNow();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        typeIma2.setVisibility(GONE);
                        typeIma3.setVisibility(GONE);
                        oneToChamber();
                        drawTypeName.setVisibility(VISIBLE);
                        state = OPEN;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                t3R.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    //re selection
                        loadImage();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //t2R.startNow();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }

            private void loadImage() {
                drawTypeIma.setImageResource(drawType == DrawObjects.DRAW_TYPE_STRAIGHT?
                        R.drawable.d0_light_g : R.drawable.d0_dark);
                typeIma2.setImageResource(drawType == DrawObjects.DRAW_TYPE_CURL?
                        R.drawable.d1_light_g : R.drawable.d1_dark);
                typeIma3.setImageResource(drawType == DrawObjects.DRAW_TYPE_FREELINE?
                        R.drawable.d2_light_g  : R.drawable.d2_dark);
            }
            private void oneToChamber() {
                switch (drawType) {
                    case DrawObjects.DRAW_TYPE_STRAIGHT:
                        drawTypeIma.setImageResource(R.drawable.d0_light_g);
                        drawTypeName.setText(R.string.draw_type_0);
                        break;
                    case DrawObjects.DRAW_TYPE_CURL:
                        drawTypeIma.setImageResource(R.drawable.d1_light_g);
                        drawTypeName.setText(R.string.draw_type_1);
                        break;
                    case DrawObjects.DRAW_TYPE_FREELINE:
                        drawTypeIma.setImageResource(R.drawable.d2_light_g);
                        drawTypeName.setText(R.string.draw_type_2);
                        break;
                    default:;
                }
            }

            private void bindAnimeToIma(boolean isReverse) {
                typeIma2.clearAnimation();
                typeIma3.clearAnimation();

                if (isReverse) {
                    typeIma2.setAnimation(t2R);
                    typeIma3.setAnimation(t3R);
                    neg.startNow();
                }else {
                    typeIma3.setAnimation(t3);
                    typeIma2.setAnimation(t2);
                    //animeSet.start();
                    pos.startNow();
                    Log.i("执行动画", ""+typeIma2.getAdjustViewBounds());
                }

            }
        });
    }

    private void animeStart(boolean isReverse) {
        this.setAnimation(null);
        if (isReverse)
            this.startAnimation(animationR);
        else
            this.startAnimation(animation);
    }



    public void upPickingSign(int sign) {
        pickingSign = sign;
    }

    private void setDrawType(int drawType) {
        this.drawType = drawType;
    }

    public int getDrawType() {
        return drawType;
    }
    private int getParentWidth() {
        return getWidth();
    }
    private int getParentHeight() {
        return getHeight();
    }
}
