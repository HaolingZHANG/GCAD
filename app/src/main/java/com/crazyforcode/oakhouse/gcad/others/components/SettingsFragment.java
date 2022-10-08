package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;


import com.crazyforcode.oakhouse.gcad.others.surfaces.AboutUs;
import com.crazyforcode.oakhouse.gcad.others.surfaces.ComparisionShow;
import com.crazyforcode.oakhouse.gcad.others.surfaces.CompassAdjust;
import com.crazyforcode.oakhouse.gcad.others.surfaces.CompassSensitivity;
import com.crazyforcode.oakhouse.gcad.others.surfaces.IntroductionFunctions;
import com.crazyforcode.oakhouse.gcad.others.surfaces.MapRevision;
import com.crazyforcode.oakhouse.gcad.others.surfaces.PrivacyPolicy;
import com.crazyforcode.oakhouse.gcad.others.surfaces.SupportFeedback;
import com.crazyforcode.oakhouse.gcad.R;
import com.crazyforcode.oakhouse.gcad.window.MainActivity;
import com.crazyforcode.oakhouse.gcad.window.SettingsActivity;

/**
 * Created by Monomania on 2/5/16.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener,
                   Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        /** 亮度调节 */
        findPreference("light").setOnPreferenceClickListener(this);
        /** 导出位置 */
        findPreference("export").setOnPreferenceClickListener(this);
        /** 打印服务 */
        findPreference("printer").setOnPreferenceClickListener(this);

        /** 方向校准 */
        findPreference("direction").setOnPreferenceClickListener(this);
        /** 精度调节 */
        findPreference("accuracy").setOnPreferenceClickListener(this);

        /** 本机锁定 */
        findPreference("locking").setOnPreferenceClickListener(this);
        /** 重选外设 */
        findPreference("choose_again").setOnPreferenceClickListener(this);

        /** 规范查看 */
        findPreference("comparision").setOnPreferenceClickListener(this);
        /** 新手设置 */
        CheckBoxPreference novice_reference = (CheckBoxPreference) findPreference("novice");
        novice_reference.setChecked(true);
        novice_reference.setOnPreferenceChangeListener(this);
        /** 功能简介 */
        findPreference("function_profile").setOnPreferenceClickListener(this);

        /** 支持反馈 */
        findPreference("feedback").setOnPreferenceClickListener(this);
        /** 隐私条款 */
        findPreference("privacy_policy").setOnPreferenceClickListener(this);
        /** 关于我们 */
        findPreference("about").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();

        switch(key) {
            case "novice":
                Boolean isNovice = (Boolean) newValue;
                MainActivity.setNovice(isNovice);
                SettingsActivity.setChange();
                return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Intent intent;

        Log.i("key ", key);
        //TODO
        switch(key) {
            case "light":
                intent = new Intent("android.settings.DISPLAY_SETTINGS");
                getActivity().startActivity(intent);
                SettingsActivity.setChange();
                return true;
            case "export":
                return true;
            case "printer":
                intent = new Intent(getActivity(), MapRevision.class);
                getActivity().startActivity(intent);
                return true;

            case "direction":
                intent = new Intent(getActivity(), CompassAdjust.class);
                getActivity().startActivity(intent);
                return true;
            case "accuracy":
                intent = new Intent(getActivity(), CompassSensitivity.class);
                getActivity().startActivity(intent);
                return true;

            case "locking":
                return true;
            case "choose_again":
                return true;

            case "comparision":
                intent = new Intent(getActivity(), ComparisionShow.class);
                getActivity().startActivity(intent);
                return true;
            case "function_profile":
                intent = new Intent(getActivity(), IntroductionFunctions.class);
                getActivity().startActivity(intent);
                return true;

            case "feedback":
                intent = new Intent(getActivity(), SupportFeedback.class);
                getActivity().startActivity(intent);
                return true;
            case "privacy_policy":
                intent = new Intent(getActivity(), PrivacyPolicy.class);
                getActivity().startActivity(intent);
                return true;
            case "about":
                intent = new Intent(getActivity(), AboutUs.class);
                getActivity().startActivity(intent);
                return true;
        }
        return false;
    }
}
