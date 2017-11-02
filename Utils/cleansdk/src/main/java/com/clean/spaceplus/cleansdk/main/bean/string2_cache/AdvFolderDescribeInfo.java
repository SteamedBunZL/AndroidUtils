package com.clean.spaceplus.cleansdk.main.bean.string2_cache;

import com.clean.spaceplus.cleansdk.main.bean.Bean;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/3 15:53
 * @copyright TCL-MIG
 */
public class AdvFolderDescribeInfo extends Bean {
    public String _id;
    public String id;
    public String lang;
    public String value;

    @Override
    public String toString() {
        return "AdvFolderDescribeInfo{" +
                "_id='" + _id + '\'' +
                ", id='" + id + '\'' +
                ", lang='" + lang + '\'' +
                ", value='" + value + '\'' +
                "} " + super.toString();
    }
}
