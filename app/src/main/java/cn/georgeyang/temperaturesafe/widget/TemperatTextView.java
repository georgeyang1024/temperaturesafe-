package cn.georgeyang.temperaturesafe.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * 温度大文本控件
 * Created by george.yang on 16/10/19.
 */

public class TemperatTextView extends TextView {
    public TemperatTextView(Context context) {
        super(context);
    }

    public TemperatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TemperatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TemperatTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setTipText(String tip) {
        setText(tip);
        setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
    }


    public void setTemperatText(String temperatText) {
        setText(temperatText);
        setTextSize(TypedValue.COMPLEX_UNIT_SP,50);
    }
}
