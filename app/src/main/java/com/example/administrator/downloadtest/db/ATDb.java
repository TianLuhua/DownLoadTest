package com.example.administrator.downloadtest.db;

import android.content.ContentValues;
import android.database.Cursor;


import com.example.administrator.downloadtest.model.DownLoadBean;
import com.example.administrator.downloadtest.ui.MainActivity;

import rx.functions.Func1;

/**
 * Created by jsion on 16/5/18.
 */
public class ATDb {

    private ATDb() {
        new RuntimeException("ATDb没有实例！");
    }

    public static abstract class DownLoadTaskTable {
        // 表名
        public static final String TABLE_NAME = "downLoadTaskTable";

        // 表字段
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_FILEFOLDER = "fileFolder";
        public static final String COLUMN_FILENAME = "fileName";
        public static final String COLUMN_MSG = "msg";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_DOWNPROGRESS = "downProgress";
        public static final String COLUMN_ALLCOUNT = "allCount";
        public static final String COLUMN_BEFORELENGTH = "beforeLength";
        public static final String COLUMN_FILECOUNT = "fileCount";
        public static final String COLUMN_FILEPATH = "filePath";


        // 建表语句
        public static final String CREATE_TABLE =
                "CREATE TABLE "
                        + TABLE_NAME
                        + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY,"
                        + COLUMN_URL + " TEXT,"
                        + COLUMN_FILEFOLDER + " TEXT,"
                        + COLUMN_FILENAME + " TEXT NOT NULL,"
                        + COLUMN_MSG + " TEXT,"
                        + COLUMN_STATUS + " INT,"
                        + COLUMN_DOWNPROGRESS + " INT NOT NULL,"
                        + COLUMN_ALLCOUNT + " LONG NOT NULL,"
                        + COLUMN_BEFORELENGTH + " LONG NOT NULL,"
                        + COLUMN_FILECOUNT + " LONG NOT NULL,"
                        + COLUMN_FILEPATH + " TEXT"
                        + " ); ";

        // 对象转字段,放入表中
        public static ContentValues toContentValues(DownLoadBean downLoadBean) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, downLoadBean.getId());
            values.put(COLUMN_URL, downLoadBean.getUrl());
            values.put(COLUMN_FILENAME, downLoadBean.getFilename());
            values.put(COLUMN_FILEFOLDER, downLoadBean.getFilePath());
            values.put(COLUMN_MSG, downLoadBean.getMsg());
            values.put(COLUMN_STATUS, downLoadBean.getStatus());
            values.put(COLUMN_DOWNPROGRESS, downLoadBean.getDownProgress());
            values.put(COLUMN_ALLCOUNT, downLoadBean.getAllCount());
            values.put(COLUMN_BEFORELENGTH, downLoadBean.getBeforeLength());
            values.put(COLUMN_FILECOUNT, downLoadBean.getFileCount());
            return values;
        }

        // 响应式的查询,根据表中的row生成一个对象
        static Func1<Cursor, DownLoadBean> PERSON_MAPPER = new Func1<Cursor, DownLoadBean>() {
            @Override
            public DownLoadBean call(Cursor cursor) {
                DownLoadBean downLoadBean = new DownLoadBean();
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                downLoadBean.setId(id);
                String url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL));
                downLoadBean.setUrl(url);
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILENAME));
                downLoadBean.setFilename(fileName);
                String fileFolder = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILEFOLDER));
                downLoadBean.setFileFolder(fileFolder);
                String msg = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG));
                downLoadBean.setMsg(msg);
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                downLoadBean.setStatus(status);
                int downProgress = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOWNPROGRESS));
                downLoadBean.setDownProgress(downProgress);
                long allCount = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ALLCOUNT));
                downLoadBean.setAllCount(allCount);
                long beforeLength = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BEFORELENGTH));
                downLoadBean.setBeforeLength(beforeLength);
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILEPATH));
                downLoadBean.setFilePath(filePath);
                return downLoadBean;
            }
        };

    }
}
