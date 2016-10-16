package cn.georgeyang.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 通用提示框
 * Created by george.yang on 15/11/17.
 */
public class DialogUtil {
    private static AlertDialog alertDialog;

    public static AlertDialog getDialog () {
        return alertDialog;
    }
    public static void showInfoDialog (Activity activity,String title,String tip) {
        showDialog(activity,true,title,tip,"确定","",null,null);
    }

    public static void showOKDialog (Activity activity,String tip) {
        showDialog(activity,true,"提示",tip,"确定","",null,null);
    }

//    public static void showInputDialog(final Activity activity, String title, String hint, String defShow, final OnInputDialogDoneListener listener) {
//        final EditText editText = new EditText(activity);
//        editText.setHint(hint);
//        if (!TextUtils.isEmpty(defShow)) {
//            editText.setText(defShow);
//        }
//        new AlertDialog.Builder(activity).setTitle(title).setView(editText).setPositiveButton("确定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        listener.onClick(dialog,which,editText);
//                    }
//                }).setNegativeButton("取消", null).show();
//    }


    public static void showNoFinishDialog (Activity activity) {
        showDialog(activity,true,"提示","功能正在开发中","确定","",null,null);
    }

    public static void showYNDialog (Activity activity,String tip,DialogInterface.OnClickListener okListener) {
        showDialog(activity,true,"提示",tip,"确定","取消",okListener,null);
    }

    public static void showNewFriendDialog (Activity activity,String tip,DialogInterface.OnClickListener okListener,DialogInterface.OnClickListener cancelListener) {
        showDialog(activity,true,"提示",tip,"同意","拒接",okListener,cancelListener);
    }

    public static void showOKDialog (Activity activity,String tip,DialogInterface.OnClickListener poListener) {
        showDialog(activity,true,"提示",tip,"确定","",poListener,null);
    }

    public static void showOKDialog (Activity activity,boolean cancelable,String tip,DialogInterface.OnClickListener poListener) {
        showDialog(activity,cancelable,"提示",tip,"确定","",poListener,null);
    }

    public static void showNetErrorDialog (Activity activity) {
        showDialog(activity,true,"","网络错误!","确定","",null,null);
    }

    public static void showPermissionDeniedDialog(Activity activity) {
        showDialog(activity,true,"","缺少权限!","确定","",null,null);
    }


//    public static void showReLoginDialog (final Activity activity) {
//        showDialog(activity,false, "提示", "请重新登录!", "确定", "", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                FragmentOnlySupportActivity loder = (FragmentOnlySupportActivity) activity;
//                loder.loadFragment("com.orange.plugmainview.general.LoginFragment");
//            }
//        }, null);
//    }
//
//    public static void showLoginDialog (final Activity activity) {
//        showDialog(activity,false, "提示", "请先登录!", "确定", "", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                FragmentOnlySupportActivity loder = (FragmentOnlySupportActivity) activity;
//                loder.loadFragment("com.orange.plugmainview.general.LoginFragment");
//            }
//        }, null);
//    }

    public static void showChatLoginFailToast(Activity activity) {
        showToast(activity,"登录聊天服务器失败!");
    }

    public static void showToast (Activity activity,String msg) {
        if (activity==null || TextUtils.isEmpty(msg)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                return;
            }
        }
        try {
            Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }


    public static void showDialog (Activity activity,boolean canCancel, String title, String tip, String positiveText, String negativeText, final DialogInterface.OnClickListener poListener, final DialogInterface.OnClickListener negativeListener) {
        if (alertDialog!=null) {
            if (alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
        if (activity==null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);  //先得到构造器
        builder.setCancelable(canCancel);
        builder.setMessage(tip); //设置内容
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title); //设置标题
        }
        if (!TextUtils.isEmpty(positiveText)) {
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() { //设置确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //关闭dialog
                    if (poListener!=null) {
                        poListener.onClick(dialog,which);
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() { //设置取消按钮
                 @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (negativeListener!=null) {
                    negativeListener.onClick(dialog,which);
                }
            }
        });
        }
//        builder.setNeutralButton("忽略", new DialogInterface.OnClickListener() {//设置忽略按钮
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        //参数都设置完成了，创建并显示出来
        alertDialog = builder.create();
        alertDialog.show();
    }


    private static ProgressDialog progressDialog;;
    public static void showProgressDialog (Activity activity) {
        showProgressDialog(activity,true);
    }

    public static void showProgressDialog (Activity activity,boolean canCancel) {
        closeProgressDialog();
        if (activity==null ){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                return;
            }
        }
        progressDialog = ProgressDialog.show(activity,null,"加载中",true,canCancel);
    }

    public static void closeProgressDialog () {
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }
    }

//    public static void showInputDialog (Activity activity,String tip,String def) {
//        final EditText et = new EditText(activity);
//        et.setText(def);
//        //获取ip而已，不用在乎
//        new AlertDialog.Builder(this).setTitle("请输入IP地址")
//                .setIcon(android.R.drawable.ic_dialog_info).setView(et)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        //数据获取
//                        //Toast.makeText(TestTabActivity.this, et.getText().toString(),
//                        //      Toast.LENGTH_LONG).show();
//                        mEditor.putString("ipadd", et.getText().toString());
//                        //关键在这儿，获取输入框的数据，原来很简单！！
//                        mEditor.commit();
//                    }
//                }).setNegativeButton("取消", null).show();
//    }
}
