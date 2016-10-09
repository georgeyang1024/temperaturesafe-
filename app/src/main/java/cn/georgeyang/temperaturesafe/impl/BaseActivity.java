package cn.georgeyang.temperaturesafe.impl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.Serializable;

import cn.georgeyang.temperaturesafe.App;
import cn.georgeyang.temperaturesafe.BLEControlService;
import cn.georgeyang.temperaturesafe.receiver.AppReceiver;
import cn.georgeyang.temperaturesafe.service.BluetoothLeService;

/**
 * Created by george.yang on 16/9/1.
 */
public abstract class BaseActivity extends AppCompatActivity implements ReceiverControl {
    private static AppReceiver appReceiver = new AppReceiver();
    public static IntentFilter intentFilter;
    {
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);


        //BLEControlService.ACTION_DATA_AVAILABLE
        intentFilter.addAction(BLEControlService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.receiverStack.add(this);
        registerReceiver(appReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.receiverStack.remove(this);
        unregisterReceiver(appReceiver);
    }

    public final <T extends Serializable> void sendMessage(int id, T object) {
        Intent intent = new Intent();
        intent.setAction(getPackageName());
        intent.putExtra("id",id);
        intent.putExtra("Serializable",object);
        sendBroadcast(intent);
    }

    public final <T extends Parcelable> void sendMessage(int id,T object) {
        Intent intent = new Intent();
        intent.setAction(getPackageName());
        intent.putExtra("id",id);
        intent.putExtra("Parcelable",object);
        sendBroadcast(intent);
    }

    @Override
    public void onReceiverMsg(Context context,Intent intent,String action, int id, Object data) {

    }

    public final Activity getActivity() {
        return this;
    }

}
