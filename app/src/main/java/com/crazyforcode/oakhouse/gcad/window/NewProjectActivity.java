package com.crazyforcode.oakhouse.gcad.window;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.crazyforcode.oakhouse.gcad.kernel.tools.GCADApplication;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.DeleteProject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.R;

import java.io.File;

public class NewProjectActivity  extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private AppCompatEditText nameText;

    private SharedPreferences preferences;
    private static String projectName;
    private int mapScale = 0;

    private static String projectPath = null;

    private boolean nameInputSuccess = false;
    private boolean numberInputSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        GCADApplication.addActivity(this, getCurrentClassName());

        /** 界面监听 */
        findViewById(R.id.layout).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** 设置 ActionBar */
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        /** 新建工程名称 */
        nameText = (AppCompatEditText) findViewById(R.id.project_name_input);
        nameText.setOnClickListener(this);

        /** 底图比例设置 */
        Spinner scaleText = (Spinner) findViewById(R.id.map_scale);
        scaleText.setOnItemSelectedListener(this);

        /** 进入底图初始化按钮 */
        findViewById(R.id.btnMapInit).setOnClickListener(this);

        /** 获取用户手册 */
        preferences = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /** 隐藏软键盘 */
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                this.finish();
                overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
                resetProjectDir();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /** 隐藏软键盘 */
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            this.finish();
            overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
            resetProjectDir();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            /** 界面监听 */
            case R.id.layout:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if (nameText.isFocusable()) {
                    projectName = String.valueOf(nameText.getText());
                    if (projectName.length() > 0 && !nameInputSuccess)
                        setProjectDir();
                }
                break;
            /** 新建工程名称 */
            case R.id.project_name_input:
                if(nameInputSuccess)
                    resetProjectDir();
                break;
            /** 进入底图初始化按钮 */
            case R.id.btnMapInit:
                if(!nameInputSuccess) {
                    projectName = String.valueOf(nameText.getText());
                    setProjectDir();
                }

                if(nameInputSuccess) {
//                    在输入了比例尺的情况下保存比例尺
                    if(numberInputSuccess)
                        SaveTotalMap.setMapScale(mapScale);

//                    进入底图初始化界面
                    Intent activityMapInit = new Intent(NewProjectActivity.this, AlignActivity.class);
                    activityMapInit.putExtra("ProjectPath", projectPath);
                    startActivity(activityMapInit);
                    overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                } else
                    TextToast.showTextToast("未成功输入工程名", getApplicationContext());
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            /** 隐藏软键盘 */
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

            if (nameText.isFocusable()) {
                projectName = String.valueOf(nameText.getText());
                if (projectName.length() > 0 && !nameInputSuccess)
                    setProjectDir();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void setProjectDir() {
        File project = new File(preferences.getString("RootPath", "") + "/" + projectName);

        if (!project.exists() && !projectName.equals("_____")) {
            nameInputSuccess = project.mkdirs();
            projectPath = project.getPath();

            File projectMap = new File(project.getPath() + "/Map");
            File projectData = new File(project.getPath() + "/Data");

            nameInputSuccess = projectMap.mkdirs();
            nameInputSuccess = projectData.mkdirs();
        } else {
            TextToast.showTextToast("已存在此工程，\n请重新命名！", getApplicationContext());
            projectName = null;
            nameInputSuccess = false;
            nameText.setText("");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void resetProjectDir() {
        File project = new File(preferences.getString("RootPath", "") + "/" + projectName);

        if (project.exists()) {
            DeleteProject.recursionDeleteFile(projectName, getApplicationContext());
            projectPath = null;
        }

        nameText.setText("");
        projectName = null;
        nameInputSuccess = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            String[] scales = getResources().getStringArray(R.array.init_scales);
            mapScale = Integer.parseInt(scales[position]);
            numberInputSuccess = true;
        } catch (NumberFormatException e) {
            numberInputSuccess = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static String getCurrentClassName() {
        return "NewProjectActivity";
    }
}