package cn.georgeyang.temperaturesafe.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.georgeyang.temperaturesafe.R;
import cn.georgeyang.temperaturesafe.entity.RecorderEntity;
import cn.georgeyang.temperaturesafe.utils.ViewHolder;

/**
 *
 * Created by george.yang on 16/10/16.
 */
public class NameAdapter extends BaseAdapter {
    private List<RecorderEntity> mList;
    private Context mContext;

    public NameAdapter (Context context,List<RecorderEntity> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        int count =  mList==null?0:mList.size();
        return count+1;
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
            convertView = LinearLayout.inflate(mContext, R.layout.item_manger,null);
        }
        TextView name = ViewHolder.get(convertView,R.id.tv_name);
        if (position!=mList.size()) {
            RecorderEntity device = mList.get(position);
            name.setText(device.name);
        } else {
            name.setText("新增成员");
        }
        return convertView;
    }
}
