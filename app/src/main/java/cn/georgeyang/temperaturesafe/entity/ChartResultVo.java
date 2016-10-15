package cn.georgeyang.temperaturesafe.entity;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

/**
 * Created by george.yang on 16/10/14.
 */

public class ChartResultVo {
    public float minX = Integer.MAX_VALUE,maxX = Integer.MIN_VALUE,minY = Integer.MAX_VALUE,maxY=Integer.MIN_VALUE;
    public List<Entry> resultList;
}
