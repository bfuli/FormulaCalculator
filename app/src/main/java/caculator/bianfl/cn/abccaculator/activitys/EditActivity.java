package caculator.bianfl.cn.abccaculator.activitys;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;

import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.beans.MathObject;
import caculator.bianfl.cn.abccaculator.utils.CommonActions;
import caculator.bianfl.cn.abccaculator.utils.Constants;
import caculator.bianfl.cn.abccaculator.utils.SQLHelper;
import caculator.bianfl.cn.abccaculator.utils.StringUtils;
import caculator.bianfl.cn.abccaculator.utils.StyleUtil;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;

public class EditActivity extends AppCompatActivity implements View.OnClickListener,Constants{

    private EditText tv_expression;
    private String result = "";
    private String formulaName = "默认";
    private String expression = "";

    private StringBuffer sb_expression = new StringBuffer();
    private String[] unknowns;//保存未知数的数组
    private Button[] btns;//保存动态生成的按钮
    private TableRow tr_unknow1,tr_unknow2;

    private Button btn_equal;
//    private TextView tv_toolbar;
    private String countResult = "";//保存本次计算的原始结果，方便下次计算

    private MathObject mathObject;
    private CommonActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        actions = new CommonActions(this);
        actions.immerse();//沉浸式状态栏
        actions.finish();//点击返回键推出界面

