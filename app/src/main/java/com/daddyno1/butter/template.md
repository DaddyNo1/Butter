根据使用场景，先定义好模板类，之后生成的时候就按这个打样。

```
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

```

```
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

```