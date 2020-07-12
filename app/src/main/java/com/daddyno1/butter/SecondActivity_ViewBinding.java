package com.daddyno1.butter;

import android.app.Activity;
import android.content.res.Resources;

import com.daddyno1.butterapi.ViewBinding;

public class SecondActivity_ViewBinding implements ViewBinding {

    @Override
    public void bind(Activity obj) {
        if(obj != null){
            SecondActivity activity = (SecondActivity) obj;

            //viewBinding
            activity.tx = activity.findViewById(R.id.tx);
            activity.tx2 = activity.findViewById(R.id.txt2);

            //string
            Resources resources = activity.getResources();
            activity.tips = resources.getResourceName(R.string.hello);
        }
    }
}
