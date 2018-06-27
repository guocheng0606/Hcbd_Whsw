package com.android.hcbd.whsw.ui.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.hcbd.whsw.MyApplication;
import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.WarnDataInfo;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.ui.activity.WarnDataSearchActivity;
import com.android.hcbd.whsw.utils.HttpUrlUtils;
import com.android.hcbd.whsw.utils.IntentUtils;
import com.android.hcbd.whsw.utils.LogUtils;
import com.android.hcbd.whsw.utils.ProgressDialogUtils;
import com.android.hcbd.whsw.utils.ToastUtils;
import com.android.hcbd.whsw.viewholder.WarnDataViewHolder;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 预警管理
 */
public class WarningFragment extends Fragment implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.warn_toolbar)
    Toolbar toolbar;
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.layout_power)
    RelativeLayout layoutPower;
    private int currentPage = 1;
    private RecyclerArrayAdapter<WarnDataInfo> adapter;
    private List<WarnDataInfo> warnDataInfoList = new ArrayList<>();
    private WarnDataInfo searchInfo;

    private String mParam1;
    private String mParam2;

    public WarningFragment() {
        // Required empty public constructor
    }

    public static WarningFragment newInstance(String param1, String param2) {
        WarningFragment fragment = new WarningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("预警管理");

        initView();
        if (TextUtils.isEmpty(MyApplication.getInstance().getPowerStr("预警管理"))) {
            swipeRefreshLayout.setVisibility(View.GONE);
            layoutPower.setVisibility(View.VISIBLE);
            tvPower.setText("您没有访问预警管理权限,请与管理员联系！");
        }else{
            initHttpData();
        }
        initListener();
        return view;
    }

    private void initListener() {
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (adapter.getAllData().get(position).isSelected())
                    adapter.getAllData().get(position).setSelected(false);
                else
                    adapter.getAllData().get(position).setSelected(true);
                adapter.notifyItemChanged(position);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getEventId()) {
            case MessageEvent.EVENT_WARNING_SEARCH_RESULT:
                searchInfo = (WarnDataInfo) event.getObj();
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
                break;
        }
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(0xFFEDEDED, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<WarnDataInfo>(getActivity()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new WarnDataViewHolder(parent);
            }
        });
        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(0xFF1191C7);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initHttpData() {
        OkGo.<String>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.warning_list_url)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())
                .params("page.currentPage", currentPage)

                .params("device.name", searchInfo == null ? "" : searchInfo.getDevice().getName())
                .params("device.sn", searchInfo == null ? "" : searchInfo.getDevice().getSn())
                .params("beginTime", searchInfo == null ? "" : "" + searchInfo.getBeginTime())
                .params("endTime", searchInfo == null ? "" : "" + searchInfo.getEndTime())
                .params("remark", searchInfo == null ? "" : searchInfo.getRemark())
                .params("isDeal", searchInfo == null ? "0" : searchInfo.getIsDeal())
                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        LogUtils.LogShow(result);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            if (jsonArray.length() > 0) {
                                warnDataInfoList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    WarnDataInfo warnDataInfo = gson.fromJson(jsonArray.getString(i), WarnDataInfo.class);
                                    warnDataInfoList.add(warnDataInfo);
                                }
                                if (currentPage == 1)
                                    adapter.clear();
                                adapter.addAll(warnDataInfoList);
                            } else {
                                if (currentPage == 1) {
                                    adapter.clear();
                                } else {
                                    adapter.stopMore();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            adapter.pauseMore();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (currentPage == 1) {
                            ToastUtils.showShortToast(MyApplication.getInstance(), "连接服务器异常");
                        } else {
                            adapter.pauseMore();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_deal:
                showDealDialog();
                break;
            case R.id.action_export:
                if(AndPermission.hasPermission(getActivity(), Permission.STORAGE)){
                    httpExport();
                }else{
                    AndPermission.defaultSettingDialog(getActivity(), 111)
                            .setTitle("权限申请")
                            .setMessage("在设置-应用-武汉水务-权限中开启读写存储卡权限，以正常使用相关功能")
                            .setPositiveButton("去设置")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
                break;
            case R.id.action_search:
                Intent intent = new Intent(getActivity(), WarnDataSearchActivity.class);
                if (searchInfo != null)
                    intent.putExtra("searchInfo", searchInfo);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void httpExport() {
        OkGo.<File>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.warning_data_export_url)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())
                .execute(new FileCallback(MyApplication.getInstance().getSDPath()+"/whswapp/download/","预警数据.xlsx") {
                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        super.onStart(request);
                        ToastUtils.showShortToast(getActivity(),"正在导出预警数据...");
                    }
                    @Override
                    public void onSuccess(Response<File> response) {
                        ToastUtils.showShortToast(getActivity(),"数据导出完成,可以在手机存储/whswapp/download/下查看。");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("预警数据导出完成");
                        builder.setMessage("是否立即打开查看？");
                        builder.setCancelable(false);
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = IntentUtils.getExcelFileIntent(getActivity(),MyApplication.getInstance().getSDPath()+"/whswapp/download/"+"预警数据.xlsx");
                                startActivity(intent);
                            }
                        });
                        builder.create().show();
                        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity());
                        mBuilder.setContentTitle("下载完成,点击打开。");//设置通知栏标题
                        mBuilder.setContentText("预警数据.xlsx"); //设置通知栏显示内容
                        PendingIntent pendingIntent= PendingIntent.getActivity(getActivity(), 2, IntentUtils.getExcelFileIntent(getActivity(),MyApplication.getInstance().getSDPath()+"/whswapp/download/"+"预警数据.xlsx") , Notification.FLAG_AUTO_CANCEL);
                        mBuilder.setContentIntent(pendingIntent); //设置通知栏点击意图
                        mBuilder.setTicker("下载完成,点击打开。"); //通知首次出现在通知栏，带上升动画效果的
                        mBuilder.setWhen(System.currentTimeMillis());//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                        mBuilder.setPriority(Notification.PRIORITY_DEFAULT); //设置该通知优先级
                        mBuilder.setAutoCancel(true);//设置这个标志当用户单击面板就可以让通知将自动取消
                        mBuilder.setOngoing(false);//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        mBuilder.setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
                        mNotificationManager.notify(2, mBuilder.build());
                    }
                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        ToastUtils.showLongToast(MyApplication.getInstance(), "连接服务器异常");
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    AlertDialog editDialog;

    private void showDealDialog() {
        if (editDialog != null)
            editDialog = null;
        editDialog = new AlertDialog.Builder(getActivity()).create();
        editDialog.show();
        editDialog.setCanceledOnTouchOutside(true);
        Window window = editDialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_deal_data_layout);

        final EditText et_idea = (EditText) window.findViewById(R.id.et_idea);
        Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(et_idea);
                if (TextUtils.isEmpty(et_idea.getText().toString())) {
                    ToastUtils.showShortToast(getActivity(), "请输入处理意见");
                    return;
                }
                dataDeal(et_idea.getText().toString());
            }
        });
    }

    private void closeDialog() {
        if (editDialog != null) {
            editDialog.dismiss();
            editDialog = null;
        }
    }

    private void dataDeal(String s) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < adapter.getAllData().size(); i++) {
            if (adapter.getAllData().get(i).isSelected())
                values.add("" + adapter.getAllData().get(i).getId());
        }
        if (values.size() > 0) {
            httpDeal(values, s);
        } else {
            ToastUtils.showShortToast(getActivity(), "请至少选择一项");
        }
    }

    private void httpDeal(List<String> values, String s) {
        OkGo.<String>post(MyApplication.getInstance().getBsaeUrl() + HttpUrlUtils.warning_deal_url)
                .params("sessionOper.code", MyApplication.getInstance().getLoginInfo().getUserInfo().getCode())
                .params("sessionOper.orgCode", MyApplication.getInstance().getLoginInfo().getUserInfo().getOrgCode())
                .params("token", MyApplication.getInstance().getLoginInfo().getToken())
                .addUrlParams("idList", values)
                .params("remark", s)
                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        ProgressDialogUtils.showLoading(getActivity());
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        LogUtils.LogShow(result);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            if (!TextUtils.isEmpty(jsonObject.getString("data"))) {
                                ToastUtils.showShortToast(getActivity(), String.valueOf(Html.fromHtml(jsonObject.getString("data"))));
                                closeDialog();
                                swipeRefreshLayout.setRefreshing(true);
                                onRefresh();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                if (!TextUtils.isEmpty(jsonObject.getString("error"))) {
                                    ToastUtils.showShortToast(getActivity(), String.valueOf(Html.fromHtml(jsonObject.getString("error"))));
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtils.showShortToast(MyApplication.getInstance(), "请检查网络是否连接");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtils.dismissLoading();
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (TextUtils.isEmpty(MyApplication.getInstance().getPowerStr("预警管理")))
            return;
        inflater.inflate(R.menu.menu_warn, menu);
        String[] strs = MyApplication.getInstance().getPowerStr("预警管理").split(",");
        if(strs.length == 4){
            if(strs[0].equals("1")){
                menu.findItem(R.id.action_deal).setVisible(true);
            }else {
                menu.findItem(R.id.action_deal).setVisible(false);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        initHttpData();
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        System.out.println("加载更多。。。" + currentPage);
        initHttpData();
    }

}
