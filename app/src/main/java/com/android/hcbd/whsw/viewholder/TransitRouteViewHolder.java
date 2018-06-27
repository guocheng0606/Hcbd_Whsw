package com.android.hcbd.whsw.viewholder;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by guocheng on 2017/8/25.
 */

public class TransitRouteViewHolder extends BaseViewHolder<MassTransitRouteLine> {

    private TextView tv_name;
    private TextView tv_duration;
    private TextView tv_distance;
    private TextView tv_walk;

    public TransitRouteViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_transit_route_layout);
        tv_name = $(R.id.tv_name);
        tv_duration = $(R.id.tv_duration);
        tv_distance = $(R.id.tv_distance);
        tv_walk = $(R.id.tv_walk);
    }

    @Override
    public void setData(MassTransitRouteLine data) {
        super.setData(data);
        List<List<MassTransitRouteLine.TransitStep>> list = data.getNewSteps();
        String title = "";
        int walkLength = 0;
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).size() == 1){
                if (list.get(i).get(0).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_BUS) {
                    title += "-"+list.get(i).get(0).getBusInfo().getName();
                }
                if (list.get(i).get(0).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_TRAIN) {
                    title += "-"+list.get(i).get(0).getTrainInfo().getName();
                }
                if(list.get(i).get(0).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_WALK){
                    String s = list.get(i).get(0).getInstructions();
                    if(MyApplication.getInstance().isInteger(s.substring(2,s.length()-1))){
                        walkLength += Integer.parseInt(s.substring(2,s.length()-1));
                    }
                }
            }else{
                for(int m=0;m<list.get(i).size();m++){
                    if (list.get(i).get(m).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_BUS) {
                        title += "-"+list.get(i).get(m).getBusInfo().getName();
                    }
                    if (list.get(i).get(m).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_TRAIN) {
                        title += "-"+list.get(i).get(m).getTrainInfo().getName();
                    }
                    if(list.get(i).get(m).getVehileType() == MassTransitRouteLine.TransitStep.StepVehicleInfoType.ESTEP_WALK){
                        String s = list.get(i).get(m).getInstructions();
                        if(MyApplication.getInstance().isInteger(s.substring(2,s.length()-1))){
                            walkLength += Integer.parseInt(s.substring(2,s.length()-1));
                        }
                    }
                }
            }
        }
        if(!TextUtils.isEmpty(title))
            tv_name.setText(title.substring(1,title.length()));
        if(data.getDuration() > 3600){
            tv_duration.setText(data.getDuration()/3600+"小时"+(data.getDuration()%3600)/60+"分钟");
        }else{
            tv_duration.setText(data.getDuration()/60+"分钟");
        }
        BigDecimal b = new BigDecimal((double)data.getDistance()/(1000));
        double f1 = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_distance.setText( f1 + "公里");
        tv_walk.setText("步行"+walkLength + "米");

    }
}
