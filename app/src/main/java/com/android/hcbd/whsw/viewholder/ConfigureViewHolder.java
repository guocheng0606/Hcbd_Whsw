package com.android.hcbd.whsw.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.ConfigureInfo;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by guocheng on 2017/8/25.
 */

public class ConfigureViewHolder extends BaseViewHolder<ConfigureInfo> {

    private TextView tv_order;
    private TextView tv_code;
    private TextView tv_unit;
    private TextView tv_value;
    private TextView tv_state;

    public ConfigureViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_configure_list_layout);
        tv_order = $(R.id.tv_order);
        tv_code = $(R.id.tv_code);
        tv_unit = $(R.id.tv_unit);
        tv_value = $(R.id.tv_value);
        tv_state = $(R.id.tv_state);
    }

    @Override
    public void setData(ConfigureInfo data) {
        super.setData(data);
        tv_order.setText(""+(getAdapterPosition()+1));
        tv_code.setText(data.getCode());
        tv_unit.setText(data.getName());
        tv_value.setText(data.getRemark());
        tv_state.setText(data.getStateContent());

    }
}
