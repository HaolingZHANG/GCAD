package com.crazyforcode.oakhouse.gcad.others.surfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.others.components.DrawTypePickerLayout;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;
import com.crazyforcode.oakhouse.gcad.others.components.LegendTileFragment;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dolia on 12/8/15.
 */

public class LegendPicker extends AppCompatActivity
        implements LegendTileFragment.OnSignChangeListener, View.OnClickListener {
    private int sign = -1;
    //private static int width;//for dynamic adapt activity&single icon

    //public static DrawTypeRadio typeChooser;
    private DrawTypePickerLayout typeChooser;
    private int objectType = DrawObjects.NONE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (savedInstanceState != null)
            objectType = savedInstanceState
                    .getInt("ObjectType");
        else
            Log.i("此时Bundle为空", "isEditing? "+KernelLayout.isEditing);*/
        setContentView(R.layout.activity_legend_picker);
        if (getIntent().getExtras() != null)
            objectType = getIntent().getExtras().getInt("objectType");
        else
            Log.i("no bundleEEEE", "during edit? :" + KernelLayout.isEditing);


        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        //   Setting ViewPager for each Tabs
        //   SettingActivity ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.legend_view_pager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.legend_picker_tab);
        tabs.setupWithViewPager(viewPager);

        findViewById(R.id.legend_cancel).setOnClickListener(this);
        findViewById(R.id.legend_commit).setOnClickListener(this);
        //typeChooser = (DrawTypeRadio) findViewById(R.id.typeChooser);
        //typeChooser.setVisibility(View.GONE);

        typeChooser = (DrawTypePickerLayout) findViewById(R.id.drawTypePicker);

//        findViewById(R.id.legend_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Intent i = new Intent(LegendPicker.this, MainActivity.class);
//                //startActivity(i);
//                setResult(-1);
//                finish();//finish this activity
//            }
//        });
//
//        findViewById(R.id.legend_commit).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                if (sign > 0) {
//                    Intent i = new Intent(LegendPicker.this, MainActivity.class);
//                    i.putExtra("sign", sign);
//                    i.putExtra("is_curl", ((CheckBox) findViewById(R.id.is_curl_but)).isChecked());
//                    //startActivity(i);
//                    setResult(0, i);
//                    finish();
//                } else {
//                    TextToast.showTextToast("未选择画笔", getBaseContext());
//                }
//            }
//        });
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        LegendTileFragment fragment = null;
        Bundle bundle = null;

        for (int i = 1; i <= 6; i++) {
            fragment = new LegendTileFragment();

            bundle = new Bundle();
            bundle.putInt("weight", i);
            bundle.putInt("objectType", objectType);
            //bundle.putSerializable("xx", Object);
            fragment.setArguments(bundle);
            //getSupportFragmentManager().beginTransaction().replace(R.id.persion_detail_container, fragment).commit();
            adapter.addFragment(fragment, ""+i);
        }
//        adapter.addFragment(new LegendTileFragment(), "二");
//        adapter.addFragment(new LegendTileFragment(), "三");
//        adapter.addFragment(new LegendTileFragment(), "四");
//        adapter.addFragment(new LegendTileFragment(), "五");
//        adapter.addFragment(new LegendTileFragment(), "六");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSignChanged(int sign) {
        this.sign = sign;

        typeChooser.upPickingSign(sign);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.legend_cancel:
                setResult(-1);
                finish();
                break;
            case R.id.legend_commit:
                if (sign > 0) {
                    Intent i = new Intent(LegendPicker.this, MainActivity.class);
                    i.putExtra("sign", sign);
                    //i.putExtra("is_curl", ((CheckBox) findViewById(R.id.is_curl_but)).isChecked());
                    i.putExtra("type", typeChooser.getDrawType());
                    //i.putExtra("type", typeChooser.getType());
                    //startActivity(i);
                    setResult(objectType == DrawObjects.NONE ? 0 : 1, i);
                    finish();
                } else {
                    TextToast.showTextToast("未选择画笔", getBaseContext());
                }
                break;
        }
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }

    /*private class DrawTypePicker extends LinearLayout {
        int drawType = 0;
        Animation animation, animationR;
        private final boolean IS_REVERSE = true;

        private DrawTypePicker(Context context, AttributeSet attrs) {
            super(context, attrs);

            animation = new ScaleAnimation(1, 0.8f, 1, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animationR = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(100);
            animationR.setDuration(100);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animeStart(IS_REVERSE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            final ImageView drawTypeIma = new ImageView(context, attrs);
            drawTypeIma.setImageResource(R.drawable.d0_light);
            addView(drawTypeIma);
            final TextView drawTypeName = new TextView(context, attrs);
            drawTypeName.setGravity(Gravity.CENTER_HORIZONTAL);
            drawTypeName.setText(R.string.draw_type_0);
            addView(drawTypeName);

            drawTypeName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!DrawObjects.isPoint(sign)) {
                        animeStart(false);

                        if (drawType == 0) {
                            drawTypeIma.setImageResource(R.drawable.d1_light);
                            drawTypeName.setText(R.string.draw_type_1);
                            drawType = 1;
                        }else if (drawType == 1 && MainActivity.isUseExternal()) {
                            drawTypeIma.setImageResource(R.drawable.d2_light);
                            drawTypeName.setText(R.string.draw_type_2);
                            drawType = 2;
                        }else {
                            drawTypeIma.setImageResource(R.drawable.d0_light);
                            drawTypeName.setText(R.string.draw_type_0);
                            drawType = 0;
                        }

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

        int getDrawType() {
            return drawType;
        }
    }*/
}
