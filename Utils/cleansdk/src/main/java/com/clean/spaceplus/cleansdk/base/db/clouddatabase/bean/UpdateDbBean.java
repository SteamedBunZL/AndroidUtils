package com.clean.spaceplus.cleansdk.base.db.clouddatabase.bean;

import com.clean.spaceplus.cleansdk.base.bean.BaseBean;

import java.util.List;

/**
 * @author shunyou.huang
 * @Description:数据库升级bean
 * @date 2016/5/20 14:44
 * @copyright TCL-MIG
 */
public class UpdateDbBean extends BaseBean{

    public List<DataBean> data;

    public static class DataBean {
        public String versionName;
        public String versionId;
        public int versionType;
        public String url;

        @Override
        public String toString() {
            return  "\n"+"DataBean{" + "\n" +
                    "versionName = "+ versionName + ",\n" +
                    "versionId = "+ versionId + ",\n" +
                    "versionType = "+ versionType + ",\n" +
                    "url = "+ url + ",\n" +
                    "}";
        }
    }

}
