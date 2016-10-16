package cn.georgeyang.database;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.georgeyang.utils.LruCache;
import cn.georgeyang.utils.ObjectUtil;
import cn.georgeyang.utils.ReflectUtil;

@SuppressLint("NewApi")
public class Mdb {
    private static String TAG = "mquerydb";
    private static LruCache dbchache;
    private static String defaulePath;
    private SQLiteDatabase db;
    private String dbfile;
    private static String dbFileName = "yy.db";
    private static final boolean debug = false;
    private static void log(String msg) {
        if (!debug  ) {
            return;
        }
        Log.e(TAG,msg);
    }


    private static Mdb mdb;
    public static void init (Context context) {
        if (mdb==null) {
            if (context!=null) {
                mdb = new Mdb(context);
            }
        }
    }

    public static Mdb getInstance() {
        return mdb;
    }


    public Mdb(Context context) {
        if (defaulePath != null) {
            init(context, defaulePath, dbFileName);
        } else {
            init(context, null, null);
        }
    }

    public Mdb(Context context, String dbfilename) {
        init(context, null, dbfilename);
    }

    public Mdb(Context context, String sdcardPath, String dbfilename) {
        init(context, sdcardPath, dbfilename);
    }

    public static void setDefaultPath(String path) {
        defaulePath = path;
    }

