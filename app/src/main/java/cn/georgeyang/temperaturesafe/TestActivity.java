package cn.georgeyang.temperaturesafe;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;

public class TestActivity extends Activity implements OnClickListener
{

    private static String TAG = "Bluetooth_State";

    private BluetoothAdapter mBluetoothAdapter; // ������������������

    private TextView btDesc;

    private Button btOpen;

    private Button btClose;

    private Button btOpenBySystem; // ����ϵͳAPIȥ������

    private Button btDiscoveryDevice;

    private Button btCancelDiscovery;
    
    private Button btDiscoveryBySystem;  //����ϵͳApiȥɨ�������豸

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // ��ñ���������������������
//        if (android.os.Build.VERSION.SDK_INT>=18) {
//            BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//            mBluetoothAdapter = manager.getAdapter();
//        }
        if (mBluetoothAdapter == null)
        {
            toast("�Բ��� ����Ļ������߱���������");
            return;
        }
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        this.registerReceiver(BluetoothReciever, bluetoothFilter);

        //����ɨ������豸
        IntentFilter btDiscoveryFilter = new IntentFilter();
        btDiscoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        btDiscoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        btDiscoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btDiscoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        this.registerReceiver(BTDiscoveryReceiver, btDiscoveryFilter);

        int initialBTState = mBluetoothAdapter.getState();
        printBTState(initialBTState); // ��ʼʱ����״̬

        initialViews();

        btDesc.setText(" Name : " + mBluetoothAdapter.getName() + " Address : "
                + mBluetoothAdapter.getAddress() + " Scan Mode --" + mBluetoothAdapter.getScanMode());
        
        //��ӡ����ǰ�Ѿ��󶨳ɹ��������豸
        Set<BluetoothDevice> bts = mBluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> iterator  = bts.iterator();
        while(iterator.hasNext())
        {
            BluetoothDevice bd = iterator.next() ;
            Log.i(TAG , " Name : " + bd.getName() + " Address : "+ bd.getAddress() ); ;
            Log.i(TAG, "Device class" + bd.getBluetoothClass());    
        }
        
        BluetoothDevice findDevice =  mBluetoothAdapter.getRemoteDevice("00:11:22:33:AA:BB");
        
