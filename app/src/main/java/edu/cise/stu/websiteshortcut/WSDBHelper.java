package edu.cise.stu.websiteshortcut;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by USOON on 2016/8/28.
 */
public class WSDBHelper extends SQLiteOpenHelper {

    private static int VERSION = 1;

    public WSDBHelper(Context context, String name){
        this(context, name, VERSION);
    }

    public WSDBHelper(Context context, String name, int version){
        this(context, name, null, version);
    }

    public WSDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("WS","Create Database");
        String sql = "CREATE TABLE "+ WSFirstData.TB_NAME +"("+
                WSFirstData.FIELD_0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WSFirstData.FIELD_1 + " VARCHAR(20)," +
                WSFirstData.FIELD_2 + " VARCHAR(150)," +
                WSFirstData.FIELD_3 + " INTEGER(2));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("WS","Upgrade Database");
    }
}
