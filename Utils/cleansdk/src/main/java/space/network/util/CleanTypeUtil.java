package space.network.util;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.KCacheCloudQuery.ScanType;

/**
 * @author dongdong.huang
 * @Description: 清理类型帮助类
 * @date 2016/4/25 14:07
 * @copyright TCL-MIG
 */
public class CleanTypeUtil {
    public static final int CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE = 65535;
    public static boolean accept(int scanClealType,KCacheCloudQuery.PkgQueryPathItem pkgQueryPathItem){
        if(pkgQueryPathItem == null){
            return false;
        }
        boolean result = false;
        if (scanClealType == ScanType.INVALID || pkgQueryPathItem.mPrivacyType == ScanType.INVALID) {
            result = false;
        }else if(scanClealType == ScanType.PRIVACY && pkgQueryPathItem.mPrivacyType > 0){
            result = true;
        } else if (scanClealType == ScanType.DEFAULT) {
            //清理所有
            if (pkgQueryPathItem.mCleanType == ScanType.CAREFUL || pkgQueryPathItem.mCleanType == ScanType.SUGGESTED) {
                result = true;
            }
        } else if (scanClealType == pkgQueryPathItem.mCleanType) {
            //清理类型符合
            result = true;
        } else if (scanClealType == ScanType.SUGGESTED_WITH_CLEANTIME) {
            if (pkgQueryPathItem.mCleanType == ScanType.SUGGESTED
                    || (pkgQueryPathItem.mCleanType == ScanType.CAREFUL && pkgQueryPathItem.mCleanTime != CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE))
                result = true;
        }
        return result;
    }
}
