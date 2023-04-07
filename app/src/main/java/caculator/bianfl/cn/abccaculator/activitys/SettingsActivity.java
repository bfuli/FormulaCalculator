package caculator.bianfl.cn.abccaculator.activitys;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.WindowManager;
import android.widget.TextView;

import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.utils.CommonActions;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        CommonActions actions = new CommonActions(this);
        actions.immerse();//沉浸式状态栏
        actions.finish();//点击返回键推出界面
        actions.setTitle("设置");
        addPreferencesFromResource(R.xml.settings);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
}