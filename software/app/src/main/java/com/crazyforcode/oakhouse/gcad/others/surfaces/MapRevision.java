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
import android.widget.Button;

import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.R;

public class MapRevision extends AppCompatActivity implements View.OnClickListener {

    private Button btnCanon;
    private Button btnHP;
    private Button btnSamsung;
    private Button btnBother;
    private Button btnEpson;
    private Button btnXerox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_revision);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        /** 检查打印服务 */
        findViewById(R.id.check).setOnClickListener(this);

        /** 打印服务按钮 */
        btnCanon = (Button) findViewById(R.id.btnCanon);
        btnHP = (Button) findViewById(R.id.btnHP);
        btnSamsung = (Button) findViewById(R.id.btnSamsung);
        btnBother = (Button) findViewById(R.id.btnBother);
        btnEpson = (Button) findViewById(R.id.btnEpson);
        btnXerox = (Button) findViewById(R.id.btnXerox);
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
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        Intent LaunchIntent;
        switch (v.getId()) {
            /** 检查打印服务 */
            case R.id.check:
                if (getPackageManager().getLaunchIntentForPackage("jp.co.canon.oip.android.opal") != null) {
                    btnCanon.setOnClickListener(this);
                    btnCanon.setTextColor(Color.parseColor("#4FC3F7"));
                }
                if (getPackageManager().getLaunchIntentForPackage("com.hp.android.print") != null) {
                    btnHP.setOnClickListener(this);
                    btnHP.setTextColor(Color.parseColor("#4FC3F7"));
                }
                if (getPackageManager().getLaunchIntentForPackage("com.sec.print.mobileprint") != null) {
                    btnSamsung.setOnClickListener(this);
                    btnSamsung.setTextColor(Color.parseColor("#4FC3F7"));
                }
                if (getPackageManager().getLaunchIntentForPackage("com.brother.mfc.brprint") != null) {
                    btnBother.setOnClickListener(this);
                    btnBother.setTextColor(Color.parseColor("#4FC3F7"));
                }
                if (getPackageManager().getLaunchIntentForPackage("epson.print") != null) {
                    btnEpson.setOnClickListener(this);
                    btnEpson.setTextColor(Color.parseColor("#4FC3F7"));
                }
                if (getPackageManager().getLaunchIntentForPackage("jp.co.fujixerox.prt.PrintUtil.PCL") != null) {
                    btnXerox.setOnClickListener(this);
                    btnXerox.setTextColor(Color.parseColor("#4FC3F7"));
                }
                break;

            case R.id.btnCanon:
                if(FinalMapActivity.madeSuccess()) {
                    LaunchIntent = getPackageManager().getLaunchIntentForPackage("jp.co.canon.oip.android.opal");
                    startActivity(LaunchIntent);
                } else
                    TextToast.showTextToast("未导出成图不得打印", getApplicationContext());
                break;

            case R.id.btnHP:
                if(FinalMapActivity.madeSuccess()) {
                    LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.hp.android.print");
                    startActivity(LaunchIntent);
                } else
                    TextToast.showTextToast("未导出成图不得打印", getApplicationContext());
                break;

            case R.id.btnSamsung:
                if(FinalMapActivity.madeSuccess()) {
                    LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.sec.print.mobileprint");
                    startActivity(LaunchIntent);
                } else
                    TextToast.showTextToast("未导出成图不得打印", getApplicationContext());
                break;

            case R.id.btnBother:
                if(FinalMapActivity.madeSuccess()) {
                    LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.brother.mfc.brprint");
                    startActivity(LaunchIntent);
                } else
                    TextToast.showTextToast("未导出成图不得打印", getApplicationContext());
                break;

            case R.id.btnEpson:
                if(FinalMapActivity.madeSuccess()) {
                    LaunchIntent = getPackageManager().getLaunchIntentForPackage("epson.print");
                    startActivity(LaunchIntent);
                } else
                    TextToast.showTextToast("未导出成图不得打印", getApplicationContext());
                break;

            case R.id.btnXerox:

                if(FinalMapActivity.madeSuccess()) {
                    LaunchIntent = getPackageManager().getLaunchIntentForPackage("jp.co.fujixerox.prt.PrintUtil.PCL");
                    startActivity(LaunchIntent);
                } else
                    TextToast.showTextToast("未导出成图不得打印", getApplicationContext());
        }
    }

//    public void searchAppIn(String appPath) {
//        PackageManager pm = this.getPackageManager();
//        PackageInfo info = pm.getPackageArchiveInfo(appPath, PackageManager.GET_ACTIVITIES);
//        ApplicationInfo appInfo;
//        if (info != null) {
//            appInfo = info.applicationInfo;
//            String packageName = appInfo.packageName;
//            System.out.println("进入app的包名 ：" + packageName);
//        }
//    }
}
