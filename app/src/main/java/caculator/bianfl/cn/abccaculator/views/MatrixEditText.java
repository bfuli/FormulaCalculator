package caculator.bianfl.cn.abccaculator.views;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.EditText;

import caculator.bianfl.cn.abccaculator.utils.Caculator;

/**
 * Created by 福利 on 2017/1/17.
 */
public class MatrixEditText extends EditText {
    private int totalLines = 0;
    private int hang, lie;
    private Layout layout;

    public MatrixEditText(Context context) {
        super(context);
    }

    public MatrixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatrixEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }

//    @Override
//    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
//        super.onTextChanged(text, start, lengthBefore, lengthAfter);
//        if (!text.toString().equals(""))init();
//    }

//    private void init() {
//        if (layout == null) layout = getLayout();
//        totalLines = layout.getLineCount();
//    }

//    private int getTotalLines() {
//        if (getText().toString().equals("")) return 0;
//        return totalLines;
//    }

    /**
     * 将EditText的文字转换成二维数组
     */
    public double[][] getArray() throws Exception {
        String temp;
        if (layout == null) layout = getLayout();
        if (getText().toString().equals("")) throw new NullPointerException("the inputs is null");
        hang = layout.getLineCount();
        //j -->包括空格，换行符在内的总行数
        int j = hang;
        for (int i = 0; i < j; i++) {
            temp = getTextAtLine(i);
            if (temp == null || temp.trim().equals("")) {
                hang = hang - 1;
            }
        }
        //hang-->去除了空格和换行符 剩下的纯数字在内的行数
        double[][] d = new double[hang][];
        int htemp = 0;
        for (int i = 0; i < j; i++) {
            temp = getTextAtLine(i);
            if (temp != null && !temp.trim().equals("")) {
                String[] s = temp.trim().split("\\s+");
                d[htemp] = toDoubleArray(s);
                htemp = htemp + 1;
            }
        }
        return d;
    }

    private double[] toDoubleArray(String[] s) throws Exception {
        double[] d = new double[s.length];
        for (int i = 0; i < s.length; i++) {
            if (s[i].contains("/")) {
                d[i] = Double.parseDouble(Caculator.Caculate(s[i],false));
            } else {
                d[i] = Double.parseDouble(s[i]);
            }
        }
        return d;
    }


    private String getTextAtLine(int line) {
        if (layout == null) layout = getLayout();
        if (line < 0 || line > hang - 1) return "";
        int start = layout.getLineStart(line);
        int end = layout.getLineEnd(line);
        String s = layout.getText().toString();
        if (!s.equals("")) {
            return s.substring(start, end).trim();
        }
        return "";
    }
}
