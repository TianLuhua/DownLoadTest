package com.example.administrator.downloadtest.ui;

import android.content.Context;

import com.example.administrator.downloadtest.model.DownLoadBean;

import java.util.List;

/**
 * Created by tlh on 2016/12/19.
 */

public class DownLoaderContract {

    //跟新UI
    public interface View {

        void upDataUI(List<DownLoadBean> downLoadTasks);

        void recyclerViewItemClick(String s, int position);

        void canCelRefresh();
    }

    //  逻辑处理
    public interface Persenter {

        boolean checkSelfPermission(Context context);

        void initRxBus();

        void upDataUI();

        void startDownLoad();

        void recyclerViewItemClick(String s, int position);

        void canCelRefresh();

    }
}
