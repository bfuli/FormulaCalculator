package caculator.bianfl.cn.abccaculator.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caculator.bianfl.cn.abccaculator.utils.CommonActions;
import caculator.bianfl.cn.abccaculator.utils.Constants;
import caculator.bianfl.cn.abccaculator.utils.ListUtil;
import caculator.bianfl.cn.abccaculator.beans.MathObject;
import caculator.bianfl.cn.abccaculator.utils.Caculator;
import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.utils.NumberFormat;
import caculator.bianfl.cn.abccaculator.utils.SQLHelper;
import caculator.bianfl.cn.abccaculator.utils.StyleUtil;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;

public class FormulaActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, View.OnClickListener ,Constants{

    private String[] unknowns;
    private String expression;
    private String formulaName;
    private String result;
    private StringBuffer sb;
    private int index = 0;
    private int maxNum;
    private List<MathObject> list;

    private LinearLayout ln_input;
    private TextView tv_calculate_result;
    private TextView tv_toolbar;
    private TextView tv_expression;
    private AlertDialog dialog_choose;
    private EditText[] edits;
    private EditText editText_focused ;
    private String[] inputs;

    private CommonActions actions;
    private static boolean user2home = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formula);
        actions = new CommonActions(this);
        actions.immerse();//沉浸式状态栏
        actions.finish();//点击返回键推出界面
        actions.setTitle("公式计算");

        index = this.getSharedPreferences("index", MODE_PRIVATE).getInt("index", 0);//获取的公式的下标
        if (index < 0) index = 0;
        initView();
        initCalculator(index);
        //更改字体
