package com.example.administrator.downloadtest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Tianluhua on 16/5/18.
 */
public class ATDbOpenHelper extends SQLiteOpenHelper {

    public static final String AT_DB_NAME = "downLoadTask.db";
    public static final int AT_DB_VERSION = 1;

    public ATDbOpenHelper(Context context) {
        super(context, AT_DB_NAME, null, AT_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("tlh", "Create Table SQL:" + ATDb.DownLoadTaskTable.CREATE_TABLE);
        db.execSQL(ATDb.DownLoadTaskTable.CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
