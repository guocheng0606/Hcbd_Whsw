package com.android.hcbd.whsw.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.crash.Cockroach;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.ui.fragment.DeviceFragment;
import com.android.hcbd.whsw.ui.fragment.HomeFragment;
import com.android.hcbd.whsw.ui.fragment.ReportFragment;
import com.android.hcbd.whsw.ui.fragment.SettingFragment;
import com.android.hcbd.whsw.ui.fragment.WarningFragment;
import com.android.hcbd.whsw.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.NormalItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab)
    PageNavigationView tab;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private DeviceFragment deviceFragment;
    private ReportFragment reportFragment;
    private WarningFragment warningFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
        initBottomNavigation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getEventId()){
            case MessageEvent.EVENT_LOGINOUT:
                startActivity(new Intent(this,LoginActivity.class));
                finishActivity();
                break;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initBottomNavigation() {
        NavigationController navigationController = tab.material()
                .addItem(R.drawable.ic_tab_1_sel,"首页地图")
                .addItem(R.drawable.ic_tab_2_sel,"设备管理")
                .addItem(R.drawable.ic_tab_3_sel,"报表管理")
                .addItem(R.drawable.ic_tab_4_sel,"预警管理")
                .addItem(R.drawable.ic_tab_5_sel,"设置")
                .build();
        //navigationController.setSelect(0);
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                //选中时触发
                setTabSelection(index);
            }

            @Override
            public void onRepeat(int index) {
                //System.out.println(index);
                //setTabSelection(index);
            }
        });
    }

    private void setTabSelection(int i) {
        fragmentTransaction = fragmentManager.beginTransaction();
        hideAllFragment(fragmentTransaction);
        switch (i){
            case 0:
                if(homeFragment == null){
                    homeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.fl_layout,homeFragment);
                }else{
                    fragmentTransaction.show(homeFragment);
                }
                break;
            case 1:
                if(deviceFragment == null){
                    deviceFragment = new DeviceFragment();
                    fragmentTransaction.add(R.id.fl_layout,deviceFragment);
                }else{
                    fragmentTransaction.show(deviceFragment);
                }
                break;
            case 2:
                if(reportFragment == null){
                    reportFragment = new ReportFragment();
                    fragmentTransaction.add(R.id.fl_layout,reportFragment);
                }else{
                    fragmentTransaction.show(reportFragment);
                }
                break;
            case 3:
                if(warningFragment == null){
                    warningFragment = new WarningFragment();
                    fragmentTransaction.add(R.id.fl_layout,warningFragment);
                }else{
                    fragmentTransaction.show(warningFragment);
                }
                break;
            case 4:
                if(settingFragment == null){
                    settingFragment = new SettingFragment();
                    fragmentTransaction.add(R.id.fl_layout,settingFragment);
                }else{
                    fragmentTransaction.show(settingFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if(homeFragment != null) fragmentTransaction.hide(homeFragment);
        if(deviceFragment != null) fragmentTransaction.hide(deviceFragment);
        if(reportFragment != null) fragmentTransaction.hide(reportFragment);
        if(warningFragment != null) fragmentTransaction.hide(warningFragment);
        if(settingFragment != null) fragmentTransaction.hide(settingFragment);
    }

    //创建一个Item
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        NormalItemView normalItemView = new NormalItemView(this);
        normalItemView.initialize(drawable, checkedDrawable, text);
        normalItemView.setTextDefaultColor(Color.GRAY);
        normalItemView.setTextCheckedColor(0xFF1D91E9);
        return normalItemView;
    }

    private static boolean mBackKeyPressed = false;//记录是否有首次按键
    @Override
    public void onBackPressed() {
        if(!mBackKeyPressed){
            ToastUtils.showShortToast(MainActivity.this,"再按一次退出程序");
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {//延时两秒，如果超出则擦错第一次按键记录
                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 2000);
        }else{//退出程序
            Cockroach.uninstall();
            this.finish();
            System.exit(0);
        }
    }

}
