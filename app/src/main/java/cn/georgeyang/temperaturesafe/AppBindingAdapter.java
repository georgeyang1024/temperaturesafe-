package cn.georgeyang.temperaturesafe;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.georgeyang.temperaturesafe.utils.L;

/**
 * Created by george.yang on 16/9/8.
 */
public class AppBindingAdapter {

    @BindingAdapter({"bind:tmp"})
    public static void showTmp(TextView view, float tmp) {
        L.showLog("showTmp:" + tmp);
        view.setText(tmp+"ºC");
    }

}
