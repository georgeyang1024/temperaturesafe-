package cn.georgeyang.temperaturesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 *
 */
public class DensityUtil {
    private static int[] windowsSize;
	public static  int[] getWindowsSize(View view, int maxWidth, int maxHeight) {
        if (windowsSize==null) {
            if (view!=null) {
                if (view.getContext() instanceof Activity) {
                    Activity activity = (Activity) view.getContext();
                    View decView = activity.getWindow().getDecorView();
                    int width = decView.getMeasuredWidth();
                    int height = decView.getMeasuredHeight();
                    windowsSize = new int[]{width,height};
                } else {
                    return new int[]{maxWidth,maxHeight};
                }
            } else {
                return new int[]{maxWidth,maxHeight};
            }
        }
		return windowsSize;
	}
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		if (density==0) { 
			density = context.getResources().getDisplayMetrics().density;
		}
		return (int) (dpValue * density + 0.5f);
	}
	private static float density;
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static float px2dip(Context context, float pxValue) {
		if (density==0) { 
			density = context.getResources().getDisplayMetrics().density;
		}
		return pxValue / density + 0.5f;
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 * @param
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 * @param
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	private static int statusBarHeight;
	// 获取手机状态栏高度 2
	public static int getStatusBarHeight(Context context) {
		if (statusBarHeight!=0) return statusBarHeight;
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0; 
		try { 
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance(); 
			field = c.getField("status_bar_height"); 
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x); 
			} catch (Exception e1) {
				e1.printStackTrace(); 
				} 
		return statusBarHeight; 
		}
	
	public static DisplayMetrics getScreenSize (Activity activity) {
		WindowManager wm = activity.getWindowManager();
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

    /**
     * 获取手机屏幕宽高
     * @param mContext
     * @return
     */
    public static int[] getScreenSize(Context mContext)
    {
        WindowManager mManager = (WindowManager)
                mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mOutMetrics = new DisplayMetrics();
        mManager.getDefaultDisplay().getMetrics(mOutMetrics);

        return new int[]{mOutMetrics.widthPixels,mOutMetrics.heightPixels};
    }

	private static DisplayMetrics md;
	public static int getWidthWithScreenByPersent (Activity activity, float persent) {
		if (md==null) {
			md  = getScreenSize(activity);
		}
		int width = (int) (md.widthPixels*persent);
		return width;
	}

	public static float limitValue(float a, float b) {
		float valve = 0;
		final float min = Math.min(a, b);
		final float max = Math.max(a, b);
		valve = valve > min ? valve : min;
		valve = valve < max ? valve : max;
		return valve;
	}

	/**
	 * 获取屏幕宽度
	 * @param activity
	 * @return
	 */
	public static int getWindowWith(Activity activity){
		WindowManager wm = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		return  width;
	}


    private static int windowsHeight = 0;
	/**
	 * 获取屏幕高度
	 * @param activity
	 * @return
	 */
	public static int getWindowHeight(Activity activity){
        if (windowsHeight==0) {
            WindowManager wm = (WindowManager) activity
                    .getSystemService(Context.WINDOW_SERVICE);
            windowsHeight = wm.getDefaultDisplay().getHeight();
        }
		return windowsHeight;
	}
}
