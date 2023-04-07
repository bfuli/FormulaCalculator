package caculator.bianfl.cn.abccaculator.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import caculator.bianfl.cn.abccaculator.beans.MathObject;

/**
 * Created by 福利 on 2016/9/20.
 */
public class SQLHelper extends SQLiteOpenHelper {
    private static final String SName = "MathExpression";
    public static final String Result = "result";
    public static final String ID = "id";
    public static final String Unknown = "unknown";
    public static final String Expression = "expression";
    public static final String FormulaName = "formulaname";
    private static final int Version = 2;
    private static final String DBName = "Expre";
    public static final String TableName = "MathExpression";

    public SQLHelper(Context context) {
        this(context, DBName, null, Version);
    }

    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //初次创建表
        String sql = "create table " + TableName + " ( id integer primary key autoincrement ," +
                " result text Not NULL," +
                " unknowns text NOT NULL," +
                " expression text NOT NULL," +
                " formulaname text NOT NULL )";
        db.execSQL(sql);
    }

    public void insert(SQLiteDatabase db,MathObject mathObject) {
        insert(db,TableName, mathObject);
    }

    public void insert(SQLiteDatabase db ,String name, MathObject mathObject) {
        db.execSQL("insert into " + name + "(result,unknowns,expression,formulaname) values(?,?,?,?)",
                new String[]{mathObject.getResult(),mathObject.getUnknown(),
                        mathObject.getExpression(),mathObject.getFormulaName()});
    }

    public void deleteById(SQLiteDatabase db,int id) {
        db.execSQL("delete from " + TableName + " where id=?", new Integer[]{id});
    }

    public List<MathObject> query(SQLiteDatabase db) {
        Cursor cursor;
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TableName);
        cursor = db.rawQuery(sb.toString(), null);

        List<MathObject> list = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MathObject mathObject = new MathObject(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("result")),
                        cursor.getString(cursor.getColumnIndex("unknowns")),
                        cursor.getString(cursor.getColumnIndex("expression")),
                        cursor.getString(cursor.getColumnIndex("formulaname"))
                );

                list.add(mathObject);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        String tempName = "_temp" + TableName ;
        String sql = "alter table " + TableName + " rename to " + tempName;
        db.execSQL(sql);

        onCreate(db);

        String insert = "insert into " + TableName + " select *, '默认' from " + tempName;
        db.execSQL(insert);

        String drop = "drop table " + tempName;
        db.execSQL(drop);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
