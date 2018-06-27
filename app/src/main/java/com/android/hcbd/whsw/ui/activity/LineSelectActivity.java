package com.android.hcbd.whsw.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.adapter.MyViewPagerAdapter;
import com.android.hcbd.whsw.event.MessageEvent;
import com.android.hcbd.whsw.ui.fragment.DrivingRouteFragment;
import com.android.hcbd.whsw.ui.fragment.TransitRouteFragment;
import com.android.hcbd.whsw.ui.fragment.WalkingRouteFragment;
import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LineSelectActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.iv_change)
    ImageView ivChange;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.iv_01)
    ImageView iv01;
    @BindView(R.id.iv_02)
    ImageView iv02;
    @BindView(R.id.iv_03)
    ImageView iv03;

    private LatLng stLatLng;
    private LatLng enLatLng;
    private String addr;
    private boolean isChange = false;

    private int bmp_width = 0;//游标宽度
    private int offset = 0;//游标图片偏移量
    private int number = 0;//当前页面编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_select);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.line_select);
        stLatLng = getIntent().getParcelableExtra("st");
        enLatLng = getIntent().getParcelableExtra("en");
        addr = getIntent().getStringExtra("addr");
        tvEnd.setText(addr);

        iv01.setSelected(true);
        iv02.setSelected(false);
        iv03.setSelected(false);
        viewPager.setOffscreenPageLimit(2);
        initViewPager();
        initListener();
    }

    private void initListener() {
        ivChange.setOnClickListener(this);
        iv01.setOnClickListener(new TabOnClickListener(0));
        iv02.setOnClickListener(new TabOnClickListener(1));
        iv03.setOnClickListener(new TabOnClickListener(2));
    }

    private void initViewPager() {
        //ViewPager关联适配器
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager());
        adapter.clearFragment();
        adapter.addFragment(TransitRouteFragment.newInstance(stLatLng, enLatLng), "公交");
        adapter.addFragment(DrivingRouteFragment.newInstance(stLatLng, enLatLng), "驾车");
        adapter.addFragment(WalkingRouteFragment.newInstance(stLatLng, enLatLng), "步行");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
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
            case R.id.iv_change:
                if (isChange) {
                    isChange = false;
                    tvStart.setText("我的位置");
                    tvEnd.setText(addr);
                } else {
                    isChange = true;
                    tvEnd.setText("我的位置");
                    tvStart.setText(addr);
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setEventId(MessageEvent.EVENT_ROUTE_CHANGE_RESULT);
                EventBus.getDefault().post(messageEvent);
                break;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    iv01.setSelected(true);
                    iv02.setSelected(false);
                    iv03.setSelected(false);
                    break;
                case 1:
                    iv01.setSelected(false);
                    iv02.setSelected(true);
                    iv03.setSelected(false);
                    break;
                case 2:
                    iv01.setSelected(false);
                    iv02.setSelected(false);
                    iv03.setSelected(true);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /*Tab页面点击监听*/
    public class TabOnClickListener implements View.OnClickListener {
        private int num = 0;

        public TabOnClickListener(int index) {
            num = index;
        }

        @Override
        public void onClick(View v) {
            switch (num){
                case 0:
                    iv01.setSelected(true);
                    iv02.setSelected(false);
                    iv03.setSelected(false);
                    break;
                case 1:
                    iv01.setSelected(false);
                    iv02.setSelected(true);
                    iv03.setSelected(false);
                    break;
                case 2:
                    iv01.setSelected(false);
                    iv02.setSelected(false);
                    iv03.setSelected(true);
                    break;
            }
            viewPager.setCurrentItem(num);
        }
    }


}
