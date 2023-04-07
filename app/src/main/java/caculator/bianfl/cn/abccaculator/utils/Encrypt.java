package caculator.bianfl.cn.abccaculator.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 福利 on 2017/11/28.
 * 加密工具
 */

public class Encrypt {
    /**
     * 加密
     * @param str 要加密的字符
     * @return 返回加密结果
     */
    public static String encrypt(String str){
        MessageDigest digest;
        byte[] bt;
        try {
            digest = MessageDigest.getInstance("SHA");
            bt = digest.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return str;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bt.length; i++) {
            int val = ((int) bt[i]) & 0xff;
            if (val < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }
}
