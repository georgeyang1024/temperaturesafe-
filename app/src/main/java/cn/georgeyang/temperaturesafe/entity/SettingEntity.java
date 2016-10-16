package cn.georgeyang.temperaturesafe.entity;

import android.annotation.SuppressLint;

import cn.georgeyang.database.Model;

/**
 * Created by george.yang on 16/9/2.
 */
@SuppressLint("ParcelCreator")
public class SettingEntity extends Model {
    public boolean vibrationAlarm,soundAlarm,lostAlarm;
    public float highTemperatureValue = 0,lowTemperatureValue = 50,adjustTemperatureValue = 0;
//    public float adjustTemperatureValue = 0;
    public int recorderId = 1;
    public long startWarningTime;//允许开始警报的时间点


    public boolean isVibrationAlarm() {
        return vibrationAlarm;
    }

    public void setVibrationAlarm(boolean vibrationAlarm) {
        this.vibrationAlarm = vibrationAlarm;
    }

    public boolean isSoundAlarm() {
        return soundAlarm;
    }

    public void setSoundAlarm(boolean soundAlarm) {
        this.soundAlarm = soundAlarm;
    }

    public boolean isLostAlarm() {
        return lostAlarm;
    }

    public void setLostAlarm(boolean lostAlarm) {
        this.lostAlarm = lostAlarm;
    }

    public float getHighTemperatureValue() {
        return highTemperatureValue;
    }

    public void setHighTemperatureValue(float highTemperatureValue) {
        this.highTemperatureValue = highTemperatureValue;
    }

    public float getLowTemperatureValue() {
        return lowTemperatureValue;
    }

    public void setLowTemperatureValue(float lowTemperatureValue) {
        this.lowTemperatureValue = lowTemperatureValue;
    }

//    public float getAdjustmentValue() {
//        return adjustmentValue;
//    }
//
//    public void setAdjustmentValue(float adjustmentValue) {
//        this.adjustmentValue = adjustmentValue;
//    }

//    public float getAdjustTemperatureValue() {
//        return adjustTemperatureValue;
//    }
//
//    public void setAdjustTemperatureValue(float adjustTemperatureValue) {
//        this.adjustTemperatureValue = adjustTemperatureValue;
//    }
}
