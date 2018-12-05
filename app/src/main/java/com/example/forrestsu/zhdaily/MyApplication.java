package com.example.forrestsu.zhdaily;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.forrestsu.zhdaily.activity.NewsActivity;
import com.example.forrestsu.zhdaily.notification.MyNotificationManager;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    //private static Context context;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        //context = getApplicationContext();

        //解决android 7.0系统拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        /*
        友盟
        在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
        参数一：当前上下文context；
        参数二：应用申请的Appkey（需替换）；
        参数三：渠道名称；
        参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
        参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
        */
        UMConfigure.init(this, "5c05f3cab465f50d75000098", "Umeng",
                UMConfigure.DEVICE_TYPE_PHONE, "4f921d39678006e060d91d85ae96b6de");

        initUpush();
    }


    //初始化Upush
    private void initUpush() {
        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //设置显示最多通知条数，超出设定将旧通知隐藏
        mPushAgent.setDisplayNotificationNumber(3);
        //设置消息免打扰时间，23：00至07：00
        mPushAgent.setNoDisturbMode(23, 0, 7, 0);
        //关闭消息免打扰
        //mPushAgent.setNoDisturbMode(0, 0, 0, 0);
        //设置30秒内同一应用接收到多条通知时不重复提醒
        mPushAgent.setMuteDurationSeconds(30);
        handler = new Handler(getMainLooper());
        //应用在前台，不显示通知消息，默认为显示
        //mPushAgent.setNotificaitonOnForeground(false);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            /*
            通知的回调方法，通知到达时会回调
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                //调用super会展示通知，不调用super不会展示通知
                super.dealWithNotificationMessage(context, msg);
            }

            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /*
            自定义通知栏样式
            */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        return MyNotificationManager.getDefaultNotification(context, msg.title, msg.text);
                        /*
                        NotificationCompat.Builder builder;
                        if (Build.VERSION.SDK_INT >= 26) {
                            //
                            NotificationManager notificationManager
                                    = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                            //创建NotificationChannel()
                            NotificationChannel channelWithSound = new NotificationChannel(
                                    "channelWithSound", "channelWithSound", NotificationManager.IMPORTANCE_DEFAULT);
                            channelWithSound.setShowBadge(true); //长按图标时是否显示通知内容
                            //channelWithSound.enableLights(true); //设置通知来时，是否亮起呼吸灯
                            //channelWithSound.setLightColor(Color.GREEN); //设置呼吸灯颜色
                            //channelWithSound.setSound(null, null); /设置通知声音
                            notificationManager.createNotificationChannel(channelWithSound);
                            builder = new NotificationCompat.Builder(context, "channelWithSound");
                        } else {
                            builder = new NotificationCompat.Builder(context, null);
                        }

                        RemoteViews  myNotificationView = new RemoteViews(
                                context.getPackageName(), R.layout.notification_test);
                        myNotificationView.setTextViewText(R.id.tv_title, msg.title);
                        myNotificationView.setTextViewText(R.id.tv_content, msg.text);

                        builder.setCustomContentView(myNotificationView)
                                .setSmallIcon(R.drawable.android_white_18dp)
                                .setAutoCancel(true);
                        return builder.build();
                        */
                    default:
                        //默认为0，若builder_id不存在，使用默认
                        return super.getNotification(context, msg);

                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                //Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                String jsonData = msg.custom;
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    int type = jsonObject.getInt("type");
                    if (type == 0) {
                        String title = jsonObject.getString("title");  //新闻标题
                        String id = jsonObject.getString("id");  //新闻id
                        Intent intent = new Intent(context, NewsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("title", title);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //使用自定义的NotificationHandler
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG,"注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG,"注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
    }

    /*
    不能用这个方法
    方法：获取Context对象
    public static Context getContext() {
        return context;
    }
    */
}
