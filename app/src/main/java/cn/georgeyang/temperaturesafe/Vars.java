package cn.georgeyang.temperaturesafe;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.temperaturesafe.entity.SettingEntity;

/**
 * Created by george.yang on 16/9/4.
 */
public class Vars {
    public static  final int Type_C = 0;
    public static final  int Type_F = 1;
    public static final int WaringTypeLost = 1;
    public static final int WaringTypeHeight = 2;
    public static final int WaringTypeUnknow = 0;
    public static final int WaringTypeLow = 3;

    public static  final int RecordInterval = 1000 * 60 * 3;//5分钟记录一条
    public static final int ShowInterval = 1000 * 60 * 6;

    public static final int WaringCheckTime = 1000 * 30;//持续30秒不正常就报警

    public static boolean waring = false;//正在报警

    public static final int WaringTimes = 10;//报警声音循环次数//循pool环次数，0为播放一次，-1为无线循环，其他正数+1为播放次数，如传递3，循环播放4次

    //上下左右空隙，便于数据查看
    public static final int EmptyY = 10,EmptyMin = 5;
    public static final int MinShowTemperature = 32,MaxShowTemperature = 42;

    //起始报警温度
    public static long unNomalStartTime;
    public static float startLowWarningTemp;
    public static float startHeightWaringTemp;

    public static int lastWaringType;//最后报警原因
}
