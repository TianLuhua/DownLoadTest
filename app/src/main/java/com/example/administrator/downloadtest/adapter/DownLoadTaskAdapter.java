package com.example.administrator.downloadtest.adapter;

import android.app.Service;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.administrator.downloadtest.R;
import com.example.administrator.downloadtest.model.DownLoadBean;
import com.example.administrator.downloadtest.ui.DownLoaderContract;

import java.util.List;

/**
 * Created by Tianluhua on 2017/1/11.
 */

public class DownLoadTaskAdapter extends RecyclerView.Adapter<DownLoadTaskAdapter.ViewHold> {

    private List<DownLoadBean> downLoadTasks;
    private Context mContext;
    private LayoutInflater inflater;
    private DownLoaderContract.Persenter persenter;


    public DownLoadTaskAdapter(Context mContext, DownLoaderContract.Persenter persenter) {
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        this.inflater = LayoutInflater.from(mContext);
        this.persenter = persenter;
    }

    public List<DownLoadBean> getDownLoadTasks() {
        return downLoadTasks;
    }

    public void setDownLoadTasks(List<DownLoadBean> downLoadTasks) {
        this.downLoadTasks = downLoadTasks;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHold hold = null;
        if (hold == null) {
            hold = new ViewHold(inflater.inflate(R.layout.recycler_item, parent, false));
        }
        return hold;
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        if (downLoadTasks != null) {
            holder.itemTile.setText(downLoadTasks.get(position).getFilename());
            holder.itemDownedProgress.setProgress(downLoadTasks.get(position).getDownProgress());
            holder.itemRootLayout.setTag(downLoadTasks.get(position).getFilename());
            switch (downLoadTasks.get(position).getStatus()) {
                case DownLoadBean.DOWNLOCADBEAN_STATUS_START:
                    holder.status.setText("开始下载！");
                    break;
                case DownLoadBean.DOWNLOCADBEAN_STATUS_PROGRESS:
                    holder.status.setText("下载中．．．");
                    break;
                case DownLoadBean.DOWNLOCADBEAN_STATUS_ERROR:
                    holder.status.setText(downLoadTasks.get(position).getMsg());
                    break;
                case DownLoadBean.DOWNLOCADBEAN_STATUS_CANCEL:
                    holder.status.setText("下载被取消！");
                    break;
                case DownLoadBean.DOWNLOCADBEAN_STATUS_WARIT:
                    holder.status.setText("等待下载...");
                    break;
                case DownLoadBean.DOWNLOCADBEAN_STATUS_FINISH:
                    holder.status.setText("下载完成！");
                    break;


            }
        }
    }

    @Override
    public int getItemCount() {
        return downLoadTasks == null ? 0 : downLoadTasks.size();
    }

    public class ViewHold extends RecyclerView.ViewHolder {

        private TextView itemTile;
        private TextView status;
        private ProgressBar itemDownedProgress;
        private LinearLayout itemRootLayout;

        public ViewHold(View itemView) {
            super(itemView);
            itemRootLayout = (LinearLayout) itemView.findViewById(R.id.item_root_layout);
            itemRootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tag = (String) view.getTag();
                    persenter.recyclerViewItemClick(tag, getAdapterPosition());
                }
            });
            status = (TextView) itemView.findViewById(R.id.item_status);
            itemTile = (TextView) itemView.findViewById(R.id.item_title);
            itemDownedProgress = (ProgressBar) itemView.findViewById(R.id.item_progress);
        }
    }
}
