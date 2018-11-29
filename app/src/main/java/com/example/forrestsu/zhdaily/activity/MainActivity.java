package com.example.forrestsu.zhdaily.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.forrestsu.zhdaily.MyOnScrollListener;
import com.example.forrestsu.zhdaily.R;
import com.example.forrestsu.zhdaily.RVLoadMoreWrapper;
import com.example.forrestsu.zhdaily.SpaceItemDecoration;
import com.example.forrestsu.zhdaily.adapter.NewsAdapter;
import com.example.forrestsu.zhdaily.beans.NewsTitle;
import com.example.forrestsu.zhdaily.utils.HttpUtil;
import com.example.forrestsu.zhdaily.utils.MyCalendar;
import com.example.forrestsu.zhdaily.utils.ParseJSONUtil;
import com.example.forrestsu.zhdaily.view.IMainactivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements IMainactivity,
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener,
        NewsAdapter.OnItemClickListener{

    private static final String TAG = "MainActivity";

    private final static int MODE_REFRESH = 1;
    private final static int MODE_LOAD_MORE = 2;
    private int currentMode = 0;

    private final String latestNewsURL = "https://news-at.zhihu.com/api/4/news/latest";
    private final String oldNewsURLHead = "https://news-at.zhihu.com/api/4/news/before/";
    private String oldNewsURL;
    private int minusDays = 1; //减去的天数
    private String dateStr;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private static SwipeRefreshLayout newsSRL;
    private RecyclerView newsRV;
    private static RVLoadMoreWrapper rvLoadMoreWrapper;
    private NewsAdapter newsAdapter;
    private List<NewsTitle> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //初始化
    public void init() {
        //findView
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        newsSRL = (SwipeRefreshLayout) findViewById(R.id.srl_news);
        newsRV = (RecyclerView) findViewById(R.id.rv_news);
        //init
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("首页");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.outline_menu_black_24);
        }
        navView.setCheckedItem(R.id.home);  //设置默认选中项

        newsList = new ArrayList<NewsTitle>();
        newsAdapter = new NewsAdapter(this, newsList);
        rvLoadMoreWrapper = new RVLoadMoreWrapper(newsAdapter);  //装饰者模式
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(rvLoadMoreWrapper);
        newsRV.addItemDecoration(new SpaceItemDecoration(30));
        newsSRL.setColorSchemeResources(R.color.myColorPrimary);
        //setListener
        navView.setNavigationItemSelectedListener(this);
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
                        getNews(oldNewsURL);
                    }
                }).start();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.login:
                Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            case R.id.night_mode:
                Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_offline_download:
                Toast.makeText(MainActivity.this, "正在施工", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(int position) {
        NewsTitle newsTitle = newsList.get(position);
        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
        intent.putExtra("id", newsTitle.getId());
        intent.putExtra("title", newsTitle.getTitle());
        startActivity(intent);
    }


    @Override
    public void onRefresh() {
        //刷新数据
        currentMode = MODE_REFRESH;
        getNews(latestNewsURL);
    }

    //获取新闻
    public void getNews(String newsURL) {
        HttpUtil.sendOkHttpRequest(newsURL, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 请求成功");
                String responseData = response.body().string();
                if (currentMode == MODE_REFRESH) {
                    newsList.clear();
                }
                //此处list中为新获取到的数据
                List<NewsTitle> list = ParseJSONUtil.parseJSONWithJSONObject(responseData);
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
                rvLoadMoreWrapper.setLoadState(rvLoadMoreWrapper.LOADING_COMPLETE);
                newsSRL.setRefreshing(false);
            }
        });
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



    @Override
    public void refreshRecyclerView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvLoadMoreWrapper.setLoadState(rvLoadMoreWrapper.LOADING_COMPLETE);
                newsSRL.setRefreshing(false);
            }
        });
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
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
            newsList.add(new NewsTitle("000", "TEST",
                    "https://pic3.zhimg.com//v2-392cc990dc15f01fa74b77f98e28b8b6.jpg", "0"));
        }
    }
}
