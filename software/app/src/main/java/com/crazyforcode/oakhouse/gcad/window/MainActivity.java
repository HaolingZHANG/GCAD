package com.crazyforcode.oakhouse.gcad.window;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.GravityCompat;

import android.util.Log;

import android.view.Gravity;
import android.view.KeyEvent;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.crazyforcode.oakhouse.gcad.bluetooth.SetBlueTooth;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.SQLAccess;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.DrawObjects;
import com.crazyforcode.oakhouse.gcad.kernel.tools.GCADApplication;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.SVGCreator;
import com.crazyforcode.oakhouse.gcad.kernel.tools.iconOperation.Icon;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.SaveProject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MThreadPool;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.InputBox;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.ScaleListDialog;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.UserPromptBox;
import com.crazyforcode.oakhouse.gcad.kernel.views.DrawView;
import com.crazyforcode.oakhouse.gcad.others.components.DrawTypeRadio;
import com.crazyforcode.oakhouse.gcad.others.components.LayerPicker;
import com.crazyforcode.oakhouse.gcad.others.surfaces.FinalMapActivity;
import com.crazyforcode.oakhouse.gcad.others.components.CompassView;
import com.crazyforcode.oakhouse.gcad.others.components.EditPicker;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;
import com.crazyforcode.oakhouse.gcad.others.surfaces.LegendPicker;
import com.crazyforcode.oakhouse.gcad.others.components.LegendTileFragment;
import com.crazyforcode.oakhouse.gcad.others.components.LoadingView;
import com.crazyforcode.oakhouse.gcad.others.surfaces.MapScaleActivity;

import com.crazyforcode.oakhouse.gcad.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private static NavigationView navigationView;
    private static KernelLayout kernelLayout;
    public static Button legendPickerButton;
    public static EditPicker editPicker;
    public static CompassView compass;
    public static Button drawStart;
    public static Button drawPause;
    private static EditText displayScale;
    private static EditText displayAltitude;
    private static LoadingView loading;
    private static TranslateAnimation translateAnimation;

    private static String projectName;
    private static String projectPath;

    public static boolean isSave = true;
    private static boolean editShow = false;
    private static boolean isNovice;
    public static boolean useExternal = false;
    private static double altitude = 0;

    public static final int CHOOSE_SYMBOL = 0;
    public static final int SEARCH_BLUETOOTH = 8;
    public static final int SETTING = 2;

    private int sign = -1;
    private int type;

    public static Activity staticActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GCADApplication.addActivity(this, getCurrentClassName());
        staticActivity = MainActivity.this;

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);
        /** ?????? toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** ?????? ActionBar */
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        /** ????????????????????? */
        kernelLayout = (KernelLayout) findViewById(R.id.kernel);

        /** Create Navigation drawer and inflate layout */
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setVerticalScrollBarEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.setVerticalScrollBarEnabled(false);

        /** ???????????????????????? */
        legendPickerButton = (Button) findViewById(R.id.legend_picker_button);
        legendPickerButton.setOnClickListener(this);

        /** ???????????????????????? */
        Button layerPickerButton = (Button) findViewById(R.id.layer_picker_button);
        layerPickerButton.setOnClickListener(this);

        /** ?????????????????? */
        editPicker = (EditPicker) findViewById(R.id.edit_picker);

        /** ?????????????????????????????? */
        drawStart = (Button) findViewById(R.id.drawStart);
        drawStart.setOnClickListener(this);

        /** ?????????????????????????????? */
        drawPause = (Button) findViewById(R.id.drawPause);
        drawPause.setOnClickListener(this);

        /** ??????????????? */
        compass = (CompassView) findViewById(R.id.compass);

        /** ?????????????????????????????? */
        displayScale = (EditText) findViewById(R.id.displayScale);
        displayScale.setOnClickListener(this);

        /** ?????????????????????????????? */
        displayAltitude = (EditText) findViewById(R.id.displayAltitude);
        displayAltitude.setOnClickListener(this);

        /** ?????????????????????????????? */
        loading = (LoadingView) findViewById(R.id.waiting);

        if(getIntent().getBooleanExtra("type", false)) {
            startLoading();
            projectPath = getIntent().getStringExtra("ProjectPath");
            projectName = projectPath.substring(projectPath.lastIndexOf("/") + 1, projectPath.length());
            SQLAccess.obtainDrawObjects(getApplicationContext());
            destroyStart(false, false);
        } else {
            projectPath = getIntent().getStringExtra("ProjectPath");
            projectName = projectPath.substring(projectPath.lastIndexOf("/") + 1, projectPath.length());
            SaveProject.saveProjectInfoToFile(projectPath);
            SQLAccess.initSQLService(getApplicationContext(), projectPath, true);
            destroyStart(true, getIntent().getBooleanExtra("process", false));
        }

        /** ??????????????????????????? */
        navigationView.bringToFront();
        mDrawerLayout.bringToFront();
        layerPickerButton.bringToFront();
        legendPickerButton.bringToFront();
        drawStart.bringToFront();
        drawPause.bringToFront();

        /** ??????????????????????????? */
        isNovice = preferences.getBoolean("novice", true);

        /** ???????????? */
