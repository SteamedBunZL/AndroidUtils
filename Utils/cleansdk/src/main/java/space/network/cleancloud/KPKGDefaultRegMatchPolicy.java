package space.network.cleancloud;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import space.network.util.hash.KQueryMd5Util;

public final class KPKGDefaultRegMatchPolicy {
    private final String srcRpkMd5;
    public KPKGDefaultRegMatchPolicy(String srcRpkMd5) {
        this.srcRpkMd5 = srcRpkMd5;

    }
    private String getSrcPkg() {
        return srcRpkMd5;
    }
    public boolean match(MessageDigest md5, KResidualCloudQuery.DirQueryData data) {
        if (data != null && data.mErrorCode == 0 && data.mResult != null
                && data.mResult.mQueryResult == KResidualCloudQuery.DirResultType.PKG_LIST) {
            boolean bExist = false;
            Collection<Long> mPackages = data.mResult.mPkgsMD5High64;
            Collection<String> mPackageRegexs = data.mResult.mPackageRegexs;

            md5.update(getSrcPkg().getBytes());
            byte[] pkgMd5 = md5.digest();
            long pkgMd5Half = KQueryMd5Util.getMD5High64BitFromMD5(pkgMd5);
            if (mPackages != null && mPackages.size() > 0) {// from cache db
                for (Iterator<Long> iterator = mPackages.iterator(); iterator
                        .hasNext();) {
                    long pkg = iterator.next();
                    if (pkgMd5Half == pkg) {
                        return true;
                    }
                }
            }
            if (mPackageRegexs != null && mPackageRegexs.size() > 0) {
                for (String regex : mPackageRegexs) {
                    if (TextUtils.isEmpty(regex))
                        continue;

                    /**
                     * @date 2014.09.19
                     * */
//					if (isRegPattern(regex, pkgMd5)) {
                    if (isRegPattern(regex, getSrcPkg())) {
                        return true;
                    }
                }
            }

            return bExist;
        }
        return false;
    }
    /**
     * @param regex
     * @param pkg
     * @return
     */
    private boolean isRegPattern(final String regex, final String pkg) {
        Pattern pkgNameRegPattern = null;
        try {
            pkgNameRegPattern = Pattern.compile(regex);
        } catch (Exception e) {
            pkgNameRegPattern = null;
        }
        if (null == pkgNameRegPattern) {
            throw new IllegalArgumentException("b.pnr.p:\"" + regex + "\".");
        }

        Matcher matcher = pkgNameRegPattern.matcher(pkg);
        if (null != matcher && matcher.matches()) {
            return true;
        }
        return false;
    }
}
