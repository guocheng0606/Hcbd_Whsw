package com.android.hcbd.whsw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hcbd.whsw.R;
import com.android.hcbd.whsw.entity.RouteTypeInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guocheng on 2017/8/31.
 */

public class RouteSteupAdapter extends BaseAdapter {

    private Context context;
    private List<RouteTypeInfo> list;
    public RouteSteupAdapter(Context context,List<RouteTypeInfo> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return list == null ? null : list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_route_steup_layout,null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.tv.setText(list.get(i).getName());
        switch (list.get(i).getType()){
            case 1:
                holder.iv.setImageResource(R.drawable.path_view1);
                break;
            case 2:
                holder.iv.setImageResource(R.drawable.path_view2);
                break;
            case 3:
                holder.iv.setImageResource(R.drawable.path_view3);
                break;
            case 4:
                holder.iv.setImageResource(R.drawable.path_view4);
                break;
        }
        return view;
    }

    class ViewHolder{
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.tv)
        TextView tv;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

}
