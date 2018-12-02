package com.example.forrestsu.zhdaily;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * 用于装饰普通的RecyclerView
 * 多返回两个item，一个在头部，一个在尾部，头部的item用于加载时间，尾部的用于加载脚布局footer（用于上拉加载）
 */

public class RVLoadMoreWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NewsAdapter";

    //脚布局
    public final int TYPE_FOOTER = 2;

    //正在加载
    public final int LOADING = 1;
    //加载完成
    public final int LOADING_COMPLETE = 2;
    //加载到底，没有更多数据了
    public final int LOADING_END = 3;
    //加载状态，默认为加载完成
    private int loadState = LOADING_COMPLETE;

    private RecyclerView.Adapter mAdapter;

    //构造
    public RVLoadMoreWrapper(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FOOTER:
                View footerView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_footer, parent, false);
                return new FooterViewHolder(footerView);
            default:
                return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            switch (loadState) {
                case LOADING:
                    footerViewHolder.progressBar.setVisibility(View.VISIBLE);
                    break;
                case LOADING_COMPLETE:
                    footerViewHolder.progressBar.setVisibility(View.INVISIBLE);
                    break;
                case LOADING_END:
                    footerViewHolder.progressBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            mAdapter.onBindViewHolder(viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        //多返回1个item，用于显示脚布局
        return mAdapter.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return mAdapter.getItemViewType(position);
        }
    }


    /*
    设置加载状态
    */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    /*
    脚布局ViewHolder
     */
    private class FooterViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        FooterViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pb_loading);
        }
    }
}
