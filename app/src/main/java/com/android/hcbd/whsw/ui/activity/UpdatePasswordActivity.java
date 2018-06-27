package com.android.hcbd.whsw.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.utils.HttpUrlUtils;
import com.android.hcbd.whsw.utils.LogUtils;
import com.android.hcbd.whsw.utils.ProgressDialogUtils;
import com.android.hcbd.whsw.utils.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UpdatePasswordActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.met_old_password)
    MaterialEditText metOldPassword;
    @BindView(R.id.btn_ok)
    Button btnOk;
    @BindView(R.id.met_new_password)
    MaterialEditText metNewPassword;
    @BindView(R.id.met_re_new_password)
    MaterialEditText metReNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.update_password);

        initListener();
    }

    private void initListener() {
        btnOk.setOnClickListener(this);
        metOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if(TextUtils.isEmpty(metOldPassword.getText().toString()))
                        metOldPassword.setError("不能为空");
                }
            }
        });
        metNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if(TextUtils.isEmpty(metNewPassword.getText().toString()))
                        metNewPassword.setError("不能为空");
                }
            }
        });
        metReNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if(TextUtils.isEmpty(metReNewPassword.getText().toString()))
                        metReNewPassword.setError("不能为空");
                }
            }
        });
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
            case R.id.btn_ok:
                if(TextUtils.isEmpty(metOldPassword.getText().toString())){
                    metOldPassword.setError("不能为空");
                    return;
                }
                if(TextUtils.isEmpty(metNewPassword.getText().toString())){
                    metNewPassword.setError("不能为空");
                    return;
                }
                if(TextUtils.isEmpty(metReNewPassword.getText().toString())){
                    metReNewPassword.setError("不能为空");
                    return;
                }
                if(!metNewPassword.getText().toString().equals(metReNewPassword.getText().toString())){
                    metReNewPassword.setError("您再次输入的密码有误");
                    return;
                }
                httpUpdatePassword();
                break;
        }
    }

    private void httpUpdatePassword() {
        OkGo.<String>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.edit_password_url)
                .tag(this)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())
                .params("userOldPwd", metOldPassword.getText().toString())
                .params("userPwd", metNewPassword.getText().toString())
                .execute(new StringCallback() {

                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        ProgressDialogUtils.showLoading(UpdatePasswordActivity.this);
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        LogUtils.LogShow(result);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            if(!TextUtils.isEmpty(jsonObject.getString("data"))){
                                ToastUtils.showLongToast(MyApplication.getInstance(),String.valueOf(Html.fromHtml(jsonObject.getString("data"))));
                                MessageEvent messageEvent = new MessageEvent();
                                messageEvent.setEventId(MessageEvent.EVENT_LOGINOUT);
                                EventBus.getDefault().post(messageEvent);
                                finishActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                if(!TextUtils.isEmpty(jsonObject.getString("error"))){
                                    ToastUtils.showLongToast(MyApplication.getInstance(),String.valueOf(Html.fromHtml(jsonObject.getString("error"))));
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtils.showShortToast(MyApplication.getInstance(),"连接服务器异常");
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
