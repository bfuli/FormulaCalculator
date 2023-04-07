package caculator.bianfl.cn.abccaculator.Logins;

/**
 * Created by 福利 on 2017/11/28.
 */

public class CheckObj {
    private String message;
    private boolean isSuccess;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
