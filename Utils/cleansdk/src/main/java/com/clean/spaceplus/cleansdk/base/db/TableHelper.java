package com.clean.spaceplus.cleansdk.base.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Collection;

/**
 * @author shunyou.huang
 * @Description:数据库表格管理器
 * @date 2016/4/22 14:16
 * @copyright TCL-MIG
 */

public interface TableHelper<T> {
    String _ID = "_id";
    /**
     * 创建相应数据表
     */
    Collection<String> getCreateTableSqls();

    /**
     * 删除相应数据表
     */
    Collection<String>  getDropTableSqls();

    /**
     * 更新数据表
     * @param oldVersion 旧版 本号
     * @param newVersion 新版本号
     */
    Collection<String>  getUpdateTableSqls(int oldVersion, int newVersion);

    /**
     * 获取表名
     * @return 返回数据表名
     */
    String getTableName();

    /**
     * 从对象实体中提取数据库插入内容
     * @param t 对象实体
     * @return 返回内容集
     */
    ContentValues getContentValues(T t);

    /**
     * 从游标中解析出对象实体对象
     * @param cursor 数据库查询游标
     * @return 返回对象实体
     */
    T parseCursor(Cursor cursor);

    /**
     * 获取对应表操作的域名
     * @return
     */
    String getProviderAuthority();
}
