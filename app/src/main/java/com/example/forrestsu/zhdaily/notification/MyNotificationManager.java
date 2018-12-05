package com.example.forrestsu.zhdaily.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.forrestsu.zhdaily.R;


public class MyNotificationManager {
    private static final String TAG = "MyNotificationManager";

    //PendingIntent.getBroadcast()的第二个参数RequestCode
    private static final int PREPARING = 0;
    private static final int DOWNLOADING = 1;
    private static final int PAUSE = 2;
    private static final int CANCEL = 3;
    private static final int SUCCESS = 4;
    private static final int FAILED = 5;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initChannel(Context context) {
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //创建NotificationChannel()
        NotificationChannel channelWithSound = new NotificationChannel(
                "channelWithSound", "channelWithSound", NotificationManager.IMPORTANCE_DEFAULT);
        channelWithSound.setShowBadge(true); //长按图标时是否显示通知内容
        //channelWithSound.enableLights(true); //设置通知来时，是否亮起呼吸灯
        //channelWithSound.setLightColor(Color.GREEN); //设置呼吸灯颜色
        //channelWithSound.setSound(null, null); /设置通知声音
        notificationManager.createNotificationChannel(channelWithSound);

        //创建NotificationChannel   IMPORTANCE_LOW
        NotificationChannel channelWithoutSound = new NotificationChannel(
                "channelWithoutSound", "channelWithoutSound", NotificationManager.IMPORTANCE_LOW);
        channelWithoutSound.setShowBadge(true); //长按图标时是否显示通知内容
        notificationManager.createNotificationChannel(channelWithoutSound);
    }


    /**
     * 默认通知，有声音提示
     * @param context
     * @param title 通知标题
     * @param content 通知内容
     */
    public static Notification getDefaultNotification(Context context, String title, String content) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            initChannel(context);
            builder = new NotificationCompat.Builder(context, "channelWithSound");
        } else {
            builder = new NotificationCompat.Builder(context, null);
        }

        RemoteViews defaultRemoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_test);
        defaultRemoteViews.setTextViewText(R.id.tv_title, title);
        defaultRemoteViews.setTextViewText(R.id.tv_content, content);

        builder.setCustomContentView(defaultRemoteViews) //设置自定义布局
            .setCustomBigContentView(defaultRemoteViews)
            //.setContentTitle(title) //设置通知标题
            //.setContentText(content) //设置通知内容
            .setWhen(System.currentTimeMillis()) //设置通知时间，这是获取了当前系统时间
            .setSmallIcon(R.drawable.android_black_48_48) //设置通知图标（系统状态栏上显示的小图标）
            //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //关闭声音、震动
            //.setLights(Color.GREEN, 1000, 1000);//设置呼吸灯
            .setAutoCancel(true); //点击通知后取消显示
        return builder.build();
    }

}
