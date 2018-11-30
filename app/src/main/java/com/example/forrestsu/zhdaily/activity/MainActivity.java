package com.example.forrestsu.zhdaily.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.forrestsu.zhdaily.MyOnScrollListener;
import com.example.forrestsu.zhdaily.R;
import com.example.forrestsu.zhdaily.RVLoadMoreWrapper;
import com.example.forrestsu.zhdaily.SpaceItemDecoration;
import com.example.forrestsu.zhdaily.adapter.BannerFragmentPagerAdapter;
import com.example.forrestsu.zhdaily.adapter.NewsAdapter;
import com.example.forrestsu.zhdaily.beans.News;
import com.example.forrestsu.zhdaily.fragment.NewsBannerFragment;
import com.example.forrestsu.zhdaily.utils.HttpUtil;
import com.example.forrestsu.zhdaily.utils.MyCalendar;
import com.example.forrestsu.zhdaily.utils.ParseJSONUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener,
        NewsAdapter.OnItemClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "MainActivity";

    private final static int MODE_REFRESH = 1;
    private final static int MODE_LOAD_MORE = 2;
    private int currentMode = 0;

    private final int DELAY_MILLIS= 5000; //自动播放时间间隔

    private int currentItem;

    private boolean isAutoPlay = false;  //是否自动播放

    private Runnable task;  //自动播放任务

    private Handler handler;

    private final String latestNewsURL = "https://news-at.zhihu.com/api/4/news/latest";
    private final String oldNewsURLHead = "https://news-at.zhihu.com/api/4/news/before/";
    private String oldNewsURL;
    private int minusDays = 1; //减去的天数
    private String dateStr;

    private DrawerLayout drawerLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private LinearLayout indicatorLL;  //指示器布局
    private Toolbar toolbar;
    private static SwipeRefreshLayout newsSRL;
    private ViewPager topNewsVP;
    private BannerFragmentPagerAdapter topNewsAdapter;
    private RecyclerView newsRV;
    private static RVLoadMoreWrapper rvLoadMoreWrapper;
    private NewsAdapter newsAdapter;
    private List<News> newsList;
    private List<News> topNewsList;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        handler = new Handler();
        task = new Runnable() {
            @Override
            public void run() {
                if (isAutoPlay) {
                    currentItem = topNewsVP.getCurrentItem();
                    if (currentItem < topNewsList.size() - 1) {
                        topNewsVP.setCurrentItem(currentItem + 1);
                    }
                    handler.postDelayed(task, DELAY_MILLIS);
                } else {
                    //每隔5秒检查一次是否可以自动播放
                    handler.postDelayed(task, 5000);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }


    //初始化
    public void init() {
        //findView
        collapsingToolbar  = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        indicatorLL = (LinearLayout) findViewById(R.id.ll_indicator);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        newsSRL = (SwipeRefreshLayout) findViewById(R.id.srl_news);
        topNewsVP = (ViewPager) findViewById(R.id.vp_top_news);
        newsRV = (RecyclerView) findViewById(R.id.rv_news);
        //init
        collapsingToolbar.setTitleEnabled(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("首页");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.outline_menu_black_24);
        }
        navView.setCheckedItem(R.id.home);  //设置默认选中项

        newsList = new ArrayList<News>();
        topNewsList = new ArrayList<News>();
        newsAdapter = new NewsAdapter(this, newsList);
        fragmentList = new ArrayList<Fragment>();

        rvLoadMoreWrapper = new RVLoadMoreWrapper(newsAdapter);  //装饰者模式
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        topNewsAdapter = new BannerFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        topNewsVP.setAdapter(topNewsAdapter);
        newsRV.setAdapter(rvLoadMoreWrapper);
        newsRV.addItemDecoration(new SpaceItemDecoration(30));
        newsSRL.setColorSchemeResources(R.color.myColorAccent);
        //setListener
        navView.setNavigationItemSelectedListener(this);
        topNewsVP.addOnPageChangeListener(this);
        newsAdapter.setOnItemClickListener(this);
        newsSRL.setOnRefreshListener(this);
        newsRV.addOnScrollListener(new MyOnScrollListener() {

            @Override
            public void onLoadMore() {
                rvLoadMoreWrapper.setLoadState(rvLoadMoreWrapper.LOADING);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //加载更多数据
                        currentMode = MODE_LOAD_MORE;
                        dateStr = minusDays(minusDays++);
                        oldNewsURL = oldNewsURLHead + dateStr;
                        getNewsList(oldNewsURL);
                    }
                }).start();
            }
        });

        //进入界面时自动刷新
        onRefresh();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.login:
                //Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            case R.id.night_mode:
                //Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                //Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_home:
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_collection:
                //Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_offline_download:
                //Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onPageSelected(int position) {

        //设置指示器
        for (int i = 0; i < indicatorLL.getChildCount(); i++) {
            if (i == position - 1) {
                indicatorLL.getChildAt(i).setBackgroundResource(R.drawable.dot_selected);
            } else {
                indicatorLL.getChildAt(i).setBackgroundResource(R.drawable.dot_unselected);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch(state) {
            //静止
            case ViewPager.SCROLL_STATE_IDLE:
                int count = topNewsAdapter.getCount();

                if (topNewsVP.getCurrentItem() == 0) {
                    Log.i(TAG, "onPageSelected: 跳转到倒数第二");
                    topNewsVP.setCurrentItem(count - 2, false);
                } else if (topNewsVP.getCurrentItem() == count - 1) {
                    Log.i(TAG, "onPageSelected: 跳转到1");
                    topNewsVP.setCurrentItem(1, false);
                }

                isAutoPlay = true;
                break;
            // 拖动中
            case ViewPager.SCROLL_STATE_DRAGGING:
                isAutoPlay = false;
                break;
            // 设置中
            case ViewPager.SCROLL_STATE_SETTLING:
                isAutoPlay = true;
                break;
        }
    }


    @Override
    public void onItemClick(int position) {
        News news = newsList.get(position);
        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
        intent.putExtra("id", news.getId());
        intent.putExtra("title", news.getTitle());
        startActivity(intent);
    }


    @Override
    public void onRefresh() {
        //刷新数据
        //刷新数据时暂停自动播放
        isAutoPlay = false;
        currentMode = MODE_REFRESH;
        getNewsList(latestNewsURL);
    }

    //获取新闻列表
    public void getNewsList(String newsUrl) {
        HttpUtil.sendOkHttpRequest(newsUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 请求成功");
                String responseData = response.body().string();
                if (currentMode == MODE_REFRESH) {
                    newsList.clear();
                    topNewsList.clear();
                    fragmentList.clear();
                    List<News> topList = ParseJSONUtil.parseTopNewsList(responseData);
                    topNewsList.add(topList.get(topList.size() - 1));
                    topNewsList.addAll(topList);
                    topNewsList.add(topList.get(0));
                }
                List<News> list = ParseJSONUtil.parseNewsList(responseData);
                //注意，这里不能直接引用list，也就是不能写成newsList = list,否则无法更新RecyclerView
                //newsList = list;
                newsList.addAll(list);
                refreshUI();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure:请求出错：" + e.getMessage());
                //加载缓存数据
            }
        });
    }


    //更新RecyclerView
    public void refreshUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentMode == MODE_REFRESH) {
                    for (int i = 0; i < topNewsList.size(); i++) {
                        Fragment fragment = new NewsBannerFragment();
                        fragmentList.add(fragment);
                        Bundle bundle = new Bundle();
                        bundle.putString ("id", topNewsList.get(i).getId());
                        bundle.putString ("title", topNewsList.get(i).getTitle());
                        bundle.putString ("imageUrl", topNewsList.get(i).getImagesUrl());
                        fragment.setArguments(bundle);
                    }
                    topNewsAdapter.notifyDataSetChanged();
                    setIndicator();
                    topNewsVP.setCurrentItem(1);
                    rvLoadMoreWrapper.notifyDataSetChanged();
                    newsSRL.setRefreshing(false);
                    if (topNewsList.size() < 2) {
                        isAutoPlay = false;
                    } else {
                        isAutoPlay = true;
                        handler.postDelayed(task, DELAY_MILLIS);
                    }
                } else if (currentMode == MODE_LOAD_MORE) {
                    rvLoadMoreWrapper.setLoadState(rvLoadMoreWrapper.LOADING_COMPLETE);
                }
            }
        });
    }


    //设置原点指示器
    public void setIndicator() {
        //清空所有子View
        indicatorLL.removeAllViews();
        for(int i = 0; i < fragmentList.size() - 2; i++) {
            View view = new View(this);
            view.setBackgroundResource(R.drawable.dot_unselected);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(16, 16);
            layoutParams.topMargin = 8;
            layoutParams.bottomMargin = 8;
            layoutParams.leftMargin = 8;
            layoutParams.rightMargin = 8;
            indicatorLL.addView(view, layoutParams);
        }
        //默认第一个圆点被选中
        indicatorLL.getChildAt(0).setBackgroundResource(R.drawable.dot_selected);
    }


    //减去n天
    public String minusDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(MyCalendar.getSysYearInt(), MyCalendar.getSysMonthInt(), MyCalendar.getSysDayInt());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        calendar.add(Calendar.DATE, -days);
        String str = sdf.format(calendar.getTime());
        Log.i(TAG, "minusOneDay: 减去"+ days +"天结果：" + str);
        return str;
    }



    /*
    //测试
    public void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvLoadMoreWrapper.notifyDataSetChanged();
                        newsSRL.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    //模拟数据
    public void initData() {
        for (int i = 0; i < 10; i++) {
            newsList.add(new News("000", "TEST",
                    "https://pic3.zhimg.com//v2-392cc990dc15f01fa74b77f98e28b8b6.jpg", "0"));
        }
    }
    */
}
