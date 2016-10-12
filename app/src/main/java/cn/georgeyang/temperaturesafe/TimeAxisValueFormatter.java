package cn.georgeyang.temperaturesafe;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class TimeAxisValueFormatter implements IAxisValueFormatter
{

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int hour = (int) (value / 60);
        int min = (int) (value % 60);
        return hour + ":" + min;
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
