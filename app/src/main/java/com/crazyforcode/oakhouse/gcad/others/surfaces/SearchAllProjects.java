package com.crazyforcode.oakhouse.gcad.others.surfaces;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;

import com.crazyforcode.oakhouse.gcad.others.components.BorderTextView;
import com.crazyforcode.oakhouse.gcad.others.components.ImageAdapter;
import com.crazyforcode.oakhouse.gcad.kernel.tools.projectOper.ProjectInfo;
import com.crazyforcode.oakhouse.gcad.R;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class SearchAllProjects extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static BorderTextView introduce;

    private static ArrayList<ProjectInfo> projectInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        if(projectInfos == null)
            projectInfos = ProjectInfo.getProject(getApplicationContext());

        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setOnItemSelectedListener(this);
        gallery.setFadingEdgeLength(0);
//        图片之间的间距
        gallery.setSpacing(30);

        Bitmap[] images = new Bitmap[projectInfos.size()];
        for(int i = 0; i < projectInfos.size(); i++)
            images[i] = projectInfos.get(i).getMap();


        ImageAdapter adapter = new ImageAdapter(this, images);
//        创建倒影效果
        adapter.createReflectedImages();
        gallery.setAdapter(adapter);

        introduce = (BorderTextView) findViewById(R.id.introduce);
        introduce.setSrokeWidth(5);

        ProjectInfo first = projectInfos.get(0);
        setIntroduce(first.getProjectName(), first.getCount(), first.getDate());
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
//        这里不做响应
    }

    @SuppressLint("SetTextI18n")
    public static void setIntroduce(String name, int totalCount, String time) {
        introduce.setText("项目名称：" + name
                + "\n总对象数：" + totalCount
                + "\n创建时间：" + time);
    }

    @SuppressLint("SetTextI18n")
    public static void setIntroduce(int count) {
        try {
            introduce.setText("项目名称：" + projectInfos.get(count).getProjectName()
                    + "\n总对象数：" + projectInfos.get(count).getCount()
                    + "\n创建时间：" + projectInfos.get(count).getDate());
        } catch(NullPointerException ignored) {

        }
    }
}
