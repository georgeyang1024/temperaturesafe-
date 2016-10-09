package cn.georgeyang.temperaturesafe;

/**
 * Created by george.yang on 16/9/2.
 */
public class BLEControlService {
    //broadcast
    public final static String ACTION_GATT_CONNECTED = "com.nordicsemi.nrfUART.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.nordicsemi.nrfUART.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.nordicsemi.nrfUART.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.nordicsemi.nrfUART.ACTION_DATA_AVAILABLE";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART = "com.nordicsemi.nrfUART.DEVICE_DOES_NOT_SUPPORT_UART";
    public final static String RECEIVE_DATA = "com.nordicsemi.nrfUART.RECEIVE_DATA";
    public final static String UUID_DATA = "com.nordicsemi.nrfUART.UUID_DATA";

    public final static String ACTION_TYPE = "com.nordicsemi.nrfUART.ACTION_TYPE";
}
