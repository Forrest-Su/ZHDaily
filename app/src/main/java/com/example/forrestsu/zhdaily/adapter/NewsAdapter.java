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
import com.example.forrestsu.zhdaily.utils.MyCalendar;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NewsAdapter";

    //日期布局
    public final int TYPE_DATE = 0;
    //普通布局
    public final int TYPE_NORMAL = 1;

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
        switch (viewType) {
            case TYPE_NORMAL:
                View normalView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_news, parent, false);
                normalView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick((Integer) v.getTag());
                        }
                    }
                });
                return new NormalViewHolder(normalView);
            default: //TYPE_DATE
                View dateView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_date, parent, false);
                return new DateViewHolder(dateView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mNewsList != null && mNewsList.size() > 0) {
            News news = mNewsList.get(position);
            if (viewHolder instanceof NormalViewHolder) {
                Log.i(TAG, "onBindViewHolder: 开始绑定数据");
                String title = news.getTitle();
                String imagesUri = news.getImagesUrl();

                NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;

                normalViewHolder.titleTV.setText(title);

                RequestOptions options = new RequestOptions().centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.DATA);
                Glide.with(mContext)
                        .load(imagesUri)
                        .apply(options)
                        .into(normalViewHolder.newsIV);
                normalViewHolder.itemView.setTag(position);
            } else if (viewHolder instanceof DateViewHolder) {
                DateViewHolder dateViewHolder = (DateViewHolder) viewHolder;

                String date = news.getDate();

                if (position == 0) {  //第一个日期是当天
                    dateViewHolder.dateTV.setText("今日热闻");
                } else {
                    String dayOfWeek = MyCalendar.getDayOfWeek(MyCalendar.strToDate(date, "yyyyMMdd"));
                    StringBuilder sb = new StringBuilder();
                    sb.append(date.substring(4,6));
                    sb.append("月");
                    sb.append(date.substring(6));
                    sb.append("日  ");
                    sb.append(dayOfWeek);
                    Log.i(TAG, "onBindViewHolder: 绑定日期：" + sb.toString());
                    dateViewHolder.dateTV.setText(sb.toString());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mNewsList.get(position).getType() == -1) {
            return TYPE_DATE;
        } else {
            return TYPE_NORMAL;
        }
    }


    /*
    日期布局
     */
    private class DateViewHolder extends RecyclerView.ViewHolder {

        TextView dateTV;

        DateViewHolder (View itemView) {
            super(itemView);
            dateTV = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }



    /*
    普通布局ViewHolder
     */
    private class NormalViewHolder extends RecyclerView.ViewHolder{

        TextView dateTV;
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
