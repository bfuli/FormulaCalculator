package caculator.bianfl.cn.abccaculator.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by 福利 on 2016/12/31.
 */
public class ToastUtil {
    private static Toast toast;
    public static String ENJOY_HAPPY = "╰(￣▽￣)╭";
    public static String ENJOY_SAD = "(＞﹏＜)";
    public static String ENJOY_WUNAI = "(⊙﹏⊙)";
    public static String ENJOY_CUTE = "(>▽<)";
    public static void showToast(Context context, String s) {
        getToast(context,s).show();
    }
    private static synchronized Toast getToast(Context context,String s){
        if (toast == null){
            synchronized (ToastUtil.class){
                if (toast == null){
                    return Toast.makeText(context, s, Toast.LENGTH_SHORT);
                }else {
                    toast.setText(s);
                    return toast;
                }
            }
        }else {
            toast.setText(s);
            return toast;
        }

    }
}
