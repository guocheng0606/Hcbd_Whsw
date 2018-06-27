package com.android.hcbd.whsw.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.ui.activity.IpAddressActivity;
import com.android.hcbd.whsw.ui.activity.RefreshFrequencyActivity;
import com.android.hcbd.whsw.ui.activity.ServiceParamActivity;
import com.android.hcbd.whsw.ui.activity.UpdatePasswordActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 设置
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.setting_toolbar)
    Toolbar toolbar;
    Unbinder unbinder;
    @BindView(R.id.rl_frequency)
    RelativeLayout rlFrequency;
    @BindView(R.id.rl_server_parameters)
    RelativeLayout rlServerParameters;
    @BindView(R.id.rl_ip_address)
    RelativeLayout rlIpAddress;
    @BindView(R.id.rl_update_password)
    RelativeLayout rlUpdatePassword;
    @BindView(R.id.rl_logout)
    RelativeLayout rlLogout;

    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("系统设置");

        rlFrequency.setOnClickListener(this);
        rlServerParameters.setOnClickListener(this);
        rlIpAddress.setOnClickListener(this);
        rlUpdatePassword.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_setting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_frequency:
                startActivity(new Intent(getActivity(), RefreshFrequencyActivity.class));
                break;
            case R.id.rl_server_parameters:
                startActivity(new Intent(getActivity(), ServiceParamActivity.class));
                break;
            case R.id.rl_ip_address:
                startActivity(new Intent(getActivity(), IpAddressActivity.class));
                break;
            case R.id.rl_update_password:
                startActivity(new Intent(getActivity(), UpdatePasswordActivity.class));
                break;
            case R.id.rl_logout:
                logout();
                break;
        }
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("您确认退出登录吗？");
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
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setEventId(MessageEvent.EVENT_LOGINOUT);
                EventBus.getDefault().post(messageEvent);
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

}
