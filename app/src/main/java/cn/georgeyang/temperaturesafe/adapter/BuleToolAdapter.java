package cn.georgeyang.temperaturesafe.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.georgeyang.temperaturesafe.R;
import cn.georgeyang.temperaturesafe.utils.ViewHolder;

/**
 * Created by george.yang on 16/9/1.
 */
public class BuleToolAdapter extends BaseAdapter {
    private List<BluetoothDevice> mList;
    private Context mContext;

    public BuleToolAdapter (Context context,List<BluetoothDevice> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = LinearLayout.inflate(mContext, R.layout.item_buletool,null);
        }
        TextView name = ViewHolder.get(convertView,R.id.tv_name);
        BluetoothDevice device = mList.get(position);
        name.setText(device.getName());
        return convertView;
    }
}
