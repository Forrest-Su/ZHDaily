package com.example.forrestsu.zhdaily.utils;

import android.util.Log;

import com.example.forrestsu.zhdaily.beans.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseJSONUtil {

    private static final String TAG = "ParseJSONUtil";

    /**
     * 解析新闻列表
     * @param jsonData
     * @return
     */
    public static List<News> parseNewsList(String jsonData) {

        List<News> list = new ArrayList<News>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            JSONArray stories = jsonObject.getJSONArray("stories");

            for (int i = 0; i < stories.length(); i++) {
                JSONObject obj = stories.getJSONObject(i);
                String title = obj.getString("title");
                String imagesUrl = obj.getJSONArray("images").get(0).toString();
                String type = obj.getString("type");
                String id = obj.getString("id");

                list.add(new News(title, imagesUrl, type, id));

                Log.i(TAG, "parseNewsList: 新闻列表");
                Log.i(TAG, "parseNewsList: type:" + type);
                Log.i(TAG, "parseNewsList: id:" + id);
                Log.i(TAG, "parseNewsList: title" + title);
                Log.i(TAG, "parseNewsList: imageUri:" + imagesUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析置顶新闻列表
     * @param jsonData
     * @return
     * 注意：新闻列表和置顶新闻列表的imageUrl的解析方法是有区别的，虽然看似结构相同，但不能用同一个方法解析
     */
    public static List<News> parseTopNewsList(String jsonData) {
        List<News> list = new ArrayList<News>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray topStories = jsonObject.getJSONArray("top_stories");

            for (int i = 0; i < topStories.length(); i++) {
                JSONObject obj = topStories.getJSONObject(i);
                String title = obj.getString("title");
                String imagesUrl = obj.getString("image");
                String type = obj.getString("type");
                String id = obj.getString("id");

                list.add(new News(title, imagesUrl, type, id));

                Log.i(TAG, "parseTopNewsList: 置顶新闻列表");
                Log.i(TAG, "parseNewsList: type:" + type);
                Log.i(TAG, "parseNewsList: id:" + id);
                Log.i(TAG, "parseNewsList: title" + title);
                Log.i(TAG, "parseNewsList: imageUri:" + imagesUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 解析新闻详情
     * @param jsonData
     * @return
     */
    public static News parseNews(String jsonData) {
        News news = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            String body = jsonObject.getString("body");  //新闻主体，html
            String imageSource = jsonObject.getString("image_source"); //图片来源
            String title = jsonObject.getString("title");  //新闻标题
            String imageUrl = jsonObject.getString("image");  //大图url
            String shareUrl = jsonObject.getString("share_url");  //分享链接
            String imagesUrl = jsonObject.getString("images");  //缩略图url
            String type = jsonObject.getString("type");  //
            String id = jsonObject.getString("id");  //新闻id
            JSONArray jsonArray = jsonObject.getJSONArray("css");
            String[] css = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                css[i] = jsonArray.get(i).toString();
            }

            news = new News(body, imageSource, title, imageUrl, shareUrl, imagesUrl, type, id, css);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return news;
    }
}
