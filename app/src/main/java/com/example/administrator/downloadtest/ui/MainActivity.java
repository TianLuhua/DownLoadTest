package com.example.administrator.downloadtest.ui;

import android.Manifest;
import android.content.Intent;
import android.preference.SwitchPreference;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.downloadtest.MyApplication;
import com.example.administrator.downloadtest.R;
import com.example.administrator.downloadtest.adapter.DownLoadTaskAdapter;
import com.example.administrator.downloadtest.databinding.ActivityMainBinding;
import com.example.administrator.downloadtest.db.ATDbManager;
import com.example.administrator.downloadtest.model.DownLoadBean;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements DownLoaderContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button button;
    private Intent intent;
    private TextView status;
    private ProgressBar progressBar;
    private RecyclerView recyclerDownLoadTasks;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ATDbManager manager;
    private DownLoadTaskAdapter adapter;

    private DownLoaderContract.Persenter persenter;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null, false);
        setContentView(rootView);
        binding = ActivityMainBinding.bind(rootView);

        initView();
//        manager = new ATDbManager(MainActivity.this);
        manager = MyApplication.getATDbManager();
        persenter = new DownLoadPersenter(this, manager);
        binding.setPersenter((DownLoadPersenter) persenter);
        persenter.initRxBus();
        if (persenter.checkSelfPermission(MainActivity.this)) {
            //没有权限,申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        intent = new Intent(MainActivity.this, DownLoadService.class);
        startService(intent);
    }

    private void initView() {

        button = binding.localItemIoc;
        recyclerDownLoadTasks = binding.recyclerDownTasks;
        recyclerDownLoadTasks.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        adapter = new DownLoadTaskAdapter(MainActivity.this, persenter);
        recyclerDownLoadTasks.setAdapter(adapter);
        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                persenter.upDataUI();
            }
        });
    }

    @Override
    public void upDataUI(List<DownLoadBean> downLoadTasks) {
        adapter.setDownLoadTasks(downLoadTasks);
        adapter.notifyDataSetChanged();
        persenter.canCelRefresh();
    }

    @Override
    public void recyclerViewItemClick(String s, int position) {
        Snackbar.make(swipeRefreshLayout, "你点击了：" + s, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void canCelRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }

}
