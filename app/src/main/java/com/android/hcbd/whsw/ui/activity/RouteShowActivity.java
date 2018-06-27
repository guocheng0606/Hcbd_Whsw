package com.android.hcbd.whsw.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.adapter.RouteSteupAdapter;
import com.android.hcbd.whsw.entity.RouteTypeInfo;
import com.android.hcbd.whsw.widget.NoScrollListView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.MassTransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouteShowActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bmapView)
    MapView mMapView;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_walk)
    TextView tvWalk;
    @BindView(R.id.listView)
    NoScrollListView listView;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.data_layout)
    ScrollView dataLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private BaiduMap mBaiduMap;
    private int type;

    private MassTransitRouteLine massTransitRouteLine;
    private DrivingRouteLine drivingRouteLine;
    private WalkingRouteLine walkingRouteLine;
    private List<RouteTypeInfo> routeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_show);
        ButterKnife.bind(this);

        type = getIntent().getIntExtra("type", 0);
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

        switch (type) {
            case 1:
                massTransitRouteLine = getIntent().getParcelableExtra("data");
                setMassTransitRouteOverlay();
                setMassTransitRouteData();
                break;
            case 2:
                drivingRouteLine = getIntent().getParcelableExtra("data");
                setDrivingRouteOverlay();
                setDrivingRouteData();
                break;
            case 3:
                walkingRouteLine = getIntent().getParcelableExtra("data");
                setWalkingRouteOverlay();
                setWalkingRouteData();
                break;
        }

        iv.setSelected(false);
        dataLayout.setVisibility(View.VISIBLE);
        llTop.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void setWalkingRouteData() {
        tvName.setText("方案" + getIntent().getStringExtra("id"));
        if (walkingRouteLine.getDuration() > 3600) {
            tvDuration.setText(walkingRouteLine.getDuration() / 3600 + "小时" + (walkingRouteLine.getDuration() % 3600) / 60 + "分钟");
        } else {
            tvDuration.setText(walkingRouteLine.getDuration() / 60 + "分钟");
        }
        BigDecimal b = new BigDecimal((double) walkingRouteLine.getDistance() / (1000));
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tvDistance.setText(f1 + "公里");
        List<WalkingRouteLine.WalkingStep> list = walkingRouteLine.getAllStep();
        for (int i = 0; i < list.size(); i++) {
            RouteTypeInfo routeTypeInfo = new RouteTypeInfo();
            routeTypeInfo.setName(list.get(i).getInstructions());
            routeTypeInfo.setType(3);
            routeList.add(routeTypeInfo);
        }
        RouteSteupAdapter adapter = new RouteSteupAdapter(RouteShowActivity.this, routeList);
        listView.setAdapter(adapter);
    }

    private void setDrivingRouteData() {
        tvName.setText("方案" + getIntent().getStringExtra("id"));
        if (drivingRouteLine.getDuration() > 3600) {
            tvDuration.setText(drivingRouteLine.getDuration() / 3600 + "小时" + (drivingRouteLine.getDuration() % 3600) / 60 + "分钟");
        } else {
            tvDuration.setText(drivingRouteLine.getDuration() / 60 + "分钟");
        }
        BigDecimal b = new BigDecimal((double) drivingRouteLine.getDistance() / (1000));
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tvDistance.setText(f1 + "公里");
        List<DrivingRouteLine.DrivingStep> list = drivingRouteLine.getAllStep();
        for (int i = 0; i < list.size(); i++) {
            RouteTypeInfo routeTypeInfo = new RouteTypeInfo();
            routeTypeInfo.setName(list.get(i).getInstructions());
            routeTypeInfo.setType(2);
            routeList.add(routeTypeInfo);
        }
        RouteSteupAdapter adapter = new RouteSteupAdapter(RouteShowActivity.this, routeList);
        listView.setAdapter(adapter);
    }

    private void setMassTransitRouteData() {
        List<List<MassTransitRouteLine.TransitStep>> list = massTransitRouteLine.getNewSteps();
        System.out.println(list.size());
        String title = "";
        int walkLength = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get(0).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_BUS) {
                title += "-" + list.get(i).get(0).getBusInfo().getName();
            }
            if (list.get(i).get(0).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_WALK) {
                String s = list.get(i).get(0).getInstructions();
                if (MyApplication.getInstance().isInteger(s.substring(2, s.length() - 1))) {
                    walkLength += Integer.parseInt(s.substring(2, s.length() - 1));
                }
            }
            System.out.println("xsize = "+list.get(i).size());
            if(list.get(i).size() == 1){
                RouteTypeInfo routeTypeInfo = new RouteTypeInfo();
                System.out.println("xxxxxxxx = "+list.get(i).get(0).getInstructions());
                routeTypeInfo.setName(list.get(i).get(0).getInstructions());
                if (list.get(i).get(0).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_BUS) {
                    routeTypeInfo.setType(1);
                } else if (list.get(i).get(0).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_DRIVING) {
                    routeTypeInfo.setType(2);
                } else if (list.get(i).get(0).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_TRAIN) {
                    routeTypeInfo.setType(4);
                } else {
                    routeTypeInfo.setType(3);
                }
                routeList.add(routeTypeInfo);
            }else{
                for(int m=0;m<list.get(i).size();m++){
                    RouteTypeInfo routeTypeInfo = new RouteTypeInfo();
                    routeTypeInfo.setName(list.get(i).get(m).getInstructions());
                    if (list.get(i).get(m).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_BUS) {
                        routeTypeInfo.setType(1);
                    } else if (list.get(i).get(m).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_DRIVING) {
                        routeTypeInfo.setType(2);
                    } else if (list.get(i).get(m).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_TRAIN) {
                        routeTypeInfo.setType(4);
                    } else {
                        routeTypeInfo.setType(3);
                    }
                    routeList.add(routeTypeInfo);
                }
            }


        }
        if (!TextUtils.isEmpty(title))
            tvName.setText(title.substring(1, title.length()));
        if (massTransitRouteLine.getDuration() > 3600) {
            tvDuration.setText(massTransitRouteLine.getDuration() / 3600 + "小时" + (massTransitRouteLine.getDuration() % 3600) / 60 + "分钟");
        } else {
            tvDuration.setText(massTransitRouteLine.getDuration() / 60 + "分钟");
        }
        BigDecimal b = new BigDecimal((double) massTransitRouteLine.getDistance() / (1000));
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tvDistance.setText(f1 + "公里");
        tvWalk.setText("步行" + walkLength + "米");

        RouteSteupAdapter adapter = new RouteSteupAdapter(RouteShowActivity.this, routeList);
        listView.setAdapter(adapter);
    }

    private void setWalkingRouteOverlay() {
        WalkingRouteOverlay walkingRouteOverlay = new WalkingRouteOverlay(mBaiduMap);
        //设置公交路线规划数据
        walkingRouteOverlay.setData(walkingRouteLine);
        mBaiduMap.setOnMarkerClickListener(walkingRouteOverlay);
        //将公交路线规划覆盖物添加到地图中
        walkingRouteOverlay.addToMap();
        walkingRouteOverlay.zoomToSpan();
    }

    private void setDrivingRouteOverlay() {
        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(mBaiduMap);
        //设置公交路线规划数据
        drivingRouteOverlay.setData(drivingRouteLine);
        mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
        //将公交路线规划覆盖物添加到地图中
        drivingRouteOverlay.addToMap();
        drivingRouteOverlay.zoomToSpan();
    }

    private void setMassTransitRouteOverlay() {
        MassTransitRouteOverlay massTransitRouteOverlay = new MassTransitRouteOverlay(mBaiduMap);
        //设置公交路线规划数据
        massTransitRouteOverlay.setData(massTransitRouteLine);
        mBaiduMap.setOnMarkerClickListener(massTransitRouteOverlay);
        //将公交路线规划覆盖物添加到地图中
        massTransitRouteOverlay.addToMap();
        massTransitRouteOverlay.zoomToSpan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        //mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_top:
                if (iv.isSelected()) {
                    iv.setSelected(false);
                    dataLayout.setVisibility(View.VISIBLE);
                } else {
                    iv.setSelected(true);
                    dataLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_back:
                finishActivity();
                break;
        }
    }
}
