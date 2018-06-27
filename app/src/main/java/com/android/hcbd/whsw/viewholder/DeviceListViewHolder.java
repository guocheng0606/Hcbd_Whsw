package com.android.hcbd.whsw.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.DeviceInfo;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by Administrator on 2017/8/25.
 */

public class DeviceListViewHolder extends BaseViewHolder<DeviceInfo> {

    private TextView tv_order;
    private TextView tv_name;
    private TextView tv_sn;
    private ImageView iv;
    private LinearLayout ll;
    private TextView tv_longitude;
    private TextView tv_latitude;
    private TextView tv_power;
    private TextView tv_remark;

    public DeviceListViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_device_list_layout);
        tv_order = $(R.id.tv_order);
        tv_name = $(R.id.tv_name);
        tv_sn = $(R.id.tv_sn);
        iv = $(R.id.iv);
        ll = $(R.id.ll);
        tv_longitude = $(R.id.tv_longitude);
        tv_latitude = $(R.id.tv_latitude);
        tv_power = $(R.id.tv_power);
        tv_remark = $(R.id.tv_remark);
    }

    @Override
    public void setData(DeviceInfo data) {
        super.setData(data);
        tv_order.setText(""+(getAdapterPosition()+1));
        tv_name.setText(data.getName());
        tv_sn.setText(data.getSn());

        tv_longitude.setText("经度："+data.getX());
        tv_latitude.setText("纬度："+data.getY());
        tv_power.setText("电量："+(String.valueOf(data.getPower()).equals("null") ? "":String.valueOf(data.getPower())));
        tv_remark.setText("备注："+data.getRemark());
        iv.setSelected(false);
        ll.setVisibility(View.GONE);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iv.isSelected()){
                    iv.setSelected(false);
                    ll.setVisibility(View.GONE);
                }
                else{
                    iv.setSelected(true);
                    ll.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}