//        StyleUtil.applyFont(this, findViewById(R.id.ac_main_root));
    }

    /**
     * 初始化控件，并根据未知数的个数动态加载EditText
     */
    private void initView() {
        ln_input = (LinearLayout) findViewById(R.id.ln_input);
        tv_expression = (TextView) findViewById(R.id.tv_expression);
        tv_calculate_result = (TextView) findViewById(R.id.tv_caculate_result);
        tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);
        ImageButton btn_del = (ImageButton) findViewById(R.id.btn_del);
        tv_calculate_result.setText("");
        tv_expression.setOnClickListener(this);
        btn_del.setOnLongClickListener(v -> {
            sb = new StringBuffer();
            if (editText_focused != null) editText_focused.setText(sb);
            return false;
        });
    }
    private void initCalculator(int index){
        initString(index);
        addView();
        setString();
    }
    //加载指定的计算式
    private void initString(int in) {
        MathObject mathObject = getMathObj(in);
        if (mathObject != null) {
            if (TextUtils.isEmpty(mathObject.getResult())) {
                result = "Reslut";
            } else {
                result = mathObject.getResult();
            }
            unknowns = mathObject.getUnknown().split(",");
            expression = mathObject.getExpression();
            formulaName = mathObject.getFormulaName();
            if (formulaName != null && tv_toolbar  != null){
                actions.setTitle("公式计算：" + formulaName);
            }
        }
    }
    private void addView() {
        ln_input.removeAllViews();
        if (unknowns == null || (unknowns.length == 1 && unknowns[0].equals(""))) {
            return;
        }
        maxNum = Math.min(unknowns.length, VariableMaxNum);
        edits = new EditText[maxNum];
        inputs = new String[maxNum];
        for (int i = 0; i < maxNum; i++) {
            TextInputLayout textInputLayout = new TextInputLayout(ln_input.getContext());
            final EditText editText = new EditText(textInputLayout.getContext());
            editText.setInputType(InputType.TYPE_NULL);//禁止弹出键盘
            editText.setCursorVisible(true);
            editText.setTag(i);//设置唯一标记
            editText.setHint("请输入变量" + unknowns[i] + "的值");
            textInputLayout.addView(editText);
            ln_input.addView(textInputLayout);
            edits[i] = editText;
        }
    }
    //给TextView设置计算式
    private void setString() {
        tv_calculate_result.setText("");
        if (!(result == null && expression == null)) {
            tv_expression.setText(result + "=" + expression);
        } else {
            tv_expression.setText("");
        }
        if (edits == null || inputs == null){
            return;
        }
        for (int i = 0; i < edits.length; i++) {
            edits[i].setText(inputs[i]);
        }
        edits[0].requestFocus();
        edits[0].setFocusable(true);
        editText_focused = edits[0];
    }

    /**
     * 键盘按钮点击事件
     *
     * @param v --键盘
     */
    public void btnOnClick(View v) {
        System.out.println("btnOnClick");
        if (ln_input == null || ln_input.getFocusedChild() == null) return;
        EditText editText_focused = ((TextInputLayout)ln_input.getFocusedChild()).getEditText();
//        if (child instanceof EditText){
//            editText_focused = (EditText) child;
//        }else {
//            return;
//        }
        sb = new StringBuffer(editText_focused.getText());
        switch (v.getId()) {
            case R.id.btn_0:
                sb.append("0");
                break;
            case R.id.btn_1:
                sb.append("1");
                break;
            case R.id.btn_2:
                sb.append("2");
                break;
            case R.id.btn_3:
                sb.append("3");
                break;
            case R.id.btn_4:
                sb.append("4");
                break;
            case R.id.btn_5:
                sb.append("5");
                break;
            case R.id.btn_6:
                sb.append("6");
                break;
            case R.id.btn_7:
                sb.append("7");
                break;
            case R.id.btn_8:
                sb.append("8");
                break;
            case R.id.btn_9:
                sb.append("9");
                break;
            case R.id.btn_dot:
                sb.append(".");
                break;
            case R.id.btn_divide:
                sb.append("/");
                break;
            case R.id.btn_minus:
                sb.append("-");
                break;
            case R.id.btn_del:
                if (sb.length() > 0) {
                    sb.delete(sb.length() - 1, sb.length());
                    editText_focused.setText(sb);
                }
                break;
            case R.id.btn_next:
                int tag = 0;
                if (editText_focused != null){
                    tag = (int) editText_focused.getTag() + 1;
                }
                tag = tag > maxNum - 1 ? 0 : tag;
                edits[tag].setFocusable(true);
                edits[tag].requestFocus();
                editText_focused = edits[tag];
                sb = new StringBuffer(editText_focused.getText());
                break;
            case R.id.btn_equal:
                if (unknowns == null ||
                        (unknowns.length == 1 && unknowns[0].equals("")) ||
                        tv_expression.getText().toString().equals(""))
                    break;//没有表达式不做计算
                if (ifHasNull()) {
                    //显示Toast
                    ToastUtil.showToast(FormulaActivity.this, "请输入所有变量的值" + ToastUtil.ENJOY_CUTE);
                } else {
                    for (int i = 0; i < edits.length; i++) {
                        String itemp = edits[i].getText().toString();
                        inputs[i] = itemp;
                    }
                    try {
                        //计算结果
                        caculate();
                    } catch (Exception e) {
                        tv_calculate_result.setText("error");
                        System.out.println("出错信息：" + e.toString());
                    }
                }
                break;
        }
        editText_focused.setText(sb);
    }

    public static void setUser2home(boolean b){
        user2home = b;
    }

    @Override
    protected void onRestart() {
        if (index < 0) {
            index = 0;
        }
        if (!user2home){//用户没有退回桌面，跳转至另一个Activity
            initCalculator(index);
        }
        super.onRestart();
    }


    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = getSharedPreferences("index", MODE_PRIVATE).edit();
        editor.putInt("index", index).commit();
        user2home = true;
        super.onPause();
    }

    private final MathObject getMathObj(int in) {
        list = ListUtil.getList(this);
        if (in - 1 > list.size()) {
            index = 0;
        }
        if (list != null && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    //判断EditText是否全为空
    private final boolean ifHasNull() {
        for (int i = 0; i < edits.length; i++) {
            if (TextUtils.isEmpty(edits[i].getText())) {
                return true;
            }
        }
        return false;
    }

    //点击计算 计算结果
    private final void caculate() {
        String re;
        String exp;
        //首先替换 系统 运算符 --> sin cos tan asin等
        exp = Caculator.replaceSysCode(expression);
        //替换用户未知数符号，将未知数符号替换为已知的数
        exp = replaceUserCoad(exp);
        //计算并格式化  保存小数点后十位
        boolean useRadian = getSharedPreferences("caculator.bianfl.cn.abccaculator_preferences", Context.MODE_PRIVATE).
                getBoolean("uses_radian", false);
        re = NumberFormat.perfectFormat(Caculator.Caculate(exp, useRadian));
        tv_calculate_result.setText(result + "=" + re);
    }

    /**
     * 按字符长度由大到小排序，再替换用户字符
     */
    private final String replaceUserCoad(String s) {
        String exp = s;
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < maxNum; i++) {
            map.put(unknowns[i], inputs[i]);
        }
        List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());
        //按字符长度由大到小排序
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o2.getKey().length() - o1.getKey().length();
            }

        });
        //替换用户字符

        for (int i = 0; i < list.size(); i++) {
            exp = exp.replaceAll(list.get(i).getKey(), "(" + list.get(i).getValue() + ")");
        }
        return exp;
    }

    //显示选择对话框
    private final void showDialog() {
        if (list == null || list.size() == 0) return;
        final View view = getLayoutInflater().inflate(R.layout.dialog_lv, null);
        ListView listView = view.findViewById(R.id.lv_choose);
        dialog_choose = new AlertDialog.Builder(this).
                setCancelable(true).
                setNegativeButton("取消", null).
                setView(view).
                setTitle("选择公式").create();
        //更改dialog字体
        StyleUtil.allpyDialogStyle(this, dialog_choose);

        listView.setAdapter(adapter);
        //选项单击事件，选择该项
        listView.setOnItemClickListener(this);
        //选项长按事件，删除该项
        listView.setOnItemLongClickListener(this);
    }

    //ListView适配器
    ListAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.lv_content, null);
                viewHolder.textView = view.findViewById(R.id.tv_choose);
                viewHolder.tv_formulaName = view.findViewById(R.id.tv_formulaName);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textView.setText(list.get(i).getResult() + "=" + list.get(i).getExpression());
            viewHolder.tv_formulaName.setText(list.get(i).getFormulaName());
            return view;
        }

        class ViewHolder {
            TextView textView;
            TextView tv_formulaName;
        }
    };

    /**
     * ListView长按点击事件：弹出是否删除对话框，处理删除或不删除事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog dialog = new AlertDialog.Builder(FormulaActivity.this).setCancelable(false).setTitle("选择操作").
                setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int n) {
                        SQLHelper sqlHelper = new SQLHelper(FormulaActivity.this);
                        SQLiteDatabase db = sqlHelper.getWritableDatabase();
                        sqlHelper.deleteById(db,list.get(position).getId());
                        db.close();
                        list.remove(position);
                        index--;
                        if (index < 0) {
                            index = 0;
                        }
                        initCalculator(index);
                        ((BaseAdapter) adapter).notifyDataSetChanged();
                    }
                }).
                setNegativeButton("取消", null).
                setNeutralButton("编辑", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MathObject mathObject = list.get(position);
                        Intent intent = new Intent(FormulaActivity.this,EditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(SQLHelper.Expression,mathObject.getExpression());
                        bundle.putString(SQLHelper.Unknown,mathObject.getUnknown());
                        bundle.putString(SQLHelper.FormulaName,mathObject.getFormulaName());
                        bundle.putString(SQLHelper.Result,mathObject.getResult());
                        bundle.putInt(SQLHelper.ID,mathObject.getId());
                        intent.putExtras(bundle);
                        if (dialog_choose != null) dialog_choose.dismiss();
                        startActivity(intent);
                    }
                }).create();
        //更改dialog字体
        StyleUtil.allpyDialogStyle(this, dialog);
        return true;
    }

    /**
     * Listview单击事件：选中当前的计算公式
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        index = position;
        initCalculator(index);
        dialog_choose.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_expression:
                showDialog();
                break;
        }
    }
}
