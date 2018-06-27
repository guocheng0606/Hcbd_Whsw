package com.android.hcbd.whsw.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.DeviceInfo;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.imageloader.GlideImageLoader;
import com.android.hcbd.whsw.utils.HttpUrlUtils;
import com.android.hcbd.whsw.utils.LogUtils;
import com.android.hcbd.whsw.utils.ProgressDialogUtils;
import com.android.hcbd.whsw.utils.ToastUtils;
import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceEditActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_sn)
    EditText etSn;
    @BindView(R.id.et_longitude)
    EditText etLongitude;
    @BindView(R.id.et_latitude)
    EditText etLatitude;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.iv_update)
    ImageView ivUpdate;
    @BindView(R.id.btn_complete)
    Button btnComplete;

    private DeviceInfo deviceInfo;

    public static final int IMAGE_PICKER = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("deviceInfo");
        String[] strs = MyApplication.getInstance().getPowerStr("设备管理").split(",");
        if(null == deviceInfo) {
            getSupportActionBar().setTitle("录入");
            if(strs.length == 4){
                if(strs[0].equals("0")){
                    btnComplete.setEnabled(false);
                    btnComplete.setBackgroundResource(R.drawable.shape_check_data_button_06);
                }
            }
        }else{
            getSupportActionBar().setTitle("编辑");
            etName.setText(deviceInfo.getName());
            etSn.setText(deviceInfo.getSn());
            etLongitude.setText(deviceInfo.getX());
            etLatitude.setText(deviceInfo.getY());
            etRemark.setText(deviceInfo.getRemark());
            Glide.with(this).load(MyApplication.getInstance().getBsaeUrl()+deviceInfo.getImg()).into(ivIcon);
            if(strs.length == 4){
                if(strs[2].equals("0")){
                    btnComplete.setEnabled(false);
                    btnComplete.setBackgroundResource(R.drawable.shape_check_data_button_06);
                }
            }
        }


        ivUpdate.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
        initImagePicker();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setMultiMode(false);
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        //imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
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
            case R.id.iv_update:
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
                break;
            case R.id.btn_complete:
                if(TextUtils.isEmpty(etName.getText().toString())){
                    ToastUtils.showShortToast(this,"请输入桩名称");
                    return;
                }
                if(TextUtils.isEmpty(etSn.getText().toString())){
                    ToastUtils.showShortToast(this,"请输入卡ID");

                }
                httpComplete();
                break;
        }
    }

    ArrayList<ImageItem> images;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                Log.d("Matisse", "mSelected : " + images.get(0).path);
                Glide.with(this).load(images.get(0).path).into(ivIcon);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void httpComplete() {
        List<File> fileList = new ArrayList<>();
        if(null != images){
            fileList.add(FileUtils.getFileByPath(images.get(0).path));
        }
        OkGo.<String>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.edit_device_url)
                .tag(this)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())

                .params("oid", deviceInfo == null ? "":""+deviceInfo.getId())
                .params("id", deviceInfo == null ? "":""+deviceInfo.getId())
                .params("name", etName.getText().toString())
                .params("code", deviceInfo == null ? "":deviceInfo.getCode())
                .params("sn", etSn.getText().toString())
                .params("x", etLongitude.getText().toString())
                .params("y", etLatitude.getText().toString())
                //.params("upload", images == null ? null : FileUtils.getFileByPath(images.get(0).path))
                .addFileParams("upload", fileList)
                .params("url", deviceInfo == null ? "":deviceInfo.getUrl())
                .params("power", deviceInfo == null ? "" : (deviceInfo.getPower() == null ? "":""+deviceInfo.getPower()))
                .params("state", deviceInfo == null ? "0":deviceInfo.getState())
                .params("remark", etRemark.getText().toString())
                .params("operNames", MyApplication.getInstance().getLoginInfo().getUserInfo().getNames())
                .params("orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        ProgressDialogUtils.showLoading(DeviceEditActivity.this);
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
                                messageEvent.setEventId(MessageEvent.EVENT_DEVICE_EDIT_RESULT);
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
