package caculator.bianfl.cn.abccaculator.Logins;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.beans.MathObject;
import caculator.bianfl.cn.abccaculator.utils.Encrypt;
import caculator.bianfl.cn.abccaculator.utils.ListUtil;
import caculator.bianfl.cn.abccaculator.utils.SQLHelper;
import caculator.bianfl.cn.abccaculator.utils.ToastUtil;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_username, et_password;
    private MyUser user;
    private Button btn_login, btn_synchro, btn_load, btn_update_pw, btn_logout;//btn_load 查看云端数据
    private static final int SYNCHS_TAG = 0,LOAD_TAG = 1,LOGIN_TAG = 2;
    private long LastLoginTime ;
    public static final long LoginDuration = 30 * 24 * 3600 * 1000l;//登陆过期时间为30天

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LastLoginTime = UserManager.getLastLoginTime();//如果不存在，0l
        long LongTime = System.currentTimeMillis() - LastLoginTime;
        user = UserManager.getCurrentCache();
//        System.out.println("LastLoginTime:"+LastLoginTime);
//        System.out.println("LongTime:"+LongTime);
//        System.out.println("LoginDuration:"+LoginDuration);

        if (user == null) {//没有登陆
            setContentView(R.layout.activity_login);
            init1Views();
        } else if(LongTime > LoginDuration){//登录时间过期
            setContentView(R.layout.activity_login);
            init1Views();
            ToastUtil.showToast(this,"登录时间过期，请重新登陆");
        } else {//已经登陆
            setContentView(R.layout.welcome_page);
            init2Views();
        }
        //沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private void init2Views() {//用户中心
        TextView tv_welcome = (TextView) findViewById(R.id.tv_welcome);
        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        btn_synchro = (Button) findViewById(R.id.btn_synchro);
        btn_load = (Button) findViewById(R.id.btn_load);
        btn_update_pw = (Button) findViewById(R.id.btn_update_pw);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        TextView tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);

        tv_toolbar.setText("用户中心");
        img_back.setOnClickListener(this);
        btn_synchro.setOnClickListener(this);
        btn_load.setOnClickListener(this);
        btn_update_pw.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        if (user != null) {
            tv_welcome.setText("欢迎您，" + user.getUsername());
        }
    }

    private void init1Views() {//登陆页面
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        TextView tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);
        tv_toolbar.setText("登陆");
        btn_login = (Button) findViewById(R.id.btn_login);
        Button btn_2sign_up = (Button) findViewById(R.id.btn_2sign_up);
        Button btn_2reset_pw = (Button) findViewById(R.id.btn_2reset_pw);
        ImageView img_back = (ImageView) findViewById(R.id.img_back);

        btn_login.setOnClickListener(this);
        btn_2sign_up.setOnClickListener(this);
        btn_2reset_pw.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SYNCHS_TAG://备份
                    btn_synchro.setEnabled(true);
                    break;
                case LOAD_TAG://导入
                    btn_load.setEnabled(true);
                    break;
                case LOGIN_TAG://登陆
                    btn_login.setEnabled(true);
                    btn_login.setText("登陆");
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_2sign_up://跳转 注册
                intent = new Intent(LoginActivity.this, SignUpActivity.class);
                LoginActivity.this.finish();
                startActivity(intent);
                break;
            case R.id.btn_2reset_pw://忘记密码 跳转 重置密码
                intent = new Intent(LoginActivity.this, EditPWActivity.class);
                intent.putExtra("msg", "reset");
                LoginActivity.this.finish();
                startActivity(intent);
                break;
            case R.id.img_back:
                LoginActivity.this.finish();
                break;
            case R.id.btn_logout://注销
                UserManager.logout();
                reload();//重新加载
                break;
            case R.id.btn_load://导入云端数据
                MyUser local_user = UserManager.getCurrentCache();
                if (local_user == null){
                    ToastUtil.showToast(this,"请先登录");
                    return;
                }
                loadData();
                break;
            case R.id.btn_synchro://备份数据
                MyUser local_user1 = UserManager.getCurrentCache();
                if (local_user1 == null){
                    ToastUtil.showToast(this,"请先登录");
                    return;
                }
                backup();
                break;
            case R.id.btn_update_pw:
                intent = new Intent(LoginActivity.this, EditPWActivity.class);
                intent.putExtra("msg", "update");
                LoginActivity.this.finish();
                startActivity(intent);
                break;
        }
    }

    /**
     * 用户登陆
     */
    private void login() {
        CheckObj checkObj = check();
        boolean isSuccess = checkObj.isSuccess();
        if (!isSuccess) {//检查不通过
            ToastUtil.showToast(LoginActivity.this, checkObj.getMessage());
            return;
        }
        //检查通过 登陆
        MyUser user = new MyUser();
        final Message msg = Message.obtain();
        user.setUsername(et_username.getText().toString());
        user.setPassword(Encrypt.encrypt(et_password.getText().toString()));
        UserManager.login(et_username.getText().toString(),
                Encrypt.encrypt(et_password.getText().toString()),
                new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser user, BmobException e) {
                        if (e == null) {
                            ToastUtil.showToast(LoginActivity.this, "登录成功" + ToastUtil.ENJOY_HAPPY);
                            reload();
                        } else {
                            ToastUtil.showToast(LoginActivity.this, e.getMessage());
                            System.out.println(e.getMessage());
                            msg.what = LOGIN_TAG;
                            mHandler.sendMessage(msg);
                        }
                    }
                });
        btn_login.setEnabled(false);
        btn_login.setText("登录中...");
    }

    /**
     * 导入云端数据
     */
    private void loadData() {
        final MyUser local_user;
        final Message msg = Message.obtain();
        local_user = UserManager.getCurrentCache();
        if (local_user == null){
            ToastUtil.showToast(this,"请先登陆");
            return;
        }
        btn_load.setEnabled(false);
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.getObject(local_user.getObjectId(), new QueryListener<MyUser>() {
            @Override
            public void done(MyUser user, BmobException e) {
                msg.what = LOAD_TAG;
                if (e != null){
                    mHandler.sendMessage(msg);
                    ToastUtil.showToast(LoginActivity.this,"导入数据失败"+e.getMessage());
                    return;
                }
                if (!user.equals(local_user)){
                    ToastUtil.showToast(LoginActivity.this,"登陆信息失效，请重新登陆");
                    UserManager.logout();
                    reload();
                    return;
                }
                String json = user.getFormulas();
                if (json == null || json.equals("")){
                    mHandler.sendMessage(msg);
                    ToastUtil.showToast(LoginActivity.this,"没有需要导入的数据"+e.getMessage());
                    return;
                }
                List<MathObject> list_net = Synchs.toList(json);
                List<MathObject> list_local = ListUtil.getList(LoginActivity.this);
                SQLHelper sqlHelper = new SQLHelper(LoginActivity.this);
                SQLiteDatabase db = sqlHelper.getWritableDatabase();
                for (MathObject mo : list_net) {
                    if (!contains(list_local,mo)){
                        sqlHelper.insert(db,mo);
                    }
                }
                db.close();
                mHandler.sendMessage(msg);
                ToastUtil.showToast(LoginActivity.this,"导入数据成功");
            }
        });
    }

    /**
     * 备份本地数据
     */
    private void backup() {
        final Message msg = Message.obtain();
        MyUser local_user = UserManager.getCurrentCache();
        if (local_user == null){
            ToastUtil.showToast(this,"请先登陆");
            return;
        }
        List<MathObject> list = ListUtil.getList(this);
        if (list == null || list.size() == 0){
            ToastUtil.showToast(this,"没有需要备份的数据");
            return;
        }
        btn_synchro.setEnabled(false);
        msg.what = SYNCHS_TAG;
        Synchs.synchs(local_user.getObjectId(), list, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    ToastUtil.showToast(LoginActivity.this,"数据已备份至云端");
                }else{
                    ToastUtil.showToast(LoginActivity.this,"数据备份失败");
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    private void reload() {//重新加载
        Intent in = getIntent();
        LoginActivity.this.finish();
        startActivity(in);
    }
    /**
     * 返回制定列表是否包含指定项
     * @param list_local
     * @param mo
     * @return
     */
    private boolean contains(List<MathObject> list_local, MathObject mo) {
        if (list_local == null){
            return false;
        }
        int s = list_local.size();
        if (mo != null){
            for (int i = 0; i < s; i++) {
                if (list_local.get(i).equals(mo)){
                    return true;
                }
            }
        }else {
            for (int i = 0; i < s; i++) {
                if (list_local.get(i) == null){
                    return true;
                }
            }
        }
        return false;
    }
    private CheckObj check() {//登陆信息检查
        CheckObj checkObj = new CheckObj();
        if (et_username.getText().toString().trim().equals("")) {
            checkObj.setSuccess(false);
            checkObj.setMessage("请输入用户名");
            return checkObj;
        }
        if (et_password.getText().toString().equals("")) {
            checkObj.setSuccess(false);
            checkObj.setMessage("请输入密码");
            return checkObj;
        }
        checkObj.setSuccess(true);
        checkObj.setMessage("检查通过");
        return checkObj;
    }
}
