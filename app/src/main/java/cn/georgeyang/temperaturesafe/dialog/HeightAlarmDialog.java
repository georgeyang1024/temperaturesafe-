package cn.georgeyang.temperaturesafe.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import cn.georgeyang.temperaturesafe.R;
import cn.georgeyang.utils.DialogUtil;

/**
 * 高温报警
 * Created by george.yang on 16/8/29.
 */
public class HeightAlarmDialog extends Dialog implements View.OnClickListener {
    private View conentView;
    private Activity activity;
    private float value;
    private EditText editText;

    public HeightAlarmDialog(Activity context,float value) {
        super(context);
        this.activity = context;
        this.value = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.dialog_heightalarm, null);
        setContentView(conentView);

        int w = activity.getWindowManager().getDefaultDisplay().getWidth();

        WindowManager.LayoutParams params = this.getWindow().getAttributes();//得到这个dialog界面的参数对象
        params.width = (int) (w * 0.95);//设置dialog的界面宽度
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;//设置dialog高度为包裹内容
        this.getWindow().setAttributes(params);//最后把这个参数对象设置进去，即与dialog绑定

        conentView.findViewById(R.id.btn_add).setOnClickListener(this);
        conentView.findViewById(R.id.btn_reduce).setOnClickListener(this);
        conentView.findViewById(R.id.tv_ok).setOnClickListener(this);
        conentView.findViewById(R.id.btn_cancel).setOnClickListener(this);

        editText = (EditText) conentView.findViewById(R.id.edt_input);
        editText.setText(value+"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                if (!checkValue(value+0.1f)){
                    return;
                }
                value += 0.1f;
                break;
            case R.id.btn_reduce:
                if (!checkValue(value-0.1f)){
                    return;
                }
                value -= 0.1f;
                break;
            case R.id.tv_ok:
                try {
                    float tmp = Float.valueOf(editText.getText().toString());
                    if (!checkValue(tmp)){
                        return;
                    }
                    value = tmp;
                    dismiss();
                } catch (Exception e) {
                    DialogUtil.showOKDialog(activity,"请输入正确的数值！",null);
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
        editText.setText(String.format("%.1f",value));
    }

    public float getValue() {
        return value;
    }

    private boolean checkValue(float tmp) {
        if (tmp<0) {
            DialogUtil.showOKDialog(activity,"不能低于0",null);
            return false;
        }
        if (tmp>100) {
            DialogUtil.showOKDialog(activity,"不能高于100",null);
            return false;
        }
        return true;
    }
}
