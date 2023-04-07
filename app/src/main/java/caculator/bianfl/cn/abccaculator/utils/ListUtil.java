package caculator.bianfl.cn.abccaculator.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import caculator.bianfl.cn.abccaculator.beans.MathObject;

/**
 * Created by 福利 on 2016/10/22.
 */
public class ListUtil {
    public static final List<MathObject> getList(Context context) {
        SQLHelper sqlHelper = new SQLHelper(context);
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        List<MathObject> list = sqlHelper.query(db);
        db.close();
        return list;
    }
}
