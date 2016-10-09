package cn.georgeyang.temperaturesafe;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.georgeyang.temperaturesafe.utils.TitleUtil;

/**
 * 设置监护人
 * Created by george.yang on 16/8/31.
 */
public class MonitorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        TitleUtil.init(this).setTitle("监护人设置").autoBack();
    }
}
