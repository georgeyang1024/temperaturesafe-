package cn.georgeyang.temperaturesafe.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.server.BootReceiver;

import cn.georgeyang.temperaturesafe.App;
import cn.georgeyang.temperaturesafe.impl.ReceiverControl;

/**
 * Created by george.yang on 16/9/1.
 */
public class AppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AppReceiver",intent.getAction());
        for (ReceiverControl control:App.receiverStack) {
            try {
                Object data = intent.getSerializableExtra("Serializable");
                if (data==null) {
                    data = intent.getParcelableExtra("Parcelable");
                }
                control.onReceiverMsg(context,intent,intent.getAction(),intent.getIntExtra("id",0),data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
