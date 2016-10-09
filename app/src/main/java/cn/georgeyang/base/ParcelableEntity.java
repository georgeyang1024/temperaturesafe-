package cn.georgeyang.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * 让实体可以Parcelable序列号
 * Created by george.yang on 16/3/12.
 */
public class ParcelableEntity implements Parcelable {
    private static final Gson gson = new Gson();
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(gson.toJson(this));
        parcel.writeString(this.getClass().getName());
    }

    public static final Creator CREATOR = new Creator() {
        public ParcelableEntity createFromParcel(Parcel in) {
            String json = in.readString();
            String clazz = in.readString();
            Object object = null;
            try {
                object = gson.fromJson(json, getClass().forName(clazz));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return (ParcelableEntity) object;
        }

        public ParcelableEntity[] newArray(int size) {
            return new ParcelableEntity[size];
        }
    };
}