//        cuttingMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        staticActivity = null;
        MThreadPool.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("requestCode:resultCode?", requestCode + "and" + resultCode);
        if (requestCode == CHOOSE_SYMBOL) {

            try {
                Bundle bundle = data.getExtras();

                sign = bundle.getInt("sign");
                type = bundle.getInt("type");

                Log.i("Sign", sign + "");
                Log.i("Type", type + "");

//                ???????????????????????????LegendButton???
                Icon icon = LegendTileFragment.icons.searchIcon(sign);
                legendPickerButton.setBackgroundResource(icon.getLegendRes());

                if (resultCode == 0) {
    ////                ????????????????????????????????????????????????????????????
    //                if(!DrawObjects.isPoint(sign)) {
    //                    findViewById(R.id.drawStart).setVisibility(View.VISIBLE);
    //                    findViewById(R.id.drawPause).setVisibility(View.VISIBLE);
    //                }
    //                ???????????????????????????????????????
                    KernelLayout.isDrawing = false;
                    LayerPicker.saveDrawBack();
                    DrawObjects.makeCurrentDrawOnTop(sign);//init or invalidate last view


    //                DrawView.chooseDrawObject(bundle.getInt("sign"), bundle.getBoolean("is_curl"));
                    //kernelLayout.finishDraw();//paintToMap, clean, invalidate, isDrawing = false;
                    DrawView.cleanPainter();

                    DrawView.chooseDrawObject(sign, type);
                    if (KernelLayout.isDrawing) {

//                        drawPause.setVisibility(View.VISIBLE);
//                        drawStart.setVisibility(View.INVISIBLE);
                    }


                    isSave = false;
                }else /**edit mode*/{
                    DrawObjects.makeCurrentDrawOnTop(sign);
                    DrawView.shapeshiftPainter(sign, type);
                }
            } catch (NullPointerException ignored) {
//                ignored.printStackTrace();
//                Log.i("here fucking exception!", ignored.getMessage());
                Log.i("???????????????", "??????");
                sign = -1;
                //click cancel back
            }

        }else if (requestCode == SEARCH_BLUETOOTH) {
            SetBlueTooth.initBlueTooth(getApplicationContext());
        } else if (requestCode == SETTING) {
            if (com.crazyforcode.oakhouse.gcad.window.SettingsActivity.isChanged())
                TextToast.showTextToast("??????????????????", getApplicationContext());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ????????????
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_bluetooth_search) {
            Intent intent = new Intent("android.settings.BLUETOOTH_SETTINGS");
            if(bluetoothAvailable())
                startActivityForResult(intent, SEARCH_BLUETOOTH);
            else
                TextToast.showTextToast("????????????????????????", getApplicationContext());
        }/* else if (id == R.id.action_synchronization_rendering) {
            if(getLocalVersionCode() <= 1)
                TextToast.showTextToast("?????????????????????????????????????????????????????????,???????????????",
                        getApplicationContext());
            else {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if(!networkInfo.isConnectedOrConnecting()) {

                    UserPromptBox.init(MainActivity.this, "GCAD??????",
                            "???????????????????????????GCAD????????????????????????????????????","??????", "??????", UserPromptBox.NETWORK_OPEN);
                    UserPromptBox.show();
                }
            }
        }*/ else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, SETTING);
            overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (id == R.id.action_save) {
            SaveProject.saveProjectInSql(DrawView.getAllDrawObjects(), getApplicationContext());
        } else if (id == R.id.action_export) {
            Intent intent = new Intent(MainActivity.this, FinalMapActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
        } else if (id == R.id.action_exit) {
            if (isSave) {
//                ??????????????????
                SaveTotalMap.deleteWhenQuit();
                KernelLayout.deleteWhenQuit();
                CompassView.deleteWhenQuit();
                SetBlueTooth.deleteWhenQuit();
                MThreadPool.release();
                System.gc();

//                ????????????App
                System.exit(0);
            } else {

                UserPromptBox.init(MainActivity.this, "GCAD??????",
                        "?????????????????????????????????\n??????????????????","?????????", "??????", UserPromptBox.EXIT);
                UserPromptBox.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            ???????????????????????????????????????????????????????????????
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            /** ???????????????????????? */
            case R.id.legend_picker_button:
                Intent i = new Intent(MainActivity.this, LegendPicker.class);
                startActivityForResult(i, CHOOSE_SYMBOL);
                break;
            /** ???????????????????????? */
            case R.id.layer_picker_button:
                View layerPickerView = findViewById(R.id.layer_picker_view);
//                ?????????????????????
                Animation animation;
                if(layerPickerView.getVisibility() == View.VISIBLE) {
                    animation = AnimationUtils.loadAnimation(this, R.anim.components_hide);
                    layerPickerView.startAnimation(animation);
                    layerPickerView.setVisibility(View.GONE);
                } else {
                    animation = AnimationUtils.loadAnimation(this,  R.anim.components_show);
                    layerPickerView.startAnimation(animation);
                    layerPickerView.setVisibility(View.VISIBLE);
                }

                /**this is only for test svg out TODO Delete this*/
                SVGCreator.OutSVGFile(null, projectPath);
                break;
            /** ?????????????????????????????? */
            case R.id.drawStart:
                //                ???????????????????????????????????????
                //DrawObjects.makeCurrentDrawOnTop(sign);
                if(sign != -1) {
//                drawStart.setVisibility(View.INVISIBLE);
//                drawPause.setVisibility(View.VISIBLE);
                    //TODO ????????????????????????????????????
                    drawStart.setBackgroundResource(R.drawable.pause_painting_button);

                    if (type != DrawTypeRadio.CURVE)
                        DrawView.chooseDrawObject(sign, DrawObjects.DRAW_TYPE_STRAIGHT);
                    else//TODO DRAW TYPE FREEDOM
                        DrawView.chooseDrawObject(sign, DrawObjects.DRAW_TYPE_CURL);
                } else
                    TextToast.showTextToast("?????????????????????", getApplicationContext());
                break;
            /** ?????????????????????????????? */
            case R.id.drawPause:
                if(KernelLayout.getLevel() != KernelLayout.ONLY_MAP) {

//                    drawPause.setVisibility(View.INVISIBLE);
//                    drawStart.setVisibility(View.VISIBLE);

                    kernelLayout.finishDraw();
                }
                break;
            /** ?????????????????????????????? */
            case R.id.displayScale:
                if(displayScale.getTextColors().getDefaultColor() != Color.argb(255, 255, 76, 0))
                    displayScale.setTextColor(Color.argb(255, 255, 76, 0));
                else
                    displayScale.setTextColor(Color.BLACK);
                break;
            /** ?????????????????????????????? */
            case R.id.displayAltitude:
                if(displayAltitude.getTextColors().getDefaultColor() != Color.argb(255, 255, 76, 0))
                    displayAltitude.setTextColor(Color.argb(255, 255, 76, 0));
                else
                    displayAltitude.setTextColor(Color.BLACK);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch(id) {
//            ??????????????????
            case R.id.action_use_external:
                if(useExternal) {
                    menuItem.setTitle("??????????????????");
                    useExternal = false;
                } else {
                    menuItem.setTitle("??????????????????");
                    //TODO ????????????????????????
                    useExternal = true;
                }
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                break;
//            ??????????????????
            case R.id.action_edit_object:
                if(KernelLayout.isEditing) {
                    menuItem.setTitle("??????????????????");
                    KernelLayout.isEditing = false;
                    drawPause.setVisibility(View.GONE);
                    //kernelLayout.finishDraw();//??????????????????????????????????????????????????????
                    //DrawView.cleanPainter();//??????DrawView??????????????????(?????????,???????????????

                    if(editPicker.isShow()) {
                        hideEditPicker();
                        editPicker.setHide();
                    } else {
                        FrameLayout.LayoutParams lp =
                                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.END | Gravity.TOP;
                        lp.setMargins(0, (editPicker.getHeight() / 8 * 6), -editPicker.getWidth(), 0);

                        editPicker.setLayoutParams(lp);
                    }
                } else {
                    //TODO ??????2??????edit?????????drawing?????????????????????????????????drawing??????????????????????????????addPoint
                    menuItem.setTitle("??????????????????");
                    KernelLayout.isEditing = true;
                    //drawPause.callOnClick();
                    drawStart.setVisibility(View.GONE);
                    drawPause.setVisibility(View.GONE);
                    KernelLayout.isDrawing = false;
                    
                    if (DrawView.isPainterLoad()) {
                        //KernelLayout.drawViews[KernelLayout.getLevel()].paintToBack();
                        //KernelLayout.drawViews[KernelLayout.getLevel()].invalidate();
                        KernelLayout.drawView.invalidate();
                    }
                    if(Math.abs(editPicker.getLeft() - kernelLayout.getWidth()) < 5)
                        EditPicker.setShow();
                }
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                return true;
//            ??????????????????
            case R.id.action_show_attitude:
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                if(displayAltitude.getVisibility() == View.VISIBLE) {
                    displayAltitude.setVisibility(View.GONE);
                    menuItem.setTitle("??????????????????");
                } else {
                    displayAltitude.setVisibility(View.VISIBLE);
                    menuItem.setTitle("??????????????????");
                }
                return true;
//            ??????????????????
            case R.id.action_reset_current_position:
                //TODO ????????????????????????
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                return true;
//            ??????????????????
            case R.id.action_reset_scale:
                ScaleListDialog.init(this);
                ScaleListDialog.show();
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                return true;
//            ????????????????????????
            case R.id.action_reset_dpi:
                InputBox.init(this, "????????????dpi", "??????", "??????");
                InputBox.show();
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                return true;
            case R.id.action_special_panel:
                if(KernelLayout.showSpecial) {
                    //KernelLayout.drawViews[4].setVisibility(View.GONE);
                    KernelLayout.drawView.levelVisible[4] = false;
                    if (KernelLayout.drawView.backgrounds[4] != null)
                        KernelLayout.drawView.invalidate();
                    KernelLayout.showSpecial = false;
                    menuItem.setTitle("??????????????????");
                } else {
                    menuItem.setTitle("??????????????????");
                    KernelLayout.showSpecial = true;
                }
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                return true;
//            ???????????????????????????
            case R.id.action_show_compass:

                SVGCreator.parseSVGFile(new File(projectPath + "/Data/cqut.svg")
                        , KernelLayout.drawView.getAllDrawObjects());


                if(compass.getVisibility() == View.VISIBLE) {
                    menuItem.setTitle("??????????????????");
                    compass.setVisibility(View.GONE);
                } else {
                    menuItem.setTitle("??????????????????");
                    compass.setVisibility(View.VISIBLE);
                }
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                return true;
//            ??????????????????
            case R.id.action_remove_all_paint:
                /*for(DrawView drawView : KernelLayout.drawViews) {
                    //drawView.initBackground();
                    drawView.clearAllDrawObjects();
                }*/
                KernelLayout.drawView.clearAllDrawObjects();
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                return true;
        }
        return false;
    }

    public void setNoDraw() {
        legendPickerButton.setBackgroundColor(Color.parseColor("#DADBDA"));
//        drawPause.setVisibility(View.INVISIBLE);
    }

    private boolean bluetoothAvailable() {
        List<ResolveInfo> list = getPackageManager()
                .queryIntentActivities(new Intent("android.settings.BLUETOOTH_SETTINGS"),
                        PackageManager.GET_ACTIVITIES);

        return  list.size() > 0;
    }

    public static String getProjectName() {
        return projectName;
    }

    public static String getProjectPath() {
        return projectPath;
    }

    public static void setShowSpecialLayer() {
        Menu menu = navigationView.getMenu();
        menu.getItem(4).setTitle("??????????????????");
        legendPickerButton.setBackgroundColor(Color.parseColor("#DADBDA"));
//        drawPause.setVisibility(View.INVISIBLE);
        KernelLayout.showSpecial = true;
    }

    public static void showEditPicker() {
        if(Math.abs(editPicker.getLeft() - kernelLayout.getWidth()) < 5)
            translateAnimation =  new TranslateAnimation(0, -editPicker.getWidth(), 0, 0);
        else
            translateAnimation =  new TranslateAnimation(0, -editPicker.getWidth() * 4 / 5, 0, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setFillEnabled(true);
        translateAnimation.setAnimationListener(new ViewAnimationListener());
        editPicker.startAnimation(translateAnimation);
        editShow = true;
    }

    public static void hideEditPicker() {
        if(KernelLayout.isEditing)
            translateAnimation = new TranslateAnimation(0, editPicker.getWidth() * 4 / 5, 0, 0);
        else
            translateAnimation = new TranslateAnimation(0, editPicker.getWidth(), 0, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setFillEnabled(true);
        translateAnimation.setAnimationListener(new ViewAnimationListener());
        editPicker.startAnimation(translateAnimation);
        editShow = false;
    }

    public static void setNovice(boolean novice) {
        isNovice = novice;
    }

    public static boolean getNovice() {
        return isNovice;
    }

    public static boolean isUseExternal() {
        return useExternal;
    }

    public static void changeAltitude(double height) {
        altitude = height;
        DecimalFormat df = new DecimalFormat("#.00");
        String number = df.format(altitude) + "M";
        if(number.charAt(0) == '.')
            number = '0' + number;
        displayAltitude.setText(String.valueOf(number));
    }

    public static double getAltitude() {
        return altitude;
    }

    public static void changeScale() {
        double showScale = Math.pow(1.0 / KernelLayout.mapView.getScale(), 2);
        if(showScale < 10000) {
            DecimalFormat df = new DecimalFormat("#.00");
            String number = df.format(showScale) + "X";
            if(number.charAt(0) == '.')
                number = '0' + number;
            displayScale.setText(number);
        }
    }

//    private void cuttingMap() {
//        MHandler mHandler = new MHandler(getApplicationContext());
//        OriginMapChipRunnable originMapChipRunnable = new OriginMapChipRunnable(mHandler, projectPath);
//
////        ????????????????????????????????????
//        MThreadPool.addTask(originMapChipRunnable);
//        MThreadPool.start();
//    }

//    public int getLocalVersionCode() {
//        PackageInfo packageInfo;
//        try {
//            PackageManager packageManager = getPackageManager();
//            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            return -1;
//        }
//    }

    public static void startLoading() {
        if(loading != null)
            loading.start();
    }

    public static void stopLoading() {
        if(loading != null)
            loading.stop();
    }

    public static String getCurrentClassName() {
        return "MainActivity";
    }

    private static void destroyStart(boolean newProject, boolean processScale) {
        AlignActivity.finishActivity();
        if(newProject) {
            if(processScale)
                GCADApplication.destroyActivity(MapScaleActivity.getCurrentClassName());

            GCADApplication.destroyActivity(AlignActivity.getCurrentClassName());
            GCADApplication.destroyActivity(NewProjectActivity.getCurrentClassName());
            GCADApplication.destroyActivity(ProgramEntry.getCurrentClassName());
        }
    }

    static class ViewAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            FrameLayout.LayoutParams lp =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.END | Gravity.TOP;
            if (editShow) {
                lp.setMargins(0, (editPicker.getHeight() / 8 * 6), 0, 0);
            } else {
                if(KernelLayout.isEditing)
                    lp.setMargins(0, (editPicker.getHeight() / 8 * 6), -(editPicker.getWidth() * 4 / 5), 0);
                else
                    lp.setMargins(0, (editPicker.getHeight() / 8 * 6), -editPicker.getWidth(), 0);
            }

            editPicker.setLayoutParams(lp);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}