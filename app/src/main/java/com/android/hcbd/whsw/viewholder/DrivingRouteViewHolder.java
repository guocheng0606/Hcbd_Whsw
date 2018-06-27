package com.android.hcbd.whsw.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.math.BigDecimal;

/**
 * Created by guocheng on 2017/8/25.
 */

public class DrivingRouteViewHolder extends BaseViewHolder<DrivingRouteLine> {

    private TextView tv_name;
    private TextView tv_duration;
    private TextView tv_distance;

    public DrivingRouteViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_driving_route_layout);
        tv_name = $(R.id.tv_name);
        tv_duration = $(R.id.tv_duration);
        tv_distance = $(R.id.tv_distance);
    }

    @Override
    public void setData(DrivingRouteLine data) {
        super.setData(data);
        tv_name.setText("方案"+(getAdapterPosition()+1));
        if(data.getDuration() > 3600){
            tv_duration.setText(data.getDuration()/3600+"小时"+(data.getDuration()%3600)/60+"分钟");
        }else{
            tv_duration.setText(data.getDuration()/60+"分钟");
        }
        BigDecimal b = new BigDecimal((double)data.getDistance()/(1000));
        double f1 = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_distance.setText( f1 + "公里");

    }
}
