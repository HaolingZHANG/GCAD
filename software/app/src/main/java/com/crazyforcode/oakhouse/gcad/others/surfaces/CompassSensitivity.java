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

import com.crazyforcode.oakhouse.gcad.others.components.CompassView;
import com.crazyforcode.oakhouse.gcad.R;

public class CompassSensitivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private AppCompatEditText show;
    private SeekBar sensitivity;
    private CompassView compass;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);

        }
        /** 获取用户手册 */
        preferences = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);

        compass = (CompassView) findViewById(R.id.compass);
        int current = preferences.getInt("CompassSensitivity", compass.getSensitivityForCompass());

        show = (AppCompatEditText) findViewById(R.id.show);
        show.setText(String.valueOf(current));

        sensitivity = (SeekBar) findViewById(R.id.sensitivity);
        sensitivity.setOnSeekBarChangeListener(this);
        sensitivity.setProgress(current - 1);

        findViewById(R.id.btnReset).setOnClickListener(this);
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("CompassSensitivity", compass.getSensitivityForCompass());
                editor.apply();
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
            editor.putInt("CompassSensitivity", compass.getSensitivityForCompass());
            editor.apply();
            this.finish();
            overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        show.setText(String.valueOf(progress + 1));
        compass.setSensitivityForCompass(progress + 1);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        show.setText(String.valueOf(seekBar.getProgress() + 1));
        compass.setSensitivityForCompass(seekBar.getProgress() + 1);
    }

    @Override
    public void onClick(View v) {
        show.setText("5");
        compass.setSensitivityForCompass(5);
        sensitivity.setProgress(4);
    }
}
