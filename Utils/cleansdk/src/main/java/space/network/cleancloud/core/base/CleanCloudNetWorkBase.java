package space.network.cleancloud.core.base;

import android.content.Context;

import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;

import space.network.cleancloud.KNetWorkHelper;
import space.network.cleancloud.KNetWorkHelper.PostResult;
import space.network.commondata.KPostConfigData;
import space.network.util.KCleanCloudUrlUtil;

public abstract class CleanCloudNetWorkBase<DATA_TYPE, CALLBACK_TYPE> {
	public static final String TAG= CleanCloudNetWorkBase.class.getSimpleName();
	private static final int CHANGE_HOST_RETRY_MAX_TIMES = 1;

	private boolean mIsUseAbroadServer = false;
	
	private int mMaxRetryTimes = CHANGE_HOST_RETRY_MAX_TIMES;
	private int mTimeOut = 60 * 1000;
	
	private KPostConfigData mConfigData = new KPostConfigData();
	private KCleanCloudUrlUtil mUrlGeter;





	public CleanCloudNetWorkBase(Context context, String[] urls){
		mUrlGeter = new KCleanCloudUrlUtil(urls, mTimeOut, KCleanCloudUrlUtil.DEFAULT_HOST_UNKNOWN_DISABLEDLIMITTIME);
	}



	protected abstract byte[] getPostData(
			KPostConfigData configData,
			Collection<DATA_TYPE> datas,
			CALLBACK_TYPE callback);

	protected abstract boolean decodeResultData(
			KPostConfigData configData,
			Collection<DATA_TYPE> datas,
			PostResult postResult);

	public boolean isUseAbroadServer() {
		return mIsUseAbroadServer;
	}

	public boolean setConfigData(KPostConfigData configData) {
		if (null == configData)
			return false;

		mConfigData = configData;
		return true;
	}
	public boolean setOthers(String uuid, int appVersion) {
		return mConfigData.setOthers(uuid, appVersion);
	}

	public boolean setMCC(String mcc) {
		return mConfigData.setMCC(mcc);
	}

	public boolean setChannelConfig(short channelId, String channelKey, String responseKey) {
		return mConfigData.setChannelConfig(channelId, channelKey, responseKey);
	}

	public boolean setLanguage(String language) {
		return mConfigData.setLanguage(language);
	}

	public void setTimeOut(int timeOut) {
		mTimeOut = timeOut;
	}


	public boolean query(Collection<DATA_TYPE> datas, CALLBACK_TYPE callback) {
		if (null == datas || datas.isEmpty()){
			return false;
		}

		byte[] postData = null;

		int postsize = 0;
		PostResult postResult = null;
		StringBuffer buffer = new StringBuffer();
		//int nTimeOutCount = 0;
		for (int i = 0; i < mMaxRetryTimes; ++i) {
			if (postData == null) {
				postData = getPostData(mConfigData, datas, callback);
				for (int j = 0; j < postData.length; j++){
					if (j == 0){
						buffer.append("[");
					}
					buffer.append(postData[j]);
					if (j != postData.length - 1){
						buffer.append(",");
					}
					if (j == postData.length - 1){
						buffer.append("]");
					}
				}
				NLog.d(TAG, "getPostData str " + buffer.toString());
				if (null == postData) {
					return false;
				}
			}
			postsize = postData.length;
			KNetWorkHelper.PostClient postClient = new KNetWorkHelper.PostClient();
			String url = mUrlGeter.getUrl(i);
			NLog.d(TAG,"postData size = " + postsize + ", url = " + url + ", mConfigData = " + mConfigData);
			postClient.setUrl(url);
			postResult = postClient.post(postData, mTimeOut);
			NLog.d(TAG,"postResult = " + postResult);

			decodeResultData(mConfigData, datas, postResult);

		}

		return (postResult != null && postResult.mErrorCode == 0);
	}
	
	public boolean query(DATA_TYPE data, CALLBACK_TYPE callback) {
		ArrayList<DATA_TYPE> datas = new ArrayList<>(1);
		datas.add(data);
		return query(datas, callback);
	}
}
