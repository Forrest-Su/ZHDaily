package com.example.forrestsu.zhdaily.my_interface;

/**
 * Http请求回调接口
 */
public interface HttpCallBackListener {
    //请求成功
    void onFinish(String response);
    //请求出错
    void onerror(Exception e);
}
