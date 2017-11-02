package com.clean.spaceplus.cleansdk.base.db.process_tips.dao;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.process_tips.CloudTipsModel;
import com.clean.spaceplus.cleansdk.junk.cleancloud.OwnThreadHandler;
import com.hawkclean.framework.log.NLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/6/27 14:56
 * @copyright TCL-MIG
 */
public class CloudTipsDAOHelper {

	public static final String TAG = CloudTipsDAOHelper.class.getSimpleName();
	/**云端文案对应进程方面的描述类型*/
	public static final int PROCESS_TIPS_TYPE = 0x10;
	/**云端文案对应应用管理方面的描述类型*/
	public static final int APK_TIPS_TYPE = 0x11;
	
	private  String UUID = null;
	private long LOCAL_TIPS_UPDATE_TIME = 0;

	/*当前是否是测试模式*/
	public static boolean isTestMode = false;

	private static CloudTipsDAOHelper instance = null;
//	private IncomingHandler handler = new IncomingHandler(this);
	private CloudTipsDao mTipsDaoImpl = new CloudTipsDao();
	private OwnThreadHandler mQueryThreadHandler = new OwnThreadHandler("quyeyProcessCloudTips");

	public static CloudTipsDAOHelper getInstance(){
		if(instance == null){
			instance = new CloudTipsDAOHelper();
		}
		return instance;
	}
	
