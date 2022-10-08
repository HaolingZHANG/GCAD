package com.crazyforcode.oakhouse.gcad.kernel.tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GCADApplication {

    private static Map<String, Activity> maps = new HashMap<>();

    private static Context context;

    public static void addActivity(Activity activity, String activityName) {
        maps.put(activityName, activity);
        if(context == null)
            context = activity.getApplicationContext();
    }

    public static void destroyActivity(String activityName) {
        Set<String> keySet = maps.keySet();
        for(String key : keySet)
            if(key.equals(activityName))
                maps.get(key).finish();
    }

    public static Activity getActivity(String activityName) {
        Set<String> keySet = maps.keySet();
        for(String key : keySet)
            if(key.equals(activityName))
                return maps.get(key);

        return null;
    }

    public static Context getContext() {
        return context;
    }
}
