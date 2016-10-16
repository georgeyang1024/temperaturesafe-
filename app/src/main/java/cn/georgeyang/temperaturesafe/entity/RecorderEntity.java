package cn.georgeyang.temperaturesafe.entity;

import cn.georgeyang.database.Model;

/**
 * 记录者
 * Created by george.yang on 16/10/16.
 */
public class RecorderEntity extends Model {
    public String name;
    public RecorderEntity() {}

    public RecorderEntity (String name_) {
        name = name_;
    }
}
