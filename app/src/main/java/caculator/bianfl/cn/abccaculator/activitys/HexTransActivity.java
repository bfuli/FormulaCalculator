package caculator.bianfl.cn.abccaculator.activitys;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.math.BigInteger;

import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.utils.CommonActions;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;

public class HexTransActivity extends AppCompatActivity{

    private StringBuffer sb = new StringBuffer();
    private EditText et_toBinery, et_toOctal, et_toInter, et_toHex;
    private EditText[] edits;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hextrans);
        CommonActions actions = new CommonActions(this);
        actions.immerse();//沉浸式状态栏
        actions.finish();//点击返回键推出界面
        actions.setTitle("进制转换");
        initViews();
    }

    private void initViews() {
        et_toBinery = (EditText) findViewById(R.id.et_toBinery);
        et_toOctal = (EditText) findViewById(R.id.et_toOctal);
        et_toInter = (EditText) findViewById(R.id.et_toInter);
        et_toHex = (EditText) findViewById(R.id.et_toHex);
        edits = new EditText[4];
        et_toBinery.setTag(2);
        et_toOctal.setTag(8);
        et_toInter.setTag(10);
        et_toHex.setTag(16);
        edits[0] = et_toBinery;
        edits[1] = et_toOctal;
        edits[2] = et_toInter;
        edits[3] = et_toHex;
        editText = et_toBinery;
        setBC(et_toBinery);
        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            edits[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBC((EditText) v);
                }
            });
        }
    }

    private void setBC(EditText e) {
        editText = e;
        for (int i = 0; i < 4; i++) {
            edits[i].setBackgroundResource(R.drawable.et_bc_recangle_nofocus);
        }
        e.setBackgroundResource(R.drawable.et_bc_recangle_focused);
    }

    public void btnOnClick(View v) {
        sb = new StringBuffer(editText.getText());
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
            case R.id.btn_a:
                sb.append("a");
                break;
            case R.id.btn_b:
                sb.append("b");
                break;
            case R.id.btn_c:
                sb.append("c");
                break;
            case R.id.btn_d:
                sb.append("d");
                break;
            case R.id.btn_eb:
                sb.append("e");
                break;
            case R.id.btn_f:
                sb.append("f");
                break;
            case R.id.btn_del:
                if (sb.length() > 0) {
                    sb.delete(sb.length() - 1, sb.length());
                    editText.setText(sb.toString());
                }
                break;
            case R.id.btn_clear:
                sb = new StringBuffer();
                for (int i = 0; i < 4; i++) {
                    edits[i].setText("");
                }
                break;
        }
        editText.setText(sb.toString());
        try {
            StringBuffer sb = new StringBuffer(editText.getText());
            if (sb.length() > 0) {
                String text = editText.getText().toString();
                BigInteger bi = new BigInteger(text, (int) editText.getTag());
                for (int i = 0; i < 4; i++) {
                    if (edits[i] != editText) {
                        edits[i].setText(bi.toString((Integer) edits[i].getTag()));
                    }
                    edits[i].setSelection(edits[i].getText().length());
                }

            } else {
                for (int i = 0; i < 4; i++) {
                    edits[i].setText("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(this, "出错啦"+ToastUtil.ENJOY_SAD);
        }
    }
}
