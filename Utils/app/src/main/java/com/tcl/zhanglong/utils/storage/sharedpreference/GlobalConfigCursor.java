package com.tcl.zhanglong.utils.storage.sharedpreference;

import android.database.AbstractCursor;
import android.support.v4.util.ArrayMap;

/**
 * Created by Steve on 16/10/25.
 */

public class GlobalConfigCursor extends AbstractCursor{

    private ArrayMap<String,String> mCursorMap = new ArrayMap<>();

    private String[] mProtection;

    GlobalConfigCursor(String[] keys,ArrayMap<String,String> map){
        initMap(keys,map);
    }

    private void initMap(String[] keys,ArrayMap<String,String> map){
        mProtection = new String[keys.length];
        System.arraycopy(keys,0,mProtection,0,keys.length);
        for(int i = 0;i<keys.length;i++){
            String key = keys[i];
            String value = map.get(key);
            mCursorMap.put(key,value);
        }
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mProtection!=null)
            count = mProtection.length;
        return count;
    }

    @Override
    public String[] getColumnNames() {
        String[] names = null;
        if (mProtection!=null)
            names = mProtection;
        return names;
    }

    @Override
    public String getString(int column) {
        if (column>=mProtection.length)
            new Exception("GlobalConfigCursor column out of index.");
        String key = mProtection[column];
        String value = mCursorMap.get(key);
        return value;
    }

    @Override
    public short getShort(int column) {
        return 0;
    }

    @Override
    public int getInt(int column) {
        return 0;
    }

    @Override
    public long getLong(int column) {
        return 0;
    }

    @Override
    public float getFloat(int column) {
        return 0;
    }

    @Override
    public double getDouble(int column) {
        return 0;
    }

    @Override
    public boolean isNull(int column) {
        return false;
    }
}