        Log.i(TAG , "findDevice Name : " + findDevice.getName() + "  findDevice Address : "+ findDevice.getAddress() ); ;
        Log.i(TAG , "findDevice class" + findDevice.getBluetoothClass());
    }

    private void initialViews()
    {
        btDesc = (TextView) findViewById(R.id.btDesc);
        btOpen = (Button) findViewById(R.id.btOpen);
        btClose = (Button) findViewById(R.id.btClose);
        btOpenBySystem = (Button) findViewById(R.id.btOpenBySystem);
        btDiscoveryDevice = (Button) findViewById(R.id.btDiscoveryDevice);
        btCancelDiscovery = (Button) findViewById(R.id.btCancelDiscovery);
        btDiscoveryBySystem = (Button) findViewById(R.id.btDiscoveryBySystem);
        
        btOpen.setOnClickListener(this);
        btClose.setOnClickListener(this);
        btOpenBySystem.setOnClickListener(this);
        btDiscoveryDevice.setOnClickListener(this);
        btCancelDiscovery.setOnClickListener(this);
        btDiscoveryBySystem.setOnClickListener(this);
    }

    private BroadcastReceiver BluetoothReciever = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction()))
            {
                Log.v(TAG, "### Bluetooth State has changed ##");

                int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.STATE_OFF);

                printBTState(btState);
            }
            else if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction()))
            {
                Log.v(TAG, "### ACTION_SCAN_MODE_CHANGED##");
                int cur_mode_state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);
                int previous_mode_state = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);
                
                Log.v(TAG, "### cur_mode_state ##" + cur_mode_state + " ~~ previous_mode_state:" + previous_mode_state);
                
            }
        }

    };

    private BroadcastReceiver BTDiscoveryReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction()))
            {
                Log.v(TAG, "### BT ACTION_DISCOVERY_STARTED ##");
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()))
            {
                Log.v(TAG, "### BT ACTION_DISCOVERY_FINISHED ##");
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
            {
                Log.v(TAG, "### BT BluetoothDevice.ACTION_FOUND ##");
                
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                
                if(btDevice != null)
                    Log.v(TAG , "Name : " + btDevice.getName() + " Address: " + btDevice.getAddress());
                    
            }
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction()))
            {
                Log.v(TAG, "### BT ACTION_BOND_STATE_CHANGED ##");
                
                int cur_bond_state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                int previous_bond_state = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE);
                
                
                Log.v(TAG, "### cur_bond_state ##" + cur_bond_state + " ~~ previous_bond_state" + previous_bond_state);
            }
        }

    };
    
    private void printBTState(int btState)
    {
        switch (btState)
        {
            case BluetoothAdapter.STATE_OFF:
                toast("STATE_OFF");
                Log.v(TAG, "BT State BluetoothAdapter.STATE_OFF ###");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                toast("STATE_TURNING_OFF");
                Log.v(TAG, "BT State :  BluetoothAdapter.STATE_TURNING_OFF ###");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                toast("STATE_TURNING_ON");
                Log.v(TAG, "BT State BluetoothAdapter.STATE_TURNING_ON ###");
                break;
            case BluetoothAdapter.STATE_ON:
                toast("STATE_ON");
                Log.v(TAG, "BT State BluetoothAdapter.STATE_ON ###");
                break;
            default:
                break;
        }
    }

    private final int REQUEST_OPEN_BT_CODE = 1;
    private final int REQUEST_DISCOVERY_BT_CODE = 2;
    
    @Override
    public void onClick(View v) {
        boolean wasBtOpened = mBluetoothAdapter.isEnabled(); // �Ƿ��Ѿ���

        switch (v.getId())
        {
            case R.id.btOpen: // ��

                boolean result = mBluetoothAdapter.enable();

                if (result)
                    toast("result");
                else if (wasBtOpened)
                    toast("result");
                else
                    toast("result");

                break;
            case R.id.btClose: // �ر�
                boolean result1 = mBluetoothAdapter.disable();
                if (result1)
                    toast("btClose");
                else if (!wasBtOpened)
                    toast("btClose");
                else
                    toast("btClose");

                break;
            case R.id.btOpenBySystem:

                Log.e(TAG, " ## click btOpenBySystem ##");
                if (!wasBtOpened) //
                {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_OPEN_BT_CODE);
                }
                else
                    toast("Hi 111 ~~~");
                break;
            case R.id.btDiscoveryDevice:
                if (!mBluetoothAdapter.isDiscovering()){
                    Log.i(TAG, "btCancelDiscovery ### the bluetooth dont't discovering");
                    mBluetoothAdapter.startDiscovery();
                }
                else
                    toast("isDiscovering ---- ");
                break;
            case R.id.btCancelDiscovery:   //ȡ��ɨ��
                if (mBluetoothAdapter.isDiscovering()){
                    Log.i(TAG, "btCancelDiscovery ### the bluetooth is isDiscovering");
                    mBluetoothAdapter.cancelDiscovery();
                }
                else
                    toast("btCancelDiscovery--- ");
                break;
            
            case R.id.btDiscoveryBySystem :  //ʹ�����ܱ�ɨ��
                Intent discoveryintent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoveryintent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(discoveryintent, REQUEST_DISCOVERY_BT_CODE);
                break ;
        }
    }

    //������
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_OPEN_BT_CODE){
            if (resultCode == RESULT_CANCELED)
            {
                toast("Sorry , REQUEST_OPEN_BT_CODE");
            }
            else
                toast("Year , REQUEST_OPEN_BT_CODE");
        }
        else if(requestCode == REQUEST_DISCOVERY_BT_CODE)
        {
            if (resultCode == RESULT_CANCELED)
            {
                toast("Sorry , REQUEST_DISCOVERY_BT_CODE");
            }
            else
                toast("Year , REQUEST_DISCOVERY_BT_CODE");
        }
            
    }

    private void toast(String str)
    {
        Toast.makeText(TestActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}
