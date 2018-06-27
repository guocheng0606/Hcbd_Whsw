package com.android.hcbd.whsw.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by guocheng on 2017/4/25.
 */

public class AutoFit2ScrollView extends ScrollView{
    private static final int MAX_HEIGHT = 450;
    public AutoFit2ScrollView(Context context) {
        super(context);
    }

    public AutoFit2ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFit2ScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View child = getChildAt(0);
        child.measure(widthMeasureSpec, heightMeasureSpec);
        int width = child.getMeasuredWidth();
        int height = Math.min(child.getMeasuredHeight(), MAX_HEIGHT);
        setMeasuredDimension(width, height);
    }
}
