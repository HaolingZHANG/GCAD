package com.crazyforcode.oakhouse.gcad.window;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v7.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazyforcode.oakhouse.gcad.bluetooth.Message;
import com.crazyforcode.oakhouse.gcad.kernel.tools.GCADApplication;
import com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet.SaveTotalMap;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.LoadProject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.UserPromptBox;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.ProjectInfo;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.test.FileTest.AllProjectDelete;
import com.crazyforcode.oakhouse.gcad.test.FileTest.ProjectInfoPrint;
import com.crazyforcode.oakhouse.gcad.test.PhoneTest;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.ProjectInfo.getCurrentProject;

public class ProgramEntry extends AppCompatActivity {

    private File dirGCAD;

    static ActionMode mActionMode;

    private static ProgramEntry activity;

    private static ArrayList<ProjectInfo> projectInfos;

    RecyclerView recyclerView;

    ContentAdapter adapter;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int childCount;
            switch (item.getItemId()) {
                case R.id.action_delete_project:
                    childCount = recyclerView.getChildCount();
                    for (int i = 0; i < childCount; ++i) {
                        View v = recyclerView.getChildAt(i);
                        TextView textView = (TextView)v.findViewById(R.id.project_name_text);
                        if (v.isSelected())
                            ProjectInfo.deleteProject(String.valueOf(textView.getText()), getApplicationContext());
                    }
                    adapter.notifyDataSetChanged();
                    mode.finish();
                    return true;
                case R.id.action_rename_project:
                    childCount = recyclerView.getChildCount();
                    for (int i = 0; i < childCount; ++i) {
                        View v = recyclerView.getChildAt(i);
                        TextView textView = (TextView)v.findViewById(R.id.project_name_text);
                        if (v.isSelected()) {
                            new RenameBox(activity, String.valueOf(textView.getText())).show();
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                View v = recyclerView.getChildAt(i);
                v.setSelected(false);
                ImageView selectedBorder = (ImageView)v.findViewById(R.id.selected_border);
                selectedBorder.setVisibility(View.INVISIBLE);
            }
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_entry);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GCADApplication.addActivity(this, getCurrentClassName());
        activity = this;

//        硬件输出测试
        PhoneTest.printHardware(getApplicationContext());

        if(hardwareWarning()) {
            TextToast.showTextToast("您的手机可能缺乏相应硬件设备\n" +
                    "如果继续使用\n" +
                    "应用可能出现异常", getApplicationContext());
        }

        try {
            /** 在没有工程文件夹的时候新建工程文件夹 */
            setProgramDir();

            if(projectInfos == null)
            /** 获取用户工程集合 */
                projectInfos = ProjectInfo.getProject(getApplicationContext());

            /** 设置历史工程 */
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            adapter = new ContentAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.getChildCount();
        } catch (NumberFormatException | NullPointerException e) {
            //遇见工程无法加载报错的时候使用，测试使用
            AllProjectDelete.deleteAdd(getApplicationContext());
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        activity = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_program_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_project:
                Intent intent = new Intent(ProgramEntry.this, NewProjectActivity.class);
                intent.putExtra("ProgramDirPath", dirGCAD.getPath());
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean hardwareWarning() {
        if(Build.VERSION.SDK_INT < 19) {
            TextToast.showTextToast("手机版本小于应用版本", getApplicationContext());
            return true;
        } else {
//            是否有蓝牙设备
            boolean hasBlueTooth = false;
//            是否有加速度传感器
            boolean hasAccelerometer = false;
//            是否有地磁传感器
            boolean hasMagnetic = false;

            if(bluetoothAvailable())
                hasBlueTooth = true;

            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

            for(Sensor sensor : sensorList) {
                String s = sensor.getName();
                if(s.contains("Accelerometer") || s.contains("Acceleration"))
                    hasAccelerometer = true;
                if(s.contains("Magnetometer") || s.contains("orientation"))
                    hasMagnetic = true;
            }

            if(!hasBlueTooth)
                TextToast.showTextToast("手机没有蓝牙设备", getApplicationContext());

            if(!hasAccelerometer)
                TextToast.showTextToast("手机没有加速度传感器", getApplicationContext());

            if(!hasMagnetic)
                TextToast.showTextToast("手机没有磁场传感器", getApplicationContext());

            return !(hasBlueTooth && hasAccelerometer && hasMagnetic);
        }
    }

    private boolean bluetoothAvailable() {
        List<ResolveInfo> list = getPackageManager()
                .queryIntentActivities(new Intent("android.settings.BLUETOOTH_SETTINGS"),
                        PackageManager.GET_ACTIVITIES);

        return  list.size() > 0;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setProgramDir() {
        String rootPath = Environment.getExternalStorageDirectory().getPath();

        dirGCAD = new File(rootPath + "/GCAD");

        Log.i("GCAD文件夹路径", dirGCAD.getPath());

        if(!dirGCAD.exists())
            dirGCAD.mkdirs();

//        记录根目录路径
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("RootPath", dirGCAD.getPath());
        editor.apply();
    }

    public static String getCurrentClassName() {
        return "ProgramEntry";
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ViewHolder(LayoutInflater inflater, final ViewGroup parent) {
            super(inflater.inflate(R.layout.project_item_tile, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void onClick(View v) {
            if (mActionMode == null) {
                TextView title = (TextView) itemView.findViewById(R.id.project_name_text);
                String projectName = String.valueOf(title.getText());
                if(LoadProject.loadProject(projectName, getApplicationContext())) {
                    Intent intent = new Intent(ProgramEntry.this, MainActivity.class);
                    intent.putExtra("type", true);
                    intent.putExtra("ProjectPath", dirGCAD.getPath() + "/" + projectName);
                    SaveTotalMap.setMapScale(getCurrentProject(projectName).getScale());
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                    finish();
                } else {
                    UserPromptBox.init(activity, "工程警告", "工程损坏是否删除工程并退出",
                            "确定", "取消", UserPromptBox.NEW_PROJECT_WORING);
                    UserPromptBox.show();
                }
            } else {
                ImageView selectedBorder = (ImageView) itemView.findViewById(R.id.selected_border);
                if (itemView.isSelected()) {
                    selectedBorder.setVisibility(View.INVISIBLE);
                    itemView.setSelected(false);
                } else {
                    selectedBorder.setVisibility(View.VISIBLE);
                    itemView.setSelected(true);
                }
                if(isMultiple())
                    findViewById(R.id.action_rename_project).setVisibility(View.GONE);
                else
                    findViewById(R.id.action_rename_project).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mActionMode != null)
                return false;
            mActionMode = startSupportActionMode(mActionModeCallback);
            ImageView selectedBorder = (ImageView) itemView.findViewById(R.id.selected_border);
            selectedBorder.setVisibility(View.VISIBLE);
            itemView.setSelected(true);
            return true;
        }

        private boolean isMultiple() {
            int childCount = recyclerView.getChildCount();
            int count = 0;
            for (int i = 0; i < childCount; ++i) {
                View child = recyclerView.getChildAt(i);
                if(child.isSelected())
                    count++;
            }

            return count > 1;
        }
    }

    class ContentAdapter extends RecyclerView.Adapter {

        public ContentAdapter() {
            super();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageView imageView = (ImageView)holder.itemView.findViewById(R.id.image);
            TextView nameTextView = (TextView)holder.itemView.findViewById(R.id.project_name_text);
            imageView.setImageBitmap(projectInfos.get(position).getMap());
            nameTextView.setText(projectInfos.get(position).getProjectName());
        }

        @Override
        public int getItemCount() {
            return projectInfos.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }
    }

    class RenameBox extends AlertDialog.Builder implements DialogInterface.OnClickListener {

        private EditText inputServer;

        private String oldName;

        public RenameBox(Context context, String oldName) {
            super(context);
            this.oldName = oldName;
            initView(context);
        }

        private void initView(Context context) {
            this.setTitle("更改文件名");
            inputServer = new EditText(context);
            inputServer.setText(oldName);
            inputServer.setTextColor(Color.BLACK);
            this.setView(inputServer);
            this.setPositiveButton("确定", this);
            this.setNegativeButton("取消", this);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                        String newName = String.valueOf(inputServer.getText());
                        if(!newName.equals("")) {
                            if(newName.equals(oldName)) {
                                TextToast.showTextToast("工程名未更改", getApplicationContext());
                                field.set(dialog, true);
                                field.setAccessible(false);
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                if(inputMethodManager != null)
                                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                            }
                            else if(ProjectInfo.isSameName(newName)) {
                                field.set(dialog, true);
                                field.setAccessible(false);
                                ProjectInfo.changeProjectName(oldName, newName, getApplicationContext());
                                hide();
                                TextToast.showTextToast("成功更改工程名", getApplicationContext());
                                adapter.notifyDataSetChanged();
                            } else
                                TextToast.showTextToast("工程名重复", getApplicationContext());
                        } else
                            TextToast.showTextToast("输入为空", getApplicationContext());
                        field.setAccessible(false);
                    } catch (Exception ignored) {

                    }
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    hide();
                    TextToast.showTextToast("工程名未更改", getApplicationContext());
                    break;
                default:break;
            }
        }

        private void hide() {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputServer.getWindowToken(), 0);
        }
    }
}