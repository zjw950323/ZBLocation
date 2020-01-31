package com.xunchijn.zblocation;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunchijn.zblocation.map.presenter.LocationContrast;
import com.xunchijn.zblocation.map.presenter.LocationPresenter;
import com.xunchijn.zblocation.util.NotificationUtils;
import com.xunchijn.zblocation.util.PreferHelper;
import com.xunchijn.zblocation.util.TimeUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends Activity implements LocationContrast.View {
    private final String TAG = "MapActivityService";
    private String permissionInfo;
    private final int SDK_PERMISSION_REQUEST = 127;
    private LocationClient mClient;
    private MyLocationListener myLocationListener = new MyLocationListener();

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button mForegroundBtn;

    private NotificationUtils mNotificationUtils;
    private Notification notification;

    private boolean isFirstLoc = true;
    private boolean isEnableLocInForeground = false;
    private LocationContrast.Presenter mPresenter;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private String number;
    private String lon;
    private String lat;
    private String address;
    private EditText mEditPosition;
    private PreferHelper mPreferHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mPreferHelper = new PreferHelper(MapActivity.this);
        mEditPosition = findViewById(R.id.edit_flag);
        getPersimmions();
        initViews();
        new LocationPresenter(MapActivity.this, this);

        // 定位初始化
        mClient = new LocationClient(this);
        LocationClientOption mOption = new LocationClientOption();
        mOption.setScanSpan(5000);
        mOption.setCoorType("bd09ll");
        mOption.setIsNeedAddress(true);
        mOption.setOpenGps(true);
        mClient.setLocOption(mOption);
        mClient.registerLocationListener(myLocationListener);
        mClient.start();

        //设置后台定位
        //android8.0及以上使用NotificationUtils
        if (Build.VERSION.SDK_INT >= 26) {
            mNotificationUtils = new NotificationUtils(this);
            Notification.Builder builder2 = mNotificationUtils.getAndroidChannelNotification
                    ("后台定位功能", "正在后台定位");
            notification = builder2.build();
        } else {
            //获取一个Notification构造器
            Notification.Builder builder = new Notification.Builder(MapActivity.this);
            Intent nfIntent = new Intent(MapActivity.this, MapActivity.class);

            builder.setContentIntent(PendingIntent.
                    getActivity(MapActivity.this, 0, nfIntent, 0)) // 设置PendingIntent
                    .setContentTitle("后台定位功能") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("正在后台定位") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            notification = builder.build(); // 获取构建好的Notification
        }
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(MapActivity.this.getPackageName());
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
            if (!hasIgnored) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + MapActivity.this.getPackageName()));
                startActivity(intent);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
        mClient.disableLocInForeground(true);
        mClient.unRegisterLocationListener(myLocationListener);
        mClient.stop();
    }


    private void initViews() {
        mForegroundBtn = (Button) findViewById(R.id.bt_foreground);
        mForegroundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableLocInForeground) {
                    //关闭后台定位（true：通知栏消失；false：通知栏可手动划除）
                    mClient.disableLocInForeground(true);
                    mEditPosition.setFocusable(true);
                    mEditPosition.setFocusableInTouchMode(true);
                    isEnableLocInForeground = false;
                    mForegroundBtn.setText(R.string.startforeground);
                    mTimer.cancel();
                    mTimer = null;
                    mTimerTask.cancel();
                    mTimerTask = null;
                } else {
                    //开启后台定位
                    number = mEditPosition.getText().toString();
                    mPreferHelper.saveNumber(number);
                    if (TextUtils.isEmpty(number)){
                        Toast.makeText(MapActivity.this, "编号不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mEditPosition.setFocusable(false);
                    mEditPosition.setFocusableInTouchMode(false);
                    mClient.enableLocInForeground(1, notification);
                    isEnableLocInForeground = true;
                    mForegroundBtn.setText(R.string.stopforeground);
                    mTimer = new Timer();
                    mTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            number = mPreferHelper.getNumber();
                            lon = mPreferHelper.getLon();
                            lat = mPreferHelper.getLat();
                            address = mPreferHelper.getAddress();
                            report();
                        }
                    };
                    mTimer.schedule(mTimerTask, 10000, 30000);
                }
            }
        });
        mMapView = (MapView) findViewById(R.id.mv_foreground);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
    }

    @Override
    public void reportLocationSuccess() {
        Toast.makeText(MapActivity.this, "上报成功", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(MapActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(LocationContrast.Presenter presenter) {
        mPresenter = presenter;
    }


    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder().accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            //地图SDK处理
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(bdLocation.getLatitude(),
                        bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            LatLng point = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            lon = bdLocation.getLongitude() + "";
            lat = bdLocation.getLatitude() + "";
            address = bdLocation.getAddrStr();
            OverlayOptions dotOption = new DotOptions().center(point).color(0xAAFF0000);
            mBaiduMap.addOverlay(dotOption);
            mPreferHelper.saveLon(lon);
            mPreferHelper.saveLat(lat);
            mPreferHelper.saveAddress(address);
            Log.d(TAG, "onReceiveLocation: " + point.toString());
            Toast.makeText(MapActivity.this,point.toString(),Toast.LENGTH_SHORT).show();
        }
    }

}