	public static void clear(){
		instance = null;
	}

//	/**
//	 * 获取进程云端文案
//	 * @param pkgName
//	 * @return
//	 */
//	public synchronized void getProcessCloudTips(String pkgName,final GetTipsCallback callback){
//		getTips(pkgName, callback, PROCESS_TIPS_TYPE);
//	}
//
//
//	public synchronized void getApkCloudTips(Collection<String> pkgNames,final GetTipsCallback callback){
//		getTips(pkgNames, callback, APK_TIPS_TYPE);
//	}
//
//	/**
//	 * 获取app自定义Label信息(该方法会读取出数据库里面相应语言的所有Label信息，如果挨个匹配建议使用异步线程)
//	 * @param pkgList
//	 * @return 返回是否有Label更新标记
//	 */
//	public synchronized boolean updateAppLabels(List<ProcessModel> pkgList){
//		if(mTipsDaoImpl == null || pkgList == null || pkgList.size() <= 0){
//			return false;
//		}
//
//		boolean hasLabelChanged = false;
//		int listLen = pkgList.size();
//		int maxQueryCount = 30;//30条数据未一组
//		int len = listLen/maxQueryCount;
//		int lastLen = listLen%maxQueryCount;
//
////		for (ProcessModel subModel : pkgList) {
////				Log.e("test", "all-->"+subModel.getPkgName());
////		}
//
//		String localCurrentLang = getLocalCurrentLanguage(SpaceApplication.getInstance().getContext());
//		SQLiteDatabase defaultDb = mTipsDaoImpl.getProcessTipsDb();
//		try {
//			if(defaultDb != null){
//				if(len > 0){//分组内
//					for (int i = 0; i < len; i++) {
//						int start = i*maxQueryCount;
//						int end = (i+1)*maxQueryCount;
//						if(listLen < end){
//							break;
//						}
//						List<ProcessModel> subList = pkgList.subList(start, end);
//						hasLabelChanged = getLabelsByList(defaultDb, subList, localCurrentLang);
//					}
//				}
//
//				if(lastLen > 0){//分组剩余部分再进行请求
//					int end = len*maxQueryCount+lastLen;
//					if(end > 0 && listLen >= end){
//						List<ProcessModel> subList = pkgList.subList(len*maxQueryCount, end);
//						hasLabelChanged = getLabelsByList(defaultDb, subList, localCurrentLang);
//					}
//				}
//			}
//		} catch (Exception e) {
//		}finally{
//			if (defaultDb != null) {
//				defaultDb.close();
//			}
//		}
//		return hasLabelChanged;
//	}
	
//	/**
//	 * 获取子列表信息
//	 * @param defaultDb
//	 * @param subList
//	 * @param localCurrentLang
//	 * @return
//	 */
//	private boolean getLabelsByList(SQLiteDatabase defaultDb,List<ProcessModel> subList,String localCurrentLang){
//		boolean hasLabelChanged = false;
//
//		if(mTipsDaoImpl == null || defaultDb == null || subList == null ||
//				subList.size() <= 0 || TextUtils.isEmpty(localCurrentLang)){
//			return false;
//		}
//
//		/*去库里读取Label信息*/
//		List<String> pkgNameList = new ArrayList<String>();
//		for (ProcessModel subModel : subList) {
//			String pkgName = CloudTipsDAOHelper.getStringMd5(subModel.getPkgName());
//			if(!TextUtils.isEmpty(pkgName)){
//				pkgNameList.add(pkgName);
////				Log.e("test", "sub-->"+subModel.getPkgName());
//			}
//		}
//		ArrayMap<String, String> labels = mTipsDaoImpl.getDefaultLabelsFromData(defaultDb, pkgNameList, localCurrentLang);
//		pkgNameList.clear();
//
//		/*把库里读取到的Label信息再赋值给列表*/
//		for (ProcessModel model : subList) {
//			String pkgName = CloudTipsDAOHelper.getStringMd5(model.getPkgName());
//			if(!TextUtils.isEmpty(pkgName) && labels.containsKey(pkgName)){
//				String label = labels.get(pkgName);
//				if(!TextUtils.isEmpty(label)){
//					model.setTitle(label);
//					hasLabelChanged = true;
//				}
//			}
//		}
//		if(labels != null){
//			labels.clear();
//		}
//
//		return hasLabelChanged;
//	}
//
//	private void getTips(Collection<String> pkgNames,final GetTipsCallback callback,int type){
//		if (null == pkgNames || pkgNames.isEmpty()){
//			return;
//		}
//		Context appContext = SpaceApplication.getInstance().getContext();
//		String currentLang = getCurrentLanguage(appContext);
//		String localCurrentLang = getLocalCurrentLanguage(appContext);
//		SQLiteDatabase defaultDb = mTipsDaoImpl.getProcessTipsDb();
//		for (String pkgName : pkgNames) {
//			_getTips(defaultDb, currentLang, localCurrentLang, pkgName, callback, type);
//		}
//		if (defaultDb != null) {
//			defaultDb.close();
//		}
//	}
//	/**
//	 * 获取本地包名对应的Tips信息
//	 * @param pkgName
//	 * @return
//	 */
//	private void getTips(String pkgName, final GetTipsCallback callback, int type){
//		Context appContext = SpaceApplication.getInstance().getContext();
//		String currentLang = getCurrentLanguage(appContext);
//		String localCurrentLang = getLocalCurrentLanguage(appContext);
//		SQLiteDatabase defaultDb = mTipsDaoImpl.getProcessTipsDb();
//		_getTips(defaultDb, currentLang, localCurrentLang, pkgName, callback, type);
////		if (defaultDb != null) {
////			defaultDb.close();
////		}
//	}
	
//	private void _getTips(
//			SQLiteDatabase defaultDb,
//			String currentLang,
//			String localCurrentLang,
//			String pkgName,
//			final GetTipsCallback callback,int type){
//
//		String tips = null;
//		final String pkgNameMd5 = getStringMd5(pkgName);
//
//		/*---获取本地高频信息---*/
//		tips = mTipsDaoImpl.getDefaultTipsFromData(defaultDb, pkgNameMd5, localCurrentLang, type);
//		if(isTestMode){
//			NLog.e(TAG, "update_tips:____"+tips);
//		}
//
//		if(!TextUtils.isEmpty(tips)){
//			callback.result(pkgName, tips);
//			return;
//		}
//
//		/*如果高频库中没有该条app信息，则查询本地云端缓存库*/
//		tips = mTipsDaoImpl.getTipsFromDB(pkgNameMd5, currentLang, type);
//
//		if(isTestMode){
//			NLog.e(TAG, "local_tips:____"+tips+"-----"+(System.currentTimeMillis() - LOCAL_TIPS_UPDATE_TIME));
//		}
//
//		/*如果本地云端缓存库信息为无效信息，则需要重新向云端请求*/
//		if(TextUtils.isEmpty(tips)){
//			ArrayList<String> pkgList = new ArrayList<String>();
//			pkgList.add(pkgNameMd5);
//			/*如果更新数据库中没有该条信息，则从云端获取*/
//			getTipsFromServer(false, pkgList, currentLang,type);
//		}else if(tips.equals(ResponseType.NO_TIPS)){//如果获取到的信息为无效信息，则清除
//			tips = null;
//		}
//		callback.result(pkgName, tips);
//	}
	

