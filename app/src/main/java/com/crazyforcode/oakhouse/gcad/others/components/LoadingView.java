package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.crazyforcode.oakhouse.gcad.R;

public class LoadingView extends ImageView {

    private Animation animation;

    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        animation = AnimationUtils.loadAnimation(context, R.anim.rotate_loading);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        animation.setInterpolator(linearInterpolator);
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        animation.setAnimationListener(listener);
    }

    public void start() {
        if(animation != null) {
            this.setVisibility(VISIBLE);
            this.startAnimation(animation);
        }
    }

    public void stop() {
        this.setVisibility(GONE);
        this.clearAnimation();
    }
}
