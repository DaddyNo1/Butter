package com.daddyno1.butter;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;

import com.daddyno1.butterapi.ViewBinding;

public class MainActivity_ViewBinding implements ViewBinding {

    @Override
    public void bind(Activity obj) {
        if(obj != null){
            MainActivity activity = (MainActivity) obj;

            //viewBinding
            activity.tvTxt = activity.findViewById(R.id.txt);
            activity.tvTxt2 = activity.findViewById(R.id.txt2);

            //click
            activity.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.click(v);
                }
            });

            //string
            Resources resources = activity.getResources();
            activity.appName = resources.getResourceName(R.string.app_name);
        }
    }
}
