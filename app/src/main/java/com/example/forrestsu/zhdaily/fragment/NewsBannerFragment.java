package com.example.forrestsu.zhdaily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.forrestsu.zhdaily.R;
import com.example.forrestsu.zhdaily.activity.NewsActivity;
import com.example.forrestsu.zhdaily.beans.News;

import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

public class NewsBannerFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "NewsBannerFragment";

    private String id;
    private String title;
    private String imageUrl;

    private ImageView newsIV;
    private TextView titleTV;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceStat) {
        View view = inflater.inflate(R.layout.fragment_news_banner, container, false);

        init(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isAdded()) {
            if (getArguments() != null) {
                id= getArguments().getString("id");
                title = getArguments().getString("title");
                imageUrl = getArguments().getString("imageUrl");

                //填充数据
                titleTV.setText(title);

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .transform(new VignetteFilterTransformation());
                Glide.with(getContext())
                        .load(imageUrl)
                        .apply(options)
                        .into(newsIV);
            }
        }
    }

    //初始化
    public void init(View view) {
        //findView
        newsIV = (ImageView) view.findViewById(R.id.iv_news);
        titleTV = (TextView) view.findViewById(R.id.tv_title);

        //setListener
        newsIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.iv_news:
                Intent toNewsActivity = new Intent(getContext(), NewsActivity.class);
                toNewsActivity.putExtra("id", id);
                toNewsActivity.putExtra("title", title);
                startActivity(toNewsActivity);
                break;
            default:
                break;
        }
    }

}
