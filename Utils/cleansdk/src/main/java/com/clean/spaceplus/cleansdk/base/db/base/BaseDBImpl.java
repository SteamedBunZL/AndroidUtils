package com.clean.spaceplus.cleansdk.base.db.base;

import android.content.Context;
import android.database.DatabaseErrorHandler;

import com.clean.spaceplus.cleansdk.base.db.PojoDao;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/28 11:08
 * @copyright TCL-MIG
 */
public abstract class BaseDBImpl implements TableFactory {

    Map<Class, Item> dbItems;
    Context mContext;

    static class Item {
        TableHelper<?> tableHelper;
        PojoDao<?> dao;
    }

    public BaseDBImpl(Context mContext) {
        this.mContext = mContext;
        dbItems = new HashMap<>();
    }


    public void addHelper(Class<?> clazz, TableHelper<?> helper, PojoDao<?> dao) {
        Item item = new Item();
        item.tableHelper = helper;
        item.dao = dao;
        dbItems.put(clazz, item);
    }

    @Override
    public TableHelper[] createAllTableHelpers() {
        if (dbItems.size() == 0)
            return null;

        TableHelper<?>[] helpers = new TableHelper<?>[dbItems.size()];
        Collection<Item> items = dbItems.values();
        int index = 0;
        for (Item item: items) {
            helpers[index++] = item.tableHelper;

        }
        return helpers;
    }

    @Override
    public <T> TableHelper<T> getTableHelper(Class<T> cls) {
        if (dbItems.size() == 0)
            return null;

        Item item = dbItems.get(cls);
        if (item != null)
            return (TableHelper<T>)item.tableHelper;

        return null;
    }

    @Override
    public <T> PojoDao<T> getDao(Class<T> cls) {
        if (dbItems.size() == 0)
            return null;

        Item item = dbItems.get(cls);
        if (item != null) {
            if (item.dao == null)
                item.dao = new PojoDao<T>(mContext, (TableHelper<T>)item.tableHelper);

            return (PojoDao<T>)item.dao;
        }

        return null;
    }

    @Override
    public DatabaseErrorHandler getErrorHandler() {
        return null;
    }
}
