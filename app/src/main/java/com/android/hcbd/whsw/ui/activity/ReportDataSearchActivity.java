package com.android.hcbd.whsw.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.ReportDataInfo;
import com.android.hcbd.whsw.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportDataSearchActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_sn)
    EditText etSn;
    @BindView(R.id.et_power)
    EditText etPower;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.iv_start_time)
    ImageView ivStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.iv_end_time)
    ImageView ivEndTime;
    @BindView(R.id.cb_normal)
    CheckBox cbNormal;
    @BindView(R.id.cb_disconnect)
    CheckBox cbDisconnect;
    @BindView(R.id.cb_danger)
    CheckBox cbDanger;
    @BindView(R.id.btn_complete)
    Button btnComplete;

    private ReportDataInfo searchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_data_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.search_info);

        cbNormal.setChecked(true);
        cbDisconnect.setChecked(false);
        cbDanger.setChecked(false);
        searchInfo = (ReportDataInfo) getIntent().getSerializableExtra("searchInfo");
        if (null != searchInfo) {
            etName.setText(searchInfo.getDevice().getName());
            etSn.setText(searchInfo.getDevice().getSn());
            etPower.setText(searchInfo.getDevice().getRemark());
            tvStartTime.setText(String.valueOf(searchInfo.getBeginTime()));
            tvEndTime.setText(String.valueOf(searchInfo.getEndTime()));
            if(searchInfo.getDevice().getState().equals("0")){
                cbNormal.setChecked(false);
                cbDisconnect.setChecked(true);
                cbDanger.setChecked(false);
            }else if(searchInfo.getDevice().getState().equals("2")){
                cbNormal.setChecked(false);
                cbDisconnect.setChecked(false);
                cbDanger.setChecked(true);
            }
        }
        initLIstener();
    }

    private void initLIstener() {
        tvStartTime.setOnClickListener(this);
        ivStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        ivEndTime.setOnClickListener(this);
        cbNormal.setOnClickListener(this);
        cbDanger.setOnClickListener(this);
        cbDisconnect.setOnClickListener(this);
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
        switch (view.getId()){
            case R.id.cb_normal:
                cbNormal.setChecked(true);
                cbDisconnect.setChecked(false);
                cbDanger.setChecked(false);
                break;
            case R.id.cb_disconnect:
                cbNormal.setChecked(false);
                cbDisconnect.setChecked(true);
                cbDanger.setChecked(false);
                break;
            case R.id.cb_danger:
                cbNormal.setChecked(false);
                cbDisconnect.setChecked(false);
                cbDanger.setChecked(true);
                break;
            case R.id.tv_start_time:
            case R.id.iv_start_time:
                Calendar beginDate = Calendar.getInstance();
                String beginStr = tvStartTime.getText().toString();
                if (!TextUtils.isEmpty(beginStr))
                    beginDate.set(Integer.parseInt(beginStr.substring(0, 4)), Integer.parseInt(beginStr.substring(5, 7)) - 1, Integer.parseInt(beginStr.substring(8, 10)));
                showTimePickerDialog(tvStartTime,beginDate);
                break;
            case R.id.tv_end_time:
            case R.id.iv_end_time:
                Calendar endDate = Calendar.getInstance();
                String endStr = tvEndTime.getText().toString();
                if (!TextUtils.isEmpty(endStr))
                    endDate.set(Integer.parseInt(endStr.substring(0, 4)), Integer.parseInt(endStr.substring(5, 7)) - 1, Integer.parseInt(endStr.substring(8, 10)));
                showTimePickerDialog(tvStartTime,endDate);
                break;
            case R.id.btn_complete:
                if (searchInfo == null)
                    searchInfo = new ReportDataInfo();
                ReportDataInfo.DeviceBean deviceBean = new ReportDataInfo.DeviceBean();
                deviceBean.setName(etName.getText().toString());
                deviceBean.setSn(etSn.getText().toString());
                deviceBean.setRemark(etPower.getText().toString());

                searchInfo.setBeginTime(tvStartTime.getText().toString());
                searchInfo.setEndTime(tvEndTime.getText().toString());
                if(cbNormal.isChecked())
                    deviceBean.setState("1");
                if(cbDisconnect.isChecked())
                    deviceBean.setState("0");
                if(cbDanger.isChecked())
                    deviceBean.setState("2");
                searchInfo.setDevice(deviceBean);
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setEventId(MessageEvent.EVENT_REPORT_SEARCH_RESULT);
                messageEvent.setObj(searchInfo);
                EventBus.getDefault().post(messageEvent);
                finishActivity();
                break;
        }

    }
}
