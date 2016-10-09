package cn.georgeyang.temperaturesafe.utils;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.temperaturesafe.R;
import cn.georgeyang.temperaturesafe.Vars;
import cn.georgeyang.temperaturesafe.entity.SettingEntity;
import cn.georgeyang.temperaturesafe.entity.TemperatureDataEntity;
import cn.georgeyang.temperaturesafe.service.BluetoothLeService;

/**
 * Created by george.yang on 16/9/4.
 */
public class AppUtil {
    public static SettingEntity getSettingEntity(Context context) {
        if (settingEntity==null) {
            Mdb.init(context);
            settingEntity = Mdb.getInstance().findOne(SettingEntity.class);
        }
        return settingEntity;
    }

    private static SettingEntity settingEntity;

    public static void setSettingEntity(SettingEntity entity) {
        if (entity==null) {
            Mdb.getInstance().dropTable(SettingEntity.class);
        } else {
            entity.save();
            settingEntity = entity;
        }
    }


    private static String HEX_CHAR="0123456789ABCDEF";
    public static String[] getValue(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length*2);
        for(int i=0;i<bytes.length;i++) {
            sb.append(HEX_CHAR.charAt((bytes[i]&0xf0)>>4));
            sb.append(HEX_CHAR.charAt((bytes[i]&0x0f)>>0));
        }
//        String hex = sb.toString();
        String tmp = sb.substring(4,6) + sb.substring(2,4);
        int v = Integer.parseInt(tmp,16);
        float v2 = v/10f;
        return new String[]{Integer.valueOf(sb.substring(0,2))  + "",v2+""};
    }

    private static MediaPlayer mediaPlayer;
    public static void playWarning (Context context) {
//        SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
//        soundPool.load(this, R.raw.warning,1);
//        soundPool.play(1,1, 1, 0, 0, 1);

        try {
            if (mediaPlayer==null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.warning);
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
            }
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopPlay(Context context) {
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
        }
    }

    private static  Vibrator vibrator;
    public static void startVibrator(Context context) {
        if (vibrator==null) {
            vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        }
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{100, 100, 100, 200,100,300,100,400,100,500,100,600,100,700,100,800,100,900,100,1000}, -1);
        }
    }

    public static void stopVibrator() {
        try {
            vibrator.cancel();
        } catch (Exception e) {

        }
    }

    public static List<TemperatureDataEntity> getDataListByDate(int year,int month,int day) {
        Random random = new Random();
        int count = random.nextInt(5000);
        List<TemperatureDataEntity> ret = new ArrayList<>();
        for (int i=0;i<count;i++) {
            TemperatureDataEntity dataEntity = new TemperatureDataEntity();
            dataEntity.temperature = (float) (random.nextDouble() * 40 + 10);
            dataEntity.type = Vars.Type_C;
            ret.add(dataEntity);
        }
        return ret;
    }

}
