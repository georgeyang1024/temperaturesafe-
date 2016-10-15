package cn.georgeyang.temperaturesafe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import cn.georgeyang.temperaturesafe.utils.TitleUtil;

/**
 * Created by george.yang on 16/10/15.
 */

public class NameMangerActivity extends AppCompatActivity {
    public ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_namemanger);
        TitleUtil.init(this).setTitle("关于").autoBack();
        listView = (ListView) findViewById(R.id.listView);
//        listView.setAdapter();
    }
}