	/**
	 * 从服务器获取tips信息
	 * @param needShow 是否需要回调到界面显示
	 * @param pkgList pkgNameMd5 包名
	 * @param language 语言
	 * @param type 类型（进程or应用）
	 */
	public synchronized void getTipsFromServer(final boolean needShow,final ArrayList<String> pkgList,final String language,final int type){
		mQueryThreadHandler.post(new Runnable() {
			@Override
			public void run() {
//				CloudTipsHttpRequest tipsRequest = new CloudTipsHttpRequest(pkgList);
//				String result = tipsRequest.postTimeOut(language, 1000);
				//FIXME BY zengtao.kuang@tcl.com 从服务器获取
				String result = null;
				ResponseResult getResult = parseResponseResult(result,pkgList.size());
				if(isTestMode){
					NLog.e(TAG, "获取的结果："+getResult);
				}
				if(getResult != null && getResult.isSuccess && getResult.hasTips){
					/*获取到信息后存入数据库*/
					
					if (mTipsDaoImpl != null)
						mTipsDaoImpl.insertOrUpdateCloudTipToDB(language,pkgList,getResult.tipsList);
				}
			}
		});
	}

//	/**
//	 * 检测当前的语言环境下是否已经得到本地化支持
//	 * @param context
//	 * @return
//	 */
//	public static String getCurrentLanguage(Context context) {
//		LanguageCountry language = ServiceConfigManager.getInstanse(context).getLanguageSelected(context);
//		String lang = language.getLanguage();
//		String country = language.getCountry();
//
//		if(!TextUtils.isEmpty(lang)){
//			lang = lang.toLowerCase(Locale.ENGLISH);
//		}
//		if(!TextUtils.isEmpty(country)){
//			lang = (lang == null ? "" : (lang+"-")) + country.toLowerCase(Locale.ENGLISH);
//		}
//
//		if(TextUtils.isEmpty(lang)){
//			lang = "en";
//		}
//		if(isTestMode)
//			NLog.i(TAG, "---CurrentLanguage____"+lang);
//		return lang;
//	}
//
//	/**
//	 * 获取本地库对应的语言
//	 * @return
//	 */
//	public static String getLocalCurrentLanguage(Context context){
//		LanguageCountry language = ServiceConfigManager.getInstanse(context).getLanguageSelected(context);
//		String lang = language.getLanguage();
//
//		if(!TextUtils.isEmpty(lang)){
//			lang = lang.toLowerCase(Locale.ENGLISH);
//		}
//
//		if(!TextUtils.isEmpty(lang) && "zh".equals(lang)) {
//			if(language.getCountry().equals("TW")) {
//				lang ="tw";
//			} else {
//				lang ="cn";
//			}
//		}
//
//		if(TextUtils.isEmpty(lang)){
//			lang = "en";
//		}
//		if(isTestMode)
//			NLog.i(TAG, "---LocalLanguage____"+lang);
//		return lang;
//	}

//	/**
//	 * 获取本机的android id值，不足32位末尾补0
//	 */
//	public String getUUID(){
//		if(!TextUtils.isEmpty(UUID)){
//			return UUID;
//		}
//		UUID = getAndroidID(SpaceApplication.getInstance().getContext());
//		int imeiLength = 0;
//		if (UUID != null)
//			imeiLength = UUID.length();
//
//		if(imeiLength <= 32){
//			StringBuilder builder = new StringBuilder();
//			if (UUID != null)
//				builder.append(UUID);
//			for (int i = 0; i < 32 - imeiLength; i++) {
//				builder.append('0');
//			}
//			UUID = builder.toString();
//		}else{
//			UUID = UUID.substring(0, 32);
//		}
//		return UUID;
//	}

//	public static String getAndroidID(Context context) {
//		try {
//			ContentResolver cr = context.getContentResolver();
//			return Settings.System.getString(cr, Settings.System.ANDROID_ID);
//		} catch (Exception e) {
//			return "";
//		}
//	}
	
//	public interface GetTipsCallback{
//		public void result(String pkgName, String tips);
//	}
//
//	/**
//	 * 获取字符的MD5值
//	 * @param plainText
//	 * @return
//	 */
//	public static String getStringMd5(String plainText) {
//		if(TextUtils.isEmpty(plainText)){
//			return null;
//		}
//		MessageDigest md = null;
//		try {
//			md = MessageDigest.getInstance("MD5");
//			md.update(plainText.getBytes());
//		} catch (Exception e) {
//			return null;
//		}
//		return encodeHex(md.digest());
//	}
//	private static String encodeHex(byte[] data) {
//		if (data == null) {
//			return null;
//		}
//		final String HEXES = "0123456789abcdef";
//		int len = data.length;
//		StringBuilder hex = new StringBuilder(len * 2);
//		for ( byte b : data ) {
//			hex.append(HEXES.charAt((b & 0xF0) >>> 4));
//			hex.append(HEXES.charAt((b & 0x0F)));
//		}
//		return hex.toString();
//	}

