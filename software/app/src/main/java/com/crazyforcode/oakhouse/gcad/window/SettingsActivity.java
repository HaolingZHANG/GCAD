package com.crazyforcode.oakhouse.gcad.window;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;


import com.crazyforcode.oakhouse.gcad.kernel.tools.GCADApplication;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;
import com.crazyforcode.oakhouse.gcad.others.surfaces.AboutUs;
import com.crazyforcode.oakhouse.gcad.others.surfaces.ExternalChooseAgain;
import com.crazyforcode.oakhouse.gcad.others.surfaces.ComparisionShow;
import com.crazyforcode.oakhouse.gcad.others.surfaces.CompassAdjust;
import com.crazyforcode.oakhouse.gcad.others.surfaces.CompassSensitivity;
import com.crazyforcode.oakhouse.gcad.others.surfaces.IntroductionFunctions;
import com.crazyforcode.oakhouse.gcad.others.surfaces.ExternalLocking;
import com.crazyforcode.oakhouse.gcad.others.surfaces.MapRevision;
import com.crazyforcode.oakhouse.gcad.others.surfaces.PrivacyPolicy;
import com.crazyforcode.oakhouse.gcad.others.surfaces.SearchAllProjects;
import com.crazyforcode.oakhouse.gcad.others.surfaces.SupportFeedback;
import com.crazyforcode.oakhouse.gcad.R;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    private AppCompatDelegate mDelegate;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Preference export;
    private CheckBoxPreference novice_reference;
    private Preference direction;
    private Preference accuracy;

    private Preference locking;

    public static boolean change = false;

    private static final int REQUEST_DIRECTORY = 0;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        change = false;

        GCADApplication.addActivity(this, getCurrentClassName());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        addPreferencesFromResource(R.xml.preferences);

        preferences = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);

        /** ???????????? */
        findPreference("light").setOnPreferenceClickListener(this);

        /** ???????????? */
        findPreference("search_all").setOnPreferenceClickListener(this);

        /** ???????????? */
        findPreference("reset").setOnPreferenceClickListener(this);

        /** ???????????? */
        export = findPreference("export");
        export.setOnPreferenceClickListener(this);
        String path = preferences.getString("export", "");
        if(!path.equals(""))
            export.setSummary(path);
        else
            export.setSummary("?????????????????????????????????????????????");

        /** ???????????? */
        findPreference("printer").setOnPreferenceClickListener(this);

        /** ???????????? */
        direction = findPreference("direction");
        direction.setOnPreferenceClickListener(this);
        int currentDirection = preferences.getInt("CompassAdjust",  0);
        if(currentDirection == 0)
            direction.setSummary("???????????????????????????????????????????????????");
        else if(currentDirection > 0)
            direction.setSummary("??????" + currentDirection + "???");
        else
            direction.setSummary("??????" + (-currentDirection) + "???");

        /** ???????????? */
        accuracy = findPreference("accuracy");
        accuracy.setOnPreferenceClickListener(this);
        int currentSensitivity = preferences.getInt("CompassSensitivity", 5);
        if(currentSensitivity != 5)
            accuracy.setSummary("???????????????" + currentSensitivity);
        else
            accuracy.setSummary("??????????????????????????????????????????");

        /** ???????????? */
        locking = findPreference("locking");
        locking.setOnPreferenceClickListener(this);

        /** ???????????? */
        findPreference("choose_again").setOnPreferenceClickListener(this);

        /** ???????????? */
        findPreference("comparision").setOnPreferenceClickListener(this);

        /** ???????????? */
        novice_reference = (CheckBoxPreference) findPreference("novice");
        novice_reference.setOnPreferenceChangeListener(this);

        /** ???????????? */
        findPreference("function_profile").setOnPreferenceClickListener(this);

        /** ???????????? */
        findPreference("feedback").setOnPreferenceClickListener(this);
        /** ???????????? */
        findPreference("privacy_policy").setOnPreferenceClickListener(this);
        /** ???????????? */
        findPreference("about_us").setOnPreferenceClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();

        int currentDirection = preferences.getInt("CompassAdjust",  0);
        if(currentDirection == 0)
            direction.setSummary("???????????????????????????????????????????????????");
        else if(currentDirection > 0)
            direction.setSummary("??????" + currentDirection + "???");
        else
            direction.setSummary("??????" + (-currentDirection) + "???");

        int currentSensitivity = preferences.getInt("CompassSensitivity", 5);
        if(currentSensitivity != 5)
            accuracy.setSummary("???????????????" + currentSensitivity);
        else
            accuracy.setSummary("??????????????????????????????????????????");
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);

        ActionBar supportActionBar = getDelegate().getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
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
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.activity_in_2, R.anim.activity_out_2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();

        switch(key) {
            case "novice":
                Boolean isNovice = (Boolean) newValue;
                MainActivity.setNovice(isNovice);
                setChange();
                editor = preferences.edit();
                editor.putBoolean("novice", isNovice);
                editor.apply();
                return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Intent intent;

        switch(key) {
            case "light":
                intent = new Intent("android.settings.DISPLAY_SETTINGS");
                startActivity(intent);
                setChange();
                return true;
            case "search_all":
                intent = new Intent(SettingsActivity.this, SearchAllProjects.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;
            case "reset":
                confirmReset();
                return true;
            case "export":
                final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);
                final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                        .newDirectoryName("DirChooserSample")
                        .allowReadOnlyDirectory(true)
                        .allowNewDirectoryNameModification(true)
                        .build();

                chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

                try {
                    startActivityForResult(Intent.createChooser(chooserIntent, "Select a Directory to Store"), REQUEST_DIRECTORY);
                } catch (ActivityNotFoundException e) {
                    TextToast.showTextToast("?????????????????????",  getApplicationContext());
                }
                return true;
            case "printer":
                intent = new Intent(SettingsActivity.this, MapRevision.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;

            case "direction":
                intent = new Intent(SettingsActivity.this, CompassAdjust.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;
            case "accuracy":
                intent = new Intent(SettingsActivity.this, CompassSensitivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;

            case "locking":
                intent = new Intent(SettingsActivity.this, ExternalLocking.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;
            case "choose_again":
                intent = new Intent(SettingsActivity.this, ExternalChooseAgain.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;

            case "comparision":
                intent = new Intent(SettingsActivity.this, ComparisionShow.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;
            case "function_profile":
                intent = new Intent(SettingsActivity.this, IntroductionFunctions.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;

            case "feedback":
                intent = new Intent(SettingsActivity.this, SupportFeedback.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;
            case "privacy_policy":
                intent = new Intent(SettingsActivity.this, PrivacyPolicy.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;
            case "about_us":
                intent = new Intent(SettingsActivity.this, AboutUs.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_1,  R.anim.activity_out_1);
                return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                String outPath = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                editor = preferences.edit();
                editor.putString("export", outPath);
                editor.apply();
                export.setSummary(outPath);
                setChange();
            }

        }
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null)
            mDelegate = AppCompatDelegate.create(this, null);

        return mDelegate;
    }

    public static void setChange() {
        change = true;
    }

    public static boolean isChanged() {
        return change;
    }

    public static String getCurrentClassName() {
        return "SettingsActivity";
    }

    public void confirmReset() {
        new ConfirmOperation(this).show();
    }

    class ConfirmOperation extends AlertDialog.Builder {

        public ConfirmOperation(Context context) {
            super(context);
            initView();
        }

        private void initView() {
            this.setTitle("????????????");

            this.setMessage("????????????????????????????????????????????????");

            this.setPositiveButton("??????", new RightDriver());

            this.setNegativeButton("??????", new CancelDriver());
        }

        class RightDriver implements DialogInterface.OnClickListener {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor = preferences.edit();
                //??????????????????
                editor.putBoolean("novice", true);
                novice_reference.setChecked(true);
                //??????????????????
                editor.putString("export", "");
                export.setSummary("?????????????????????????????????????????????");
                //??????????????????
                editor.putInt("CompassAdjust", 0);
                direction.setSummary("???????????????????????????????????????????????????");
                //??????????????????
                editor.putInt("CompassSensitivity", 5);
                accuracy.setSummary("??????????????????????????????????????????");
                editor.apply();

                dialog.cancel();
            }
        }

        class CancelDriver implements DialogInterface.OnClickListener {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }
    }
}
