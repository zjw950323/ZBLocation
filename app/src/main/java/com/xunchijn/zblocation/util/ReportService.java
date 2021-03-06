package com.xunchijn.zblocation.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.xunchijn.zblocation.R;
import com.xunchijn.zblocation.map.presenter.LocationContrast;
import com.xunchijn.zblocation.map.presenter.LocationPresenter;

import java.util.Timer;
import java.util.TimerTask;

//上传服务

public class ReportService extends Service implements LocationContrast.View {
    private final String TAG = "ReportService";
    private String number;
    private String lon;
    private String lat;
    private String address;
    private LocationContrast.Presenter mPresenter;
    private NotificationUtils mNotificationUtils;
    private Notification notification;
    private PreferHelper mPreferHelper;
    private Timer mTimer;
    private TimerTask mTimerTask;
    Context mContext;
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mPreferHelper = new PreferHelper(mContext);
        new LocationPresenter(ReportService.this, this);
        if (Build.VERSION.SDK_INT >= 26) {
            mNotificationUtils = new NotificationUtils(this);
            Notification.Builder builder4 = mNotificationUtils.getAndroidChannelNotification
                    ("后台上传功能", "正在上传数据");
            notification = builder4.build();
        } else {
            //获取一个Notification构造器
            Notification.Builder builder3 = new Notification.Builder(ReportService.this);
            Intent nfIntent = new Intent(ReportService.this, ReportService.class);

            builder3.setContentIntent(PendingIntent.
                    getActivity(ReportService.this, 0, nfIntent, 0)) // 设置PendingIntent
                    .setContentTitle("后台上传功能") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("正在上传数据") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            notification = builder3.build(); // 获取构建好的Notification
        }
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //创建PowerManager对象
        number = mPreferHelper.getNumber();
        lon = mPreferHelper.getLon();
        lat = mPreferHelper.getLat();
        address = mPreferHelper.getAddress();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ReportService.class.getName());
                wakeLock.acquire();
                report();
                if (wakeLock != null) {
                    wakeLock.release();
                    wakeLock = null;
                }
            }
        };
        mTimer.schedule(mTimerTask, 10000, 30000);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void reportLocationSuccess() {
        Toast.makeText(ReportService.this, "上报成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void report() {
        if (TextUtils.isEmpty(number)) {
            showError("编号不能为空！");
            return;
        }
        if (TextUtils.isEmpty(lon)) {
            showError("经度不能为空！");
            return;
        }
        if (TextUtils.isEmpty(lat)) {
            showError("纬度不能为空！");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            showError("位置不能为空");
        }
        String lonLat = lon + "," + lat;
        String timeStamp = TimeUtils.getTimeStamp();
        if (mPresenter != null) {
            mPresenter.reportLocation(number, lon, lat, lonLat, address, timeStamp);
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(ReportService.this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(LocationContrast.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mTimer.cancel();
        mTimer = null;
        mTimerTask.cancel();
        mTimerTask = null;
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        stopSelf();
    }
}
