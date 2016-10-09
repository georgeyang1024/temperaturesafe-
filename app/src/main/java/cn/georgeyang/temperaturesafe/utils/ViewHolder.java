package cn.georgeyang.temperaturesafe.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by george.yang on 16/9/1.
 */
public class ViewHolder {

    public static <T extends View> T get(View view,int id) {
        SparseArray<View> sparseArray = (SparseArray) view.getTag();
        if (sparseArray==null) {
            sparseArray = new SparseArray<>();
            view.setTag(sparseArray);
        }
        View ret = sparseArray.get(id);
        if (ret==null) {
            ret = view.findViewById(id);
            sparseArray.put(id,ret);
        }
        return (T) ret;
    }
}
