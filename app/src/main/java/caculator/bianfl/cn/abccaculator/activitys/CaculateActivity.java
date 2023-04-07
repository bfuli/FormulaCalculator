package caculator.bianfl.cn.abccaculator.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caculator.bianfl.cn.abccaculator.Logins.LoginActivity;
import caculator.bianfl.cn.abccaculator.Logins.MyUser;
import caculator.bianfl.cn.abccaculator.Logins.UserManager;
import caculator.bianfl.cn.abccaculator.beans.MathObject;
import caculator.bianfl.cn.abccaculator.utils.Caculator;
import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.utils.Constants;
import caculator.bianfl.cn.abccaculator.utils.NumberFormat;
import caculator.bianfl.cn.abccaculator.utils.SQLHelper;
import caculator.bianfl.cn.abccaculator.utils.StringUtils;
import caculator.bianfl.cn.abccaculator.utils.StyleUtil;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;
import caculator.bianfl.cn.abccaculator.utils.UpdateUtils;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;

public class CaculateActivity extends AppCompatActivity implements View.OnClickListener,
        TextWatcher ,NavigationView.OnNavigationItemSelectedListener,Constants{

    private EditText tv_expression, et_unknown;
    private TextView tv_user;
    private EditText tv_caculate_history;
    private String resultTag = "";
    private String formulaName = "默认";
    private boolean isFirstTime;
    private boolean isFirstTimeAdd;
    private EditText tv_result;
    private ImageView ima_call_input_dialog;
    private TableRow tr_unknow1,tr_unknow2;
    private NavigationView navigationView;

    private StringBuffer sb_expression = new StringBuffer();//保存计算式
    private String[] unknowns;//保存未知数的数组
    private Button[] btns;//保存动态生成的按钮
    private long lastUpdateTime;
    private Button btn_equal;
    private String CauResult = "";//计算结果
    private String pushContent;

    private boolean HASPRESSED_EQUAL = false;//记录是否已经按下了等于号
    private boolean CACULATE_ISNUMBER = true;//CACULATE_ISNUMBER为true说明当前进行算数运算，为false说明当前正在输入公式
    private boolean needAppend = true;

    private long startMills, endMills;
    private boolean firstTimeDown = true;
    private StringBuffer sbhistory;
    private boolean hasRead;

    private static PushMeHandler pmHandler;
//    private static final String AppId = "19fb56d94021afc6fe90873acde36f93";
    private static final String AppId = "280216cca2d1eae356d609c1c94cc61";
    private String pushUrl = "https://bfuli.github.io/PrivacyPolicy_Calculators.html";
    private static int px;//保存键盘按钮右边距的值
    private TableRow.LayoutParams param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_navigation);
        Bmob.initialize(this, AppId);//Bmob后端云初始化
        initData();//初始化数据
        initView();//初始化控件

//        initPush();//初始化消息推送
        long timemill = System.currentTimeMillis();
        if ((timemill - lastUpdateTime) > 20 * 3600 * 1000l) {//根据一定的事件间隔检查更新 20小时
//            PermissionUtils permissionUtils = new PermissionUtils(this);
//            if (permissionUtils.hasPermission()) {
            checkUpdate(false);
//            }
        }
        SharedPreferences.Editor editor = getSharedPreferences("isFirstTime", MODE_PRIVATE).edit();
        editor.putLong("lastUpdateTime", timemill).commit();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        isFirstTime = getSharedPreferences("isFirstTime", MODE_PRIVATE).getBoolean("isFirstTime", true);
        isFirstTimeAdd = getSharedPreferences("isFirstTime", MODE_PRIVATE).getBoolean("isFirstTimeAdd", true);
        lastUpdateTime = getSharedPreferences("isFirstTime", MODE_PRIVATE).getLong("lastUpdateTime", 0l);
        pushContent = getSharedPreferences("isFirstTime", MODE_PRIVATE).getString("pushContent", "");
        hasRead = getSharedPreferences("isFirstTime", MODE_PRIVATE).getBoolean("hasRead", true);
    }

    /**
     * 初始化推送消息
     * 使用推送服务时的初始化操作
     */
    private void initPush() {//
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if (e == null) {
                    System.out.println(bmobInstallation.getObjectId() + ":" + bmobInstallation.getInstallationId());
                } else {
                    System.out.println(e.getMessage());
                }
            }
        });
        if (pmHandler == null) {
            pmHandler = new PushMeHandler();
        }
        // 启动推送服务
