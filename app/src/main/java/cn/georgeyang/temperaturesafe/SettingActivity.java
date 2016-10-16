package cn.georgeyang.temperaturesafe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.jingchen.timerpicker.PickerView;

import java.util.ArrayList;
import java.util.List;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.temperaturesafe.databinding.ActivitySettingBinding;
import cn.georgeyang.temperaturesafe.dialog.AdjustDialog;
import cn.georgeyang.temperaturesafe.dialog.HeightAlarmDialog;
import cn.georgeyang.temperaturesafe.dialog.LowAlarmDialog;
import cn.georgeyang.temperaturesafe.entity.SettingEntity;
import cn.georgeyang.temperaturesafe.impl.BaseActivity;
import cn.georgeyang.temperaturesafe.utils.AppUtil;
import cn.georgeyang.temperaturesafe.utils.DensityUtil;
import cn.georgeyang.temperaturesafe.utils.TitleUtil;


/**
 * Created by george.yang on 16/8/27.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private SettingEntity settingEntity;
    private Switch sw_vibrationAlarm,sw_lostAlarm,sw_soundAlarm,sw_lowAlarm;
    private TextView tv_low,tv_height,tv_abjust;

    private static final List<String> temChoiceList = new ArrayList<>();
    private static final List<String> ajuChoiceList = new ArrayList<>();
    static {
        for (float i=32;i<=42;i+=0.1) {
            temChoiceList.add(String.format("%.1f", i));
        }
        for (float i=-2;i<2.2;i+=0.1) {
            ajuChoiceList.add(String.format("%.1f", i));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Mdb.init(this);
        settingEntity = AppUtil.getSettingEntity(this);

        ActivitySettingBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_setting);
        binding.setSetting(settingEntity);

        sw_soundAlarm = (Switch) findViewById(R.id.sw_soundAlarm);
        sw_soundAlarm.setChecked(settingEntity.soundAlarm);
        sw_soundAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AppUtil.stopPlay(getActivity());
                }
            }
        });

        sw_vibrationAlarm = (Switch) findViewById(R.id.sw_vibrationAlarm);
        sw_vibrationAlarm.setChecked(settingEntity.vibrationAlarm);
        sw_vibrationAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AppUtil.stopVibrator();
                }
            }
        });

        sw_lostAlarm = (Switch) findViewById(R.id.sw_lostAlarm);
        sw_lostAlarm.setChecked(settingEntity.lostAlarm);
        sw_lostAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AppUtil.stopPlay(getActivity());
                    AppUtil.stopVibrator();
                }
            }
        });

        sw_lowAlarm = (Switch) findViewById(R.id.sw_lowAlarm);
        sw_lowAlarm.setChecked(settingEntity.lowAlarm);
        sw_lowAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AppUtil.stopPlay(getActivity());
                    AppUtil.stopVibrator();
                }
            }
        });

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
            settingEntity.lowAlarm = sw_lowAlarm.isChecked();

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
                View view = View.inflate(this,R.layout.layout_tmpchoice,null);
                final PickerView pickerView = (PickerView) view.findViewById(R.id.picker);
                pickerView.setData(temChoiceList);
                String choice = String.format("%.1f", settingEntity.highTemperatureValue);
                pickerView.setSelected(temChoiceList.indexOf(choice));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                builder.setTitle("选择最高报警温度!");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String choice = temChoiceList.get(pickerView.getSelected());
                        settingEntity.highTemperatureValue = Float.valueOf(choice);
                        tv_height.setText(settingEntity.highTemperatureValue+"ºC");
                        if (settingEntity.highTemperatureValue<Vars.startHeightWaringTemp) {
                            AppUtil.stopPlay(getActivity());
                            AppUtil.stopVibrator();
                        }
                    }
                });
                builder.show();
                break;
            case R.id.layout_low:
                View view2 = View.inflate(this,R.layout.layout_tmpchoice,null);
                final PickerView pickerView2 = (PickerView) view2.findViewById(R.id.picker);
                pickerView2.setData(temChoiceList);
                String choice2 = String.format("%.1f", settingEntity.lowTemperatureValue);
                pickerView2.setSelected(temChoiceList.indexOf(choice2));

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setView(view2);
                builder2.setTitle("选择最低报警温度!");
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String choice = temChoiceList.get(pickerView2.getSelected());
                        settingEntity.lowTemperatureValue = Float.valueOf(choice);
                        tv_low.setText(settingEntity.lowTemperatureValue+"ºC");
                        if (settingEntity.lowTemperatureValue>Vars.startLowWarningTemp) {
                            AppUtil.stopPlay(getActivity());
                            AppUtil.stopVibrator();
                        }
                    }
                });
                builder2.show();

//                lowAlarmDialog = new LowAlarmDialog(this,settingEntity.lowTemperatureValue);
//                lowAlarmDialog.show();
//                lowAlarmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        settingEntity.lowTemperatureValue = lowAlarmDialog.getValue();
//                        tv_low.setText(settingEntity.lowTemperatureValue+"ºC");
//                    }
//                });
                break;
            case R.id.layout_adjust:
                View view3 = View.inflate(this,R.layout.layout_tmpchoice,null);
                final PickerView pickerView3 = (PickerView) view3.findViewById(R.id.picker);
                pickerView3.setData(ajuChoiceList);
                String choice3 = String.format("%.1f", settingEntity.adjustTemperatureValue);
                pickerView3.setSelected(ajuChoiceList.indexOf(choice3));

                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setView(view3);
                builder3.setTitle("选择温度校准值!");
                builder3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String choice = ajuChoiceList.get(pickerView3.getSelected());
                        settingEntity.adjustTemperatureValue = Float.valueOf(choice);
                        tv_abjust.setText(settingEntity.adjustTemperatureValue+"ºC");
                    }
                });
                builder3.show();

//                adjustDialog = new AdjustDialog(this,settingEntity.adjustTemperatureValue);
//                adjustDialog.show();
//                adjustDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        settingEntity.adjustTemperatureValue = adjustDialog.getValue();
//                        tv_abjust.setText(settingEntity.adjustTemperatureValue+"ºC");
//                    }
//                });
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
