package com.android.hcbd.whsw.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.adapter.MapDeviceStateAdapter;
import com.android.hcbd.whsw.entity.DeviceStateListInfo;
import com.android.hcbd.whsw.entity.LocationInfo;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.listener.MyLocationListener;
import com.android.hcbd.whsw.listener.MyOrientationListener;
import com.android.hcbd.whsw.ui.activity.LineSelectActivity;
import com.android.hcbd.whsw.utils.HttpUrlUtils;
import com.android.hcbd.whsw.utils.LogUtils;
import com.android.hcbd.whsw.utils.ToastUtils;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 首页地图
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.home_toolbar)
    Toolbar toolbar;
    Unbinder unbinder;
    @BindView(R.id.tv_disconnect)
    TextView tvDisconnect;
    @BindView(R.id.tv_normal)
    TextView tvNormal;
    @BindView(R.id.tv_danger)
    TextView tvDanger;
    @BindView(R.id.bmapView)
    MapView mMapView;
    @BindView(R.id.ib_large)
    ImageButton ibLarge;
    @BindView(R.id.ib_small)
    ImageButton ibSmall;
    @BindView(R.id.ib_mode)
    ImageButton ibMode;
    @BindView(R.id.ib_loc)
    ImageButton ibLoc;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.rl_map_layout)
    RelativeLayout rlMapLayout;

    private SearchView mSearchView;

    private DeviceStateListInfo deviceStateListInfo;

    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDLocationListener mBDLocationListener = new MyLocationListener();
    private boolean isFirstLocation = true;
    //模式切换，正常模式
    private boolean modeFlag = true;
    //当前地图缩放级别
    private float zoomLevel;
    /**
     * 方向传感器的监听器
     */
    private MyOrientationListener myOrientationListener;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;
    private LocationInfo mCurrentlocation;

    private List<DeviceStateListInfo.DataInfo> normalList = new ArrayList<>();
    private List<DeviceStateListInfo.DataInfo> dangerList = new ArrayList<>();
    private List<DeviceStateListInfo.DataInfo> disconnectList = new ArrayList<>();
    private List<DeviceStateListInfo.DataInfo> searchList = new ArrayList<>();
    //设置定时器
    private Timer timer;
    // private String creatTime;

    private boolean isrefresh = true;

    private boolean isHidden = false;

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("首页地图");
        mBaiduMap = mMapView.getMap();

        // 隐藏百度的LOGO
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 是否显示地图上比例尺
        mMapView.showScaleControl(true);
        // 是否显示地图缩放控件（按钮控制栏）
        mMapView.showZoomControls(false);

        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setIndoorEnable(true);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));

        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setOverlookingGesturesEnabled(false);

        mLocationClient = new LocationClient(getActivity());     //声明LocationClient类
        mLocationClient.registerLocationListener(mBDLocationListener);
        initLocation();
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        /*BitmapDescriptor bitmap = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_geo);*/
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null,4521984,4521984);
        mBaiduMap.setMyLocationConfiguration(config);
        httpData();
        initListener();
        initOritationListener();
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getEventId()) {
            case MessageEvent.EVENT_LOCATION_SUCCESS:
                mCurrentlocation = (LocationInfo) event.getObj();
                if(mCurrentlocation == null)
                    return;
                if (isFirstLocation) {
                    isFirstLocation = false;
                    if (mBaiduMap.getMapStatus().zoom < 12) {
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
                        ibLarge.setEnabled(true);
                        ibLarge.setImageResource(R.drawable.icon_zoomin);
                        ibSmall.setEnabled(true);
                        ibSmall.setImageResource(R.drawable.icon_zoomout);
                    }
                    LatLng ll = new LatLng(mCurrentlocation.getLatitude(), mCurrentlocation.getLongitude());
                    MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(update);
                }
                //构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mCurrentlocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection).latitude(mCurrentlocation.getLatitude())
                        .longitude(mCurrentlocation.getLongitude()).build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                break;
            case MessageEvent.EVENT_DEVICE_EDIT_RESULT:
                httpData();
                break;
        }
    }

    private void httpData() {
        OkGo.<String>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.device_state_list_url)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())


                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        LogUtils.LogShow(result);
                        realjson(result);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void realjson(final String json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    if (deviceStateListInfo == null)
                        deviceStateListInfo = new DeviceStateListInfo();

                    deviceStateListInfo.setIndexRate(jsonObject.getString("indexRate"));
                    deviceStateListInfo.setDanger(jsonObject.getString("danger"));
                    deviceStateListInfo.setNormal(jsonObject.getString("normal"));
                    deviceStateListInfo.setDisconnect(jsonObject.getString("disconnect"));

                    Gson gson = new Gson();
                    JSONArray array = new JSONArray(jsonObject.getString("data"));
                    List<DeviceStateListInfo.DataInfo> dataInfoList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        DeviceStateListInfo.DataInfo dataInfo = gson.fromJson(array.getString(i), DeviceStateListInfo.DataInfo.class);
                        dataInfoList.add(dataInfo);
                    }
                    deviceStateListInfo.setDataInfoList(dataInfoList);

                    Message message = new Message();
                    message.what = 0x10;
                    mHandler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x10:
                    if (isrefresh) {
                        isrefresh = false;
                        openTime();
                    }

                    /*SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss ");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    creatTime = formatter.format(curDate);*/

                    tvDisconnect.setText(Html.fromHtml("断开" + "<font color=\"#ff9000\">（" + deviceStateListInfo.getDisconnect() + "）</font>"));
                    tvNormal.setText(Html.fromHtml("正常" + "<font color=\"#00ff00\">（" + deviceStateListInfo.getNormal() + "）</font>"));
                    tvDanger.setText(Html.fromHtml("危险" + "<font color=\"#ff1919\">（" + deviceStateListInfo.getDanger() + "）</font>"));
                    mBaiduMap.clear();
                    final List<DeviceStateListInfo.DataInfo> dataInfoList = deviceStateListInfo.getDataInfoList();
                    for (int i = 0; i < dataInfoList.size(); i++) {
                        //定义Maker坐标点
                        LatLng point = new LatLng(Double.parseDouble(dataInfoList.get(i).getY()), Double.parseDouble(dataInfoList.get(i).getX()));
                        System.out.println("转换前的坐标：" + point.toString());
                        CoordinateConverter converter = new CoordinateConverter();
                        converter.from(CoordinateConverter.CoordType.GPS);
                        // sourceLatLng待转换坐标
                        converter.coord(point);
                        //double[] dd = GPSUtils.gps84_To_bd09(Double.parseDouble(dataInfoList.get(i).getY()),Double.parseDouble(dataInfoList.get(i).getX()));
                        final LatLng desLatLng = converter.convert();
                        System.out.println("转换后的坐标：" + desLatLng.toString());

                        final int finalI = i;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = null;
                                try {
                                    bitmap = Glide.with(getActivity())
                                            .load(MyApplication.getInstance().getBsaeUrl() + dataInfoList.get(finalI).getImg())
                                            .asBitmap()
                                            .override(60, 72)
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                            .get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                final Bitmap finalBitmap = bitmap;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //构建Marker图标
                                        BitmapDescriptor bt = null;
                                        if (finalBitmap == null) {
                                            bt = BitmapDescriptorFactory.fromResource(R.drawable.ic_mapdevice);
                                        } else {
                                            bt = BitmapDescriptorFactory.fromBitmap(finalBitmap);
                                        }
                                        OverlayOptions option = new MarkerOptions()
                                                .position(desLatLng)
                                                .icon(bt)
                                                .title(dataInfoList.get(finalI).getName());
                                        //在地图上添加Marker，并显示
                                        mBaiduMap.addOverlay(option);
                                    }
                                });
                            }
                        }).start();
                    }
                    normalList.clear();
                    dangerList.clear();
                    disconnectList.clear();
                    for (int i = 0; i < deviceStateListInfo.getDataInfoList().size(); i++) {
                        if (deviceStateListInfo.getDataInfoList().get(i).getState().equals("正常"))
                            normalList.add(deviceStateListInfo.getDataInfoList().get(i));
                        if (deviceStateListInfo.getDataInfoList().get(i).getState().equals("危险"))
                            dangerList.add(deviceStateListInfo.getDataInfoList().get(i));
                        if (deviceStateListInfo.getDataInfoList().get(i).getState().equals("断开"))
                            disconnectList.add(deviceStateListInfo.getDataInfoList().get(i));
                    }
                    break;
            }
        }
    };

    private void getIndexRate() {
        OkGo.<String>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.get_index_rate_url)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        LogUtils.LogShow(result);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            if (!TextUtils.isEmpty(jsonObject.getString("data"))) {
                                if (!jsonObject.getString("data").equals(deviceStateListInfo.getIndexRate())) {
                                    httpData();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                if (!TextUtils.isEmpty(jsonObject.getString("error"))) {

                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                });
    }

    /*private void queryDeviceData(){
        OkGo.<String>post(HttpUrlUtils.query_device_data_url)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())
                .params("beginTime", creatTime)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        LogUtils.LogShow(result);

                    }
                });
    }*/

    private void initListener() {
        tvDisconnect.setOnClickListener(this);
        tvNormal.setOnClickListener(this);
        tvDanger.setOnClickListener(this);
        ibSmall.setOnClickListener(this);
        ibLarge.setOnClickListener(this);
        ibLoc.setOnClickListener(this);
        ibMode.setOnClickListener(this);
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                int width = ibLoc.getMeasuredWidth();
                int height = mMapView.getMeasuredHeight();
                mMapView.setScaleControlPosition(new Point(width + width / 2, height - mMapView.getScaleControlViewHeight() - width / 2));
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < deviceStateListInfo.getDataInfoList().size(); i++) {
                    LatLng point = new LatLng(Double.parseDouble(deviceStateListInfo.getDataInfoList().get(i).getY()), Double.parseDouble(deviceStateListInfo.getDataInfoList().get(i).getX()));
                    CoordinateConverter converter = new CoordinateConverter();
                    converter.from(CoordinateConverter.CoordType.GPS);
                    // sourceLatLng待转换坐标
                    converter.coord(point);
                    LatLng desLatLng = converter.convert();
                    if (desLatLng.toString().equals(marker.getPosition().toString())) {
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.map_window_layout,null);
                        TextView tv = (TextView) view.findViewById(R.id.tv);
                        tv.setText(deviceStateListInfo.getDataInfoList().get(i).getName());

                        //Button button = new Button(getActivity());
                        //button.setText(deviceStateListInfo.getDataInfoList().get(i).getName());
                        InfoWindow mInfoWindow = new InfoWindow(view,desLatLng, -60);
                        mBaiduMap.showInfoWindow(mInfoWindow);
                        showMarkerPopupWinder(deviceStateListInfo.getDataInfoList().get(i), desLatLng);
                        break;
                    }
                }
                return false;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                mBaiduMap.hideInfoWindow();
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
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
                if (Math.abs(zoomLevel - mapStatus.zoom) < 0.000001) {
                    return;
                }
                zoomLevel = mapStatus.zoom;
                if (zoomLevel == mBaiduMap.getMinZoomLevel() + 1) {
                    ibSmall.setImageResource(R.drawable.icon_zoomout_dis);
                    ibSmall.setEnabled(false);
                    ToastUtils.showShortToast(MyApplication.getInstance(), "已缩小至最低级别");
                    ibLarge.setEnabled(true);
                    ibLarge.setImageResource(R.drawable.icon_zoomin);
                } else if (zoomLevel == (modeFlag ? mBaiduMap.getMaxZoomLevel() - 1 : mBaiduMap.getMaxZoomLevel())) {
                    ibLarge.setImageResource(R.drawable.icon_zoomin_dis);
                    ibLarge.setEnabled(false);
                    ToastUtils.showShortToast(MyApplication.getInstance(), "已放大至最高级别");
                    ibSmall.setEnabled(true);
                    ibSmall.setImageResource(R.drawable.icon_zoomout);
                } else {
                    ibLarge.setEnabled(true);
                    ibLarge.setImageResource(R.drawable.icon_zoomin);
                    ibSmall.setEnabled(true);
                    ibSmall.setImageResource(R.drawable.icon_zoomout);
                }
            }
        });

    }

    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(getActivity());
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

            @Override
            public void onOrientationChanged(float x) {
                mXDirection = (int) x;
                //构造定位数据
                System.out.println("direction = " + mXDirection);
                if(mCurrentlocation == null)
                    return;
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mCurrentlocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection).latitude(mCurrentlocation.getLatitude())
                        .longitude(mCurrentlocation.getLongitude()).build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list:
                showDevicePopupWindow(0);
                break;
            case R.id.action_search:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_map, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        if(mSearchView == null)
            return;
        mSearchView.setQueryHint("请输入...");
        setCursorIcon();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>0){
                    System.out.println("query = "+query);
                    searchList.clear();
                    for (int i = 0; i < deviceStateListInfo.getDataInfoList().size(); i++) {
                        if (deviceStateListInfo.getDataInfoList().get(i).getName().indexOf(query) != -1
                                || deviceStateListInfo.getDataInfoList().get(i).getSn().indexOf(query) != -1)
                            searchList.add(deviceStateListInfo.getDataInfoList().get(i));
                    }
                    mSearchView.clearFocus();
                    if(searchList.size() == 1){
                        LatLng ll = new LatLng(Double.valueOf(searchList.get(0).getY()), Double.valueOf(searchList.get(0).getX()));
                        CoordinateConverter converter = new CoordinateConverter();
                        converter.from(CoordinateConverter.CoordType.GPS);
                        // sourceLatLng待转换坐标
                        converter.coord(ll);
                        LatLng desLatLng = converter.convert();
                        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(desLatLng);
                        mBaiduMap.animateMapStatus(update);
                    }else{
                        showDevicePopupWindow(4);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setCursorIcon(){
        try {
            Class cls = Class.forName("android.support.v7.widget.SearchView");
            Field field = cls.getDeclaredField("mSearchSrcTextView");
            field.setAccessible(true);
            TextView tv  = (TextView) field.get(mSearchView);

            Class[] clses = cls.getDeclaredClasses();
            for(Class cls_ : clses){
                if(cls_.toString().endsWith("android.support.v7.widget.SearchView$SearchAutoComplete")) {
                    Class targetCls = cls_.getSuperclass().getSuperclass().getSuperclass().getSuperclass();
                    Field cuosorIconField = targetCls.getDeclaredField("mCursorDrawableRes");
                    cuosorIconField.setAccessible(true);
                    cuosorIconField.set(tv, R.drawable.cursor_color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        locationStart();
        // 开启方向传感器
        myOrientationListener.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        //mMapView.onResume();
        if (!isHidden) {
            mMapView.onResume();
            LogUtils.LogShow("onResume");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        //mMapView.onPause();
        if (!isHidden) {
            mMapView.onPause();
            LogUtils.LogShow("onPause");
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        locationStop();
        // 关闭方向传感器
        myOrientationListener.stop();
        closeTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        if (isHidden) {
            mMapView.onPause();
            LogUtils.LogShow("onPause");
            closeTimer();
        } else {
            mMapView.onResume();
            LogUtils.LogShow("onResume");
            openTime();
        }

    }

    private void openTime() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getIndexRate();
                }
            }, 0, 6 * 1000);
        }
    }

    private void closeTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void locationStart() {
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    public void locationStop() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_disconnect:
                showDevicePopupWindow(1);
                break;
            case R.id.tv_normal:
                showDevicePopupWindow(2);
                break;
            case R.id.tv_danger:
                showDevicePopupWindow(3);
                break;
            case R.id.ib_large:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                zoomLevel = mBaiduMap.getMapStatus().zoom;
                LogUtils.LogShow("zoomLevel = " + zoomLevel);
                LogUtils.LogShow("getMaxZoomLevel = " + mBaiduMap.getMaxZoomLevel());
                if (zoomLevel < (modeFlag ? mBaiduMap.getMaxZoomLevel() - 1 : mBaiduMap.getMaxZoomLevel())) {
                    ibSmall.setEnabled(true);
                    ibSmall.setImageResource(R.drawable.icon_zoomout);
                } else {
                    ibLarge.setImageResource(R.drawable.icon_zoomin_dis);
                    ibLarge.setEnabled(false);
                    ToastUtils.showShortToast(MyApplication.getInstance(), "已放大至最高级别");
                }
                break;
            case R.id.ib_small:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                zoomLevel = mBaiduMap.getMapStatus().zoom;
                LogUtils.LogShow("zoomLevel = " + zoomLevel);
                LogUtils.LogShow("getMinZoomLevel = " + mBaiduMap.getMinZoomLevel());
                if (zoomLevel > mBaiduMap.getMinZoomLevel() + 1) {
                    ibLarge.setEnabled(true);
                    ibLarge.setImageResource(R.drawable.icon_zoomin);
                } else {
                    ibSmall.setImageResource(R.drawable.icon_zoomout_dis);
                    ibSmall.setEnabled(false);
                    ToastUtils.showShortToast(MyApplication.getInstance(), "已缩小至最低级别");
                }
                break;
            case R.id.ib_loc:
                isFirstLocation = true;
                break;
            case R.id.ib_mode:
                if (modeFlag) {
                    modeFlag = false;
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                } else {
                    modeFlag = true;
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
                zoomLevel = mBaiduMap.getMapStatus().zoom;
                if (zoomLevel == mBaiduMap.getMinZoomLevel()) {
                    ibSmall.setImageResource(R.drawable.icon_zoomout_dis);
                    ibSmall.setEnabled(false);
                    ibLarge.setEnabled(true);
                    ibLarge.setImageResource(R.drawable.icon_zoomin);
                } else if (zoomLevel == (modeFlag ? mBaiduMap.getMaxZoomLevel() - 1 : mBaiduMap.getMaxZoomLevel())) {
                    ibLarge.setImageResource(R.drawable.icon_zoomin_dis);
                    ibLarge.setEnabled(false);
                    ibSmall.setEnabled(true);
                    ibSmall.setImageResource(R.drawable.icon_zoomout);
                } else {
                    ibLarge.setEnabled(true);
                    ibLarge.setImageResource(R.drawable.icon_zoomin);
                    ibSmall.setEnabled(true);
                    ibSmall.setImageResource(R.drawable.icon_zoomout);
                }
                break;
        }
    }

    PopupWindow popupWindow;
    private void showMarkerPopupWinder(final DeviceStateListInfo.DataInfo data, final LatLng latLng) {
        if(popupWindow != null){
            popupWindow.dismiss();
            popupWindow = null;
        }
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_map_marker_info_layout, null);
        popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        final LinearLayout ll_layout = (LinearLayout) popupView.findViewById(R.id.ll_layout);
        TextView tvName = (TextView) popupView.findViewById(R.id.tv_name);
        TextView tvSn = (TextView) popupView.findViewById(R.id.tv_sn);
        final TextView tvAddress = (TextView) popupView.findViewById(R.id.tv_address);
        TextView tvPoint = (TextView) popupView.findViewById(R.id.tv_point);
        ImageView ivFollow = (ImageView) popupView.findViewById(R.id.iv_follow);
        final ProgressBar progress = (ProgressBar) popupView.findViewById(R.id.progress);
        tvName.setText("设备名称：" + data.getName());
        tvSn.setText("SN：" + data.getSn());
        tvPoint.setText("坐标：" + data.getPoint());

        final GeoCoder mSearch = GeoCoder.newInstance();
        final String[] addr = new String[1];
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }
                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    tvAddress.setText("");
                    return;
                }
                //获取反向地理编码结果
                addr[0] = reverseGeoCodeResult.getAddress() + reverseGeoCodeResult.getSematicDescription();
                tvAddress.setText("地址：" + reverseGeoCodeResult.getAddress() + reverseGeoCodeResult.getSematicDescription());
                progress.setVisibility(View.GONE);
                ll_layout.setVisibility(View.VISIBLE);
            }
        });
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));

        ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity()).setTitle("请选择")
                        .setItems(new String[]{"查看线路", "本机地图"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        Intent intent = new Intent(getActivity(), LineSelectActivity.class);
                                        intent.putExtra("st",new LatLng(mCurrentlocation.getLatitude(), mCurrentlocation.getLongitude()));
                                        intent.putExtra("en", latLng);
                                        intent.putExtra("addr", addr[0]);
                                        startActivity(intent);
                                        if(popupWindow != null)
                                            popupWindow.dismiss();
                                        break;
                                    case 1:
                                        //LatLng desLatLng = CoordUtils.convertBaiduToGPS(latLng);
                                        //Uri mUri = Uri.parse("geo:"+desLatLng.latitude+","+desLatLng.longitude+","+data.getName());
                                        try{
                                            Uri mUri = Uri.parse("geo:" + data.getY() + "," + data.getX() + "," + data.getName());
                                            Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                                            startActivity(mIntent);
                                            if(popupWindow != null)
                                                popupWindow.dismiss();
                                        }catch (ActivityNotFoundException e){
                                            e.printStackTrace();
                                            ToastUtils.showShortToast(MyApplication.getInstance(),"未检测到本机地图");
                                        }

                                        /*if(AppUtils.isInstallApp("com.baidu.BaiduMap")){
                                            Intent i1 = new Intent();
                                            // 反向地址解析
                                            i1.setData(Uri.parse("baidumap://map/geocoder?location="+latLng.latitude+","+latLng.longitude));
                                            startActivity(i1);
                                        }else{
                                            if(AppUtils.isInstallApp("com.autonavi.minimap")){
                                                LatLng desLatLng = CoordUtils.convertBaiduToGPS(latLng);

                                                Intent i2 = new Intent();
                                                i2.setAction("android.intent.action.VIEW");
                                                i2.setPackage("com.autonavi.minimap");
                                                i2.addCategory("android.intent.category.DEFAULT");
                                                i2.setData(Uri.parse("androidamap://viewReGeo?sourceApplication=whsw&lat="+desLatLng.latitude+"&lon="+desLatLng.longitude+"&dev=1"));
                                                startActivity(i2);
                                            }else{
                                                ToastUtils.showShortToast(MyApplication.getInstance(),"请先安装百度或高德地图");
                                            }
                                        }*/
                                        break;
                                }
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });

        // 在底部显示
        popupWindow.showAtLocation(rlMapLayout, Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mSearch.destroy();
            }
        });
    }

    private void showDevicePopupWindow(final int i) {
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_map_device_state_layout, null);
        PopupWindow window = new PopupWindow(popupView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        // 设置popWindow的显示和消失动画
        //window.setAnimationStyle(R.style.mypopwindow_anim_style);

        TextView title = (TextView) popupView.findViewById(R.id.tv_title);
        ListView listView = (ListView) popupView.findViewById(R.id.listView);
        LinearLayout ll_title = (LinearLayout) popupView.findViewById(R.id.ll_title);
        MapDeviceStateAdapter adapter = null;
        if (i == 1) {
            if (disconnectList.size() > 0) {
                title.setText("断开设备列表");
                adapter = new MapDeviceStateAdapter(getActivity(), disconnectList);
                listView.setAdapter(adapter);
            } else {
                title.setText("无断开状态设备");
                ll_title.setVisibility(View.GONE);
            }
        } else if (i == 2) {
            if (normalList.size() > 0) {
                title.setText("正常设备列表");
                adapter = new MapDeviceStateAdapter(getActivity(), normalList);
                listView.setAdapter(adapter);
            } else {
                title.setText("无正常状态设备");
                ll_title.setVisibility(View.GONE);
            }
        } else if (i == 3) {
            if (dangerList.size() > 0) {
                title.setText("危险设备列表");
                adapter = new MapDeviceStateAdapter(getActivity(), dangerList);
                listView.setAdapter(adapter);
            } else {
                title.setText("无危险状态设备");
                ll_title.setVisibility(View.GONE);
            }
        } else if( i == 4){
            if (searchList.size() > 0) {
                title.setText("设备列表（"+searchList.size()+"）");
                adapter = new MapDeviceStateAdapter(getActivity(), searchList);
                listView.setAdapter(adapter);
            } else {
                title.setText("无设备列表");
                ll_title.setVisibility(View.GONE);
            }
        }else {
            if (deviceStateListInfo.getDataInfoList().size() > 0) {
                title.setText("设备列表（"+deviceStateListInfo.getDataInfoList().size()+"）");
                adapter = new MapDeviceStateAdapter(getActivity(), deviceStateListInfo.getDataInfoList());
                listView.setAdapter(adapter);
            } else {
                title.setText("无设备列表");
                ll_title.setVisibility(View.GONE);
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LatLng ll = null;
                if (i == 1) {
                    ll = new LatLng(Double.valueOf(disconnectList.get(position).getY()), Double.valueOf(disconnectList.get(position).getX()));
                } else if (i == 2) {
                    ll = new LatLng(Double.valueOf(normalList.get(position).getY()), Double.valueOf(normalList.get(position).getX()));
                } else if (i == 3) {
                    ll = new LatLng(Double.valueOf(dangerList.get(position).getY()), Double.valueOf(dangerList.get(position).getX()));
                } else if(i == 4){
                    ll = new LatLng(Double.valueOf(searchList.get(position).getY()), Double.valueOf(searchList.get(position).getX()));
                }else {
                    ll = new LatLng(Double.valueOf(deviceStateListInfo.getDataInfoList().get(position).getY()), Double.valueOf(deviceStateListInfo.getDataInfoList().get(position).getX()));
                }
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                // sourceLatLng待转换坐标
                converter.coord(ll);
                LatLng desLatLng = converter.convert();
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(desLatLng);
                mBaiduMap.animateMapStatus(update);
            }
        });
        // 在底部显示
        window.showAsDropDown(llTop);
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

}
