package caculator.bianfl.cn.abccaculator.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by 福利 on 2017/10/14.
 */

public class PermissionUtils {
    private Activity context;
    public PermissionUtils(Activity context){
        this.context = context;
    }
    public void requestPermission(){
        if (!hasPermission()){//申请权限
            ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
    }
    public boolean hasPermission(){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }
}
