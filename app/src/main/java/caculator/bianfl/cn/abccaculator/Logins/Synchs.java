package caculator.bianfl.cn.abccaculator.Logins;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import caculator.bianfl.cn.abccaculator.beans.MathObject;
import caculator.bianfl.cn.abccaculator.utils.SQLHelper;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 福利 on 2017/11/29.
 * 从云端同步数据
 */

public class Synchs {

    /**
     * 同步数据至云端
     * @param objectId
     * @param list
     * @param listener
     */
    public static void synchs(String objectId,List<MathObject> list,UpdateListener listener){
        MyUser user = new MyUser();
        user.setFormulas(toJsonString(list));
        user.update(objectId,listener);
    }
    private static String toJsonString(List<MathObject> list){
        JSONArray array = new JSONArray();
        for (MathObject mo: list ) {
            JSONObject jo = new JSONObject();
            try {
                jo.put(SQLHelper.Result,mo.getResult());
                jo.put(SQLHelper.Unknown,mo.getUnknown());
                jo.put(SQLHelper.Expression,mo.getExpression());
                jo.put(SQLHelper.FormulaName,mo.getFormulaName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(jo);
        }
        return array.toString();
    }
    public static List<MathObject> toList(String json){
        List<MathObject> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                MathObject mo = new MathObject();
                mo.setResult(object.getString(SQLHelper.Result));
                mo.setExpression(object.getString(SQLHelper.Expression));
                mo.setFormulaName(object.getString(SQLHelper.FormulaName));
                mo.setUnknown(object.getString(SQLHelper.Unknown));
                list.add(mo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
}
