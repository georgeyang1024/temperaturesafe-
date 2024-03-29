package cn.georgeyang.temperaturesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import cn.georgeyang.temperaturesafe.R;
import cn.georgeyang.temperaturesafe.SettingActivity;

/**
 * Created by george.yang on 16/9/1.
 */
public class TitleUtil {
    public static TitleUtil init(View view) {
        return new TitleUtil(view);
    }
    public static TitleUtil init (Activity activity) {
        return new TitleUtil(activity);
    }

    private Activity activity;
    private View view;
    public TitleUtil (View view) {
        this.view = view;
    }

    public TitleUtil (Activity activity) {
        this.activity = activity;
        this.view = activity.getWindow().getDecorView();
    }

    public TitleUtil setTitle(String title) {
        TextView textView = (TextView) view.findViewById(R.id.tv_title);
        textView.setText(title);
        return this;
    }

    public TitleUtil autoBack() {
        View btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity!=null) {
                    activity.finish();
                } else  {
                    Activity activity = (Activity) v.getContext();
                    activity.finish();
                }
            }
        });
        return this;
    }

    public TitleUtil autoGoSetting() {
        View btn_back = view.findViewById(R.id.tv_setting);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), SettingActivity.class);
                    v.getContext().startActivity(intent);
            }
        });
        return this;
    }
}
