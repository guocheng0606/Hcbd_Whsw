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
import com.android.hcbd.whsw.entity.WarnDataInfo;
import com.android.hcbd.whsw.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WarnDataSearchActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_sn)
    EditText etSn;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.iv_start_time)
    ImageView ivStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.iv_end_time)
    ImageView ivEndTime;
    @BindView(R.id.et_idea)
    EditText etIdea;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    @BindView(R.id.cb_all)
    CheckBox cbAll;
    @BindView(R.id.cb_processed)
    CheckBox cbProcessed;
    @BindView(R.id.cb_untreated)
    CheckBox cbUntreated;

    private WarnDataInfo searchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_data_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.search_info);

        searchInfo = (WarnDataInfo) getIntent().getSerializableExtra("searchInfo");
        cbAll.setChecked(false);
        cbProcessed.setChecked(false);
        cbUntreated.setChecked(false);
        if (null != searchInfo) {
            etName.setText(searchInfo.getDevice().getName());
            etSn.setText(searchInfo.getDevice().getSn());
            tvStartTime.setText(String.valueOf(searchInfo.getBeginTime()));
            tvEndTime.setText(String.valueOf(searchInfo.getEndTime()));
            etIdea.setText(searchInfo.getRemark());
            if(searchInfo.getIsDeal().equals("2")){
                cbAll.setChecked(true);
            }else if(searchInfo.getIsDeal().equals("1")){
                cbProcessed.setChecked(true);
            }else{
                cbUntreated.setChecked(true);
            }
        }else{
            cbUntreated.setChecked(true);
        }
        initLIstener();

    }

    private void initLIstener() {
        tvStartTime.setOnClickListener(this);
        ivStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        ivEndTime.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
        cbAll.setOnClickListener(this);
        cbProcessed.setOnClickListener(this);
        cbUntreated.setOnClickListener(this);
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
            case R.id.tv_start_time:
            case R.id.iv_start_time:
                Calendar beginDate = Calendar.getInstance();
                String beginStr = tvStartTime.getText().toString();
                if (!TextUtils.isEmpty(beginStr))
                    beginDate.set(Integer.parseInt(beginStr.substring(0, 4)), Integer.parseInt(beginStr.substring(5, 7)) - 1, Integer.parseInt(beginStr.substring(8, 10)));
                showTimePickerDialog(tvStartTime, beginDate);
                break;
            case R.id.tv_end_time:
            case R.id.iv_end_time:
                Calendar endDate = Calendar.getInstance();
                String endStr = tvEndTime.getText().toString();
                if (!TextUtils.isEmpty(endStr))
                    endDate.set(Integer.parseInt(endStr.substring(0, 4)), Integer.parseInt(endStr.substring(5, 7)) - 1, Integer.parseInt(endStr.substring(8, 10)));
                showTimePickerDialog(tvStartTime, endDate);
                break;
            case R.id.cb_all:
                cbAll.setChecked(true);
                cbProcessed.setChecked(false);
                cbUntreated.setChecked(false);
                break;
            case R.id.cb_processed:
                cbAll.setChecked(false);
                cbProcessed.setChecked(true);
                cbUntreated.setChecked(false);
                break;
            case R.id.cb_untreated:
                cbAll.setChecked(false);
                cbProcessed.setChecked(false);
                cbUntreated.setChecked(true);
                break;
            case R.id.btn_complete:
                if (searchInfo == null)
                    searchInfo = new WarnDataInfo();
                WarnDataInfo.DeviceBean deviceBean = new WarnDataInfo.DeviceBean();
                deviceBean.setName(etName.getText().toString());
                deviceBean.setSn(etSn.getText().toString());

                searchInfo.setBeginTime(tvStartTime.getText().toString());
                searchInfo.setEndTime(tvEndTime.getText().toString());
                searchInfo.setRemark(etIdea.getText().toString());
                searchInfo.setDevice(deviceBean);
                searchInfo.setIsDeal(cbAll.isChecked() ? "2" : (cbProcessed.isChecked() ? "1" :"0"));
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setEventId(MessageEvent.EVENT_WARNING_SEARCH_RESULT);
                messageEvent.setObj(searchInfo);
                EventBus.getDefault().post(messageEvent);
                finishActivity();
                break;
        }
    }


}
