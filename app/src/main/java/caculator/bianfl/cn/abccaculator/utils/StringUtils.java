package caculator.bianfl.cn.abccaculator.utils;

/**
 * Created by 福利 on 2017/12/17.
 */

public class StringUtils {
    /**
     * 向表达式末尾添加字符串
     *
     * @param string
     */
    public static final StringBuffer appends(StringBuffer sb_expression, String string) {
        int len = sb_expression.length();
        if (".".equals(string)) {
            if (len == 0) {
                sb_expression.append("0");
            }else if(sb_expression.toString().endsWith(".")){
                sb_expression = deletes(sb_expression, 1);
            } else {
                char ch = sb_expression.charAt(len - 1);
                String s = String.valueOf(ch);
                if (s.matches("[^0-9]")) {
                    sb_expression.append("0");
                }
            }
        }

        if (sb_expression.toString().equals("0") && !".".equals(string)) {//sb_expression等于0，且要添加的不是"."
            sb_expression = deletes(sb_expression, 1);
        }

        sb_expression.append(string);
        return sb_expression;
    }

    /**
     * 根据长度从末尾删除
     *
     * @param lenth
     */
    public static final StringBuffer deletes(StringBuffer sb_expression, int lenth) {
        sb_expression.delete(sb_expression.length() - lenth, sb_expression.length());
        return sb_expression;
    }

    /**
     * 防止出现基本运算符重复
     */
    public static final StringBuffer avoidTwo(StringBuffer sb_expression) {
        String t = sb_expression.toString();
        if (t != null && t.length() > 0 && (t.endsWith("+") ||
                t.endsWith("-") || t.endsWith("×") || t.endsWith("÷"))) {
            sb_expression = deletes(sb_expression, 1);
        }
        return sb_expression;
    }

    //当作为普通计算器时，添加上次计算的结果
    public static final StringBuffer addLast_result(StringBuffer sb_expression, String CauResult) {
        return StringUtils.appends(sb_expression, CauResult);
    }

    public static final int endsWithUnKnown(String[] unknowns, String s) {
        if (unknowns != null && unknowns.length > 0) {
            for (int i = 0; i < unknowns.length; i++) {
                if (s.endsWith(unknowns[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
}
