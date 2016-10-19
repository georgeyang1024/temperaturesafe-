package cn.georgeyang.temperaturesafe;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.georgeyang.temperaturesafe.adapter.BuleToolAdapter;
import cn.georgeyang.temperaturesafe.impl.BaseActivity;
import cn.georgeyang.temperaturesafe.receiver.AppReceiver;
import cn.georgeyang.temperaturesafe.utils.L;
import cn.georgeyang.temperaturesafe.utils.TitleUtil;
import cn.georgeyang.temperaturesafe.widget.DeviceScanEmptyView;

/**
 * Created by george.yang on 16/9/1.
 */
public class SelectDriveActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private BuleToolAdapter mAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        TitleUtil.init(this).setTitle("选择连接设备").autoBack();
        listView = (ListView) findViewById(R.id.listView);
        mAdapter = new BuleToolAdapter(this,deviceList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        BluetoothAdapter adapter = getBuletoothAdapter();

        //adapter不等于null，说明本机有蓝牙设备
        if(adapter != null) {
            //如果蓝牙设备未开启
            if (!adapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //请求开启蓝牙设备
                startActivityForResult(intent, 1);
            } else {
                startScan();
            }
        }
    }


    private void showMessage (String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter getBuletoothAdapter() {
        if (bluetoothAdapter==null) {
            if (android.os.Build.VERSION.SDK_INT>=18) {
                BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                bluetoothAdapter = manager.getAdapter();
            } else {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
        }
        return bluetoothAdapter;
    }



    @Override
    public void onReceiverMsg(Context context,Intent intent,String action, int id, Object data) {
        switch (action) {
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btDevice!=null) {
                    if (!deviceList.contains(btDevice)) {
                        deviceList.add(btDevice);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                showMessage( "扫描完成!");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
               startScan();
            } else if (resultCode == RESULT_CANCELED) {
                showMessage( "不允许蓝牙开启");
                finish();
            }
        }
    }

    private void startScan() {
        showMessage("开始扫描...");
        BluetoothAdapter adapter = getBuletoothAdapter();
        adapter.setDiscoverableTimeout(30000);
        deviceList.clear();
        if (!adapter.isDiscovering()){
//            adapter.startDiscovery();
            adapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (device!=null) {
                        if (!deviceList.contains(device)) {
                            deviceList.add(device);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            getBuletoothAdapter().cancelDiscovery();
        } catch (Exception e) {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = deviceList.get(position);
        getBuletoothAdapter().cancelDiscovery();
        Intent intent = new Intent();
        intent.putExtra("device", device);
        setResult(RESULT_OK, intent);
        finish();
    }





//        new Thread() {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//            @Override
//            public void run() {
//                BluetoothGatt mBluetoothGatt =  device.connectGatt(SelectDriveActivity.this, false, new BluetoothGattCallback() {
//                    @Override
//                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//                        gatt.discoverServices();
//                    }
//
//                    @Override
//                    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//                        super.onDescriptorRead(gatt, descriptor, status);
//                        L.showLog("onDescriptorRead");
//                    }
//
//                    @Override
//                    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//                        super.onDescriptorWrite(gatt, descriptor, status);
//                        L.showLog("onDescriptorWrite");
//                    }
//
//                    @Override
//                    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
//                        super.onReliableWriteCompleted(gatt, status);
//                        L.showLog("onReliableWriteCompleted");
//                    }
//                });
//            }
//        }.start();


//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//                BluetoothSocket tmp = null;
//                Method method;
//                try {
//                    method = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
//                    tmp = (BluetoothSocket) method.invoke(device, 1);
//                } catch (Exception e) {
//                    Log.e("TAG", e.toString());
//                }
//                try {
//                    tmp.connect();
//                    L.showLog("connect!");
//                } catch (Exception e) {
//                    Log.e("TAG", e.toString());
//                }
//            }
//        });
//        thread.start();

//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        String tag = "test";
////                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//                        UUID uuid = UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb");
//                            // 连接建立之前的先配对
//                            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
//                                //先尝试反射机制连接
//                                Method creMethod = BluetoothDevice.class
//                                        .getMethod("createBond");
//                                Log.e("TAG", "开始配对");
//                                creMethod.invoke(device);
//                            } else {
//                            }
//
//                        BluetoothSocket mSocket = device.createRfcommSocketToServiceRecord(uuid);
//                        mSocket.connect();
//                        InputStream inputStream = mSocket.getInputStream();
//                        while (true) {
//                            L.showLog(inputStream.read()+"");
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();

//    }
}
