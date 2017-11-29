package com.administrator.sps;

import android.R.integer;
import android.R.menu;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    /**
     * 侧滑布局对象，用于通过手指滑动将左侧的菜单布局进行显示或隐藏。
     */
    private SlidingLayout slidingLayout;

    /**
     * menu按钮，点击按钮展示左侧布局，再点击一次隐藏左侧布局。
     */
    private Button menuButton;
    /**
     * 放在content布局中的ListView。
     */
    private ListView contentListView;
    /*
    * 响应触摸事件的区域
    */
    private LinearLayout ScrollEvent;

    // 百度地图控件
    private MapView mMapView = null;
    private Toast mToast;
    private LatLng latLng;
    private BMapManager mBMapManager;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public BDLocationListener mLocationListener = new MyLocationListener();
   // public BaiduMap.OnMapClickListener myListener1 = new MyOnMapClickListener();

    // 百度地图对象
    private BaiduMap bdMap;
    private String address;
    private LatLng mylatlng;
    private ArrayList<LatLng> latlng=new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initMap();
        initParam();
        //initListener();
        //initPark();
    }

    private void initParam(){
        slidingLayout = (SlidingLayout) findViewById(R.id.slidingLayout);
        menuButton = (Button) findViewById(R.id.menuButton);
        ScrollEvent = (LinearLayout) findViewById(R.id.Scrollevent);
        slidingLayout.setScrollEvent(ScrollEvent);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 实现点击一下menu展示左侧布局，再点击一下隐藏左侧布局的功能
                if (slidingLayout.isLeftLayoutVisible()) {
                    slidingLayout.scrollToRightLayout();
                } else {
                    slidingLayout.scrollToLeftLayout();
                }
            }
        });
    }
    /**
     * 初始化方法
     */
    private void initMap() {
        mMapView = (MapView) findViewById(R.id.bmapview);
        bdMap = mMapView.getMap();
        mMapView.removeViewAt(1);
        bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        bdMap = mMapView.getMap();
        // 改变地图状态
        MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        bdMap.setMapStatus(mMapStatusUpdate);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mLocationClient.start();
        //bdMap.setOnMapClickListener(myListener1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /*private void initPark(){
        latlng.add(new LatLng(30.32391910722308,120.348082203764487));
        //清空地图
       // bdMap.clear();
        //创建marker的显示图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.park);
        LatLng lat = null;
        Marker marker;
        OverlayOptions options;
        for(LatLng info:latlng){
            //获取经纬度
            lat = new LatLng(info.latitude,info.longitude);
            //设置marker
            options = new MarkerOptions()
                    .position(lat)//设置位置
                    .icon(bitmap)//设置图标样式
                    .zIndex(9) // 设置marker所在层级
                    .draggable(true); // 设置手势拖拽;
            //添加marker
            marker = (Marker) bdMap.addOverlay(options);
            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
        }
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(lat);
        bdMap.setMapStatus(msu);
        bdMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent=new Intent(MainActivity.this,Login.class);
                startActivity(intent);
                return true;
            }
        });
    }*/

    /*private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(false);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }*/

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");


            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            //定义Maker坐标点
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.landmark);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap)
                    .draggable(true);
            //在地图上添加Marker，并显示
            bdMap.addOverlay(option);
            bdMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point)); //更新地图状态
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    /*
    * 定位到自己当前位置
    *
    * */
   /*private void initlocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.local);
        bdMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker
        ));

        mLocationClient.registerLocationListener(mLocationListener);
        final LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//坐标类型
        option.setIsNeedAddress(true);//返回当前位置
        option.setOpenGps(true);
        option.setScanSpan(1000);//过多少秒请求一次
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        bdMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
            }
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                latLng = mapStatus.target;
                bdMap.clear();
            }
        });
    }*/

    /*private void initListener() {
        bdMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus status) {
                // updateMapState(status);
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus status) {
                bdMap.clear();
                int zoom = (int) status.zoom;
                updateMapState(status,mylatlng);
                if (zoom < 17) {
                    //这里放着你的地图覆盖物的添加函数
                    //setMakerPool(mylatlng);
                    latlng.add(new LatLng(30.32391910722308,120.348082203764487));
                    //清空地图
                    // bdMap.clear();
                    //创建marker的显示图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.park);
                    LatLng lat = null;
                    Marker marker;
                    OverlayOptions options;
                    for(LatLng info:latlng){
                        //获取经纬度
                        lat = new LatLng(info.latitude,info.longitude);
                        //设置marker
                        options = new MarkerOptions()
                                .position(lat)//设置位置
                                .icon(bitmap)//设置图标样式
                                .zIndex(9) // 设置marker所在层级
                                .draggable(true); // 设置手势拖拽;
                        //添加marker
                        marker = (Marker) bdMap.addOverlay(options);
                        //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
                    }
                }
            }

            @Override
            public void onMapStatusChange(MapStatus status) {
                // updateMapState(status);
            }
        });
    }*/

   // private void updateMapState(MapStatus status,LatLng latlng) {
       // LatLng mCenterLatLng = status.target;
        /*获取经纬度*/
        //latlng=new LatLng(mCenterLatLng.latitude, mCenterLatLng.longitude);
   // }

   /*  public class MyOnMapClickListener implements BaiduMap.OnMapClickListener {

        @Override
        public boolean onMapPoiClick(MapPoi arg0) {
            // TODO Auto-generated method stub
            return false;
        }

       //此方法就是点击地图监听
        @Override
        public void onMapClick(LatLng latLng) {
            //获取经纬度
            BitmapDescriptor Bitmap = BitmapDescriptorFactory.fromResource(R.drawable.local);
            final double latitude = latLng.latitude;
            final double longitude = latLng.longitude;
            //System.out.println("latitude=" + latitude + ",longitude=" + longitude);
            // 定义Maker坐标点
            LatLng point = new LatLng(latitude, longitude);
            // 构建MarkerOption，用于在地图上添加Marker
            MarkerOptions options = new MarkerOptions().position(point)
                    .icon(Bitmap).draggable(true);
            // 在地图上添加Marker，并显示
            bdMap.addOverlay(options);
            //实例化一个地理编码查询对象
            GeoCoder geoCoder = GeoCoder.newInstance();
            //设置反地理编码位置坐标
            ReverseGeoCodeOption op = new ReverseGeoCodeOption();
            op.location(latLng);
            //发起反地理编码请求(经纬度->地址信息)
            geoCoder.reverseGeoCode(op);
            geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
                    //获取点击的坐标地址
                    address = arg0.getAddress();
                }
                @Override
                public void onGetGeoCodeResult(GeoCodeResult arg0) {
                }
            });

            LatLng llText = new LatLng(latitude, longitude);
            //构建文字Option对象，用于在地图上添加文字
            OverlayOptions textOption = new TextOptions()
                    //.bgColor(0xAAFFFF00)
                    .fontSize(16)
                    .fontColor(Color.BLACK)
                    .text(address+latitude+" "+longitude+"here")
                    //.rotate(-30)
                    .position(llText);
            //在地图上添加该文字对象并显示
            bdMap.addOverlay(textOption);

        }
    }*/
}