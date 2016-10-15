package cn.georgeyang.temperaturesafe.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.georgeyang.temperaturesafe.R;

/**
 * Created by george.yang on 16/10/15.
 */

public class DeviceScanEmptyView extends LinearLayout {
    public DeviceScanEmptyView(Context context) {
        super(context);
        init();
    }

    public DeviceScanEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DeviceScanEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DeviceScanEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init () {
        addView(inflate(getContext(), R.layout.layout_devicesstatus,null));
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvStatus.setText("");
    }

    TextView tvStatus;

    public void setStatus (String status) {
        tvStatus.setText(status);
    }
}
