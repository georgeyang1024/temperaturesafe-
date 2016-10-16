package cn.georgeyang.temperaturesafe.widget;

/**
 * Created by george.yang on 16/10/9.
 */
import android.app.DatePickerDialog;
        import android.content.Context;
        import android.view.View;
        import android.widget.DatePicker;

public class AppPickerDialog extends DatePickerDialog {
    private int chooiceYear, chooiceMonth, chooiceDay;
    private DatePicker picker;
    private Context mContent;
    private OnDateSetListener mCallBack;

    public AppPickerDialog(final Context context,
                           final OnDateSetListener callBack, int year, int monthOfYear,
                           int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        mContent = context;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        picker = view;
        chooiceYear = year;
        chooiceMonth = month;
        chooiceDay = day;
    }

    /**
     * 完成事件
     * @param callBack
     */
    public void setOnPositiveListener(OnDateSetListener callBack) {
        mCallBack = callBack;
    }

    @Override
    public void show() {
        super.show();

//        this.getWindow().getDecorView().findViewById(android.R.id.button1)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dismiss();
//                        if (mCallBack!=null) {
//                            mCallBack.onDateSet(picker, chooiceYear, chooiceMonth,chooiceDay);
//                        }
//                    }
//                });
    }
}