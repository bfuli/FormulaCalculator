package caculator.bianfl.cn.abccaculator.Logins;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.utils.Encrypt;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText et_username,et_password,et_confirm_pw ,et_answer;
    private Button btn_sign_up;
    private AppCompatSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        initViews();
    }

    private void initViews() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confirm_pw = (EditText) findViewById(R.id.et_confirm_pw);
        et_answer = (EditText) findViewById(R.id.et_answer);
        spinner = (AppCompatSpinner) findViewById(R.id.spn_safe_question);

        btn_sign_up = (Button) findViewById(R.id.btn_sign_up);
        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        TextView tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);
        tv_toolbar.setText("注册");

        btn_sign_up.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sign_up:
                signUp();
                break;
            case R.id.img_back:
                SignUpActivity.this.finish();
                break;
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            btn_sign_up.setEnabled(true);
        }
    };
    private void signUp() {
        CheckObj checkObj = check();
        boolean isSucess = checkObj.isSuccess();
        if (!isSucess){
            ToastUtil.showToast(this,checkObj.getMessage());
            return;
        }
        btn_sign_up.setEnabled(false);
        String[] choises = getResources().getStringArray(R.array.safequestions);
        MyUser user = new MyUser();
        user.setUsername(et_username.getText().toString());
        user.setPassword(Encrypt.encrypt(et_password.getText().toString()));
        user.setPw_question(choises[spinner.getSelectedItemPosition()]);
        user.setPw_anser(et_answer.getText().toString().trim());
        UserManager.signUp(user, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    ToastUtil.showToast(SignUpActivity.this,"注册成功！");
                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(intent);
                    SignUpActivity.this.finish();
                }else {
                    mHandler.sendEmptyMessage(0);
                    int code = e.getErrorCode();
                    System.out.println("注册出错："+e.toString());
                    if (code == 401){//用户名重复
                        ToastUtil.showToast(SignUpActivity.this,"该用户已存在，请更换用户名或直接登陆");
                        return;
                    }
                    if (code ==9016){//无网路
                        ToastUtil.showToast(SignUpActivity.this,"无网路连接");
                        return;
                    }
                    ToastUtil.showToast(SignUpActivity.this,"未知原因出错");
                }
            }
        });
    }

    private CheckObj check() {
        CheckObj checkObj = new CheckObj();
        if (TextUtils.isEmpty(et_username.getText()) ||
                TextUtils.isEmpty(et_password.getText()) ||
                TextUtils.isEmpty(et_confirm_pw.getText()) ||
                TextUtils.isEmpty(et_answer.getText())){
            checkObj.setSuccess(false);
            checkObj.setMessage("输入项不能为空");
            return checkObj;
        }
        if(!et_username.getText().toString().matches("[a-zA-Z0-9\\u4e00-\\u9fa5]*?")){
            checkObj.setSuccess(false);
            checkObj.setMessage("用户名不合法");
            return checkObj;
        }
        if (et_password.getText().toString().length() < 6){
            checkObj.setSuccess(false);
            checkObj.setMessage("密码长度不能小于6位");
            return checkObj;
        }
        if (!et_password.getText().toString().equals(et_confirm_pw.getText().toString())){
            checkObj.setMessage("两次密码不一致");
            checkObj.setSuccess(false);
            return checkObj;
        }
        checkObj.setSuccess(true);
        checkObj.setMessage("信息核对成功");
        return checkObj;
    }
}
