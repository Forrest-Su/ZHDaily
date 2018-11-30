package com.example.forrestsu.zhdaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.forrestsu.zhdaily.R;
import com.example.forrestsu.zhdaily.beans.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NewsAdapter";

    private Context mContext;
    private List<News> mNewsList;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //构造
    public NewsAdapter(Context context, List<News> newsList) {
        mContext = context;
        mNewsList = newsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick((Integer) v.getTag());
                }
            }
        });
            return new NormalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mNewsList != null && mNewsList.size() > 0) {
            Log.i(TAG, "onBindViewHolder: 开始绑定数据");
            News news = mNewsList.get(position);
            String id = news.getId();
            String title = news.getTitle();
            String imagesUri = news.getImagesUrl();
            Log.i(TAG, "onBindViewHolder: id:" + id);
            Log.i(TAG, "onBindViewHolder: title:" + title);
            Log.i(TAG, "onBindViewHolder: imageUri:" + imagesUri);

            NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;

            normalViewHolder.titleTV.setText(title);

            RequestOptions options = new RequestOptions().centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.DATA);
            Glide.with(mContext)
                    .load(imagesUri)
                    .apply(options)
                    .into(normalViewHolder.newsIV);
            normalViewHolder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }


    /*
    普通布局ViewHolder
     */
    private class NormalViewHolder extends RecyclerView.ViewHolder{

        TextView titleTV;
        ImageView newsIV;

        NormalViewHolder (View itemView) {
            super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.tv_title);
            newsIV = (ImageView) itemView.findViewById(R.id.iv_news);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
