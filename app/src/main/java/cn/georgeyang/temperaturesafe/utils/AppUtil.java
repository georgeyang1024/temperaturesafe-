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

import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.temperaturesafe.R;
import cn.georgeyang.temperaturesafe.Vars;
import cn.georgeyang.temperaturesafe.entity.ChartResultVo;
import cn.georgeyang.temperaturesafe.entity.RecorderEntity;
import cn.georgeyang.temperaturesafe.entity.SettingEntity;
import cn.georgeyang.temperaturesafe.entity.TemperatureDataEntity;
import cn.georgeyang.temperaturesafe.service.BluetoothLeService;
import cn.georgeyang.utils.DialogUtil;

/**
 * Created by george.yang on 16/9/4.
 */
public class AppUtil {
    public static SettingEntity getSettingEntity(Context context) {
        if (settingEntity==null) {
            Mdb.init(context);
            settingEntity = Mdb.getInstance().findOne(SettingEntity.class);
        }
        if (settingEntity==null) {
            settingEntity = new SettingEntity();
            //first Install
            new RecorderEntity("爸爸").save();
            new RecorderEntity("妈妈").save();
            new RecorderEntity("儿子").save();
            new RecorderEntity("女儿").save();
            settingEntity.save();
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
    private static SoundPool soundPool;
    public static void playWarning (Context context) {
        Vars.waring = true;
//        if (Vars.WaringTimes>0) {
//            try {
//                soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
//                soundPool.load(context, R.raw.warning,Vars.WaringTimes);
//                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                    @Override
//                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                        soundPool.play(1,1, 1, 0,Vars.WaringTimes, 1);
//                    }
//                });
//            } catch (Exception e) {
//
//            }
//        } else {
            try {
                if (mediaPlayer!=null) {
                    stopPlay(context);
                }

                mediaPlayer = MediaPlayer.create(context, R.raw.warning);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setVolume(1f, 1f);
//                mediaPlayer.prepare();
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Vars.waring = false;
                        try {
                            DialogUtil.getDialog().dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void stopPlay(Context context) {
        Vars.waring = false;
//        Vars.unNomalStartTime = 0;//del for 30S
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
        Vars.waring = false;
//        Vars.unNomalStartTime = 0;//del for 30s
        try {
            vibrator.cancel();
        } catch (Exception e) {

        }
    }


    private static final Calendar cc = Calendar.getInstance();
    /**
     * 根据年月日获取一整天的数据
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static List<TemperatureDataEntity> getDataListByDate(int year,int month,int day,int selectRecorderId) {
        Calendar tagDateZeroTime = Calendar.getInstance();
        tagDateZeroTime.set(year,month,day);
        tagDateZeroTime.set(Calendar.HOUR_OF_DAY,0);
        tagDateZeroTime.set(Calendar.MINUTE,0);
        tagDateZeroTime.set(Calendar.SECOND,0);
        Calendar tagDateEndTime = Calendar.getInstance();
        tagDateEndTime.set(year,month,day);
        tagDateEndTime.set(Calendar.HOUR_OF_DAY,23);
        tagDateEndTime.set(Calendar.MINUTE,59);
        tagDateEndTime.set(Calendar.SECOND,59);


        String where = String.format("_addTime>%s and _addTime<%s and recordId = %s",new Object[]{tagDateZeroTime.getTimeInMillis(),tagDateEndTime.getTimeInMillis(),selectRecorderId});
        List<TemperatureDataEntity> ret = Mdb.getInstance().findAllbyWhere(TemperatureDataEntity.class,where);

//        Log.d("test","size:" + ret.size());
//        Random random = new Random();
//        int count = 1000;
//        List<TemperatureDataEntity> ret = new ArrayList<>();
//        for (int i=1;i<count;i++) {
//            TemperatureDataEntity dataEntity = new TemperatureDataEntity();
//            dataEntity.temperature = (float) (random.nextDouble() * 40 + 10);
//            dataEntity.type = Vars.Type_C;
//            dataEntity._addTime = todayZeroTime.getTimeInMillis() + 1000 * 60 * i;
//            ret.add(dataEntity);
//        }
        return ret;
    }

    /**
     * 通过间隔获取显示的数据
     * @param interval ms
     * @return
     */
    public static List<TemperatureDataEntity> getChartListByInterval(List<TemperatureDataEntity> list,long interval) {
        List<TemperatureDataEntity> ret = new ArrayList<>();

        long lastMs = 0;
        for (TemperatureDataEntity entity:list) {
            Log.i("test",new Gson().toJson(entity));
            if (entity._addTime >= lastMs + interval) {
                cc.setTimeInMillis(entity._addTime);

                ret.add(entity);
                lastMs = entity._addTime;
            }
        }
        return ret;
    }


    public static final long ONEDAYMILLIS = 1000*60*60*24;
    /**
     * 将自带间隔的list转城list
     * @param intervalList
     * @return
     */
    public static ChartResultVo buildChartList (List<TemperatureDataEntity> intervalList,int year,int month,int day) {
        ChartResultVo ret = new ChartResultVo();
        ret.resultList  = new ArrayList<Entry>();

        Calendar todayZeroTime = Calendar.getInstance();
        todayZeroTime.set(year,month,day);
        todayZeroTime.set(Calendar.HOUR_OF_DAY,0);
        todayZeroTime.set(Calendar.MINUTE,0);
        todayZeroTime.set(Calendar.SECOND,0);
        long zeroTime = todayZeroTime.getTimeInMillis();



        for (TemperatureDataEntity entity:intervalList) {
            //今天运行了多少毫秒
            long todayMillis =  entity._addTime - zeroTime;
            if (todayMillis<=0 || todayMillis>=ONEDAYMILLIS) {
                continue;
            }
            long todayMINUTE = todayMillis / (1000*60);

            if (entity.temperature < ret.minY) {
                ret.minY = entity.temperature;
            }
            if (entity.temperature > ret.maxY) {
                ret.maxY = entity.temperature;
            }

            if (todayMINUTE < ret.minX) {
                ret.minX = todayMINUTE;
            }
            if (todayMINUTE > ret.maxX) {
                ret.maxX = todayMINUTE;
            }

            Entry entry = new Entry(todayMINUTE,entity.temperature);
            ret.resultList.add(entry);
        }

//        for (TemperatureDataEntity entity:dataList) {
//            //今天运行了多少毫秒
//            long todayMillis =  entity._addTime - zeroTime;
//            if (todayMillis<=0 || todayMillis>=1000*60*60*24) {
//                continue;
//            }
//            //这个时间运行了多少分钟
//            long todayMINUTE = todayMillis / (1000*60);
//            if (todayMINUTE%15==0) {
//                if (entity.temperature < minY) {
//                    minY = entity.temperature;
//                }
//                if (entity.temperature> maxY) {
//                    maxY = entity.temperature;
//                }
//
//                //刚好15分钟
//                if (todayMINUTE < minX) {
//                    minX = todayMINUTE;
//                }
//                if (todayMINUTE > maxX) {
//                    maxX = todayMINUTE;
//                }
//                Entry entry = new Entry(todayMINUTE,entity.temperature);
//                values.add(entry);
//            }
//        }


        return ret;
    }

}
