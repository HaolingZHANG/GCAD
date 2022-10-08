package com.crazyforcode.oakhouse.gcad.window;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.crazyforcode.oakhouse.gcad.kernel.tools.GCADApplication;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.MapInfo;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.UriToFile;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MThreadPool;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.CopyFileRunnable;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.ObtainTotalMapRunnable;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.others.components.AlignView;
import com.crazyforcode.oakhouse.gcad.others.components.LoadingView;
import com.crazyforcode.oakhouse.gcad.others.surfaces.MapScaleActivity;
import com.crazyforcode.oakhouse.gcad.R;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

import static android.app.AlertDialog.*;

public class AlignActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int FILE_SELECT_CODE = 0;
    private static Button show;
    private static RelativeLayout addArea;
    private static Button enter;
    private static Button right;
    private static Button left;
    private static Button reset;
    public static AlignView view;
    private static LoadingView loading;
    private static DpiInputBox inputBox;

    private static AlignActivity activity;
    private static String projectPath;
    private static Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_align);

        GCADApplication.addActivity(this, getCurrentClassName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        activity = this;

        projectPath = getIntent().getStringExtra("ProjectPath");

        loading = (LoadingView) findViewById(R.id.waiting);

        Button addButton = (Button) findViewById(R.id.addView_button);
        addButton.setOnClickListener(this);

        addArea = (RelativeLayout) findViewById(R.id.addView_area);
        addArea.setOnClickListener(this);

        show = (Button) findViewById(R.id.btnShow);

        view = (AlignView) findViewById(R.id.view);

        enter = (Button) findViewById(R.id.btnEnter);
        enter.setOnClickListener(this);

        right = (Button) findViewById(R.id.btnLeft);

        left = (Button) findViewById(R.id.btnRight);

        reset = (Button) findViewById(R.id.btnReset);
        reset.setOnClickListener(this);

        inputBox = new DpiInputBox(this);
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
        switch(v.getId()) {
            case R.id.btnLeft:
                view.littleLeft();
                break;
            case R.id.btnRight:
                view.littleRight();
                break;
            case R.id.btnReset:
                view.reset();
                break;
            case R.id.addView_button:
            case R.id.addView_text:
            case R.id.addView_area:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                } catch (ActivityNotFoundException e) {
                    TextToast.showTextToast("文件夹打开失败", getApplicationContext());
                }
                break;
            case R.id.btnEnter:
                if(view.getTotalRotate() != 0)
                    SaveTotalMap.correctTotalMap(view.getTotalRotate());

                if(SaveTotalMap.haveMap() && MapInfo.getMapDPI() > 1) {
                    initProject();
                    Intent activity;
                    if(SaveTotalMap.getMapScale() == 0)
                        activity = new Intent(AlignActivity.this, MapScaleActivity.class);
                    else
                        activity = new Intent(AlignActivity.this, MainActivity.class);
                    activity.putExtra("ProjectPath", projectPath);
                    activity.putExtra("process", false);
                    startActivity(activity);
                    overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE)
            try {
//                获得图片的uri
                uri = data.getData();

//                判断文件类型
                if(checkTypeFile(uri)) {
//                     设立新的线程
                    MHandler mHandler = new MHandler(getApplicationContext());
                    ObtainTotalMapRunnable obtainTotalMapRunnable =
                            new ObtainTotalMapRunnable(mHandler, uri, MHandler.INPUT_MAP_INIT);
                    obtainTotalMapRunnable.setMillisecond(10000);
//                    将该线程放入线程池并执行
                    MThreadPool.addTask(obtainTotalMapRunnable);
                    MThreadPool.start();
//                    开始显示等待控件
                    startLoading();

                    showView();
                } else
                    TextToast.showTextToast("请更换成图片格式", getApplicationContext());

            } catch (NullPointerException e) {
                TextToast.showTextToast("没有正确获取到底图", getApplicationContext());
            }
    }

    private boolean checkTypeFile(Uri uri) {
        String uriString = uri.toString();

        if (uriString.contains(".jpeg")
                || uriString.contains(".jpg")
                || uriString.contains(".bmp")
                || uriString.contains(".png"))
            return true;

        ContentResolver resolver = getContentResolver();
        String fileType = resolver.getType(uri);

        return fileType != null
                && (fileType.contains("jpeg")
                || fileType.contains("jpg")
                || uriString.contains(".bmp")
                || uriString.contains(".png"));
    }

    public static void setDegree(double degree) {
        DecimalFormat df = new DecimalFormat("#.0");
        String number = df.format(degree) + "°";
        if(number.charAt(0) == '.')
            number = '0' + number;
        else if(number.contains("-."))
            number = "-0." + number.substring(2, number.length());

        show.setText(number);
    }

    public static String getCurrentClassName() {
        return "AlignActivity";
    }

    private static void showView() {
        addArea.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        show.setVisibility(View.VISIBLE);
        right.setVisibility(View.VISIBLE);
        left.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
        enter.setBackgroundResource(R.color.theme_color_secondary_dark);

        if(checkDpi())
            TextToast.showTextToast("底图dpi =" + MapInfo.getMapDPI(), activity.getApplicationContext());
        else
            inputBox.show();
    }

    public static void showMap() {
        view.reset();
    }

    private static boolean checkDpi() {
        MapInfo.setMapDPI(UriToFile.uriToFile(uri, activity.getApplicationContext()));
        return MapInfo.getMapDPI() > 1;
    }

    private void initProject() {
//        设立新的线程
        MHandler mHandler = new MHandler(getApplicationContext());
        File originMapFile = UriToFile.uriToFile(uri, getApplicationContext());
        CopyFileRunnable copyMapRunnable = new CopyFileRunnable(mHandler, originMapFile,
                new File(projectPath + "/Map/OriginTotalMap.gcadP"));
//        将该线程放入线程池并执行
        copyMapRunnable.setMillisecond(3000);
        MThreadPool.addTask(copyMapRunnable);
        MThreadPool.start();
    }

    public static void finishActivity() {
        if(activity != null)
            activity = null;
    }

    public static void startLoading() {
        if(loading != null)
            loading.start();
    }

    public static void stopLoading() {
        if(loading != null)
            loading.stop();
    }

    class DpiInputBox extends Builder implements DialogInterface.OnClickListener {

        private EditText inputServer;

        private Context context;
        public DpiInputBox(Context context) {
            super(context);
            this.context = context;
            inputServer = new EditText(getApplicationContext());
            inputServer.setTextColor(Color.BLACK);
            inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputServer.setText(String.valueOf(MapInfo.getMapDPI()));

            this.setTitle("初始化底图dpi");
            this.setView(inputServer);

            this.setPositiveButton("确定", this);
            this.setNegativeButton("取消", this);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                        if(!String.valueOf(inputServer.getText()).equals("")) {
                            int dpi = Integer.parseInt(String.valueOf(inputServer.getText()));

                            if(dpi == -1) {
                                field.set(dialog, true);
                                field.setAccessible(false);
                                hide();
                                TextToast.showTextToast("dpi未更改", context);
                            } else {
                                field.set(dialog, true);
                                field.setAccessible(false);
                                MapInfo.setMapDPI(dpi);
                                hide();
                                TextToast.showTextToast("成功更改工程名", context);
                            }
                        } else
                            TextToast.showTextToast("dpi为空", context);
                        field.setAccessible(false);
                    } catch (Exception ignored) {

                    }
                    break;
                case BUTTON_NEGATIVE:
                    hide();
                    TextToast.showTextToast("dpi未初始化", context);
                    activity.finish();
                    overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
                    break;
                default:break;
            }
        }

        private void hide() {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputServer.getWindowToken(), 0);
        }
    }
}
