package com.example.forrestsu.zhdaily.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.forrestsu.zhdaily.R;
import com.example.forrestsu.zhdaily.beans.News;
import com.example.forrestsu.zhdaily.utils.HttpUtil;
import com.example.forrestsu.zhdaily.utils.ParseJSONUtil;
import com.example.forrestsu.zhdaily.utils.WebUtils;

import java.io.IOException;

import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewsActivity extends BaseActivity {

    private static final String TAG = "NewsActivity";

    private static final String NEWSURL = "https://news-at.zhihu.com/api/4/news/";

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView newsIV;
    private TextView titleTV, imageSourceTV;
    private WebView webView;
    private String id;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        init();

        getNews(NEWSURL + id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_news, menu);
        return true;
    }

    //初始化
    public void init() {
        //findView
        collapsingToolbar  = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        newsIV = (ImageView) findViewById(R.id.iv_news);
        titleTV = (TextView) findViewById(R.id.tv_title);
        imageSourceTV = (TextView) findViewById(R.id.tv_image_source);
        webView = (WebView) findViewById(R.id.web_view);

        collapsingToolbar.setTitleEnabled(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent toMainActivity = new Intent(NewsActivity.this, MainActivity.class);
                startActivity(toMainActivity);
                break;
            case R.id.share:
                break;
            case R.id.collection:
                break;
            case R.id.comments:
                break;
            case R.id.popularity:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //获取新闻
    public void getNews(String newsUrl) {
        HttpUtil.sendOkHttpRequest(newsUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 请求成功");
                News news = ParseJSONUtil.parseNews(response.body().string());
                if (news != null) {
                    refreshUI(news);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: 请求出错：" + e.getMessage());
            }
        });
    }

    //更新UI
    public void refreshUI(final News news) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //测试
                Log.i(TAG, "run: body:" + news.getBody());
                Log.i(TAG, "run: imageSource:" + news.getImageSource());
                Log.i(TAG, "run: title:" + news.getTitle());
                Log.i(TAG, "run: imageUr:" + news.getImageUrl());
                Log.i(TAG, "run: shareUrl:" + news.getShareUrl());
                Log.i(TAG, "run: imagesUrl:" + news.getImagesUrl());
                Log.i(TAG, "run: type:" + news.getType());
                Log.i(TAG, "run: id:" + news.getId());
                Log.i(TAG, "run: css" + news.getCss()[0]);

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .transform(new VignetteFilterTransformation());
                Glide.with(NewsActivity.this)
                        .load(news.getImageUrl())
                        .apply(options)
                        .into(newsIV);

                titleTV.setText(news.getTitle());
                imageSourceTV.setText(news.getImageSource());

                if (TextUtils.isEmpty(news.getBody())) {
                    Toast.makeText(NewsActivity.this, "无法加载", Toast.LENGTH_SHORT).show();
                } else {
                    String data = WebUtils.buildHtmlWithCss(news.getBody(), news.getCss(),false);
                    webView.loadDataWithBaseURL(WebUtils.BASE_URL, data, WebUtils.MIME_TYPE, WebUtils.ENCODING, WebUtils.FAIL_URL);
                }
            }
        });
    }
}
