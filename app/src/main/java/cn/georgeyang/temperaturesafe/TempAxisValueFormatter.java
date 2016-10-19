package cn.georgeyang.temperaturesafe;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class TempAxisValueFormatter implements IAxisValueFormatter
{

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int vint = (int) value;
        float minDis = Float.MAX_VALUE,minDisValue = 0f;
        for (float i=vint-1;i<=vint+1;i+=0.5f) {
            float dis = Math.abs(i-value);
            if (dis<minDis) {
                minDis = dis;
                minDisValue = i;
            }

        }
        return String.format("%.1f", minDisValue);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
