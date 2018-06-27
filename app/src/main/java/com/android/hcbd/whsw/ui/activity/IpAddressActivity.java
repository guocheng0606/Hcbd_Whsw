package com.android.hcbd.whsw.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.utils.LogUtils;
import com.android.hcbd.whsw.utils.SharedPreferencesUtil;
import com.android.hcbd.whsw.viewholder.IpHistoryViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IpAddressActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.met_ip)
    MaterialEditText metIp;
    @BindView(R.id.btn_ok)
    Button btnOk;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    private Gson gson;
    private RecyclerArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_address);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EventBus.getDefault().register(this);

        gson = new Gson();
        String ipStr = MyApplication.getInstance().getIpInfo();
        metIp.setText(ipStr);
        metIp.setSelection(ipStr.length());

        setRecyclerData();
        btnOk.setOnClickListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getEventId()) {
            case MessageEvent.EVENT_IPADDRESS_DEL:
                int pos = (int) event.getObj();
                adapter.remove(pos);
                String strHis = SharedPreferencesUtil.get(IpAddressActivity.this, "ip_history");
                final List<String> list;
                if (TextUtils.isEmpty(strHis)) {
                    list = new ArrayList<>();
                } else {
                    list = gson.fromJson(strHis, new TypeToken<List<String>>(){}.getType());
                }
                Collections.reverse(list); // 倒序排列
                list.remove(pos);
                Collections.reverse(list); // 倒序排列
                SharedPreferencesUtil.save(IpAddressActivity.this, "ip_history", gson.toJson(list));
                break;
        }
    }

    private void setRecyclerData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(IpAddressActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<String>(IpAddressActivity.this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new IpHistoryViewHolder(parent);
            }
        });
        String strHis = SharedPreferencesUtil.get(IpAddressActivity.this, "ip_history");
        final List<String> list;
        if (TextUtils.isEmpty(strHis)) {
            list = new ArrayList<>();
        } else {
            list = gson.fromJson(strHis, new TypeToken<List<String>>(){}.getType());
        }
        Collections.reverse(list); // 倒序排列
        adapter.addAll(list);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                metIp.setText(""+adapter.getAllData().get(position));
                metIp.setSelection(metIp.getText().toString().length());
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
                if (TextUtils.isEmpty(metIp.getText().toString())) {
                    metIp.setError("请输入服务器地址");
                    return;
                }
                update();
                break;
        }
    }

    private void update() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setCancelable(false);
        builder.setMessage("修改服务器地址将重新登录，是否继续？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                String ip = metIp.getText().toString();
                SharedPreferencesUtil.save(IpAddressActivity.this, "ip_info", ip);

                String strHis = SharedPreferencesUtil.get(IpAddressActivity.this, "ip_history");
                LogUtils.LogShow("cache = "+strHis);
                List<String> list;
                if (!TextUtils.isEmpty(strHis)) {
                    list = gson.fromJson(strHis, new TypeToken<List<String>>(){}.getType());
                } else {
                    list = new ArrayList<>();
                }
                if(list.size() > 0){
                    boolean flag = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (ip.equals(list.get(i)))
                            flag = true;
                    }
                    if(!flag)
                        list.add(ip);
                }else{
                    list.add(ip);
                }
                SharedPreferencesUtil.save(IpAddressActivity.this, "ip_history", gson.toJson(list));


                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setEventId(MessageEvent.EVENT_LOGINOUT);
                EventBus.getDefault().post(messageEvent);
                dialogInterface.dismiss();
                finishActivity();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