	/**
	 * 解析请求结果
	 * @param result 请求结果原始内容
	 * @param requestCount 请求条数
	 * @return
	 */
	private ResponseResult parseResponseResult(String result,int requestCount){
		if(TextUtils.isEmpty(result)){
			return null;
		}
		ResponseResult resInfo = new ResponseResult();
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(jsonObject.length() == 0){
				resInfo.isSuccess = true;
				resInfo.hasTips = false;
				if(isTestMode){
					NLog.e(TAG, "请求成功，无该app记录");
				}
			}else if(jsonObject.has("e")){
				resInfo.isSuccess = false;
				resInfo.errorMsg = jsonObject.getString("e");
				if(isTestMode){
					NLog.e(TAG, "请求失败："+resInfo.errorMsg);
				}
			}else if(jsonObject.has(ResponseType.RESULT)){
				resInfo.isSuccess = true;
				JSONArray resultArray = jsonObject.getJSONArray(ResponseType.RESULT);
				int resultLen = resultArray.length();

				if(resultLen == requestCount){//返回信息的条数与请求条数相同时才解析信息，否则信息容易对应错误
					resInfo.hasTips = true;
					ArrayList<CloudTipsModel> tipsList = new ArrayList<CloudTipsModel>();
					for (int i = 0; i < resultLen; i++) {
						String tips = resultArray.getString(i);
						if(!TextUtils.isEmpty(tips)){
							CloudTipsModel model = new CloudTipsModel();
							JSONObject arrayObject = new JSONObject(tips);	
							if(arrayObject.has(ResponseType.RESULT_PROCESS)){//解析进程tips信息
								String processTips = arrayObject.getString(ResponseType.RESULT_PROCESS);
								if(!TextUtils.isEmpty(processTips)){
									model.setProcessTips(processTips);
								}else{
									model.setProcessTips(ResponseType.NO_TIPS);
								}
							}
							if(arrayObject.has(ResponseType.RESULT_APK)){//解析应用管理Tips信息
								String apkTips = arrayObject.getString(ResponseType.RESULT_APK);
								if(!TextUtils.isEmpty(apkTips)){
									model.setApkTips(apkTips);
								}else{
									model.setApkTips(ResponseType.NO_TIPS);
								}
							}
							tipsList.add(model);
						}
						if(isTestMode){
							NLog.e(TAG, "请求成功："+tips);
						}
					}
					resInfo.tipsList = tipsList;
				}else{
					resInfo.hasTips = false;
					if(isTestMode){
						NLog.e(TAG, "请求成功，无该app记录");
					}
				}
			}else{
				resInfo.isSuccess = true;
				resInfo.hasTips = false;
				if(isTestMode){
					NLog.e(TAG, "请求成功，无该app记录");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resInfo;
	}

	/**
	 * 请求结果对象体
	 * @author
	 *
	 */
	private class ResponseResult{
		boolean isSuccess;
		boolean hasTips;
		String errorMsg;
		ArrayList<CloudTipsModel> tipsList;
	}

	public class ResponseType{
		static final String RESULT = "s";
		static final String RESULT_PROCESS = "p";
		static final String RESULT_APK = "a";
		
		public static final String NO_TIPS = "NO_TIPS";
	}

}
