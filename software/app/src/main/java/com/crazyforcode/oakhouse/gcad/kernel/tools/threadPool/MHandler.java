package com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.SQLFromFileRunnable;
import com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper.SQLToFileRunnable;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.LoadProject;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.kernel.views.DrawView;
import com.crazyforcode.oakhouse.gcad.others.components.KernelLayout;
import com.crazyforcode.oakhouse.gcad.others.surfaces.FinalMapActivity;
import com.crazyforcode.oakhouse.gcad.window.AlignActivity;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;

public class MHandler extends Handler {

    public static final int INPUT_MAP_INIT = 1;
    public static final int COPY_FILE_INIT = 2;
    public static final int OUTPUT_MAP = 3;
    public static final int LOAD_PROJECTS = 4;
    public static final int INPUT_MAP_AGAIN = 5;
    public static final int GET_SQL_FROM_FILE = 6;
    public static final int SAVE_SQL_TO_FILE = 7;

    public static final int SUCCESS = 100;
    public static final int FAILURE = 101;

    private Context userPromptContext;

    public MHandler(Context context) {
        this.userPromptContext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch(msg.what) {
            case INPUT_MAP_INIT:
                if(msg.arg1 == SUCCESS) {
                    AlignActivity.stopLoading();
                    AlignActivity.view.setMatrix();
                } else {
                    AlignActivity.stopLoading();
                    MainActivity.stopLoading();
                }
                break;
            case INPUT_MAP_AGAIN:
                if(msg.arg1 == SUCCESS) {
                    MainActivity.stopLoading();
                    LoadProject.setDpi();
                    TextToast.showTextToast("载入工程完毕", userPromptContext);
                } else {
                    AlignActivity.stopLoading();
                    MainActivity.stopLoading();
                }
                break;
            case COPY_FILE_INIT:
                try {
                    if(KernelLayout.drawView/*s[0]*/.getBackgroundBitmap(0) == null) {
                        KernelLayout.initBackgrounds();//TODO dont do this
                        Log.i("模式","载入");
                    }
                } catch (NullPointerException | IllegalArgumentException ignored) {
                    Log.i("模式","新建");
                }
                TextToast.showTextToast("初始化工程完毕", userPromptContext);
                break;
            case OUTPUT_MAP:
                if(msg.arg1 == SUCCESS) {
                    TextToast.showTextToast("成图导出完毕", userPromptContext);
                    FinalMapActivity.setSuccess();
                } else
                    TextToast.showTextToast("成图导出失败", userPromptContext);
                break;
            case LOAD_PROJECTS:
                if(msg.arg1 == SUCCESS)
                    TextToast.showTextToast("工程导入完毕", userPromptContext);
                else
                    TextToast.showTextToast("工程导入失败", userPromptContext);
                break;
            case GET_SQL_FROM_FILE:
                if(msg.arg1 == SUCCESS) {
                    DrawView.setAllDrawObjects(SQLFromFileRunnable.getDrawObjects());
                    SQLFromFileRunnable.clearObjects();
                } else
                    TextToast.showTextToast("载入失败", userPromptContext);
                break;
            case SAVE_SQL_TO_FILE:
                if(msg.arg1 == SUCCESS)
                    TextToast.showTextToast("保存成功", userPromptContext);
                else
                    TextToast.showTextToast("保存失败", userPromptContext);
                MainActivity.isSave = SQLToFileRunnable.isSuccess();
                break;
        }
    }

    public Context getUserPromptContext() {
        return userPromptContext;
    }
}
