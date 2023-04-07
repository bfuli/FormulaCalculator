package caculator.bianfl.cn.abccaculator.utils;

import java.text.DecimalFormat;

/**
 * Created by 福利 on 2018/2/22.
 */

public class NumberFormat {
    /**
     * 将数字 字符串 按标准输出 111111111111111--->1.11111111E14
     * 仅限于整数位大于10位
     * @param t
     * @return
     */
    public static final String perfectFormat(String t) {
        DecimalFormat f1 = new DecimalFormat("#,###.#########");
        DecimalFormat f2 = new DecimalFormat("0.######E0");
        String temp;
        Double d = Double.parseDouble(t);
        if ((d-10000000000f) < 0){
            temp = f1.format(d);
            if (temp.equals("-0")){
                temp = String.valueOf(0);
            }
        }else{
            temp = f2.format(d);
        }
        return temp;
    }
    public static final String format(String s){
        DecimalFormat f = new DecimalFormat("#.#########");
        String temp = f.format(Double.parseDouble(s));
        if (temp.equals("-0")){
            temp = "0";
        }
        return temp;
    }
}
