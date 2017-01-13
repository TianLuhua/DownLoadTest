package com.example.administrator.downloadtest.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.administrator.downloadtest.MyApplication;
import com.example.administrator.downloadtest.db.ATDbManager;
import com.example.administrator.downloadtest.model.DownLoadBean;
import com.example.administrator.downloadtest.R;
import com.example.administrator.downloadtest.rx.RxBus;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.StorageReadWriteError;
import com.yolanda.nohttp.error.StorageSpaceNotEnoughError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.example.administrator.downloadtest.ui.DownLoadPersenter.DOWNLOAD_TASK_CHENGED;

/**
 * Created by tlh on 2016/11/25 0025.
 */

public class DownLoadService extends Service {

    /**
     * 下载队列.
     */
    private DownloadQueue downloadQueue;
    private RxBus rxBus;
    private ATDbManager manager;
    private Map<Integer, DownLoadBean> downLoadBeanMap;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("info", "DownLoadService:" + "Start");
        if (downloadQueue == null)
            downloadQueue = NoHttp.getDownloadQueueInstance();
        rxBus = RxBus.getInstance();
        manager = MyApplication.getATDbManager();
        downLoadBeanMap = new LinkedHashMap<Integer, DownLoadBean>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("info", "onStartCommand");
        rxBus.receive(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                Log.e("info", "Func1");
                if (o instanceof DownLoadBean) {
                    DownLoadBean downLoadBean = (DownLoadBean) o;
                    return downLoadBean.getStatus() == DownLoadBean.DOWNLOCADBEAN_STATUS_NOMAL ? true : false;
                }
                return false;
            }
        }, new Action1<Object>() {
            @Override
            public void call(Object o) {
                DownLoadBean downLoadBean = (DownLoadBean) o;
                Log.e("info", "startDownLoad_receive:" + System.currentTimeMillis());
                if (download(downLoadBean, downloadListener)) {
                    downLoadBeanMap.put(downLoadBean.getId(), downLoadBean);
                    manager.addPerson(downLoadBean);
                    rxBus.send(DOWNLOAD_TASK_CHENGED);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("info", "DownLoadService---receive出错了!" + throwable.getMessage());

            }
        }, new Action0() {
            @Override
            public void call() {
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开始下载。
     */
    private boolean download(DownLoadBean downLoadBean, DownloadListener downloadListener) {


        DownloadRequest downloadRequest = null;
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (downloadRequest != null && downloadRequest.isStarted() && !downloadRequest.isFinished()) {
            // 暂停下载。
            downloadRequest.cancel();
            return false;
        } else if (downloadRequest == null) {// 没有开始就重新下载。

            /**
             * 这里不传文件名称、不断点续传，则会从响应头中读取文件名自动命名，如果响应头中没有则会从url中截取。
             */
            // url 下载地址。
            // fileFolder 文件保存的文件夹。
            // isDeleteOld 发现文件已经存在是否要删除重新下载。
            // mDownloadRequest = NoHttp.createDownloadRequest(Constants.URL_DOWNLOADS[0], AppConfig.getInstance().APP_PATH_ROOT, true);

            /**
             * 如果使用断点续传的话，一定要指定文件名喔。
             */
            // url 下载地址。
            // fileFolder 保存的文件夹。
            // fileName 文件名。
            // isRange 是否断点续传下载。
            // isDeleteOld 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功。
            Log.e("info", "downLoadBean:" + downLoadBean.getFilename());
            downloadRequest = NoHttp.createDownloadRequest(downLoadBean.getUrl(), downLoadBean.getFileFolder(), downLoadBean.getFilename(), true, false);

            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            downloadQueue.add(downLoadBean.getId(), downloadRequest, downloadListener);
            return true;
        }
        return false;
    }

    /**
     * 下载监听
     */
    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
            int progress = 0;
            if (allCount != 0) {
                progress = (int) (beforeLength * 100 / allCount);
            }
            DownLoadBean task = downLoadBeanMap.get(what);
            task.setId(what);
            task.setDownProgress(progress);
            task.setStatus(DownLoadBean.DOWNLOCADBEAN_STATUS_START);
            task.setMsg("开始下下载！");
            task.setBeforeLength(beforeLength);
            task.setAllCount(allCount);
            manager.updateById(task);
            rxBus.send(DOWNLOAD_TASK_CHENGED);
            Log.e("info", what + "----onStart:" + "开始下下载!");
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            String message = getString(R.string.download_error);
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = getString(R.string.download_error_server);
            } else if (exception instanceof NetworkError) {
                messageContent = getString(R.string.download_error_network);
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = getString(R.string.download_error_storage);
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = getString(R.string.download_error_space);
            } else if (exception instanceof TimeoutError) {
                messageContent = getString(R.string.download_error_timeout);
            } else if (exception instanceof UnKnownHostError) {
                messageContent = getString(R.string.download_error_un_know_host);
            } else if (exception instanceof URLError) {
                messageContent = getString(R.string.download_error_url);
            } else {
                messageContent = getString(R.string.download_error_un);
            }
            DownLoadBean task = downLoadBeanMap.get(what);
            message = String.format(Locale.getDefault(), message, messageContent);
            task.setId(what);
            task.setStatus(DownLoadBean.DOWNLOCADBEAN_STATUS_ERROR);
            task.setMsg(message);
            manager.updateById(task);
            rxBus.send(DOWNLOAD_TASK_CHENGED);
            Log.e("info", what + "----onDownloadError:" + message);

        }

        @Override
        public void onProgress(int what, int progress, long fileCount) {
            DownLoadBean task = downLoadBeanMap.get(what);
            task.setId(what);
            task.setStatus(DownLoadBean.DOWNLOCADBEAN_STATUS_PROGRESS);
            task.setDownProgress(progress);
            task.setFileCount(fileCount);
            task.setMsg("正在下载。。。");
            manager.updateById(task);
            rxBus.send(DOWNLOAD_TASK_CHENGED);
            Log.e("info", what + "--progress:" + progress);
        }

        @Override
        public void onFinish(int what, String filePath) {
            DownLoadBean task = downLoadBeanMap.get(what);
            task.setId(what);
            task.setStatus(DownLoadBean.DOWNLOCADBEAN_STATUS_FINISH);
            task.setFilePath(filePath);
            task.setMsg("下载完成下载完成！");
            manager.updateById(task);
            rxBus.send(DOWNLOAD_TASK_CHENGED);
            Log.e("info", "onFinish:" + what + "--filePath:" + filePath);

        }

        @Override
        public void onCancel(int what) {
            DownLoadBean task = downLoadBeanMap.get(what);
            task.setId(what);
            task.setStatus(DownLoadBean.DOWNLOCADBEAN_STATUS_CANCEL);
            manager.updateById(task);
            rxBus.send(DOWNLOAD_TASK_CHENGED);
            Log.e("info", what + "---onCancel:");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        rxBus.unsubscribe();
        manager.close();
        Log.e("info", "DownLoadService---stop");
    }
}
