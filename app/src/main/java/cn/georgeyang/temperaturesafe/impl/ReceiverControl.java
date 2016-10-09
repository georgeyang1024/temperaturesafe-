package cn.georgeyang.temperaturesafe.impl;

import android.content.Context;
import android.content.Intent;

/**
 * Created by george.yang on 16/9/1.
 */
public interface ReceiverControl {
    void onReceiverMsg(Context context, Intent intent,String action, int id, Object data);
}
