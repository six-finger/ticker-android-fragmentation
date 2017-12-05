package me.yokeyword.sample.demo_wechat.net;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.sample.R;
import me.yokeyword.sample.demo_wechat.entity.Alert;

/**
 * Created by charles on 2017/12/5 0005.
 */

public class ServiceDemo extends IntentService {
    private List<Alert> list = (new AlertManager()).getTriggeredAlert();

    public ServiceDemo() {
        super("ServiceDemo");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("Service onHandleIntent");

        try {
            while(true){
                list = (new AlertManager()).getTriggeredAlert();
//                for(int i = 0; i < 1; i++){
                for (Alert alert:list){
                    Intent intent1 = new Intent("CLOCK");
                    intent.putExtra("msg", "lalalala");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pendingIntent);
                    System.out.println("Service am");

                    setNotification(alert);
                }
                (new AlertManager()).cleanAlert();
                Thread.sleep(1000);
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    //设置提示栏提示
    private void setNotification(Alert alert){
        try {
            NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle(alert.alert_name)//设置通知栏标题
                    .setContentText(alert.alert_msg) ///<span style="font-family: Arial;">/设置通知栏显示内容</span>
//                            .setNumber(number) //设置通知集合的数量
                    .setTicker("Ticker提醒") //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
//  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                    .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
            mNotifyManager.notify(1, mBuilder.build());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service onDestroy");
    }
}
