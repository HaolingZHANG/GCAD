package com.crazyforcode.oakhouse.gcad.others.surfaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.others.components.EditView;
import com.crazyforcode.oakhouse.gcad.R;

public class FinalMapActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static EditView editView;

    private static boolean madeSuccess = false;

    private static Button btnTitle;
    private static Button btnNorth;
    private static Button btnScale;

    private double currentScale;

    private String outputPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("config",  Context.MODE_PRIVATE);
        outputPath = preferences.getString("export", null);

        currentScale = SaveTotalMap.getMapScale();

        editView = (EditView) findViewById(R.id.edit);

        btnTitle = (Button) findViewById(R.id.title);
        btnNorth = (Button) findViewById(R.id.north);
        btnScale = (Button) findViewById(R.id.scale);
        Button btnOut = (Button) findViewById(R.id.btnOutput);

        btnTitle.setOnClickListener(this);
        btnNorth.setOnClickListener(this);
        btnScale.setOnClickListener(this);
        btnOut.setOnClickListener(this);

        Spinner scaleChange = (Spinner) findViewById(R.id.scaleChange);

        String[] scales = getResources().getStringArray(R.array.final_scales);
        for(int i = 0; i < scales.length; i++) {
            if(Integer.parseInt(scales[i]) == currentScale) {
                scaleChange.setSelection(i);
                break;
            }
        }
        scaleChange.setOnItemSelectedListener(this);

        /** 判断是否有输出路径 否则导出无法点击 */
        if(outputPath == null)
            btnOut.setBackgroundColor(Color.parseColor("#ff757575"));
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

    public static void setSuccess() {
        madeSuccess = true;
    }

    public static boolean madeSuccess() {
        return madeSuccess;
    }

    public static boolean isVisible() {
        return btnTitle.getVisibility() == View.VISIBLE;
    }

    public static void showButton() {
        btnTitle.setVisibility(View.VISIBLE);
        btnNorth.setVisibility(View.VISIBLE);
        btnScale.setVisibility(View.VISIBLE);
    }

    public static void hideButton() {
        btnTitle.setVisibility(View.GONE);
        btnNorth.setVisibility(View.GONE);
        btnScale.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title:
                if(editView.needTitle()) {
                    editView.removeTitle();
                    v.setBackgroundResource(R.drawable.title_dark);
                } else {
                    editView.addTitle();
                    v.setBackgroundResource(R.drawable.title_light);
                }
                break;
            case R.id.north:
                if(editView.needNorth()) {
                    editView.removeNorth();
                    v.setBackgroundResource(R.drawable.north_dark);
                } else {
                    editView.addNorth();
                    v.setBackgroundResource(R.drawable.north_light);
                }
                break;
            case R.id.scale:
                if(editView.needScale()) {
                    editView.removeScale();
                    v.setBackgroundResource(R.drawable.scale_dark);
                } else {
                    editView.addScale();
                    v.setBackgroundResource(R.drawable.scale_light);
                }
                break;
            case R.id.btnOutput:
                if(outputPath == null)
                    TextToast.showTextToast("未设置导出路径\n请在设置界面进行设置\n否则无法导出", getApplicationContext());
                else
                    editView.putInDir(outputPath);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] scales = getResources().getStringArray(R.array.final_scales);
        double change = currentScale / Double.parseDouble(scales[position]);
        currentScale = Integer.parseInt(scales[position]);
        editView.setChangeScale(change);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
