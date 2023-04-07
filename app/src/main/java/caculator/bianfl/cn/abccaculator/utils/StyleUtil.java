package caculator.bianfl.cn.abccaculator.utils;


import android.content.Context;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import caculator.bianfl.cn.abccaculator.R;

public class StyleUtil {
    /**
     * 更改字体风格
     *
     * @param context
     * @param root
     */
//    public static void applyFont(final Context context, final View root) {
//        try {
//            if (root instanceof ViewGroup) {
//                ViewGroup viewGroup = (ViewGroup) root;
//                for (int i = 0; i < viewGroup.getChildCount(); i++)
////                    applyFont(context, viewGroup.getChildAt(i));
//            } else if (root instanceof TextView)
//                ((TextView) root).setTypeface(FontType.getTypeFace(context));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 更改Dialog风格
     *
     * @param context
     * @param dialog
     */
    public static void allpyDialogStyle(Context context, AlertDialog dialog) {
        dialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        dialog.show();
        try {
            int tvid = context.getResources().getIdentifier("alertTitle", "id", "caculator.bianfl.cn.abccaculator");
            int btnid = context.getResources().getIdentifier("button1", "id", "android");
            View btn = dialog.findViewById(btnid);
            View view = dialog.findViewById(tvid);
            ((Button) btn).setTextColor(Color.RED);
            ((TextView) view).setTextColor(Color.RED);

        } catch (Exception e) {

        }

    }

    /**
     * 更改字体风格
     *
     * @param context
     * @param toast
     */
//    public static void allpyToastFont(Context context, Toast toast) {
//        try {
//            final int tvid = context.getResources().getIdentifier("message", "id", "android");
//        } catch (Exception e) {
//        }
//
//
//    }
}
