package caculator.bianfl.cn.abccaculator.Logins;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caculator.bianfl.cn.abccaculator.utils.Encrypt;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 福利 on 2017/11/27.
 */

public class UserManager {
    private static Context context = Bmob.getApplicationContext();
    private static final String LastTime_Login = "ssdf_fett_kc_wx";
    private static final String UserName = "username",
            PW_Question = "pw_question",PW_Answer = "pw_anser",
            ObjectID = "objectId";

    public static void login(final String username, final String password, final SaveListener<MyUser> listener) {
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo(UserName,username);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e != null){//登陆失败
                    listener.done(null,e);
                    return;
                }
                if (list == null || list.size() == 0){
                    e = new BmobException("该用户未注册");
                    listener.done(null,e);
                    return;
                }
                MyUser user = list.get(0);
                if (!password.equals(user.getPassword())){//密码不一致，登陆失败
                   e = new BmobException("密码错误");
                    listener.done(null,e);
                    return;
                }
                write2Cache(user);//写入当前用户缓存，代表登陆成功
                setLoginTime(System.currentTimeMillis());//写入当前登陆时间
                listener.done(user,e);
            }
        });
    }
    public static void signUp(MyUser user,SaveListener<String> listener){//注册
        user.save(listener);
    }
    public static void logout(){//退出登陆
        context.getSharedPreferences("bmob_sp",Context.MODE_PRIVATE).edit().remove("user").commit();//清空缓存
    }

    public static void write2Cache(MyUser user) {
        if (user == null){
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put(ObjectID, user.getObjectId());
            obj.put(UserName,user.getUsername());
            obj.put(PW_Question,user.getPw_question());
            if (context == null){
                return;
            }
            logout();
            context.getSharedPreferences("bmob_sp",Context.MODE_PRIVATE).edit().putString("user",obj.toString()).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static MyUser getCurrentCache(){
        if (context == null){
            return null;
        }
        String obj = context.getSharedPreferences("bmob_sp",Context.MODE_PRIVATE).getString("user",null);
        if (obj == null){
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(obj);
            MyUser user = new MyUser();
            user.setUsername(jsonObject.getString(UserName));
            user.setPw_question(jsonObject.getString(PW_Question));
            user.setObjectId(jsonObject.getString(ObjectID));
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 根据username查找到 id,password 比对密码 根据ID更新密码
     * @param user
     * @param listener
     */
    public static void update(String objectID,final MyUser user, final UpdateListener listener){
        user.update(objectID,listener);
    }
    public static void setLoginTime(long time){
        context.getSharedPreferences("bmob_sp",Context.MODE_PRIVATE).edit().putLong(LastTime_Login,time).commit();
    }

    public static long getLastLoginTime() {
        return context.getSharedPreferences("bmob_sp",Context.MODE_PRIVATE).getLong(LastTime_Login,0l);
    }
}
