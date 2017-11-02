package com.clean.spaceplus.cleansdk.junk.cleancloud.residual;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 15:17
 * @copyright TCL-MIG
 */
public class KPkgRegexQuery {
    public static class PkgRegxData {
        public int mPkgId;
        public String mPkgRegex;
        public String[] mDirs;
    }

    static class InnerPkgRegxData {
        PkgRegxData mData;
        Pattern mPattern;
    }

    private volatile boolean isInited = false;
    private ArrayList<InnerPkgRegxData> mRegxDatyas = new ArrayList<InnerPkgRegxData>();

    public boolean isInitialized() {
        return isInited;
    }

    public boolean initialize(Collection<PkgRegxData> regxDatyas) {
        synchronized (this) {
            if (isInited)
                return true;

            if (null == regxDatyas)
                return false;

            boolean result = true;

            if (regxDatyas.isEmpty()) {
                isInited = true;
                return result;
            }

            mRegxDatyas.ensureCapacity(regxDatyas.size());
            for (PkgRegxData data : regxDatyas) {
                if (TextUtils.isEmpty(data.mPkgRegex))
                    continue;

                Pattern p = null;

                try {
                    p = Pattern.compile(data.mPkgRegex);
                } catch (Exception e) {
                    p = null;
                }
                if (null == p)
                    continue;

                InnerPkgRegxData newData = new InnerPkgRegxData();
                newData.mData = data;
                newData.mPattern = p;
                mRegxDatyas.add(newData);
            }

            isInited = true;
            return result;
        }
    }

/*	public void unInitialize() {
		if (isInited) {
			mRegxDatyas.clear();
			isInited = false;
		}
	}*/

    public LinkedList<PkgRegxData> query(String pkgname) {
        if (TextUtils.isEmpty(pkgname))
            return null;

        if (mRegxDatyas.isEmpty())
            return null;

        LinkedList<PkgRegxData> result = null;
        for (InnerPkgRegxData data : mRegxDatyas) {
            if (data.mData.mDirs == null || data.mData.mDirs.length == 0)
                continue;

            Matcher matcher = data.mPattern.matcher(pkgname);

            if (null == matcher || !matcher.matches()) {
                continue;
            }

            if (null == result) {
                result = new LinkedList<PkgRegxData>();
            }
            result.add(clonePkgRegxData(data.mData));
        }
        return result;
    }

    PkgRegxData clonePkgRegxData(PkgRegxData data) {
        PkgRegxData result = new PkgRegxData();
        result.mPkgId = data.mPkgId;
        result.mPkgRegex = data.mPkgRegex;
        if (data.mDirs != null) {
            result.mDirs = new String[data.mDirs.length];
            System.arraycopy(data.mDirs, 0, result.mDirs, 0, data.mDirs.length);
        }
        return result;
    }
}
