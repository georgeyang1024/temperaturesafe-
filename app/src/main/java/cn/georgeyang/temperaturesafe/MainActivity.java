package cn.georgeyang.temperaturesafe;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.temperaturesafe.impl.BaseActivity;
import cn.georgeyang.temperaturesafe.service.BluetoothLeService;
import cn.georgeyang.temperaturesafe.utils.AppUtil;
import cn.georgeyang.temperaturesafe.utils.TitleUtil;
import cn.georgeyang.utils.DialogUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private String mDeviceAddress;
    private TextView tv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mdb.init(this);

        TitleUtil.init(this).setTitle(getResources().getString(R.string.app_name)).autoGoSetting();

        findViewById(R.id.tv_check).setOnClickListener(this);
        findViewById(R.id.layout_check).setOnClickListener(this);
        tv_show = (TextView) findViewById(R.id.tv_show);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        boolean bll = bindService(gattServiceIntent, mServiceConnection,
                BIND_AUTO_CREATE);
        if (bll) {
            System.out.println("---------------");
        } else {
            System.out.println("===============");
        }

        findViewById(R.id.tv_check).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AppUtil.playWarning(getActivity());
                return true;
            }
        });

        AppUtil.stopPlay(this);
        AppUtil.stopVibrator();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;

        AppUtil.stopPlay(getActivity());
        AppUtil.stopVibrator();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_check:
//                startActivity(new Intent(MainActivity.this,DataHistoryActivity.class));
                startActivity(new Intent(MainActivity.this,HistoryActivity.class));
                break;
            case R.id.layout_check:
                startActivityForResult(new Intent(MainActivity.this,SelectDriveActivity.class),1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK) {
            BluetoothDevice device = data.getParcelableExtra("device");
            mDeviceAddress = device.getAddress();

            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            if (result) {
                tv_show.setText("正在获取数据");
            } else {
                tv_show.setText("连接失败");
            }

        }
    }

    private static final String TAG = "tag";
    private BluetoothLeService mBluetoothLeService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    @Override
    public void onReceiverMsg(Context context, Intent intent, String action, int id, Object data) {
        super.onReceiverMsg(context, intent, action, id, data);
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            mConnected = true;
            tv_show.setText("已连接");
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnected = false;
            tv_show.setText("已断开");
        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            displayGattServices(mBluetoothLeService.getSupportedGattServices());
        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
             try {
                 byte[] bytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                 String hexString = string2HexStr(new String(bytes));
//                 Log.d("hex", hexString);
                 String[] value = getValue(hexString);
                 tv_show.setText(value[1]);
//                 Log.d("hex",value[1]);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         } else if (BluetoothLeService.ACTION_START_WARING.equals(action)) {
            //开始警报
            Log.d("test","开始报警！");
            DialogUtil.showOKDialog(getActivity(), "点击取消警报可以取消警报", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppUtil.stopPlay(getActivity());
                    AppUtil.stopVibrator();
                }
            });
        }
    }

    private boolean mConnected;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            Log.d(TAG, "=======");
            Log.d(TAG, "displayGattServices: "+uuid);
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                Log.d("test","BluetoothGattCharacteristic:" + uuid);
                if (uuid.contains("2a1c-")) {
                    Log.e("console", "2gatt Characteristic: " + uuid);
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                    mBluetoothLeService.readCharacteristic(gattCharacteristic);
                }
            }
        }
    }

    private String[] getValue(String hex) {
        String tmp = hex.substring(4,6) + hex.substring(2,4);
        int v = Integer.parseInt(tmp,16);
        float v2 = v/10f;
        return new String[]{Integer.valueOf(hex.substring(0,2))  + "",v2+""};
    }

    private static String hexString="0123456789ABCDEF";
    public static String string2HexStr(String str) {
        byte[] bytes=str.getBytes();
        StringBuilder sb=new StringBuilder(bytes.length*2);
        for(int i=0;i<bytes.length;i++) {
            sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
            sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
        }
        return sb.toString();
    }

//    /**
//     * byte转16进制
//     *
//     * @param b
//     * @return
//     */
//    public static String byte2HexStr(byte[] b) {
//        String stmp = "";
//        StringBuilder sb = new StringBuilder("");
//        for (int n = 0; n < b.length; n++) {
//            stmp = Integer.toHexString(b[n] & 0xFF);
//            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
//            sb.append(" ");
//        }
//        return sb.toString().toUpperCase().trim();
//    }

}
