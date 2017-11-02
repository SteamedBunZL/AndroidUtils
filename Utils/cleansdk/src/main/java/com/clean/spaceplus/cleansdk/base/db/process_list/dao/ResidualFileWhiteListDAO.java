package com.clean.spaceplus.cleansdk.base.db.process_list.dao;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListProvider;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author zengtao.kuang
 * @Description: ResidualFile白名单DAO
 * @date 2016/5/11 18:51
 * @copyright TCL-MIG
 */
public class ResidualFileWhiteListDAO extends WhiteListDAO{

    private ProcessListProvider mProvider;
    ResidualFileWhiteListDAO(){
        mProvider = ProcessListProvider.getInstance(SpaceApplication.getInstance().getContext());
    }

    @Override
    public ProcessListProvider getProvider() {
        return mProvider;
    }

    protected String getNameString(String str) {
        if (TextUtils.isEmpty(str))
            return str;

        //modify by qiuruifeng 2013.11.21
        //由于云端残留等查询获取的路径大小写可能和原始的路径大写不一致,所以对于路径，一律转成小写
        String strName;
        if (str.contains("/")) {
            strName = StringUtils.toLowerCase(str);
        } else {
            strName = str;
        }
        return strName;
    }


    public boolean queryExists(String strPackageName){
        boolean result = super.queryExists(getNameString(strPackageName));
        return result;
    }
}
