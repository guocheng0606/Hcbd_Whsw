package com.android.hcbd.whsw.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.DeviceInfo;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.utils.HttpUrlUtils;
import com.android.hcbd.whsw.utils.LogUtils;
import com.android.hcbd.whsw.utils.ProgressDialogUtils;
import com.android.hcbd.whsw.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceInfoActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_sn)
    TextView tvSn;
    @BindView(R.id.tv_longitude)
    TextView tvLongitude;
    @BindView(R.id.tv_latitude)
    TextView tvLatitude;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.btn_update)
    Button btnUpdate;

    private DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.device_info);

        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("deviceInfo");
        if(null != deviceInfo) {
            tvName.setText(deviceInfo.getName());
            tvSn.setText(deviceInfo.getSn());
            tvLongitude.setText(deviceInfo.getX());
            tvLatitude.setText(deviceInfo.getY());
            tvRemark.setText(deviceInfo.getRemark());
            Glide.with(this).load(MyApplication.getInstance().getBsaeUrl() + deviceInfo.getImg()).into(iv);
            String[] strs = MyApplication.getInstance().getPowerStr("设备管理").split(",");
            if(strs.length == 4){
                if(strs[2].equals("0")){
                    btnUpdate.setEnabled(false);
                    btnUpdate.setBackgroundResource(R.drawable.shape_check_data_button_06);
                }
            }
        }
        btnUpdate.setOnClickListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getEventId()) {
            case MessageEvent.EVENT_DEVICE_EDIT_RESULT:
                finishActivity();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivity();
                break;
            case R.id.action_del:
                new AlertDialog.Builder(this)
                        .setTitle("删除提示")
                        .setMessage("您确认删除改设备吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                httpDelete();
                                dialogInterface.dismiss();
                            }
                        }).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void httpDelete() {
        OkGo.<String>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.del_device_url)
                .tag(this)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())
                .params("id", String.valueOf(deviceInfo == null ? "" : deviceInfo.getId()))
                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        ProgressDialogUtils.showLoading(DeviceInfoActivity.this);
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        LogUtils.LogShow(result);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            if(!TextUtils.isEmpty(jsonObject.getString("data"))){
                                ToastUtils.showShortToast(MyApplication.getInstance(),String.valueOf(Html.fromHtml(jsonObject.getString("data"))));
                                MessageEvent messageEvent = new MessageEvent();
                                messageEvent.setEventId(MessageEvent.EVENT_DEVICE_DELETE_RESULT);
                                EventBus.getDefault().post(messageEvent);
                                finishActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                if(!TextUtils.isEmpty(jsonObject.getString("error"))){
                                    ToastUtils.showShortToast(MyApplication.getInstance(),String.valueOf(Html.fromHtml(jsonObject.getString("error"))));
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtils.showShortToast(MyApplication.getInstance(), "连接服务器异常");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtils.dismissLoading();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_del, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_update:
                Intent intent = new Intent(this, DeviceEditActivity.class);
                intent.putExtra("deviceInfo", deviceInfo);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }

}
