package cn.georgeyang.temperaturesafe;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.georgeyang.temperaturesafe.utils.TitleUtil;

/**
 * Created by george.yang on 16/8/29.
 */
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TitleUtil.init(this).setTitle("关于").autoBack();
    }
}
