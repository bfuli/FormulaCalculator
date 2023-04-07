package caculator.bianfl.cn.abccaculator.utils;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import caculator.bianfl.cn.abccaculator.R;

/**
 * Created by 福利 on 2018/2/22.
 */

public class CommonActions {
    private Activity activity;

    public CommonActions(Activity activity){
        this.activity = activity;
    }
    /**
     * 点击返回键退出界面
     */
    public void finish(){
        ImageView img_back = (ImageView) activity.findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
    public void setTitle(String title){
        TextView tv_toolbar = (TextView) activity.findViewById(R.id.tv_toolbar);
        tv_toolbar.setText(title);
    }

    /**
     * 沉浸式状态栏
     */
    public void immerse(){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
}
