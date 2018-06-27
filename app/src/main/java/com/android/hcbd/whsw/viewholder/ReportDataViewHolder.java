package com.android.hcbd.whsw.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.ReportDataInfo;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by guocheng on 2017/8/25.
 */

public class ReportDataViewHolder extends BaseViewHolder<ReportDataInfo> {

    private TextView tv_order;
    private TextView tv_name;
    private TextView tv_content;
    private TextView tv_power;
    private ImageView iv;
    private LinearLayout ll;
    private TextView tv_longitude;
    private TextView tv_latitude;
    private TextView tv_sn;
    private TextView tv_time;


    public ReportDataViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_report_data_list_layout);
        tv_order = $(R.id.tv_order);
        tv_name = $(R.id.tv_name);
        tv_content = $(R.id.tv_content);
        tv_power = $(R.id.tv_power);
        iv = $(R.id.iv);
        ll = $(R.id.ll);
        tv_longitude = $(R.id.tv_longitude);
        tv_latitude = $(R.id.tv_latitude);
        tv_sn = $(R.id.tv_sn);
        tv_time = $(R.id.tv_time);
    }

    @Override
    public void setData(ReportDataInfo data) {
        super.setData(data);
        tv_order.setText(""+(getAdapterPosition()+1));
        tv_name.setText(data.getDevice().getName());
        tv_content.setText(data.getContent());
        tv_power.setText(String.valueOf(data.getPower()));

        tv_longitude.setText("经度："+data.getX());
        tv_latitude.setText("纬度："+data.getY());
        tv_sn.setText("ID："+data.getDevice().getSn());
        tv_time.setText("时间："+data.getCreateTime());
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
