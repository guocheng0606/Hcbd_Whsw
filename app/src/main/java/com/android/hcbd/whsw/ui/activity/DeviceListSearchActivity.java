package com.android.hcbd.whsw.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.DeviceInfo;
import com.android.hcbd.whsw.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceListSearchActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_sn)
    EditText etSn;
    @BindView(R.id.et_start_power)
    EditText etStartPower;
    @BindView(R.id.et_end_power)
    EditText etEndPower;
    @BindView(R.id.btn_complete)
    Button btnComplete;


    private DeviceInfo searchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.search_info);

        searchInfo = (DeviceInfo) getIntent().getSerializableExtra("searchInfo");
        if (null != searchInfo) {
            etName.setText(searchInfo.getName());
            etSn.setText(searchInfo.getSn());
            etStartPower.setText(String.valueOf(searchInfo.getBeginPower()));
            etEndPower.setText(String.valueOf(searchInfo.getEndPower()));
        }
        btnComplete.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_complete:
                if (searchInfo == null)
                    searchInfo = new DeviceInfo();
                searchInfo.setName(etName.getText().toString());
                searchInfo.setSn(etSn.getText().toString());
                searchInfo.setBeginPower(Double.parseDouble(etStartPower.getText().toString()));
                searchInfo.setEndPower(Double.parseDouble(etEndPower.getText().toString()));
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setEventId(MessageEvent.EVENT_DEVICE_SEARCH_RESULT);
                messageEvent.setObj(searchInfo);
                EventBus.getDefault().post(messageEvent);
                finishActivity();
                break;
        }
    }
}
