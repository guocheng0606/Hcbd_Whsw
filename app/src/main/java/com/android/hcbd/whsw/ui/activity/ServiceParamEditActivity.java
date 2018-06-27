package com.android.hcbd.whsw.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.ConfigureInfo;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.utils.HttpUrlUtils;
import com.android.hcbd.whsw.utils.LogUtils;
import com.android.hcbd.whsw.utils.ProgressDialogUtils;
import com.android.hcbd.whsw.utils.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceParamEditActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_addr)
    EditText etAddr;
    @BindView(R.id.cb_enable)
    CheckBox cbEnable;
    @BindView(R.id.cb_frozen)
    CheckBox cbFrozen;
    @BindView(R.id.cb_close)
    CheckBox cbClose;
    @BindView(R.id.btn_complete)
    Button btnComplete;

    private ConfigureInfo configureInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_param_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit);

        configureInfo = (ConfigureInfo) getIntent().getSerializableExtra("data_info");
        if(null != configureInfo){
            tvCode.setText(configureInfo.getCode());
            etName.setText(configureInfo.getName());
            etAddr.setText(configureInfo.getRemark());
            if(configureInfo.getState().equals("1")){
                cbEnable.setChecked(true);
                cbFrozen.setChecked(false);
                cbClose.setChecked(false);
            }else if(configureInfo.getState().equals("2")){
                cbEnable.setChecked(false);
                cbFrozen.setChecked(true);
                cbClose.setChecked(false);
            }else{
                cbEnable.setChecked(false);
                cbFrozen.setChecked(false);
                cbClose.setChecked(true);
            }
        }

        String[] strs = MyApplication.getInstance().getPowerStr("服务器参数").split(",");
        if(strs.length == 4){
            if(strs[2].equals("0")){
                btnComplete.setEnabled(false);
                btnComplete.setBackgroundResource(R.drawable.shape_check_data_button_06);
            }
        }

        cbEnable.setOnClickListener(this);
        cbFrozen.setOnClickListener(this);
        cbClose.setOnClickListener(this);
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
            case R.id.cb_enable:
                cbEnable.setChecked(true);
                cbFrozen.setChecked(false);
                cbClose.setChecked(false);
                break;
            case R.id.cb_frozen:
                cbEnable.setChecked(false);
                cbFrozen.setChecked(true);
                cbClose.setChecked(false);
                break;
            case R.id.cb_close:
                cbEnable.setChecked(false);
                cbFrozen.setChecked(false);
                cbClose.setChecked(true);
                break;
            case R.id.btn_complete:
                if(TextUtils.isEmpty(etName.getText().toString())){
                    ToastUtils.showShortToast(ServiceParamEditActivity.this,"请输入名称");
                    return;
                }
                if(TextUtils.isEmpty(etAddr.getText().toString())){
                    ToastUtils.showShortToast(ServiceParamEditActivity.this,"请输入地址");
                    return;
                }
                httpEdit();
                break;
        }
    }

    private void httpEdit() {
        OkGo.<String>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.configure_type_edit_url)
                .tag(this)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())
                .params("params", "A002")

                .params("oid", configureInfo == null ? "":""+configureInfo.getId())
                .params("id", configureInfo == null ? "":""+configureInfo.getId())
                .params("type", "A002")
                .params("code", configureInfo == null ? "":configureInfo.getCode())
                .params("name", etName.getText().toString())
                .params("remark", etAddr.getText().toString())
                .params("state", cbEnable.isChecked() ? "1":(cbFrozen.isChecked()?"2":"3"))
                .params("operNames", MyApplication.getInstance().getLoginInfo().getUserInfo().getNames())
                .params("orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("typeNote", "A002")
                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        ProgressDialogUtils.showLoading(ServiceParamEditActivity.this);
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
                                messageEvent.setEventId(MessageEvent.EVENT_CONFIGURE_EDIT_RESULT);
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
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }

}