        Bundle bundle = getIntent().getExtras();
        initMathObject(bundle);//将传进来的数据封装成 MathObject
        initView();
        initCaculator();
        FormulaActivity.setUser2home(false);
        //修改字体
//        StyleUtil.applyFont(this,findViewById(R.id.act_root));
    }

    private void initMathObject(Bundle bundle) {//int id,String result,String unknown,String expression,String formulaName
        mathObject = new MathObject(
                bundle.getInt(SQLHelper.ID),
                bundle.getString(SQLHelper.Result),
                bundle.getString(SQLHelper.Unknown),
                bundle.getString(SQLHelper.Expression),
                bundle.getString(SQLHelper.FormulaName)
        );
    }

    //初始化控件
    private void initView() {
        ImageButton btn_del = (ImageButton) findViewById(R.id.btn_del);
//        tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);
        tv_expression = (EditText) findViewById(R.id.tv_expression);
        btn_equal = (Button) findViewById(R.id.btn_equal);
        tr_unknow1 = (TableRow) findViewById(R.id.tr_unknow1);
        tr_unknow2 = (TableRow) findViewById(R.id.tr_unknow2);

        btn_equal.setOnClickListener(this);//计算
        btn_del.setOnClickListener(this);
    }

    //准备接收 输入 和计算
    private final void initCaculator() {
        //清空输入公式的字符串
        sb_expression = new StringBuffer();
        //将所有的输入显示控件恢复至初始状态
        clearAll();
        unknowns = mathObject.getUnknown().split(",|，");//未知数 数组
        result = mathObject.getResult();//结果表示变量
        expression = mathObject.getExpression();//计算表达式
        //添加控件
        addView(unknowns);
        //初始化 tv_expression算数字符串
        sb_expression.append(expression);
        if (!result.equals("")) {
            tv_expression.setText(result + "=" + expression);
        }
        actions.setTitle("公式编辑：" + mathObject.getFormulaName());
    }

    /**
     * *将所有的输入显示控件恢复至初始状态
     */
    private final void clearAll() {
        result = "";
        unknowns = null;
        tv_expression.setText("");
        if (btns == null) return;
        for (int m = 0; m < 5; m++) {
            btns[m].setVisibility(View.GONE);
        }
    }

    //根据未知数数量显示Button
    private void addView(String[] unknowns) {
        btns = new Button[VariableMaxNum];
        Button btn_x1 = (Button) findViewById(R.id.btn_x1);
        Button btn_x2 = (Button) findViewById(R.id.btn_x2);
        Button btn_x3 = (Button) findViewById(R.id.btn_x3);
        Button btn_x4 = (Button) findViewById(R.id.btn_x4);
        Button btn_x5 = (Button) findViewById(R.id.btn_x5);
        Button btn_x6 = (Button) findViewById(R.id.btn_x6);
        Button btn_x7 = (Button) findViewById(R.id.btn_x7);
        Button btn_x8 = (Button) findViewById(R.id.btn_x8);
        Button btn_x9 = (Button) findViewById(R.id.btn_x9);
        Button btn_x10 = (Button) findViewById(R.id.btn_x10);
        btns[0] = btn_x1;
        btns[1] = btn_x2;
        btns[2] = btn_x3;
        btns[3] = btn_x4;
        btns[4] = btn_x5;
        btns[5] = btn_x6;
        btns[6] = btn_x7;
        btns[7] = btn_x8;
        btns[8] = btn_x9;
        btns[9] = btn_x10;
        for (int m = 0; m < VariableMaxNum; m++) {
            btns[m].setVisibility(View.GONE);
        }
        //j 表示用户输入的未知数与最大允许未知数中的较小值
        int j = Math.min(VariableMaxNum, unknowns.length);
        if (mathObject.getUnknown().equals("") || unknowns == null || unknowns.length == 0) {
            return;
        }
        tr_unknow1.setVisibility(View.VISIBLE);
        if (j > 5){
            tr_unknow2.setVisibility(View.VISIBLE);
        }
        TableRow.LayoutParams layout;
        for (int i = 0; i < j; i++) {
            btns[i].setVisibility(View.VISIBLE);
            btns[i].setText(unknowns[i]);
            layout = (TableRow.LayoutParams) (btns[i].getLayoutParams());
            if (i == j-1 || i == 4){
                layout.rightMargin = 0;
            }else{
                layout.rightMargin = CaculateActivity.getPx();
            }
            btns[i].setLayoutParams(layout);
        }
        //逐个button添加监听
        for (int i = 0; i < btns.length; i++) {
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appends(((Button) view).getText().toString());
                    showString(sb_expression.toString());
                }
            });
        }
    }

    //键盘按键Button点击事件
    public void btnOnClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sin:
                appends("sin(");
                break;
            case R.id.btn_chengfang:
                appends("^");
                break;
            case R.id.btn_kaifang:
                appends("√");
                break;
            case R.id.btn_pi:
                appends("π");
                break;
            case R.id.btn_clear:
                sb_expression = new StringBuffer();
                break;
            case R.id.btn_cos:
                appends("cos(");
                break;
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
            case R.id.btn_zuokuo:
            case R.id.btn_youkuo:
            case R.id.btn_e:
                appends(((Button) v).getText().toString());
                break;
            case R.id.btn_tan:
                appends("tan(");
                break;
            case R.id.btn_add:
                avoidTwo();
                appends("+");
                break;
            case R.id.btn_ln:
                appends("ln(");
                break;
            case R.id.btn_minus:
                avoidTwo();
                appends("-");
                break;
            case R.id.btn_lg:
                appends("lg(");
                break;
            case R.id.btn_mult:
                avoidTwo();
                appends("×");
                break;
            case R.id.btn_asin:
                appends("asin(");
                break;
            case R.id.btn_dot:
                appends(".");
                break;
            case R.id.btn_div:
                avoidTwo();
                appends("÷");
                break;
            case R.id.btn_acos:
                appends("acos(");
                break;
            case R.id.btn_atan:
                appends("atan(");
                break;
            case R.id.btn_ans:
                appends(countResult);
                break;
            case R.id.btn_ok://确定保存表达式
                save();
                break;
        }
        showString(sb_expression.toString());
    }
    /**
     * 防止出现基本运算符重复
     */
    private final void avoidTwo() {
        sb_expression = StringUtils.avoidTwo(sb_expression);
    }

    private void appends(String s) {
        sb_expression = StringUtils.appends(sb_expression,s);
    }

    //返回结尾未知数的位置
    private final int endsWithUnKnown(String s) {
       return StringUtils.endsWithUnKnown(unknowns,s);
    }

    //判断表达式中左括号和右括号是否相等，避免低级错误
    private final boolean isKuoEqual(String str) {
        char ch;
        int left = 0;
        int right = 0;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (ch == '(') {
                left++;
            } else if (ch == ')') {
                right++;
            }
        }
        return left == right;
    }

    //保存计算表达式
    private final void save() {
        //计算表达式为空，直接返回
        if (sb_expression.toString().equals("") || result.equals("")) {
            return;
        }
        //表达式左右括号不相等，提示并返回
        if (!isKuoEqual(sb_expression.toString())) {
            ToastUtil.showToast(EditActivity.this, "左右括号数不相等，请仔细检查" + ToastUtil.ENJOY_WUNAI);
            return;
        }
        //经过前面的检测没有问题，保存
        View edit = LayoutInflater.from(this).inflate(R.layout.dialog_editname, null);
        final EditText et_editname = (EditText) edit.findViewById(R.id.et_editname);
        et_editname.setText(mathObject.getFormulaName());
        final AlertDialog dialog = new AlertDialog.Builder(this).
                setCancelable(false).
                setTitle("保存").
                setView(edit).
                setPositiveButton("保存", null).
                setNegativeButton("取消", null).
                create();
        StyleUtil.allpyDialogStyle(this, dialog);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et_editname.getText().toString().trim();
                if (text.equals("")) {
                    ToastUtil.showToast(EditActivity.this, "请输入公式名字");
                    return;
                }
                formulaName = text;
                mathObject.setFormulaName(formulaName);
                //更新数据库
                SQLHelper sqlHelper = new SQLHelper(EditActivity.this);
                SQLiteDatabase db = sqlHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(SQLHelper.Expression,sb_expression.toString());
                values.put(SQLHelper.FormulaName,formulaName);
                db.update(SQLHelper.TableName,values,"id=?",new String[]{String.valueOf(mathObject.getId())});
                db.close();
                //公式保存成功，改变状态为 算数计算状态
                ToastUtil.showToast(EditActivity.this, "更新成功" + ToastUtil.ENJOY_HAPPY);
                actions.setTitle("公式编辑：" + mathObject.getFormulaName());
                dialog.dismiss();
            }
        });
    }

    private final void showString(String s) {
        if (result.equals("")) {
            tv_expression.setText(s);
            tv_expression.setSelection(s.length());
        } else {
            tv_expression.setText(result + "=" + s);
            tv_expression.setSelection((result + "=" + s).length());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_del:
                if (sb_expression.length() > 0 ) {
                    String sS = sb_expression.toString();
                    if (sS.endsWith("asin(") || sS.endsWith("acos(") || sS.endsWith("atan(")) {
                        deletes(5);
                    } else if (sS.endsWith("sin(") || sS.endsWith("cos(") || sS.endsWith("tan(")) {
                        deletes(4);
                    } else if (sS.endsWith("lg(") || sS.endsWith("ln(")) {
                        deletes(3);
                    } else if (endsWithUnKnown(sS) != -1) {
                        deletes(unknowns[endsWithUnKnown(sS)].length());
                    } else {
                        deletes(1);
                    }
                    showString(sb_expression.toString());
                }
                break;
        }
    }

    private void deletes(int i) {
        sb_expression = StringUtils.deletes(sb_expression,i);
    }

}
