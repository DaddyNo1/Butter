package com.daddyno1.butterapi;

import android.app.Activity;
import android.util.Log;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;

public class Butter {
    private static final String TAG = "Butter";
    private static LruCache<String, ViewBinding> lruCache;
    private static List<String> blackList;

    static {
        lruCache = new LruCache<String, ViewBinding>(66);
        blackList = new ArrayList<>();
    }

    public static void bind(Activity activity){
        String fullName = activity.getClass().getName();
        if(blackList.contains(fullName)) return;;

        ViewBinding viewBinding = lruCache.get(fullName);
        if (viewBinding == null){
            try {
                String bindingFullName = fullName + "_ViewBinding";
                Class cls = Class.forName(bindingFullName);
                viewBinding = (ViewBinding) cls.newInstance();
                lruCache.put(fullName, viewBinding);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                blackList.add(fullName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                blackList.add(fullName);
            } catch (InstantiationException e) {
                e.printStackTrace();
                blackList.add(fullName);
            }
        }

        if(viewBinding != null){
            viewBinding.bind(activity);
        }
    }
}
