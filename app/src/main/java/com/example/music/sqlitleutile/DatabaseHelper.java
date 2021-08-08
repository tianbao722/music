package com.example.music.sqlitleutile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //数据库的名字
    public static final String DATABASE_NAME = "search.db";

    public static final String TABLE_NAME = "yuepu";//图片||PDF乐谱
    public static final String TABLE_NAME1 = "music";//我的音乐
    public static final String TABLE_NAME2 = "dongtai";//动态乐谱

    //The column names
    public static final String ID = "ID";
    public static final String NAME = "NAME";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建表
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT)");
        sqLiteDatabase.execSQL("create table " + TABLE_NAME1 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT)");
        sqLiteDatabase.execSQL("create table " + TABLE_NAME2 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT)");
//        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,SURNAME TEXT,MARKS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //删除表
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(sqLiteDatabase);
    }

    //添加数据
    public boolean insertData(String tableName, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        long result = db.insert(tableName, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //查询所有数据
    public Cursor getAllData(String tableName) {
        //To Query Data
        SQLiteDatabase db = this.getWritableDatabase();
        //The Table Instance
        Cursor res = db.rawQuery("SELECT * FROM " + tableName, null);
        return res;
    }

    //修改数据
    public boolean updateData(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(NAME, name);

        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});

        return true;
    }

    //删除数据
    public Integer deleteData(String tableName, String id) {
        //Primary Key 代表獨特標籤

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName, "ID = ?", new String[]{id});
    }
}
