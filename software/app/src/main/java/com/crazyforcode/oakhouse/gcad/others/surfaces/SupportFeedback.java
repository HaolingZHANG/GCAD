package com.crazyforcode.oakhouse.gcad.others.surfaces;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.R;

public class SupportFeedback extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, TextWatcher {

    private EditText opinion;

    private static final int QUESTION = 0;
    private static final int EXPECT = 1;
    private int type = QUESTION;

    private String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);

        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.gendergroup);
        radioGroup.setOnCheckedChangeListener(this);

        opinion = (EditText) findViewById(R.id.feedback_content_edit);
        opinion.addTextChangedListener(this);

        findViewById(R.id.btnSend).setOnClickListener(this);
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.question)
            type = QUESTION;
        else
            type = EXPECT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onClick(View v) {
        if(v.getId() == R.id.btnSend) {
            String userAttitude;
            StringBuilder userMobileSolution = new StringBuilder("User solution:\n");
            StringBuilder userOpinion = new StringBuilder("User opinion:\n");

            if(type == QUESTION)
                userAttitude = "问题:\n";
            else
                userAttitude = "期望:\n";

            userMobileSolution.append("手机型号: ").append(android.os.Build.MODEL).append("\n");
            userMobileSolution.append("手机系统版本号: ").append(android.os.Build.VERSION.RELEASE).append("\n");
            userMobileSolution.append("手机SDK版本号:");
            userMobileSolution.append(android.os.Build.VERSION.SDK);
            userMobileSolution.append("\n");

            userOpinion.append(String.valueOf(opinion.getText()));

            String sendText = userAttitude + userMobileSolution + userOpinion;

            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:1055472913@qq.com"));
            data.putExtra(Intent.EXTRA_SUBJECT, "用户报告");
            data.putExtra(Intent.EXTRA_TEXT, sendText);
            startActivity(data);

            this.finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        temp = s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {
        int editStart = opinion.getSelectionStart();
        int editEnd = opinion.getSelectionEnd();
        if (temp.length() > 500) {
            TextToast.showTextToast("字数超过500", getApplicationContext());
            s.delete(editStart -1, editEnd);
            opinion.setText(s);
            opinion.setSelection(editStart);
        }
    }
}
