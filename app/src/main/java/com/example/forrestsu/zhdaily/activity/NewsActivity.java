package com.example.forrestsu.zhdaily.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.forrestsu.zhdaily.R;

public class NewsActivity extends BaseActivity {

    private static final String TAG = "NewsActivity";

    private TextView textView;
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
    }

    public void init() {
        textView = (TextView) findViewById(R.id.text_view);
        textView.setText(("id:" + id + "\ntitle:" + title));
    }
}
