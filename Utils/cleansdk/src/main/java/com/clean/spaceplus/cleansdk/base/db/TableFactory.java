package com.clean.spaceplus.cleansdk.base.db;

import android.database.DatabaseErrorHandler;

/**
 * @author shunyou.huang
 * @Description:数据库表格处理
 * @date 2016/4/22 14:10
 * @copyright TCL-MIG
 */

public interface TableFactory {
    /**
     * 获取数据库名称
     * @return 返回数据库名称
     */
    String getDatabaseName();

    /**
     * 获取数据库版本
     * @return
     */
    int getDatabaseVersion();
    /**
     * 创建所有数据库表格辅助对象
     * @return
     */
    @SuppressWarnings("rawtypes")
    TableHelper[] createAllTableHelpers();

    /**
     * 根据实体类的类型获取到相应的表格辅助器
     * @param cls 类
     * @return 返回对应实体bean的表操作辅助对象
     */
    <T> TableHelper<T> getTableHelper(Class<T> cls);

    /**
     * 获取特定类型的DAO操作接口
     * @param cls 类
     * @return
     */
    <T> PojoDao<T>	getDao(Class<T> cls);

    /**
     * 获取数据库错误处理器
     * @return
     */
    DatabaseErrorHandler getErrorHandler();
}
