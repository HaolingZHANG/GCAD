package com.crazyforcode.oakhouse.gcad.others.surfaces;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crazyforcode.oakhouse.gcad.bluetooth.SetBlueTooth;
import com.crazyforcode.oakhouse.gcad.kernel.tools.GCADApplication;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.others.components.ScaleView;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

import java.text.DecimalFormat;

public class MapScaleActivity extends AppCompatActivity implements View.OnClickListener {

    private static TextView distance;
    private static TextView scale;

    private ScaleView view;

    private static double realMeters = 0;
    private static double virtualMeters = 0;

    private boolean inputSuccess = false;

    private static final int SEARCH_BLUETOOTH = 1;

    private String projectPath;
    private static String className;

    //TODO 结合外设书写
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_scale);

        GCADApplication.addActivity(this, getCurrentClassName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        projectPath = getIntent().getStringExtra("ProjectPath");

        distance = (TextView) findViewById(R.id.distance);
        distance.setText("0.0M");
        scale = (TextView) findViewById(R.id.scale);
        scale.setText("1:0");

        findViewById(R.id.btnReset).setOnClickListener(this);
        findViewById(R.id.bluetooth).setOnClickListener(this);
        findViewById(R.id.btnEnter).setOnClickListener(this);
        view = (ScaleView) findViewById(R.id.view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetooth:
                Intent intent = new Intent("android.settings.BLUETOOTH_SETTINGS");
                startActivityForResult(intent, SEARCH_BLUETOOTH);
                break;
            case R.id.btnReset:
                view.resetPosition();
                break;
            case R.id.btnEnter:
                if(inputSuccess) {
                    Intent activityMain = new Intent(MapScaleActivity.this, MainActivity.class);
                    activityMain.putExtra("ProjectPath", projectPath);
                    activityMain.putExtra("process", true);
                    startActivity(activityMain);
                    overridePendingTransition(R.anim.activity_in_1, R.anim.activity_out_1);
                }
                break;
        }
    }

    public static void setRealMeters(double real) {
        realMeters = real;
        DecimalFormat df = new DecimalFormat("#.00");
        String number = df.format(realMeters) + "M";
        if(number.charAt(0) == '.')
            number = '0' + number;
        distance.setText(number);
    }

    public static void setScale(double virtual) {
        virtualMeters = virtual;
        DecimalFormat df = new DecimalFormat("#.00");
        String number = df.format(realMeters / virtualMeters) + "M";
        number = "1:" + number;
        distance.setText(number);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_BLUETOOTH) {
            SetBlueTooth.initBlueTooth(getApplicationContext());
            findViewById(R.id.bluetooth).setVisibility(View.GONE);
            findViewById(R.id.show).setVisibility(View.VISIBLE);

            //TODO 测试代码
            findViewById(R.id.btnEnter).setBackgroundColor(Color.parseColor("#4FC3F7"));
            inputSuccess = true;
            SaveTotalMap.setMapScale(1000);

//            double realMeters = getRealMeters();
//            SaveTotalMap.setMapScale((int)(realMeters / view.getDistanceM()));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getCurrentClassName() {
        if(className == null) {
            int level = 1;
            StackTraceElement[] stacks = new Throwable().getStackTrace();

            className = stacks[level].getClassName();
        }
        return className;
    }
}