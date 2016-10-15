package cn.georgeyang.temperaturesafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.temperaturesafe.databinding.ActivitySettingBinding;
import cn.georgeyang.temperaturesafe.dialog.AdjustDialog;
import cn.georgeyang.temperaturesafe.dialog.HeightAlarmDialog;
import cn.georgeyang.temperaturesafe.dialog.LowAlarmDialog;
import cn.georgeyang.temperaturesafe.entity.SettingEntity;
import cn.georgeyang.temperaturesafe.impl.BaseActivity;
import cn.georgeyang.temperaturesafe.utils.AppUtil;
import cn.georgeyang.temperaturesafe.utils.TitleUtil;


/**
 * Created by george.yang on 16/8/27.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private SettingEntity settingEntity;
    private Switch sw_vibrationAlarm,sw_lostAlarm,sw_soundAlarm;
    private TextView tv_low,tv_height,tv_abjust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
         Mdb.init(this);

        settingEntity = AppUtil.getSettingEntity(this);
        if (settingEntity == null) {
            settingEntity = new SettingEntity();
        }


        ActivitySettingBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_setting);
        binding.setSetting(settingEntity);

        sw_vibrationAlarm = (Switch) findViewById(R.id.sw_vibrationAlarm);
        sw_vibrationAlarm.setChecked(settingEntity.vibrationAlarm);
        sw_lostAlarm = (Switch) findViewById(R.id.sw_lostAlarm);
        sw_lostAlarm.setChecked(settingEntity.lostAlarm);
        sw_soundAlarm = (Switch) findViewById(R.id.sw_soundAlarm);
        sw_soundAlarm.setChecked(settingEntity.soundAlarm);

        tv_low = (TextView) findViewById(R.id.tv_low);
        tv_height = (TextView) findViewById(R.id.tv_height);
        tv_abjust = (TextView) findViewById(R.id.tv_adjust);

        TitleUtil.init(this).setTitle("设置").autoBack();
        findViewById(R.id.layout_buleTool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,SelectDriveActivity.class));
            }
        });
        findViewById(R.id.layout_batter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,TestActivity.class));
            }
        });
        findViewById(R.id.layout_height).setOnClickListener(this);
        findViewById(R.id.layout_low).setOnClickListener(this);
        findViewById(R.id.layout_adjust).setOnClickListener(this);
        findViewById(R.id.layout_name).setOnClickListener(this);
        findViewById(R.id.layout_about).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (settingEntity!=null) {
            settingEntity.vibrationAlarm = sw_vibrationAlarm.isChecked();
            settingEntity.soundAlarm = sw_soundAlarm.isChecked();
            settingEntity.lostAlarm = sw_lostAlarm.isChecked();

            AppUtil.setSettingEntity(settingEntity);
        }
    }

    private HeightAlarmDialog heightAlarmDialog;
    private LowAlarmDialog lowAlarmDialog;
    private AdjustDialog adjustDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_height:
                heightAlarmDialog = new HeightAlarmDialog(this,settingEntity.highTemperatureValue);
                heightAlarmDialog.show();
                heightAlarmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        settingEntity.highTemperatureValue = heightAlarmDialog.getValue();
                        tv_height.setText(settingEntity.highTemperatureValue+"");
                    }
                });
                break;
            case R.id.layout_low:
                lowAlarmDialog = new LowAlarmDialog(this,settingEntity.lowTemperatureValue);
                lowAlarmDialog.show();
                lowAlarmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        settingEntity.lowTemperatureValue = lowAlarmDialog.getValue();
                        tv_low.setText(settingEntity.lowTemperatureValue+"");
                    }
                });
                break;
            case R.id.layout_adjust:
                adjustDialog = new AdjustDialog(this,settingEntity.adjustTemperatureValue);
                adjustDialog.show();
                adjustDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        settingEntity.adjustTemperatureValue = adjustDialog.getValue();
                        tv_abjust.setText(settingEntity.adjustTemperatureValue+"");
                    }
                });
                break;
            case R.id.layout_name:
                startActivity(new Intent(this,NameMangerActivity.class));
                break;
            case R.id.layout_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
        }
    }
}
