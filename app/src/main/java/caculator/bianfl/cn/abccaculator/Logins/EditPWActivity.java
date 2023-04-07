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

import java.util.List;
import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.utils.Encrypt;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class EditPWActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText et_newpassword,et_confirm_pw,et_answer, et_oldpassword,et_username;
    private AppCompatSpinner spinner;
    private static final int RESET_TAG = 0, UPDATE_TAG = 1;
    private Button btn_update,btn_reset;
    private TextView tv_toobar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String msg = getIntent().getStringExtra("msg");
        if ("reset".equals(msg)){
            setContentView(R.layout.activity_reset_pw);
            init1Views();//初始化重置密码 界面控件
        }else if("update".equals(msg)){
            setContentView(R.layout.activity_update_pw);
            init2Views();//初始化修改密码 界面控件
        }
        //沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private void init1Views() {//初始化 重置密码 界面控件
        btn_reset = (Button) findViewById(R.id.btn_reset);
        et_newpassword = (EditText) findViewById(R.id.et_newpassword);
        et_confirm_pw = (EditText) findViewById(R.id.et_confirm_pw);
        et_answer = (EditText) findViewById(R.id.et_answer);
        et_username = (EditText) findViewById(R.id.et_username);
        spinner = (AppCompatSpinner) findViewById(R.id.spn_safe_question);
        tv_toobar = (TextView) findViewById(R.id.tv_toolbar);
        tv_toobar.setText("忘记密码");

        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
    }

    private void init2Views() {//初始化 修改密码 界面控件
        btn_update = (Button) findViewById(R.id.btn_update);
        et_username = (EditText) findViewById(R.id.et_username);
        et_oldpassword = (EditText) findViewById(R.id.et_oldpassword);
        et_newpassword = (EditText) findViewById(R.id.et_newpassword);
        et_confirm_pw = (EditText) findViewById(R.id.et_confirm_pw);
        tv_toobar = (TextView) findViewById(R.id.tv_toolbar);
        tv_toobar.setText("修改密码");

        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        btn_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update://修改密码
                update_pw();
                break;
            case R.id.btn_reset://重置密码
                reset_pw();
                break;
            case R.id.img_back:
                EditPWActivity.this.finish();
                break;
        }
    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RESET_TAG:
                    btn_reset.setEnabled(true);
                    break;
                case UPDATE_TAG:
                    btn_update.setEnabled(true);
                    break;
            }
        }
    };

    /**
     * 重置密码
     */
    private void reset_pw() {
        final CheckObj obj = check(RESET_TAG);
        if (!obj.isSuccess()){//检查不通过
            ToastUtil.showToast(EditPWActivity.this,obj.getMessage());
            return;
        }
        btn_reset.setEnabled(false);
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username",et_username.getText().toString());
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                final Message msg = Message.obtain();
                msg.what = RESET_TAG;
                MyUser user;
                if (e != null){
                    ToastUtil.showToast(EditPWActivity.this,"出现错误"+e.getMessage());
                    mHandler.handleMessage(msg);
                    return;
                }
                if (list == null || list.size() == 0){//用户不存在
                    ToastUtil.showToast(EditPWActivity.this,"该用户不存在");
                    mHandler.handleMessage(msg);
                    return;
                }
                user = list.get(0);
                String[] choises = getResources().getStringArray(R.array.safequestions);
                if(!TextUtils.equals(choises[spinner.getSelectedItemPosition()],user.getPw_question())){
                    ToastUtil.showToast(EditPWActivity.this,"安全问题不正确");
                    mHandler.handleMessage(msg);
                    return;
                }
                if(!TextUtils.equals(et_answer.getText(),user.getPw_anser())){
                    ToastUtil.showToast(EditPWActivity.this,"安全问题的答案不正确");
                    mHandler.handleMessage(msg);
                    return;
                }
                user.setPassword(Encrypt.encrypt(et_newpassword.getText().toString()));
                UserManager.update(user.getObjectId(), user, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            ToastUtil.showToast(EditPWActivity.this,"密码重置成功,请重新登陆");
                            UserManager.logout();
                            Intent intent = new Intent(EditPWActivity.this,LoginActivity.class);
                            EditPWActivity.this.finish();
                            startActivity(intent);
                        }else{
                            mHandler.handleMessage(msg);
                            ToastUtil.showToast(EditPWActivity.this,"出现错误"+e.getMessage());
                        }
                    }
                });
            }
        });
    }

    /**
     * 修改密码
     */
    private void update_pw() {
        final CheckObj obj = check(UPDATE_TAG);
        if (!obj.isSuccess()){//检查不通过
            ToastUtil.showToast(EditPWActivity.this,obj.getMessage());
            return;
        }
        btn_update.setEnabled(false);
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username",et_username.getText().toString());
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                MyUser user;
                final Message msg = Message.obtain();
                msg.what = UPDATE_TAG;
                if (e != null){
                    ToastUtil.showToast(EditPWActivity.this,"出现错误"+e.getMessage());
                    mHandler.sendMessage(msg);
                    return;
                }
                if (list == null || list.size() == 0){//用户不存在
                    ToastUtil.showToast(EditPWActivity.this,"该用户不存在");
                    mHandler.sendMessage(msg);
                    return;
                }
                user = list.get(0);
                if(!Encrypt.encrypt(et_oldpassword.getText().toString()).equals(user.getPassword())){
                    ToastUtil.showToast(EditPWActivity.this,"原密码不正确");
                    mHandler.sendMessage(msg);
                    return;
                }
                user.setPassword(Encrypt.encrypt(et_newpassword.getText().toString()));
                UserManager.update(user.getObjectId(), user, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            ToastUtil.showToast(EditPWActivity.this,"密码修改成功,请重新登陆");
                            UserManager.logout();
                            Intent intent = new Intent(EditPWActivity.this,LoginActivity.class);
                            EditPWActivity.this.finish();
                            startActivity(intent);
                        }else{
                            mHandler.sendMessage(msg);
                            ToastUtil.showToast(EditPWActivity.this,"出现错误"+e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private CheckObj check(int tag){
        CheckObj checkObj = new CheckObj();
        if (tag == RESET_TAG){//重置密码 检查
            if (TextUtils.isEmpty(et_username.getText()) ||
                    TextUtils.isEmpty(et_newpassword.getText()) ||
                    TextUtils.isEmpty(et_confirm_pw.getText()) ||
                    TextUtils.isEmpty(et_answer.getText())){
                checkObj.setMessage("输入项不能为空");
                checkObj.setSuccess(false);
                return checkObj;
            }
            if (et_newpassword.getText().toString().length() < 6){
                checkObj.setSuccess(false);
                checkObj.setMessage("密码长度不能小于6位");
                return checkObj;
            }
            if (!TextUtils.equals(et_newpassword.getText(),et_confirm_pw.getText())){
                checkObj.setMessage("两次输入的密码不一致");
                checkObj.setSuccess(false);
                return checkObj;
            }
        }else{//修改密码检查
            if (TextUtils.isEmpty(et_username.getText()) ||
                    TextUtils.isEmpty(et_oldpassword.getText()) ||
                    TextUtils.isEmpty(et_newpassword.getText()) ||
                    TextUtils.isEmpty(et_confirm_pw.getText())){
                checkObj.setMessage("输入项不能为空");
                checkObj.setSuccess(false);
                return checkObj;
            }
            if (et_newpassword.getText().toString().length() < 6){
                checkObj.setSuccess(false);
                checkObj.setMessage("密码长度不能小于6位");
                return checkObj;
            }
            if (!TextUtils.equals(et_newpassword.getText(),et_confirm_pw.getText())){
                checkObj.setMessage("两次输入的密码不一致");
                checkObj.setSuccess(false);
                return checkObj;
            }
        }
        checkObj.setSuccess(true);
        checkObj.setMessage("检查通过");
        return checkObj;
    }
}
