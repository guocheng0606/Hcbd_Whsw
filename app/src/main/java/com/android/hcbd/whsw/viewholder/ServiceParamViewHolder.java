package com.android.hcbd.whsw.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.ConfigureInfo;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by guocheng on 2017/8/25.
 */

public class ServiceParamViewHolder extends BaseViewHolder<ConfigureInfo> {

    private TextView tv_order;
    private TextView tv_code;
    private TextView tv_name;
    private ImageView iv;
    private LinearLayout ll;
    private TextView tv_state;
    private TextView tv_remark;

    public ServiceParamViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_service_param_list_layout);
        tv_order = $(R.id.tv_order);
        tv_code = $(R.id.tv_code);
        tv_name = $(R.id.tv_name);
        iv = $(R.id.iv);
        ll = $(R.id.ll);
        tv_state = $(R.id.tv_state);
        tv_remark = $(R.id.tv_remark);
    }

    @Override
    public void setData(ConfigureInfo data) {
        super.setData(data);
        tv_order.setText(""+(getAdapterPosition()+1));
        tv_code.setText(data.getCode());
        tv_name.setText(data.getName());
        tv_state.setText("状态："+data.getStateContent());
        tv_remark.setText("地址："+data.getRemark());
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
