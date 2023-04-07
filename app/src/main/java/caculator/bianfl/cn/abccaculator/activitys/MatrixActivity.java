package caculator.bianfl.cn.abccaculator.activitys;

import android.content.ClipboardManager;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.text.DecimalFormat;

import Jama.Matrix;
import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.utils.CommonActions;
import caculator.bianfl.cn.abccaculator.utils.NumberFormat;
import caculator.bianfl.cn.abccaculator.utils.StyleUtil;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;
import caculator.bianfl.cn.abccaculator.views.MatrixEditText;

public class MatrixActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener{
    private MatrixEditText et_matrix1, et_matrix2;
    private TextView tv_matrix1, tv_matrix2;
    private AppCompatSpinner spinner1, spinner2, spinner3;
    private HorizontalScrollView hsv_1, hsv_2;
    private Button btn_cclt1, btn_cclt2, btn_cclt3,btn_copy1,btn_copy2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);
        CommonActions actions = new CommonActions(this);
        actions.immerse();//沉浸式状态栏
        actions.finish();//点击返回键推出界面
        actions.setTitle("矩阵运算");
        //初始化控件
        initViews();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        //弹出数字键盘 并限制输入
        String digists = "0123456789./-π()'\n '";
        KeyListener keyListener = DigitsKeyListener.getInstance(digists);
        et_matrix1 = (MatrixEditText) findViewById(R.id.et_matrix1);
        et_matrix2 = (MatrixEditText) findViewById(R.id.et_matrix2);
        tv_matrix1 = (TextView) findViewById(R.id.tv_matrix1);
        tv_matrix2 = (TextView) findViewById(R.id.tv_matrix2);
        spinner1 = (AppCompatSpinner) findViewById(R.id.spinner1);
        spinner2 = (AppCompatSpinner) findViewById(R.id.spinner2);
        spinner3 = (AppCompatSpinner) findViewById(R.id.spinner3);
        hsv_1 = (HorizontalScrollView) findViewById(R.id.hsv_1);
        hsv_2 = (HorizontalScrollView) findViewById(R.id.hsv_2);
        btn_cclt1 = (Button) findViewById(R.id.btn_cclt1);
        btn_cclt2 = (Button) findViewById(R.id.btn_cclt2);
        btn_cclt3 = (Button) findViewById(R.id.btn_cclt3);
        btn_copy1 = (Button) findViewById(R.id.btn_copy1);
        btn_copy2 = (Button) findViewById(R.id.btn_copy2);

        et_matrix1.setKeyListener(keyListener);
        et_matrix2.setKeyListener(keyListener);
        //计算矩阵按钮监听
        btn_cclt1.setOnClickListener(this);
        btn_cclt2.setOnClickListener(this);
        btn_cclt3.setOnClickListener(this);
        btn_copy1.setOnClickListener(this);
        btn_copy2.setOnClickListener(this);

        et_matrix1.setOnTouchListener(this);
        et_matrix2.setOnTouchListener(this);
    }
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    /**
     * 控件点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cclt1:
                try {
                    tv_matrix1.setText(getMResult(spinner1, new Matrix(et_matrix1.getArray())));
                    hsv_1.scrollTo(hsv_1.getChildAt(0).getWidth(), 0);
                } catch (Exception e) {
                    ToastUtil.showToast(this, "矩阵不正确"+ToastUtil.ENJOY_WUNAI);
                    tv_matrix1.setText("");
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cclt2:
                try {
                    tv_matrix2.setText(getMResult(spinner2, new Matrix(et_matrix2.getArray())));
                    hsv_2.scrollTo(hsv_2.getChildAt(0).getWidth(), 0);
                } catch (Exception e) {
                    ToastUtil.showToast(this, "矩阵不正确"+ToastUtil.ENJOY_WUNAI);
                    tv_matrix2.setText("");
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cclt3:
                /*
                <item>0 A+B</item>
                <item>1 A-B</item>
                <item>2 A×B</item>
                <item>3 A/B</item>
                <item>4 B-A</item>
                <item>5 B×A</item>
                <item>6 B/A</item>
                 */
                try {
                    String r = "";
                    Matrix A = new Matrix(et_matrix1.getArray());
                    Matrix B = new Matrix(et_matrix2.getArray());
                    switch (spinner3.getSelectedItemPosition()) {
                        case 0://A+B
                            r = MtoString(A.plus(B));
                            break;
                        case 1://A-B
                            r = MtoString(A.minus(B));
                            break;
                        case 2://A*B
                            r = MtoString(A.times(B));
                            break;
                        case 3://A/B
                            r = MtoString(B.solve(A));
                            break;
                        case 4://B-A
                            r = MtoString(B.minus(A));
                            break;
                        case 5://B*A
                            r = MtoString(B.times(A));
                            break;
                        case 6://B/A
                            r = MtoString(A.solve(B));
                            break;
                    }
                    final String finalR = r;
                    AlertDialog dialog = new AlertDialog.Builder(this).
                            setMessage(r).
                            setPositiveButton("确认",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setNeutralButton("复制", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            copyToClipboard(finalR);
                        }
                    }).create();
                    StyleUtil.allpyDialogStyle(this,dialog);
                    hsv_1.scrollTo(0, 0);
                    hsv_2.scrollTo(0, 0);
                } catch (Exception e) {
                    ToastUtil.showToast(this, "矩阵不正确"+ToastUtil.ENJOY_WUNAI);
                    e.printStackTrace();
                }
                break;
            case R.id.btn_copy1:
                //复制计算结果到剪切板
                copyToClipboard(tv_matrix1);
                break;
            case R.id.btn_copy2:
                //复制计算结果到剪切板
                copyToClipboard(tv_matrix2);
                break;
        }
    }

    /**
     * 复制计算结果到剪切板
     */
    private final void copyToClipboard(TextView tv){
        copyToClipboard(tv.getText().toString());
    }
    /**
     * 复制计算结果到剪切板
     */
    private final void copyToClipboard(String s){
        ClipboardManager clipboardManager2 = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager2.setText(s);
        ToastUtil.showToast(this, "复制成功"+ToastUtil.ENJOY_HAPPY);
    }
    /**
     * 根据Spinner的选择获取计算之后的字符串
     * @param spinner
     * @param m       需要计算的矩阵
     * @return
     */
    private final String getMResult(AppCompatSpinner spinner, Matrix m) {
        /*
        <item>0转置</item>
        <item>1求逆</item>
        <item>2平方</item>
        <item>3立方</item>
        <item>4行列式</item>
        <item>5条件数</item>
        <item>6矩阵秩</item>
         */
        String ms = null;
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                ms = MtoString(m.transpose());
                break;
            case 1:
                ms = MtoString(m.inverse());
                break;
            case 2:
                ms = MtoString(m.times(m));
                break;
            case 3:
                ms = MtoString(m.times(m).times(m));
                break;
            case 4:
                ms = NumberFormat.format(String.valueOf(m.det()));
                break;
            case 5:
                ms = NumberFormat.format(String.valueOf(m.cond()));
                break;
            case 6:
                ms = NumberFormat.format(String.valueOf(m.rank()));
                break;
        }
        return ms;
    }

    /**
     * 将矩阵转换成字符串
     *
     * @param m 目标矩阵
     * @param n 转换的数据保留小数点后 n位
     * @return
     */
    private final String MtoString(Matrix m) {
        int n = 4;
        StringBuffer ss = new StringBuffer("#.");
        int te = Math.max(1, n);
        for (int i = 0; i < te; i++) {
            ss.append("#");
        }
        DecimalFormat format = new DecimalFormat(ss.toString());
        StringBuffer sb = new StringBuffer();
        for (int var4 = 0; var4 < m.getRowDimension(); ++var4) {
            for (int var5 = 0; var5 < m.getColumnDimension(); ++var5) {
                String var6 = format.format(m.getArray()[var4][var5]);
                int var7 = Math.max(1, 5 - var6.length());
                for (int var8 = 0; var8 < var7; ++var8) {
                    sb.append(' ');
                }
                sb.append(var6);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if ((v.getId() == R.id.et_matrix1) && canVerticalScroll((EditText) v)) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }
}
