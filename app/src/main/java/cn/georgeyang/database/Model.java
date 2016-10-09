package cn.georgeyang.database;


import cn.georgeyang.base.ParcelableEntity;

/**
 *
 * Created by george.yang on 2015/9/16.
 */
public abstract class Model extends ParcelableEntity {
    public int _id;
    public long _addTime;
    public long _updateTime;

    public Model (){
    }

    public boolean delete() {
        return Mdb.getInstance().delete(this);
    }

    public boolean save() {
        return Mdb.getInstance().insertOrUpdate(this);
    }

}

