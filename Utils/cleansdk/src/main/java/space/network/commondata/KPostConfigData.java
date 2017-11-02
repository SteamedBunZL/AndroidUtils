//=============================================================================
/**
 * @file KPostConfigData.java
 * @brief
 */
//=============================================================================
package space.network.commondata;

import android.text.TextUtils;

import com.hawkclean.framework.log.NLog;

import space.network.util.KMiscUtils;
import space.network.util.compress.EnDeCodeUtils;

public class KPostConfigData {
    public static final String TAG = KPostConfigData.class.getSimpleName();
    public static final int XAID_BUFFER_SIZE = 24;
    public short  mChannelId;
    public int	   mVersion;
    public byte[] mXaid = new byte[XAID_BUFFER_SIZE];
    public byte[] mUuid = new byte[16];
    public byte[] mLang = new byte[6];
    public byte[] mPostDataEnCodeKey;
    public byte[] mResponseDecodeKey;
    public short mMCC = 0;

    public boolean setLanguage(String lang) {
        NLog.d(TAG, "setLanguage lang= %s", lang);
        if (TextUtils.isEmpty(lang))
            return false;

        byte langBytes[] = KMiscUtils.getLanguageBytes(lang);
        int len = (mLang.length < langBytes.length) ? mLang.length : langBytes.length;
        System.arraycopy(langBytes, 0, mLang, 0, len);
        return true;
    }

    public boolean setOthers(String uuid, int appVersion) {
        NLog.d(TAG, "uuid = %s, appVersion = %d", uuid,appVersion);
        mVersion = appVersion;

        if (uuid != null) {
            EnDeCodeUtils.copyHexStringtoBytes(uuid, mUuid, 0, 16);

            byte[] xaidBytes = null;
            if (!TextUtils.isEmpty(uuid)) {
                try {
                    xaidBytes = uuid.getBytes("utf-8");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (xaidBytes != null && xaidBytes.length > 0) {
                int len = xaidBytes.length > XAID_BUFFER_SIZE ? XAID_BUFFER_SIZE : xaidBytes.length;
                System.arraycopy(xaidBytes, 0, mXaid, 0, len);
            }
        }
        return true;
    }

    public boolean setMCC(String mcc) {
        if (!TextUtils.isEmpty(mcc)) {
            try {
                mMCC = Integer.valueOf(mcc).shortValue();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public boolean setChannelConfig(short channelId, String channelKey, String responseKey) {
        if (null == channelKey || null == responseKey)
            return false;

        mChannelId = channelId;

        mPostDataEnCodeKey = channelKey.getBytes();
        mResponseDecodeKey = responseKey.getBytes();

        return true;
    }

    @Override
    public String toString() {
        return "KPostConfigData{" +
                "mChannelId=" + mChannelId +
                ", mVersion=" + mVersion +
                ", mPostDataEnCodeKey=" + new String(mPostDataEnCodeKey) +
                ", mLang=" + new String(mLang) +
                ", mResponseDecodeKey=" + new String(mResponseDecodeKey) +
                ", mMCC=" + mMCC +
                '}';
    }
}