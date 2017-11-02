package com.clean.spaceplus.cleansdk.base.db.residual_dir_hf;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author shunyou.huang
 * @Description:PkgQuery数据提供
 * @date 2016/4/23 15:08
 * @copyright TCL-MIG
 */

public class ResidualDirHfProvider extends BaseDatabaseProvider {

    private final static String TAG = ResidualDirHfProvider.class.getSimpleName();
    private static ResidualDirHfProvider pkgQueryHfProvider = null;

    public static synchronized ResidualDirHfProvider getInstance() {
        if (pkgQueryHfProvider == null) {
            pkgQueryHfProvider = new ResidualDirHfProvider(SpaceApplication.getInstance().getContext());
        }
        return pkgQueryHfProvider;
    }

    public ResidualDirHfProvider(Context context){
        //onCreate(context,PkgQueryHfFactory.createFactory(context));
        onCreate(context, BaseDBFactory.getTableFactory(context, BaseDBFactory.TYPE_RESIDUAL_DIR_HF));
    }


}
