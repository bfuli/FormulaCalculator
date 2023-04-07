package caculator.bianfl.cn.abccaculator.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 福利 on 2016/9/29.
 */
public class NetUtils {
    //网络是否连接并可以上网
    public static final boolean isAvailable(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info!=null&&info.isConnected()){
                return true;
            }
            return false;
        }else{
            return false;
        }
    }
    //返回网络连接状态
    public static final int getNetKind(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return info.getType();
            }
        }
        return -1;
    }
}
