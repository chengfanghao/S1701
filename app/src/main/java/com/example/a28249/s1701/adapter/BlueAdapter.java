package com.example.a28249.s1701.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a28249.s1701.Config;
import com.example.a28249.s1701.R;
import com.example.a28249.s1701.pojo.BluetoothDeviceWithRSSI;

import java.util.List;

public class BlueAdapter extends BaseAdapter {
    private List<BluetoothDeviceWithRSSI> mBlueList;
    //布局装载器对象
    private LayoutInflater layoutInflater;

    private Context context;

    // context:要使用当前的Adapter的界面对象
    public BlueAdapter(List<BluetoothDeviceWithRSSI> list, Context context) {
        this.context = context;
        mBlueList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mBlueList.size();
    }

    @Override
    public Object getItem(int i) {
        return mBlueList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clear() {
        mBlueList.clear();
        Config.singletonMap.clear();
        notifyDataSetChanged();
    }


    @SuppressLint("SetTextI18n")
    @Override
    //返回每一项的显示内容
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        //如果view未被实例化过，缓存池中没有对应的缓存
        if (view == null) {
            viewHolder = new ViewHolder();
            // 由于我们只需要将XML转化为View，并不涉及到具体的布局，所以第二个参数通常设置为null
            view = layoutInflater.inflate(R.layout.list_item, null);

            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceDesc = (TextView) view.findViewById(R.id.device_desc);
            viewHolder.deviceData = (TextView) view.findViewById(R.id.device_data);

            viewHolder.minDesc = (TextView) view.findViewById(R.id.min_desc);
            //通过setTag将view与viewHolder关联
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice blueDevice = mBlueList.get(i).getBluetoothDevice();

        viewHolder.deviceAddress.setText("MAC地址：" + blueDevice.getAddress());
        viewHolder.deviceDesc.setText("状态：" + mBlueList.get(i).getJudgeDesc() + "  时间：" + mBlueList.get(i).getTime());
        viewHolder.deviceData.setText("数据：" + mBlueList.get(i).getData());

        viewHolder.minDesc.setText(mBlueList.get(i).getDesc());


        return view;
    }

    class ViewHolder {
        TextView deviceAddress;
        TextView deviceDesc;
        TextView deviceData;

        TextView minDesc;
    }
}
