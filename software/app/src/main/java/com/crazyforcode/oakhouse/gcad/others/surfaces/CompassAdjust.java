package com.crazyforcode.oakhouse.gcad.others.surfaces;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.crazyforcode.oakhouse.gcad.others.components.CompassDirection;
import com.crazyforcode.oakhouse.gcad.others.components.CompassView;
import com.crazyforcode.oakhouse.gcad.R;

public class CompassAdjust extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private AppCompatEditText showChange;
    private SeekBar direction;
    private static CompassDirection showDirection;

    private SharedPreferences preferences;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);

        }

        /** 获取用户手册 */
        preferences = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);

        int current = preferences.getInt("CompassAdjust", CompassView.getAdjustmentDegree());
        showChange = (AppCompatEditText) findViewById(R.id.show);

        if(current == 0)
            showChange.setText("原始方向");
        else if(current < 0)
            showChange.setText("已西调整" + String.valueOf(Math.abs(current)) + "度");
        else
            showChange.setText("已东调整" + String.valueOf(current) + "度");

        direction = (SeekBar) findViewById(R.id.direction);
        direction.setProgress(current + 179);
        direction.setOnSeekBarChangeListener(this);

        showDirection = (CompassDirection) findViewById(R.id.showDirection);
        showDirection.setAdjustDegree(current);

        findViewById(R.id.west).setOnClickListener(this);
        findViewById(R.id.east).setOnClickListener(this);
        findViewById(R.id.btnReset).setOnClickListener(this);
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("CompassAdjust", direction.getProgress() - 179);
                editor.apply();
                CompassView.setAdjustmentDegree(direction.getProgress() - 179);
                this.finish();
                overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("CompassAdjust", direction.getProgress() - 179);
            editor.apply();
            CompassView.setAdjustmentDegree(direction.getProgress() - 179);
            this.finish();
            overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int current = progress - 179;
        if(current == 0)
            showChange.setText("原始方向");
        else if(current < 0)
            showChange.setText("已西调整" + String.valueOf(Math.abs(current)) + "度");
        else
            showChange.setText("已东调整" + String.valueOf(current) + "度");

        showDirection.setAdjustDegree(current);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onClick(View v) {
        int current;
        switch (v.getId()) {
            case R.id.west:
                direction.setProgress(direction.getProgress() - 1);
                current = direction.getProgress() - 179;
                if(current == 0)
                    showChange.setText("原始方向");
                else if(current < 0)
                    showChange.setText("已西调整" + String.valueOf(Math.abs(current)) + "度");
                else
                    showChange.setText("已东调整" + String.valueOf(current) + "度");
                showDirection.setAdjustDegree(current);
                break;
            case R.id.east:
                direction.setProgress(direction.getProgress() + 1);
                current = direction.getProgress() - 179;
                if(current == 0)
                    showChange.setText("原始方向");
                else if(current < 0)
                    showChange.setText("已西调整" + String.valueOf(Math.abs(current)) + "度");
                else
                    showChange.setText("已东调整" + String.valueOf(current) + "度");
                showDirection.setAdjustDegree(current);
                break;
            case R.id.btnReset:
                current = 0;
                showChange.setText("原始方向");
                direction.setProgress(current + 179);
                showDirection.setAdjustDegree(current);
                break;
        }
    }
}
