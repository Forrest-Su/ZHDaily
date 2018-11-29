package com.example.forrestsu.zhdaily;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class MyOnScrollListener extends RecyclerView.OnScrollListener {

    //是否正在向上滑动，默认为false
    private boolean isSlidingUpward = false;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //dy：垂直滚动距离， 大于0表示向上滑动，小于0表示向下滑动
        //dx：水平滚动距离，大于0表示向左滑动，小于0表示向右滑动
        if (dy > 0) {
            isSlidingUpward = true;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //当RecyclerView静止时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //获取最后一个完全显示在屏幕中的item的position
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();

            //如果滑动到了最后一个item,执行加载更多
            if (lastItemPosition + 1 == manager.getItemCount() && isSlidingUpward) {
                onLoadMore();
            }
        }
    }

    //加载更多
    public abstract void onLoadMore();
}
