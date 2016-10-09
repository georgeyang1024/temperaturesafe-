package cn.georgeyang.temperaturesafe.utils;

import android.util.Log;

/**
 * Created by george.yang on 16/9/2.
 */
public class L {
    private static final boolean isDebug = true;
    public static void showLog(String str) {
        if (isDebug) {
            Log.i("test",str);
        }
    }
}
