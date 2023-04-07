package caculator.bianfl.cn.abccaculator.Logins;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import caculator.bianfl.cn.abccaculator.beans.MathObject;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by 福利 on 2017/11/27.
 * 对用户信息进行封装
 */

public class MyUser extends BmobObject {
    private String formulas;
    private String username, password;
    private String pw_question, pw_anser;

    public String getFormulas() {
        return formulas;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setFormulas(String formulas) {
        this.formulas = formulas;
    }

    public void setPw_question(String pw_question) {
        this.pw_question = pw_question;
    }

    public void setPw_anser(String pw_anser) {
        this.pw_anser = pw_anser;
    }

    public String getPw_question() {
        return pw_question;
    }

    public String getPw_anser() {
        return pw_anser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null){
            return false;
        }
        if (o instanceof MyUser){
            MyUser user = (MyUser) o;
            return (this.getUsername().equals(user.getUsername()) &&
                    this.getPw_question().equals(user.getPw_question()) &&
                    this.getObjectId().equals(user.getObjectId()));

        }
        return false;
    }
}
