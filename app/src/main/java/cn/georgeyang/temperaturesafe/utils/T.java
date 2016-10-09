package cn.georgeyang.temperaturesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by george.yang on 16/9/3.
 */
public class T {
    public static void showToast(Context context,String string) {
        Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
    }
}
