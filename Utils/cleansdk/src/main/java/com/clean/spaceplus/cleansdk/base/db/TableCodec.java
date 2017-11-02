package com.clean.spaceplus.cleansdk.base.db;

/**
 * @author shunyou.huang
 * @Description:表格辅助类
 * @date 2016/4/26 10:56
 * @copyright TCL-MIG
 */

public class TableCodec<T> {
    public Class<T> clazz;
    public TableHelper<T> helper;
    public PojoDao<T> dao;

    public TableCodec(Class<T> c, TableHelper<T> h, PojoDao<T> d) {
        this.clazz = c;
        this.helper = h;
        this.dao = d;
    }
}
