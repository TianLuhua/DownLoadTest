package com.example.administrator.downloadtest.db;

import android.content.Context;


import com.example.administrator.downloadtest.model.DownLoadBean;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jsion on 16/5/18.
 */
public class ATDbManager {
    private static final String TAG = "ATDbManager";
    // rx响应式数据库,
    private BriteDatabase briteDatabase;

    public ATDbManager(Context context) {
        ATDbOpenHelper dbOpenHelper;
        // sqlbrite 初始化,构造出响应式数据库,添加log
        SqlBrite sqlBrite;
        sqlBrite = SqlBrite.create(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
            }
        });
        // 原生的sqllitehelper 用来建立数据库和数据表,以及构造,rx响应式数据库
        dbOpenHelper = new ATDbOpenHelper(context);
        // 执行slqbirte 构造数据库的语句
        briteDatabase = sqlBrite.wrapDatabaseHelper(dbOpenHelper, Schedulers.io());
        briteDatabase.setLoggingEnabled(true);
    }

    public void close() {
        if (briteDatabase != null)
            briteDatabase.close();
    }


    public Observable<List<DownLoadBean>> queryPerson() {
        return briteDatabase
                .createQuery(ATDb.DownLoadTaskTable.TABLE_NAME, "SELECT * FROM " + ATDb.DownLoadTaskTable.TABLE_NAME)
                .mapToList(ATDb.DownLoadTaskTable.PERSON_MAPPER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    //更新
//    triggers.onNext(tables);

    //查询
//    final Observable<SqlBrite.Query> queryObservable = triggers //
//            .filter(tableFilter) // Only trigger on tables we care about.
//            .map(new Func1<Set<String>, SqlBrite.Query>() {
//                @Override public SqlBrite.Query call(Set<String> trigger) {
//                    return query;
//                }
//            }) //
//            .onBackpressureLatest() // Guard against uncontrollable frequency of upstream emissions.
//            .startWith(query) //
//            .observeOn(scheduler) //
//            .onBackpressureLatest() // Guard against uncontrollable frequency of scheduler executions.
//            .doOnSubscribe(new Action0() {
//                @Override public void call() {
//                    if (transactions.get() != null) {
//                        throw new IllegalStateException(
//                                "Cannot subscribe to observable query in a transaction.");
//                    }
//                }
//            });

    public Observable<List<DownLoadBean>> queryPersonByName(String name) {
        return briteDatabase.createQuery(ATDb.DownLoadTaskTable.TABLE_NAME, "SELECT * FROM "
                        + ATDb.DownLoadTaskTable.TABLE_NAME
                        + " WHERE "
                        + ATDb.DownLoadTaskTable.COLUMN_FILENAME
                        + " = ?"
                , name)
                .mapToList(ATDb.DownLoadTaskTable.PERSON_MAPPER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public long addPerson(DownLoadBean DownLoadBean) {
        return briteDatabase.insert(ATDb.DownLoadTaskTable.TABLE_NAME, ATDb.DownLoadTaskTable.toContentValues(DownLoadBean));
    }

    public int deletePersonByName(final String name) {

        return briteDatabase.delete(ATDb.DownLoadTaskTable.TABLE_NAME, ATDb.DownLoadTaskTable.COLUMN_FILENAME + "=?", name);
    }

    public int updateById(final DownLoadBean downLoadBean) {
        return briteDatabase.update(ATDb.DownLoadTaskTable.TABLE_NAME, ATDb.DownLoadTaskTable.toContentValues(downLoadBean), ATDb.DownLoadTaskTable.COLUMN_ID + "=" + downLoadBean.getId());
    }
}
