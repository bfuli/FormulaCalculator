package caculator.bianfl.cn.abccaculator.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import caculator.bianfl.cn.abccaculator.activitys.CaculateActivity;
import cn.bmob.push.PushConstants;

/**
 * Created by 福利 on 2017/10/11.
 */

public class PushMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String con = intent.getStringExtra("msg");
            String content = null;
            try {
                JSONObject json = new JSONObject(con);
                content = json.getString("alert");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (content != null){
                Message msg = Message.obtain();
                msg.obj = content;
                CaculateActivity.getPmHandler().sendMessage(msg);
//                System.out.println("收到通知:"+content);
            }

        }
    }
}
