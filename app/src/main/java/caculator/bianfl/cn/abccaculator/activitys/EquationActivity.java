package caculator.bianfl.cn.abccaculator.activitys;

import android.content.Context;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.equation.Equation;
import caculator.bianfl.cn.abccaculator.equation.EquationFactory;
import caculator.bianfl.cn.abccaculator.utils.Caculator;
import caculator.bianfl.cn.abccaculator.utils.CommonActions;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;

public class EquationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Equation equation;

    private TextView tv_equation_expressions, tv_equation_result;
    private LinearLayout ll_add;
    private EditText[] ets;

    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equation);
        CommonActions actions = new CommonActions(this);
        actions.immerse();//沉浸式状态栏
        actions.finish();//点击返回键推出界面
        actions.setTitle("解方程");
        initViews();
        initEquation();
    }

    /**
     * 初始化方程界面
     */
    private void initEquation() {
        equation = EquationFactory.createEquation(selectedPosition);
        addViews();
        initDate();
    }

    private void initDate() {
        tv_equation_result.setText("");
        tv_equation_expressions.setText(equation.getExpression());
    }

    private void initViews() {
        tv_equation_expressions = (TextView) findViewById(R.id.tv_equation_expressions);
        tv_equation_result = (TextView) findViewById(R.id.tv_equation_result);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);

        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.sp_equation_kinds);
        spinner.setOnItemSelectedListener(this);
        Button btn_solve_equation = (Button) findViewById(R.id.btn_solve_equation);
        btn_solve_equation.setOnClickListener(this);
    }

    /**
     * 根据方程的系数添加输入项
     */
    private void addViews() {
        ll_add.removeAllViews();
        final int quotietyNum = equation.getQuotietyNum();
        CharSequence[] qlabels = equation.getQuotietyLabels();
        ets = new EditText[quotietyNum];
        View.OnKeyListener keyBoradListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_ENTER) {
                    return false;
                }
                if (event.getAction() != KeyEvent.ACTION_UP){//判断当按键释放时才执行下面的动作，防止事件执行两次
                    return false;
                }
                for (int i = 0; i < quotietyNum; i++) {
                    if (ets[i] == v) {
                        if (i == quotietyNum-1){
                            /*
                            先隐藏输入法
                             */
                            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            input.hideSoftInputFromWindow(EquationActivity.this.getCurrentFocus().getWindowToken(),0);

                            solveEquation();//如果最后一个获取焦点，计算解方程
                        }else{
                            EditText et_focus = ets[i+1];
                            et_focus.requestFocus();//获取焦点
                            et_focus.setSelection(et_focus.getText().length());
                        }
                        break;
                    }
                }
                return true;
            }
        };
        String digists = "0123456789./-'\n '";
        KeyListener keyListener = DigitsKeyListener.getInstance(digists);
        LinearLayout ll = new LinearLayout(this);
        int mod;
        if (quotietyNum > 3) {
            mod = 2;
        } else {
            mod = 1;
        }
        LinearLayout.LayoutParams params;
        for (int i = 0; i < quotietyNum; i++) {
            if (i != 0 && (i % mod) == 0) {
                ll_add.addView(ll);
                ll = new LinearLayout(this);
            }
            ll.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(this);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            tv.setText(qlabels[i]);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.weight = 1f;
            tv.setLayoutParams(params);
            tv.setTextColor(Color.parseColor("#0ea8e3"));

            EditText et = new EditText(this);
            et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            et.setSingleLine();
            et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setMinWidth(200);
            et.setKeyListener(keyListener);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 4f;
            params.gravity = Gravity.LEFT;
            et.setLayoutParams(params);
            et.setOnKeyListener(keyBoradListener);
            ets[i] = et;
            ll.addView(tv);
            ll.addView(et);
        }
        ll_add.addView(ll);
    }

    private void setData() {
        int len = ets.length;
        double[] ds = new double[len];
        String text;
        for (int i = 0; i < len; i++) {
            text = ets[i].getText().toString();
            if (text.contains("/")){
                text = Caculator.Caculate(text,false);
            }
            ds[i] = Double.parseDouble(text);
        }
        equation.setQuotietys(ds);
    }

    /**
     * 返回输入框是否有空项
     *
     * @param ets
     * @return
     */
    public boolean hasNull(EditText[] ets) {
        for (EditText et : ets) {
            if (TextUtils.isEmpty(et.getText())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;
        initEquation();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_solve_equation:
                solveEquation();
                break;
        }
    }

    /**
     * 解方程组
     *
     */
    private final void solveEquation() {
        if (hasNull(ets)) {
            ToastUtil.showToast(this, "系数不能为空");
            return;
        }
        try {
            setData();
            tv_equation_result.setText(equation.getResults());
        } catch (Exception e) {
            tv_equation_result.setText("出错" + e.getMessage());
        }
    }
}