//        BmobPush.startWork(this);
        if (isFirstTime) {
            onRecivePush(pushUrl);
        }
    }

    public static PushMeHandler getPmHandler() {
        return pmHandler;
    }

    private void checkUpdate(boolean notify) {
        UpdateUtils updateUtils = new UpdateUtils(this);
        updateUtils.checkUpdate(notify);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && isFirstTime) {
            showHelp("温馨提示");
            isFirstTime = false;
            getSharedPreferences("isFirstTime", MODE_PRIVATE).edit().putBoolean("isFirstTime", isFirstTime).commit();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    //初始化控件
    private void initView() {
        initPx();//初始化 1dp = ？px
        ImageButton btn_del = (ImageButton) findViewById(R.id.btn_del);
        ima_call_input_dialog = (ImageView) findViewById(R.id.ima_call_input_dialog);
        TextView tv_help = (TextView) findViewById(R.id.tv_help);
        tv_expression = (EditText) findViewById(R.id.tv_expression);
        tv_result = (EditText) findViewById(R.id.tv_result);
        tv_caculate_history = (EditText) findViewById(R.id.tv_caculate_history);
        btn_equal = (Button) findViewById(R.id.btn_equal);

        tr_unknow1 = (TableRow) findViewById(R.id.tr_unknow1);
        tr_unknow2 = (TableRow) findViewById(R.id.tr_unknow2);
        //计算
        btn_equal.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        tv_help.setOnClickListener(this);
        tv_caculate_history.setMovementMethod(ScrollingMovementMethod.getInstance());
        ima_call_input_dialog.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.rl_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.setItemIconTintList(null);//设置显示图标本来的颜色
        //根据用户登录信息初始化导航栏头部信息
        userWelcome();
        setNotice(!hasRead);
    }
    private void initPx(){
        Button b = (Button) findViewById(R.id.btn_0);
        param = (TableRow.LayoutParams) b.getLayoutParams();
        px = param.rightMargin;

    }

    public static int getPx() {
        return px;
    }

    /**
     * 根据用户登录信息初始化导航栏头部信息
     */
    private void userWelcome() {
        if (tv_user == null){
            tv_user = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_user);
            tv_user.setOnClickListener(this);
        }

        long LastLoginTime = UserManager.getLastLoginTime();//如果不存在，0l
        long LongTime = System.currentTimeMillis() - LastLoginTime;
        MyUser user = UserManager.getCurrentCache();

        if (user == null) {//没有登陆
            tv_user.setText("点击登陆...");
        } else if(LongTime > LoginActivity.LoginDuration){//登录时间过期
            tv_user.setText("登陆时间过期，请重新登陆");
        } else {//已经登陆
            tv_user.setText("欢迎您，"+ user.getUsername());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        userWelcome();
    }

    //准备接收 输入 和计算
    private final void initCaculator(boolean isNumber) {
        //清空输入公式的字符串
        sb_expression = new StringBuffer();
        CauResult = "";
        HASPRESSED_EQUAL = false;
        //将所有的输入显示控件恢复至初始状态
        clearAll();
        if (!isNumber) {
            getUnknowns();
            //添加控件
            addView(unknowns);
            //初始化 tv_expression算数字符串
            if (!resultTag.equals("")) {
                tv_expression.setText(resultTag + "=");
            }
            ima_call_input_dialog.setImageResource(R.mipmap.ima_cacel);
        } else {
            ima_call_input_dialog.setImageResource(R.mipmap.im_input);
        }
    }

    /**
     * *将所有的输入显示控件恢复至初始状态
     */
    private final void clearAll() {
        tr_unknow1.setVisibility(View.GONE);
        tr_unknow2.setVisibility(View.GONE);
        resultTag = "";
        unknowns = null;
        tv_result.setText("");
        tv_expression.setText("");
        tv_caculate_history.setText("");
        tv_caculate_history.setVisibility(View.GONE);
        if (btns == null) return;
        for (int m = 0; m < btns.length; m++) {
            btns[m].setVisibility(View.GONE);
        }
    }

    private final void getUnknowns() {
        //生成包括结果在内的所有的未知数数组
        String[] temps = et_unknown.getText().toString().trim().split(",|，");
        resultTag = temps[0];
        //未知数数组
        unknowns = new String[temps.length - 1];
        for (int i = 1; i < temps.length; i++) {
            unknowns[i - 1] = temps[i];
        }
    }

    //根据未知数数量显示Button
    private void addView(String[] unk) {
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
        if (et_unknown.getText().toString().equals("") || unk == null || unk.length == 0) {
            return;
        }
        //j 表示用户输入的未知数与最大允许未知数中的较小值
        int j = Math.min(VariableMaxNum, unk.length);
        tr_unknow1.setVisibility(View.VISIBLE);
        if (j > 5){
            tr_unknow2.setVisibility(View.VISIBLE);
        }
        TableRow.LayoutParams layout;
        for (int i = 0; i < j; i++) {
            btns[i].setVisibility(View.VISIBLE);
            btns[i].setText(unk[i]);
            layout = (TableRow.LayoutParams) (btns[i].getLayoutParams());
            if (i == j-1 || i == 4){
                layout.rightMargin = 0;
            }else{
                layout.rightMargin = px;
            }
            btns[i].setLayoutParams(layout);
        }
        //逐个button添加监听
        for (int i = 0; i < btns.length; i++) {
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sb_expression = StringUtils.appends(sb_expression, ((Button) view).getText().toString());
                    showString(sb_expression.toString());
                }
            });
        }
    }

    //限制输入
    private final String stringFilter(String str) {
        StringBuffer sb = new StringBuffer("[(^√π");
        sb.append(Caculator.sin).append(Caculator.cos).append(Caculator.tan).
                append(Caculator.atan).append(Caculator.asin).append(Caculator.acos).
                append(Caculator.lg).append(Caculator.ln).append("e)]");
        String regex = sb.toString();//"[(^√πacstCSTgne)]"
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("").trim();
    }

    //键盘按键Button点击事件
    public void btnOnClick(View v) {
        if (HASPRESSED_EQUAL) {
            sb_expression = new StringBuffer();
            tv_caculate_history.setVisibility(View.VISIBLE);
            if (!tv_caculate_history.getText().toString().equals(""))
                tv_caculate_history.append("\n");
            tv_caculate_history.append(sbhistory.toString());
            tv_caculate_history.setSelection(tv_caculate_history.getText().length());
            HASPRESSED_EQUAL = false;
        }
        switch (v.getId()) {
            case R.id.btn_sin:
                sb_expression = StringUtils.appends(sb_expression, "sin(");
                break;
            case R.id.btn_chengfang:
                sb_expression = StringUtils.appends(sb_expression, "^");
                break;
            case R.id.btn_kaifang:
                sb_expression = StringUtils.appends(sb_expression, "√");
                break;
            case R.id.btn_pi:
                sb_expression = StringUtils.appends(sb_expression, "π");
                break;
            case R.id.btn_clear:
                needAppend = false;
                String temp = CauResult;
                initCaculator(CACULATE_ISNUMBER);
                CauResult = temp;
                break;
            case R.id.btn_cos:
                sb_expression = StringUtils.appends(sb_expression, "cos(");
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
                sb_expression = StringUtils.appends(sb_expression, ((Button) v).getText().toString());
                break;
            case R.id.btn_tan:
                sb_expression = StringUtils.appends(sb_expression, "tan(");
                break;
            case R.id.btn_add:
                addLast_result();
                avoidTwo();//防止出现基本运算符重复
                sb_expression = StringUtils.appends(sb_expression, "+");
                break;
            case R.id.btn_ln:
                sb_expression = StringUtils.appends(sb_expression, "ln(");
                break;
            case R.id.btn_minus:
                addLast_result();
                avoidTwo();//防止出现基本运算符重复
                sb_expression = StringUtils.appends(sb_expression, "-");
                break;
            case R.id.btn_lg:
                sb_expression = StringUtils.appends(sb_expression, "lg(");
                break;
            case R.id.btn_mult:
                addLast_result();
                avoidTwo();//防止出现基本运算符重复
                sb_expression = StringUtils.appends(sb_expression, "×");
                break;
            case R.id.btn_asin:
                sb_expression = StringUtils.appends(sb_expression, "asin(");
                break;
            case R.id.btn_dot:
                sb_expression = StringUtils.appends(sb_expression, ".");
                break;
            case R.id.btn_div:
                addLast_result();
                avoidTwo();//防止出现基本运算符重复
                sb_expression = StringUtils.appends(sb_expression, "÷");
                break;
            case R.id.btn_acos:
                sb_expression = StringUtils.appends(sb_expression, "acos(");
                break;
            case R.id.btn_atan:
                sb_expression = StringUtils.appends(sb_expression, "atan(");
                break;
            case R.id.btn_ans:
                if (!CauResult.equals("error")){
                    sb_expression = StringUtils.appends(sb_expression, CauResult);
                }
                break;
            case R.id.btn_ok://确定保存表达式
                if (!CACULATE_ISNUMBER){
                    save();
                }
                break;
        }
        showString(sb_expression.toString());
    }


    //返回结尾未知数的位置
    private final int endsWithUnKnown(String s) {
        return StringUtils.endsWithUnKnown(unknowns, s);
    }

    //当作为普通计算器时，添加上次计算的结果
    private final void addLast_result() {
        if (sb_expression.length() == 0 && needAppend && !CauResult.equals("error")){
            sb_expression = StringUtils.addLast_result(sb_expression, CauResult);
        }
    }

    /**
     * 防止出现基本运算符重复
     */
    private final void avoidTwo() {
        sb_expression = StringUtils.avoidTwo(sb_expression);
    }

    /**
     * 计算
     * 返回计算结果
     */
    private final String caculateNum(String str) {
        sbhistory = new StringBuffer();
        try {
            boolean useRadian = getSharedPreferences("caculator.bianfl.cn.abccaculator_preferences", MODE_PRIVATE).
                    getBoolean("uses_radian", false);
            String temp = NumberFormat.format(Caculator.Caculate(replace(str), useRadian));
            if (temp.equals("∞")) {//计算结果为无穷大时，本次计算历史的记录为空
                CauResult = "";
            } else {
                CauResult = NumberFormat.perfectFormat(temp);//showResult-->格式化显示计算结果
            }
        } catch (Exception e) {
            CauResult = "error";
        }
        return CauResult;
    }

    /**
     * 替换格式化之后的字符 1E13，333,333,3
     *
     * @param str
     * @return
     */
    private final String replace(String str) {
        return str.replaceAll("E", "*10^").replaceAll(",", "");
    }

    //判断表达式中左括号和右括号是否相等，避免低级错误
    private final boolean isKuoEqual(String str) {
        char ch;
        int zuo = 0;
        int you = 0;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (ch == '(') {
                zuo++;
            } else if (ch == ')') {
                you++;
            }
        }
        return zuo == you;
    }

    //保存计算表达式
    private final void save() {
        //计算表达式为空，直接返回
        if (sb_expression.toString().equals("") || resultTag.equals("")) {
            return;
        }
        //表达式左右括号不相等，提示并返回
        if (!isKuoEqual(sb_expression.toString())) {
            ToastUtil.showToast(CaculateActivity.this, "左右括号数不相等，请仔细检查" + ToastUtil.ENJOY_WUNAI);
            return;
        }
        //经过前面的检测没有问题，保存
        View edit = LayoutInflater.from(this).inflate(R.layout.dialog_editname, null);
        final EditText et_editname = (EditText) edit.findViewById(R.id.et_editname);
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
                    ToastUtil.showToast(CaculateActivity.this, "请输入公式名字");
                    return;
                }
                formulaName = text;
                StringBuffer sb_unknown = new StringBuffer();
                for (String str : unknowns) {
                    sb_unknown.append(str).append(",");
                }
                int unKnownLength = Math.max(0, sb_unknown.toString().length() - 1);
                SQLHelper sqlHelper = new SQLHelper(CaculateActivity.this);
                SQLiteDatabase db = sqlHelper.getWritableDatabase();
                //String resultTag,String unknown,String sb_expression,String formulaName
                MathObject mathObject = new MathObject(resultTag,
                        sb_unknown.toString().substring(0, unKnownLength),
                        sb_expression.toString(),
                        formulaName);
                sqlHelper.insert(db, mathObject);
                db.close();
                //公式保存成功，改变状态为 算数计算状态
                CACULATE_ISNUMBER = true;
                initCaculator(CACULATE_ISNUMBER);
                ToastUtil.showToast(CaculateActivity.this, "添加成功" + ToastUtil.ENJOY_HAPPY);
                dialog.dismiss();
            }
        });
    }

    private final void showString(String s) {
        if (resultTag.equals("")) {
            tv_expression.setText(s);
            tv_expression.setSelection(s.length());
        } else {
            tv_expression.setText(resultTag + "=" + s);
            tv_expression.setSelection((resultTag + "=" + s).length());
        }
    }

    private final void showHelp(String title) {
        AlertDialog dialog = new AlertDialog.Builder(this).
                setCancelable(true).
                setTitle(title).
                setMessage("\t\t当您添加公式时，点击屏幕右上角的加号，然后输入所有的未知数，并用逗号分隔开(中英文均可)" +
                        "点击确定(屏幕右上角的加号变成叉号)，即可输入你自己的公式。(第一个未知数表示结果变量，如果你的公式是y=x+z，" +
                        "就需要输入y,x,z或y,z,x，但y必须在第一位)。" +
                        "\n\t\t公式输入完毕之后点击保存即可。这时系统会自动切换成普通计算器状态，如果您要再次创建公式只需重复以上步骤" +
                        "如果您中途需要普通的算数运算，点击右上角叉号即可切换成普通计算器模式").
                setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isFirstTime = false;
                        getSharedPreferences("isFirstTime", MODE_PRIVATE).edit().putBoolean("isFirstTime", isFirstTime).commit();
                        dialogInterface.dismiss();
                    }
                }).create();
        StyleUtil.allpyDialogStyle(this, dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_equal:
                String exp = sb_expression.toString();
                if (CACULATE_ISNUMBER && !exp.equals("")) {
                    String re = caculateNum(exp);
                    tv_result.setText(re);
                    tv_result.setSelection(re.length());
                    sbhistory.append(exp).append("=").append(re);
                    HASPRESSED_EQUAL = true;
                    needAppend = true;
                }
                break;
            case R.id.tv_help:
                showHelp("帮助");
                break;
            case R.id.btn_del:
                if (sb_expression.length() > 0 && !HASPRESSED_EQUAL) {
                    String sS = sb_expression.toString();
                    if (sS.endsWith("asin(") || sS.endsWith("acos(") || sS.endsWith("atan(")) {
                        sb_expression = StringUtils.deletes(sb_expression, 5);
                    } else if (sS.endsWith("sin(") || sS.endsWith("cos(") || sS.endsWith("tan(")) {
                        sb_expression = StringUtils.deletes(sb_expression, 4);
                    } else if (sS.endsWith("lg(") || sS.endsWith("ln(")) {
                        sb_expression = StringUtils.deletes(sb_expression, 3);
                    } else if (endsWithUnKnown(sS) != -1) {
                        sb_expression = StringUtils.deletes(sb_expression, unknowns[endsWithUnKnown(sS)].length());
                    } else {
                        sb_expression = StringUtils.deletes(sb_expression, 1);
                    }
                    showString(sb_expression.toString());
                }
                break;
            case R.id.ima_call_input_dialog:
                if (CACULATE_ISNUMBER) {
                    //第一次添加公式，提示未知数的输入规则
                    if (isFirstTimeAdd) {
                        showAttention();
                        return;
                    }
                    addFumular();//添加公式
                } else {
                    CACULATE_ISNUMBER = true;
                    initCaculator(CACULATE_ISNUMBER);
                }
                break;
            case R.id.tv_user:
                Intent intent = new Intent(CaculateActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 添加公式
     */
    private final void addFumular() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_input, null);
        et_unknown = view.findViewById(R.id.et_unknow);
        et_unknown.addTextChangedListener(CaculateActivity.this);
        final AlertDialog dialog = new AlertDialog.Builder(this).
                setCancelable(false).
                setView(view).
                setTitle("请输入所有的变量").
                setPositiveButton("确定", null).
                setNeutralButton("取消", null).create();
        StyleUtil.allpyDialogStyle(this, dialog);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //准备接收计算,记录当前为输入公式状态
                if (TextUtils.isEmpty(et_unknown.getText()) ||
                        et_unknown.getText().toString().split(",|，").length <= 1) {
                    ToastUtil.showToast(CaculateActivity.this, "请输入变量" + ToastUtil.ENJOY_CUTE);
                } else if (!checkedSuccess()) {
                    ToastUtil.showToast(CaculateActivity.this, "变量中含有非法字符！！！");
                } else {
                    CACULATE_ISNUMBER = false;
                    initCaculator(CACULATE_ISNUMBER);
                    dialog.dismiss();
                }
            }
            //检查输入变量是否合法
            private boolean checkedSuccess() {
                String s = et_unknown.getText().toString();
                //判断是否包含系统字符
                String[] chars = {"sin", "cos", "tan", "atan", "asin", "acos", "ln", "lg"};
                for (int i = 0; i < chars.length; i++) {
                    if (s.contains(chars[i])) {
                        return false;
                    }
                }
                //判断输入变量中是否有重复
                String[] sArray = s.split(",|，");
                Set<String> set = new HashSet<String>();
                for (String temp:sArray) {
                    set.add(temp);
                }
                if (set.contains("") || set.contains(null)){
                    return false;
                }
                if (set.size() < sArray.length){
                    return false;
                }
                return true;
            }
        });
    }

    private void showAttention() {
        final AlertDialog dialog = new AlertDialog.Builder(this).
                setCancelable(false).
                setTitle("注意！！！").
                setMessage("\t\t为了提升用户体验，此版本之后减少了对变量的规则限制（现在您可以输入asincos这样的变量）" +
                        "但是这样会导致变量中包含'sin','cos','asin'等运算符，" +
                        "导致公式在计算时分不清变量和运算符而出现错误，所以请您在选择变量时注意避免。" +
                        "\n\t\t另外当您选择了正确的变量之后，也要避免这些变量组合后包含系统运算符，" +
                        "例（公式：y=l×n,不可写成：y=ln;但是y=a×b可以写成y=ab）").
                setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isFirstTimeAdd = false;
                        SharedPreferences.Editor editor = getSharedPreferences("isFirstTime", MODE_PRIVATE).edit();
                        editor.putBoolean("isFirstTimeAdd", isFirstTimeAdd).commit();
                        addFumular();//添加公式
                    }
                }).create();
        StyleUtil.allpyDialogStyle(this, dialog);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (firstTimeDown) {
            startMills = System.currentTimeMillis();
            firstTimeDown = false;
            ToastUtil.showToast(this, "再次点击返回键退出");
            return;
        } else {
            endMills = System.currentTimeMillis();
            if ((endMills - startMills) > 2 * 1000) {
                startMills = System.currentTimeMillis();
                firstTimeDown = false;
                ToastUtil.showToast(this, "再次点击返回键退出");
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String editable = et_unknown.getText().toString();
        String str = stringFilter(editable.toString());
        if (!editable.equals(str)) {
            et_unknown.setText(str);
            et_unknown.setSelection(str.length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 接收到推送操作
     */
    public void onRecivePush(String content) {
        if (TextUtils.isEmpty(content)){
            return;
        }
        pushContent = content;
        getSharedPreferences("isFirstTime", MODE_PRIVATE).edit().putString("pushContent", pushContent).commit();
        setNotice(true);
    }


    /**
     * 申请权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            //获取到了权限
//            checkUpdate(true);
//            ToastUtil.showToast(this, "正在检查更新..." + ToastUtil.ENJOY_CUTE);
//        } else {
//            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        switch (id) {
            //去评分
            case R.id.nav_gomark:
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    ToastUtil.showToast(this, "启动应用商店失败" + ToastUtil.ENJOY_SAD);
                } finally {
                    break;
                }
                //检查更新
            case R.id.nav_update:
//                PermissionUtils permissionUtils = new PermissionUtils(this);
//                if (permissionUtils.hasPermission()) {
                    checkUpdate(true);
                    ToastUtil.showToast(this, "正在检查更新..." + ToastUtil.ENJOY_CUTE);
//                } else {
//                    permissionUtils.requestPermission();
//                }
                break;
            case R.id.nav_user:
                intent = new Intent(CaculateActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_pushrecive:
//            case R.id.tv_toast://通知
//                if (pushContent == null || pushContent.equals("")) {
//                    ToastUtil.showToast(this, "暂无通知");
//                } else {
//                    intent = new Intent(this, ShowPushActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("msg", pushContent);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    setNotice(false);
//                }
                intent = new Intent(this, ShowPushActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("msg", pushUrl);
                intent.putExtras(bundle);
                startActivity(intent);
                setNotice(false);
                break;
            //跳转矩阵运算
            case R.id.nav_matrix:
                intent = new Intent(this, MatrixActivity.class);
                startActivity(intent);
                break;
            //跳转添加公式 计算器
            case R.id.nav_formula:
                intent = new Intent();
                intent.setClass(this, FormulaActivity.class);
                startActivity(intent);
                break;
            //跳转进制转换
            case R.id.nav_hextrans:
                intent = new Intent(this, HexTransActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            //跳转解方程
            case R.id.nav_equation:
                intent = new Intent(this,EquationActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    /**
     * 设置是否显示通知
     * @param notice 值为true 菜单通知前图片显示红点提醒
     */
    private void setNotice(boolean notice) {
        hasRead = !notice;
        getSharedPreferences("isFirstTime", MODE_PRIVATE).edit().putBoolean("hasRead", hasRead).commit();
        if (navigationView == null)return;
        MenuItem item = navigationView.getMenu().getItem(4).getSubMenu().getItem(1);
        if (notice){
            item.setIcon(R.mipmap.ic_push_on);
        }else{
            item.setIcon(R.mipmap.ic_push_off);
        }
    }

    public class PushMeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            onRecivePush((String) msg.obj);
            super.handleMessage(msg);
        }
    }
}
