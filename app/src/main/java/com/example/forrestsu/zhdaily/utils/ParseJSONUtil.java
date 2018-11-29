package com.example.forrestsu.zhdaily.utils;

import android.util.Log;

import com.example.forrestsu.zhdaily.beans.NewsTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseJSONUtil {

    private static final String TAG = "ParseJSONUtil";

    //使用JSONObject解析请求结果
    public static List<NewsTitle> parseJSONWithJSONObject(String jsonData) {

        List<NewsTitle> list = new ArrayList<NewsTitle>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            JSONArray stories = jsonObject.getJSONArray("stories");

            for (int i = 0; i < stories.length(); i++) {
                JSONObject obj = stories.getJSONObject(i);
                String type = obj.getString("type");
                String id = obj.getString("id");
                String title = obj.getString("title");
                String imageUri = obj.getJSONArray("images").get(0).toString();
                list.add(new NewsTitle(id, title, imageUri, type));

                Log.i(TAG, "parseJSONWithJSONObject: type:" + type);
                Log.i(TAG, "parseJSONWithJSONObject: id:" + id);
                Log.i(TAG, "parseJSONWithJSONObject: title" + title);
                Log.i(TAG, "parseJSONWithJSONObject: imageUri:" + imageUri);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
