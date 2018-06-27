package com.android.hcbd.whsw.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.event.MessageEvent;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by guocheng on 2017/6/20.
 */

public class IpHistoryViewHolder extends BaseViewHolder<String> {

    private TextView tv_name;
    private ImageView iv_del;

    public IpHistoryViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_history_layout);
        tv_name = $(R.id.tv_name);
        iv_del = $(R.id.iv_del);
    }

    @Override
    public void setData(String data) {
        super.setData(data);
        tv_name.setText(data);
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setEventId(MessageEvent.EVENT_IPADDRESS_DEL);
                messageEvent.setObj(getAdapterPosition());
                EventBus.getDefault().post(messageEvent);
            }
        });
    }
}
