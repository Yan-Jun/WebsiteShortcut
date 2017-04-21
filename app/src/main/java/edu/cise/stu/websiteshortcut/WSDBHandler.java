package edu.cise.stu.websiteshortcut;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by USOON on 2016/9/2.
 */
public class WSDBHandler {

    public void insert(Context context, String webName, String urlStr){
        SQLiteDatabase db = new WSDBHelper(context, WSFirstData.DB_NAME).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(WSFirstData.FIELD_1, webName);
        cv.put(WSFirstData.FIELD_2, urlStr);
        cv.put(WSFirstData.FIELD_3, 0);
        db.insert(WSFirstData.TB_NAME, null, cv);
    }

    public static List query(Context context){
        SQLiteDatabase db = new WSDBHelper(context, WSFirstData.DB_NAME).getReadableDatabase();
        String sql_query =  "SELECT * FROM " + WSFirstData.TB_NAME;
        Cursor cursor = db.rawQuery(sql_query, null);

        ArrayList<Map> list = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Map<String, String> data = new HashMap<>();
                data.put("webId", cursor.getString(cursor.getColumnIndex(WSFirstData.FIELD_0)));
                data.put("webName", cursor.getString(cursor.getColumnIndex(WSFirstData.FIELD_1)));
                data.put("urlStr",  cursor.getString(cursor.getColumnIndex(WSFirstData.FIELD_2)));
                list.add(data);
            } while (cursor.moveToNext());
        }
        db.close();

        return list;
    }

    public static List queryIf(Context context, String keyWord){
        SQLiteDatabase db = new WSDBHelper(context, WSFirstData.DB_NAME).getReadableDatabase();
        String sql_query =  "SELECT * FROM " + WSFirstData.TB_NAME + " WHERE " + WSFirstData.FIELD_1 + " LIKE '%" + keyWord + "%'";
        Cursor cursor = db.rawQuery(sql_query, null);

        ArrayList<Map> list = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Map<String, String> data = new HashMap<>();
                data.put("webId", cursor.getString(cursor.getColumnIndex(WSFirstData.FIELD_0)));
                data.put("webName", cursor.getString(cursor.getColumnIndex(WSFirstData.FIELD_1)));
                data.put("urlStr",  cursor.getString(cursor.getColumnIndex(WSFirstData.FIELD_2)));
                list.add(data);
            } while (cursor.moveToNext());
        }
        db.close();

        return list;
    }


    public void delete(Context context, String _id){
        SQLiteDatabase db = new WSDBHelper(context, WSFirstData.DB_NAME).getWritableDatabase();
        String sql_delete =  " DELETE FROM " + WSFirstData.TB_NAME +
                             " WHERE " + WSFirstData.FIELD_0 + "=" + _id;
        db.execSQL(sql_delete);
    }

}
