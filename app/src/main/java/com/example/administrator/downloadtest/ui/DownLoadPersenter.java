package com.example.administrator.downloadtest.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.example.administrator.downloadtest.db.ATDbManager;
import com.example.administrator.downloadtest.rx.RxBus;
import com.example.administrator.downloadtest.model.DownLoadBean;

import java.io.File;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by andy.shangguan on 2016/12/19.
 */

public class DownLoadPersenter implements DownLoaderContract.Persenter {


    private static final String TAG = DownLoadPersenter.class.getSimpleName();
    private DownLoaderContract.View view;
    private RxBus rxBus;
    private ATDbManager manager;


    public DownLoadPersenter(DownLoaderContract.View view, ATDbManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean checkSelfPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {
            return true;
        }
        return false;
    }

    @Override
    public void initRxBus() {
        rxBus = RxBus.getInstance();
        rxBus.receive(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object obj) {
                Log.e("info", "Object-toString:" + obj.toString());
                if (obj instanceof Integer) {
                    int i = (Integer) obj;
                    Log.e("info", "receive---receive:" + i);
                    return i == 1 ? true : false;
                }
                return false;
            }
        }, new Action1<Object>() {
            @Override
            public void call(Object baseBean) {
//                DownLoadBean receiveBaseBean = (DownLoadBean) baseBean;
//                Log.e("info", "receiveBaseBean.getDownProgress:" + receiveBaseBean.getDownProgress());
//                manager.addPerson(receiveBaseBean);
                Log.e("info", "upDataUI_receive:" + System.currentTimeMillis());
                upDataUI();
            }


        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("info", "RxBus 接收出错了！");
                rxBus.unsubscribe();
            }
        }, new Action0() {
            @Override
            public void call() {
                Log.e("info", "RxBus 接收结束了！");
            }
        });
    }

    @Override
    public void upDataUI() {
        Log.e("info", "upDataUI_before:" + System.currentTimeMillis());
        Observable<List<DownLoadBean>> listObservable = manager.queryPerson();
        listObservable
                .subscribe(new Subscriber<List<DownLoadBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DownLoadBean> downLoadTasks) {
                        Log.e("info", "upDataUI_after:" + downLoadTasks);
                        if (downLoadTasks != null)
                            view.upDataUI(downLoadTasks);
                        this.unsubscribe();
                    }
                });
    }


    @Override
    public void startDownLoad() {
        for (int i = 0; i < 5; i++) {
            //noHttp是按照文件名称来判断是否为同一个文件，所以这里在文件名称前面把 i 加上去表示这10个文件不同
            DownLoadBean downloadTask = new DownLoadBean(i, "http://gdown.baidu.com/data/wisegame/4f45d1baacb6ee7f/baidushoujizhushouyuan91zhu_16789458.apk", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "APK", i + "baidushoujizhushouyuan91zhu.apk");
            rxBus.send(downloadTask);
            Log.e("info", "startDownLoad:" + System.currentTimeMillis());
        }
    }

    @Override
    public void recyclerViewItemClick(String s, int position) {
        view.recyclerViewItemClick(s, position);
    }

    @Override
    public void canCelRefresh() {
        view.canCelRefresh();
    }


}