    /**
     * copy data object to another object
     *
     * @param newObject
     * @param dbObject
     */
    public static void updataObject(Object newObject, Object dbObject) {
        try {
            Field[] fieds = newObject.getClass().getDeclaredFields();
            Field[] fieds2 = dbObject.getClass().getDeclaredFields();
            for (Field field : fieds) {
                field.setAccessible(true);//允许访问私有字段
                for (Field field2 : fieds2) {
                    field2.setAccessible(true);
                    try {
                        if (field.getName().equals(field2.getName())) {
                            Object newobj = field.get(newObject);
                            if (newobj == null) {
                                field.set(newObject, field2.get(dbObject));
                                continue;
                            }

                            boolean isNeedupdata = false;
                            if (newobj instanceof Long) {
                                if (((Long) newobj).longValue() == 0) {
                                    isNeedupdata = true;
                                }
                            } else if (newobj instanceof Integer) {
                                if (((Integer) newobj).intValue() == 0) {
                                    isNeedupdata = true;
                                }
                            } else if (newobj instanceof Double) {
                                if (((Double) newobj).doubleValue() == 0) {
                                    isNeedupdata = true;
                                }
                            }


                            if (isNeedupdata) {
                                field.set(newObject, field2.get(dbObject));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printCursor(Cursor cursor) {
        log( "prineCursor:" + cursor);
        if (cursor == null) {
            log(  "cursor = null");
        } else {
            StringBuffer sb = new StringBuffer();

            int count = cursor.getColumnCount();
            log(  "count:" + count);
            while (cursor.moveToNext()) {
                log(  "moveNet");
                for (int i = 0; i < count; i++) {
                    sb.append(cursor.getString(i) + "\t");
                }
            }
            String resu = sb.toString();
            if (resu == null || resu.equals("")) {
                log(  "cursor have no result!");
            } else {
                log(  sb.toString());
            }
        }
    }

    /**
     * cursor to object
     *
     * @param cursor
     * @param cls
     * @return
     */
    public static <T> T cursor2object(Cursor cursor, Class<T> cls) {
        Field[] fieds = ReflectUtil.getAllNoStaticFiedFromClassAndSuper(cls);
        T result = null;
        try {
            result = cls.newInstance();
            for (Field field : fieds) {
                field.setAccessible(true);//允许访问私有字段
                Class<?> subcls = field.getType();
                String subname = subcls.getSimpleName();
                int cindex = cursor.getColumnIndex(getDBFiedName(cls,field));//字段名称);
                int ctype = -1;
                try {
                    Method method = Cursor.class.getDeclaredMethod("getType", int.class);
                    if (method == null) {
                        ctype = getCursorTypeInLowApi(subname);
                    } else {
                        ctype = cursor.getType(cindex);
                    }
                } catch (Exception e) {
                    log( "error:" + e.getMessage());
                    ctype = getCursorTypeInLowApi(subname);
                }

                if (cindex != -1) {
                    if (ctype == Cursor.FIELD_TYPE_STRING) {
                        if (subname.equals("String")) {
                            field.set(result, cursor.getString(cindex));
                        } else if (subname.equals("long") || subname.equals("Long")) {
                            field.set(result, cursor.getLong(cindex));
                        } else if (subname.equals("double") || subname.equals("Double")) {
                            String str = cursor.getString(cindex);
                            double res = 0;
                            try {
                                res = Double.parseDouble(str);
                            } catch (Exception e) {
                            }
                            field.set(result, res);
                        }
                    } else if (ctype == Cursor.FIELD_TYPE_INTEGER) {
                        if (subname.equals("int") || subname.equals("Integer")) {
                            field.set(result, cursor.getInt(cindex));
                        } else if (subname.equals("boolean") || subname.equals("Boolean")) {
                            int var = cursor.getInt(cindex);
                            if (var == 1) {
                                field.set(result, true);
                            } else {
                                field.set(result, false);
                            }
                        }
                    } else if (subname.equals("float") && ctype == Cursor.FIELD_TYPE_FLOAT) {
                        field.set(result, cursor.getFloat(cindex));
                    } else if (ctype == Cursor.FIELD_TYPE_BLOB) {
                        //other object
                        field.set(result, ObjectUtil.toObject(cursor.getBlob(cindex)));
                    } else {
                        //type is null,do nothing
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * object to ContentValues
     *
     * @return
     */
    public static ContentValues object2contentvalue(Object object) {
        ContentValues result = new ContentValues();

        ClassInfo classInfo = dbchache.get(object.getClass().getSimpleName());

        Field[] fieds = classInfo.fields;
        for (Field field : fieds) {
            field.setAccessible(true);
            Class<?> subcls = field.getType();
            String subname = subcls.getSimpleName();
            String keyname = getDBFiedName(classInfo._class,field);

//            if (keyname.equals("_id") || (classInfo.isActivDB && "id".equals(keyname))) {
//                continue;
//            }
            try {
                if (subname.equals("String")) {
                    result.put(keyname, (String) field.get(object));
                } else if (subname.equals("int") || subname.equals("Integer")) {
                    int _int = field.getInt(object);
                    if (keyname.equals("_id") || (classInfo.isActivDB && "id".equals(keyname))) {
                        if (_int==0) {
                            continue;
                        }
                    }
                    result.put(keyname, _int);
                } else if (subname.equals("boolean") || subname.equals("Boolean")) {
                    result.put(keyname, field.getBoolean(object));
                } else if (subname.equals("long") || subname.equals("Long")) {
                    if (keyname.equals("_updateTime")) {
                        result.put(keyname, System.currentTimeMillis() + "");
                        continue;
                    }
                    if (keyname.equals("_addTime")) {
                        long addtime = System.currentTimeMillis();
                        try {
                            long addtime2 = field.getLong(object);
                            if (addtime2 > 0) {
                                addtime = addtime2 < addtime ? addtime2 : addtime;
                            }
                        } catch (Exception e) {
                        }
                        result.put(keyname, addtime + "");
                        continue;
                    }
                    result.put(keyname, "" + field.get(object));
                } else if (subname.equals("float") || subname.equals("Float")) {
                    result.put(keyname, field.getFloat(object));
                } else if (subname.equals("double") || subname.equals("Double")) {
                    result.put(keyname, field.get(object) + "");
                } else {
                    Object value = field.get(object);
                    if (value instanceof Serializable) {
                        byte[] data = ObjectUtil.toByteArray((Serializable) value);
                        result.put(keyname, data);
                    } else {
                        String info = String.format("field (%s) not implements Serializable,can not save!",new Object[]{field.getName()});
//                        throw  new IllegalStateException(info) ;
                        log(info);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * get fieldName on database
     *
     * @param field
     * @return
     */
    public static String getFiedType(Field field) {
        String classname = field.getType().getSimpleName();
        String type = null;
        if (classname.equals("String")) {
            type = "text";
        } else if (classname.equals("long") || classname.equals("Long")) {
            type = "text";
        } else if (classname.equals("int") || classname.equals("Integer")) {
            type = "integer";
        } else if (classname.equals("boolean") || classname.equals("Boolean")) {
            type = "integer";
        } else if (classname.equals("float") || classname.equals("Float")) {
            type = "Float";
        } else if (classname.equals("double") || classname.equals("Double")) {
            type = "text";
        } else {
            type = "BLOB";
        }
        return type;
    }

    /**
     *
     *
     * @param field
     * @return
     */
    public static String getDBFiedName(Class clazz,Field field) {
        ClassInfo classInfo = dbchache.get(clazz.getSimpleName());
        return getDBFiedName(classInfo.isActivDB,field);
    }

    public static String getDBFiedName(boolean isActivDb,Field field) {
        if (isActivDb) {
            String fName = ReflectUtil.getValueByFieldAnntation(field, "com.activeandroid.annotation.Column", "name");
            if (!TextUtils.isEmpty(fName)) {
                return fName;
            }
        }
        String name = field.getName();
//		if (name.equals("id")) {
//			return "_id";
        if (name.equals("values")) {
            return "_values";
        } else if (name.equals("table")) {
            return "_table";
        } else if (name.equals("group")) {
            return "_group";
        } else if (name.equals("index")) {
            return "_index";
        } else {
            return name;
        }
    }

    /**
     * @return
     */
    public static int getCursorTypeInLowApi(String subname) {
        //2.3不支持该方法
        if (subname.equals("String")) {
            return Cursor.FIELD_TYPE_STRING;
        } else if (subname.equals("long") || subname.equals("Long")) {
            return Cursor.FIELD_TYPE_STRING;
        } else if (subname.equals("double") || subname.equals("Double")) {
            return Cursor.FIELD_TYPE_STRING;
        } else if (subname.equals("int") || subname.equals("Integer")) {
            return Cursor.FIELD_TYPE_INTEGER;
        } else if (subname.equals("boolean") || subname.equals("Boolean")) {
            return Cursor.FIELD_TYPE_INTEGER;
        } else if (subname.equals("float") || subname.equals("Float")) {
            return Cursor.FIELD_TYPE_FLOAT;
        } else {
            //(subname == Cursor.FIELD_TYPE_BLOB)
            return Cursor.FIELD_TYPE_BLOB;
        }
    }

    /**
     *
     */
    public static String getValues(String value) {
        if (value.indexOf("'") != -1) {
            value = value.replace("'", "\'");
        }
        return value;
    }

    private void init(Context context, String path, String dbfilename) {
        if (context == null) {
            throw new IllegalStateException("context is null,Dbl cannot be create");
        }
        if (dbchache == null) {
            dbchache = new LruCache(1000);
        }
        if (dbfilename == null) {
            dbfilename = dbFileName;
        }
        if (path == null) {
            path = context.getDatabasePath(dbfilename).getParent();
//            path = context.getFilesDir().getAbsolutePath();
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dbfile = path + File.separator + dbfilename;
        log( "path:" + dbfile);

        db = dbchache.get(dbfile);
        if (db == null) {
            db = context.openOrCreateDatabase(dbfile, Application.MODE_PRIVATE, null);
            dbchache.put(dbfilename, db);
        }
    }

    public SQLiteDatabase getdb() {
        return this.db;
    }

    /**
     * @param clazz
     */
    public void updataTable(Class clazz) {
        log( "updataTable:" +clazz);
        checktable(clazz);
        log( "updataTable:start");
            try {
                ClassInfo classInfo = dbchache.get(clazz.getSimpleName());
                List<Field> list = getNewFields(clazz);
                    for (Field field:list) {
                        String sql = "alter table %s add %s %s";
                        String dbfield = Mdb.getDBFiedName(classInfo.isActivDB, field);// 字段名称 / field.getName();//字段名
                        String dbtype = Mdb.getFiedType(field);
                        String tableName = classInfo.tableName;
                        String excSql = String.format(sql, new Object[]{tableName, dbfield, dbtype});
                        db.execSQL(excSql);
                        log( excSql);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    /**
     * @param clazz
     * @return
     */
    public List<Field> getNewFields(Class clazz) {
        List<Field> ret = new ArrayList<>();
        try {
            ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
            Field[] fieds = cinfo.fields;
            String strSQL = "PRAGMA table_info([" + cinfo.tableName + "])";
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(strSQL, null);
                for (Field field : fieds) {
                    boolean fieldExistInTable = false;

                    String dbfied = getDBFiedName(clazz,field);
                    log( "field2DBField:" + field.getName() + ">>" + dbfied);
                    if (cursor.moveToFirst()) {
                        do {
                            String name = cursor.getString(cursor.getColumnIndex("name"));
                            if (dbfied.equals(name)) {
                                fieldExistInTable = true;
                                break;
                            }
                        } while (cursor.moveToNext());
                        log(  "isExistInDBField?:" + fieldExistInTable);

                        if (!fieldExistInTable) {
                            ret.add(field);
                        }
                    } else {
                        ret.add(field);
                    }
                }
            } catch (Exception e) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 表的字段 是否包含 class的全部字段
     *
     * @param clazz
     * @return
     */
    public boolean isLastTable(Class clazz) {
        return getNewFields(clazz).size()==0;
    }

    public <T> List<T> findAll(Class<T> clazz) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName;
        return findAllBySql(clazz, strSQL);
    }

    public int getCount(Class clazz) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        return getCount(cinfo.tableName);
    }

    public int getCount(String tabname) {
        String strSQL = "select count(_Id) from " + tabname;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(strSQL, null);
            if (cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCountByWhere(Class clazz, String where) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        return getCountByWhere(cinfo.tableName, where);
    }

    public int getCountByWhere(String tabname, String where) {
        String strSQL = "select count(_Id) from " + tabname + " where " + where;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(strSQL, null);
            if (cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getLastId (Class clazz) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        Integer id =  getMax(clazz,cinfo.id.getName(),Integer.class);
        if (id==null) {
            return 0;
        } else {
            return id.intValue();
        }
    }

    public <T> T getMax(Class clazz, String fiedname, Class<T> back) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());

        String strSQL = "select " + fiedname + " from " + cinfo.tableName + " order by " + fiedname + " desc limit 1";
        log(  "getmaxsql:" + strSQL);
        Cursor cursor = null;
        Object result = null;
        try {
            cursor = db.rawQuery(strSQL, null);
            if (cursor.moveToNext()) {
                String cname = back.getSimpleName();
                int index = cursor.getColumnIndex(fiedname);
                if (cname.equals("int") || cname.equals("Integer")) {
                    result = cursor.getInt(index);
                } else if (cname.equals("float") || cname.equals("Float")) {
                    result = cursor.getFloat(index);
                } else if (cname.equals("long") || cname.equals("Long")) {
                    result = cursor.getLong(index);
                } else if (cname.equals("double") || cname.equals("Double")) {
                    String str = cursor.getString(index);
                    double res = 0;
                    try {
                        res = Double.parseDouble(str);
                    } catch (Exception e) {
                    }
                    result = res;
                } else if (cname.equals("String")) {
                    result = cursor.getString(index);
                } else {
                    result = ObjectUtil.toObject(cursor.getBlob(index));
                }
            }
        } catch (Exception e) {
            //Failed to read row 0, column -1 from a CursorWindow which has 1 rows, 1 columns.
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            cursor = null;
        }
        return (T) result;
    }

    public <T> List<T> findAllbyDesc(Class<T> clazz, String order) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " order by " + order + " desc";
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyAsc(Class<T> clazz, String order) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " order by " + order + " asc";
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyPageDesc(Class<T> clazz, String order, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " order by " + order + " desc" + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyPageAsc(Class<T> clazz, String order, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " order by " + order + " asc" + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyPageDesc(Class<T> clazz, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " order by _id desc" + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyPageAsc(Class<T> clazz, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " order by _id asc" + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyWhere(Class<T> clazz, String where) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyWhereDesc(Class<T> clazz, String where, String order) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where + " order by " + order + " desc";
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyWhereAsc(Class<T> clazz, String where, String order) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where + " order by " + order + " asc";
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyPage(Class<T> clazz, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyWherePage(Class<T> clazz, String where, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyWherePageAsc(Class<T> clazz, String where, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where + " order by _id asc " + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyWherePageDesc(Class<T> clazz, String where, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where + " order by _id desc " + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyWhereOrderPageDesc(Class<T> clazz, String where, String order, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where + " order by " + order + " desc " + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> List<T> findAllbyWhereOrderPageAsc(Class<T> clazz, String where, String order, int page, int onepagecount) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where + " order by " + order + " asc " + " limit " + (page - 1) * onepagecount + "," + onepagecount;
        return findAllBySql(clazz, strSQL);
    }

    public <T> T findOnelBySql(Class<T> clazz, String strSQL) {
//		try {
        List<T> list = findAllBySql(clazz, strSQL);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
//		} catch (Exception e) {
//
//		}
//		return null;
    }

    public <T> List<T> findAllBySql(Class<T> clazz, String strSQL) {
        return findAllBySql(clazz,strSQL,null);
    }

    public <T> List<T> findAllBySql(Class<T> clazz, String strSQL,String[] selectionArgs) {
        checktable(clazz);
        log(  strSQL);
        Cursor cursor = null;
        List<T> list = new ArrayList<T>();
        try {
            cursor = db.rawQuery(strSQL, selectionArgs);
            while (cursor.moveToNext()) {
                T data = cursor2object(cursor, clazz);
                list.add(data);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return list;
    }

    public <T> T findOne(Class<T> clazz) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " order by _id desc";
        return findOnelBySql(clazz, strSQL);
    }

    public <T> T findOnebyWhere(Class<T> clazz, String fieldName,Object value) {
        return findOnebyWhere(clazz,String.format("%s='%s'",new Object[]{fieldName,value}));
    }
    public <T> T findOnebyWhere(Class<T> clazz, String where) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + getValues(where);
        return findOnelBySql(clazz, strSQL);
    }

    public <T> T findOnebyWhereDesc(Class<T> clazz, String order, String where) {
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        String strSQL = "select * from " + cinfo.tableName + " where " + where + " order by " + order + " desc ";
        return findOnelBySql(clazz, strSQL);
    }

    public boolean save(Object object) {
        return insert(object)>=0;
    }

    public long insert(Object object) {
        Class clazz = object.getClass();
        checktable(clazz);
        ClassInfo cinfo = dbchache.get(clazz.getSimpleName());
        long re = db.insert(cinfo.tableName, null, object2contentvalue(object));
        log(  "insert result:" + re);
        return re;
    }

    public long insertOrUpdateRetId(Object object) {
        int objId = getObjectId(object);
        log( "object id:" + objId);

//        Object findObj = findOnebyWhere(object.getClass(), "_id=" + objId);
        Object findObj =  findOnebyWhereDesc(object.getClass(),"_id","_id=" + objId);
        log(  "object:" + findObj);
        if (findObj == null) {
            return insert(object);
        } else {
            if (updata(object)){
                return objId;
            }
        }
        return -1;
    }

    public boolean insertOrUpdate(Object object) {
        int objId = getObjectId(object);
        log(  "object id:" + objId);
        Object findObj = findOnebyWhere(object.getClass(), "_id=" + objId);
        log(  "object:" + findObj);
        if (findObj == null) {
            return save(object);
        } else {
            return updata(object);
        }
    }

    public boolean isSuccess(int i){
        return i >= 0;
    }

    public int getObjectId(Object object) {
        checktable(object.getClass());
        ClassInfo cinfo = dbchache.get(object.getClass().getSimpleName());
        Field cid = cinfo.id;
        try {
            if (cid == null) {
                throw new IllegalStateException(object.getClass().getName() + " has no 'id' or '_id',can not updata");
            } else {
                return cid.getInt(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updata(Object object) {
        checktable(object.getClass());
        ClassInfo cinfo = dbchache.get(object.getClass().getSimpleName());
        Field cid = cinfo.id;
        if (cid == null) {
            throw new IllegalStateException(object.getClass().getName() + " has no 'id' or '_id',can not updata");
        } else {
            try {
                String where[] = {cid.getInt(object) + ""};
                return updata(object, "_id=?", where);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updata(Object object, String whereClause, String[] whereArgs) {
        log( "update:" + whereClause + Arrays.toString(whereArgs));
        checktable(object.getClass());
        ClassInfo cinfo = dbchache.get(object.getClass().getSimpleName());
        int re = this.db.update(cinfo.tableName, object2contentvalue(object), whereClause, whereArgs);
        log( "effect "+re+"rows");
        return re == 0 ? false : true;
    }

    public boolean updata(Object object, int _id) {
        String where[] = {_id + ""};
        return updata(object, "_id=?", where);
    }

    public boolean delete(Object object) {
        checktable(object.getClass());
        ClassInfo cinfo = dbchache.get(object.getClass().getSimpleName());
        Field cid = cinfo.id;
        if (cid == null) {
            throw new IllegalStateException(object.getClass().getName() + " has no 'id' or '_id',can not update");
        } else {
            try {
                return delete(cinfo, cid.getName() + "='" + cid.get(object) + "'");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean delete(ClassInfo cinfo, String where) {
        try {
            String delsql = "delete from " + cinfo.tableName + " where " + where;
            log(  delsql);
            this.db.execSQL(delsql);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean delete(Class cls, String where) {
        checktable(cls);
        ClassInfo cinfo = dbchache.get(cls.getSimpleName());
        return delete(cinfo,where);
    }

    private boolean checktable(Class cls) {
        if (!tableIsExist(cls)) {
            ClassInfo cinfo = dbchache.get(cls.getSimpleName());
            db.execSQL(cinfo.createSql);
            log(  cinfo.createSql);
            cinfo.tableIsExist = true;
            return true;
        }
        return false;
    }

    public boolean dropTable(Class cls) {
        return dropTable(cls.getSimpleName());
    }

    public boolean dropTable(String name) {
        String sql = "DROP TABLE " + name;
        log(  sql);
        try {
            this.db.execSQL(sql);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean tableIsExist(Class cls) {
        ClassInfo info = dbchache.get(cls.getSimpleName());
        if (info == null) {
            dbchache.put(cls.getSimpleName(), new ClassInfo(cls));
            boolean exist = tableIsExist(cls.getSimpleName());
            log( "exist:" +exist);
            if (exist) {
                //when first time check this table,if table is exist,update it
                updataTable(cls);

            }
            return exist;
        } else {
            return info.tableIsExist;
        }
    }

    public boolean tableIsExist(String tablename) {
        Cursor cursor = null;
        try {
            String sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + tablename + "' ";
            log(  sql);
            cursor = this.db.rawQuery(sql, null);
            if ((cursor != null) && (cursor.moveToNext())) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
            }
        }
        return false;
    }



}
