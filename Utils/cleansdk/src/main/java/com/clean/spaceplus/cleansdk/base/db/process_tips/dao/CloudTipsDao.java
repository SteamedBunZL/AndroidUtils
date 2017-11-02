package com.clean.spaceplus.cleansdk.base.db.process_tips.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.process_tips.CloudTipTable;
import com.clean.spaceplus.cleansdk.base.db.process_tips.CloudTipsModel;
import com.clean.spaceplus.cleansdk.base.db.process_tips.ProcessTipProvider;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.ArrayList;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/6/27 14:56
 * @copyright TCL-MIG
 */
public class CloudTipsDao {

	public static final String TAG = CloudTipsDao.class.getSimpleName();
//	private final int ORIGINAL_VERSION = 1;//数据库的初始版本，不可变更
//	private final int CURRENT_VERSION = 2;//当前数据库的版本
//
//	public static final long VALIDITY_TIME = 10*24*60*60*1000;//目前设置有效期为7天
////	private final long VALIDITY_TIME_EMPTY = 7*24*60*60*1000;//空字符目前设置有效期为2天

	private ProcessTipProvider mProvider;
	CloudTipsDao() {
		mProvider = ProcessTipProvider.getInstance(SpaceApplication.getInstance().getContext());
	}

//	/**
//	 * 获取云端文案在本地的数据库记录信息
//	 * @param pkgNameMd5 包名（MD5）
//	 * @param lang 语言
//	 * @param type 类型
//	 * @return
//	 */
//	public synchronized String getTipsFromDB(String pkgNameMd5,String lang,int type){
//		Cursor cursor = null;
//		try{
//			if(type == CloudTipsDAOHelper.PROCESS_TIPS_TYPE){//进程描述
//				String sql = String.format("select %s,%s from %s where %s = ? AND %s = ?",
//						CloudTipTable.PROCESS_TIPS,
//						CloudTipTable.UPDATE_TIME,
//						CloudTipTable.TABLE_NAME,
//						CloudTipTable.PACKAGE_NAME,
//						CloudTipTable.LANUGAGE);
//				cursor = mProvider.rawQuery(sql, new String[]{pkgNameMd5,lang});
//			}else if(type == CloudTipsDAOHelper.APK_TIPS_TYPE){//应用描述
//				String sql = String.format("select %s,%s from %s where %s = ? AND %s = ?",
//						CloudTipTable.APK_TIPS,
//						CloudTipTable.UPDATE_TIME,
//						CloudTipTable.TABLE_NAME,
//						CloudTipTable.PACKAGE_NAME,
//						CloudTipTable.LANUGAGE);
//				cursor = mProvider.rawQuery(sql, new String[]{pkgNameMd5,lang});
//			}
//			if(cursor != null  && cursor.moveToFirst()){
//				String tips = cursor.getString(0);
//				long updateTime = cursor.getLong(1);
//				if(!TextUtils.isEmpty(tips)){
//					if((tips.equals(CloudTipsDAOHelper.ResponseType.NO_TIPS) && ((System.currentTimeMillis() - updateTime) > VALIDITY_TIME_EMPTY))||
//							(!tips.equals(CloudTipsDAOHelper.ResponseType.NO_TIPS) && ((System.currentTimeMillis() - updateTime) > VALIDITY_TIME))){
//						///该条信息的有效期超时，则需要请求服务器，重新更新
//						ArrayList<String> pkgList = new ArrayList<String>();
//						pkgList.add(pkgNameMd5);
//						CloudTipsDAOHelper.getInstance().getTipsFromServer(false, pkgList, lang,type);
//					}
//				}
//				return tips;
//			}
//
//		}catch (Exception e) {
//			NLog.printStackTrace(e);
//		}finally{
//			if(cursor != null && !cursor.isClosed()){
//				cursor.close();
//				cursor = null;
//			}
//		}
//		return null;
//	}

	/**
	 * 判断该条信息是否存在于数据库中
	 * @param pkgNameMd5
	 * @param lang
	 * @return
	 */
	private synchronized boolean isTipsInDatabase(String pkgNameMd5,String lang){
		Cursor cursor = null;
		try{
			String sql = String.format("select %s from %s where %s = ? AND %s = ?",
					CloudTipTable.ID,
					CloudTipTable.TABLE_NAME,
					CloudTipTable.PACKAGE_NAME,
					CloudTipTable.LANUGAGE);
			cursor = mProvider.rawQuery(sql, new String[]{pkgNameMd5,lang});
			if(cursor != null  && cursor.getCount() > 0){
				return true;
			}

		}catch (Exception e) {
			NLog.printStackTrace(e);
		}finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
				cursor = null;
			}
		}
		return false;
	}

	/**
	 * 插入一条数据到提示库里面
	 * @param lang 语言
	 * @param pkgList 包名（MD5）
	 */
	public synchronized void insertOrUpdateCloudTipToDB(String lang, ArrayList<String> pkgList,ArrayList<CloudTipsModel> tipsList){

		if(tipsList == null || tipsList.size() <= 0 || pkgList == null || pkgList.size() != tipsList.size()){
			//如果需要的描述信息都是为空，则不存入信息
			return;
		}

		try {//处理bug 4077495018 磁盘空间不足，写入数据库异常问题
			//database.beginTransaction();
			try {
				int len = tipsList.size();
				for (int i = 0; i < len; i++) {
					CloudTipsModel model = tipsList.get(i);
					if(model == null || (TextUtils.isEmpty(model.getProcessTips()) && TextUtils.isEmpty(model.getApkTips()))){
						//如果需要的描述信息都是为空，则不存入信息
						return;
					}
					ContentValues values = new ContentValues();
					values.put(CloudTipTable.PACKAGE_NAME, pkgList.get(i));
					values.put(CloudTipTable.LANUGAGE, lang);
					//存入进程信息
					if(!TextUtils.isEmpty(model.getProcessTips())){
						values.put(CloudTipTable.PROCESS_TIPS, model.getProcessTips());
					}
					//存入应用管理信息
					if(!TextUtils.isEmpty(model.getApkTips())){
						values.put(CloudTipTable.APK_TIPS, model.getApkTips());
					}
					values.put(CloudTipTable.UPDATE_TIME, System.currentTimeMillis());
					if(isTipsInDatabase( pkgList.get(i), lang)){
						mProvider.update(CloudTipTable.TABLE_NAME, values, CloudTipTable.PACKAGE_NAME+" = ? AND " + CloudTipTable.LANUGAGE + " = ?", new String[]{ pkgList.get(i),lang});
					}else{
						mProvider.insert(CloudTipTable.TABLE_NAME, null, values);
					}
				}
				//	database.setTransactionSuccessful();
				if(CloudTipsDAOHelper.isTestMode)
					NLog.d(TAG, "-------数据插入成功-------"+CloudTipTable.PROCESS_TIPS+"\n"+lang);
			} catch (Exception e) {
				if(CloudTipsDAOHelper.isTestMode)
					NLog.d(TAG, "--1-----数据插入失败-------"+"\n"+e);
			} finally {
				// 结束事务
				//	database.endTransaction();
			}
		} catch (Exception e) {
			if(CloudTipsDAOHelper.isTestMode)
				NLog.d(TAG, "--2-----数据插入失败-------"+"\n"+e);
		}
	}

//	public SQLiteDatabase getProcessTipsDb() {
//		SQLiteDatabase db = null;
//		try{
//			db = mProvider.getDatabase();
//		}
//		catch (Exception e) {
//		}
//		return db;
//	}
//
//	public synchronized String getDefaultTipsFromData(SQLiteDatabase db, String pkgNameMd5,String lang,int type){
//		if (null == db)
//			return null;
//
//		String tips = null;
//		TipsData tipsData = getTipsDataFromLocalProcessTipsDB(db, pkgNameMd5, lang);
//		if (tipsData == null)
//			return tips;
//
//		if(type == CloudTipsDAOHelper.PROCESS_TIPS_TYPE){//进程描述
//			tips = tipsData.mProcTips;
//		} else if (type == CloudTipsDAOHelper.APK_TIPS_TYPE) {//apk描述
//			tips = tipsData.mAppTips;
//		}
//
//		if(CloudTipsDAOHelper.isTestMode){
//			NLog.e(TAG, "获取本地信息"+pkgNameMd5+"-----"+lang+"-----"+tips);
//		}
//		return tips;
//	}
	
//	private static class TipsData {
//		String mPkgNameMd5;
//		String mProcTips;
//		String mProcTipsId;
//		String mAppTips;
//		String mAppTipsId;
//	}
//
//	private static class LabelData {
//		String mPkgNameMd5;
//		String mProcLabel;
//		String mProcLabelId;
//	}
//
//	String[] parseLangTextIds(String str, String language, int targetCount) {
//		if (TextUtils.isEmpty(str))
//			return null;
//
//		int pos = str.indexOf(language);
//		if (pos == -1)
//			return null;
//
//		int pos2 = str.indexOf(':', pos);
//		if (pos2 == -1)
//			return null;
//
//		int start = pos2 + 1;
//		String[] result = new String[targetCount];
//		int len = str.length();
//		int cnt = 0;
//		char c;
//		StringBuilder sb = new StringBuilder(10);
//		boolean isStop = false;
//		for (int i = start;
//				i < len && cnt < targetCount && !isStop;
//				++i) {
//			c = str.charAt(i);
//			switch (c) {
//			case ',':
//			case '|':
//				result[cnt++] = sb.toString();
//				sb.delete(0, sb.length());
//				if (c == '|') {
//					isStop = true;
//				}
//				break;
//			default:
//				sb.append(c);
//			}
//		}
//		if (cnt < targetCount){
//			result[cnt++] = sb.toString();
//		}
//		return result;
//	}
	
//	String[] getLangTextFromIds(SQLiteDatabase db, String[] ids) {
//		if (null == ids || ids.length == 0)
//			return null;
//
//		StringBuilder builder = new StringBuilder();
//		builder.append("select _id,content from string_content where _id in(");
//		int appendCnt = 0;
//		for(int i = 0; i < ids.length; ++i) {
//			if (TextUtils.isEmpty(ids[i]))
//				continue;
//
//			if (appendCnt > 0) {
//				builder.append(',');
//			}
//			builder.append(ids[i]);
//			++appendCnt;
//		}
//
//		builder.append(')');
//		String sql = builder.toString();
//		String[] result = new  String[ids.length];
//		TreeMap<String, String> textResult = new TreeMap<String, String>();
//		Cursor cursor = null;
//		String strId;
//		String strContent;
//		try {
//			cursor = db.rawQuery(sql, null);
//			if (cursor != null && cursor.getCount() != 0) {
//				while (cursor.moveToNext()) {
//					strId = cursor.getString(0);
//					if (!cursor.isNull(1)) {
//						strContent = cursor.getString(1);
//						textResult.put(strId, strContent);
//					}
//				}
//			}
//			for (int i = 0; i < ids.length; ++i) {
//				result[i] = textResult.get(ids[i]);
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
//		return result;
//	}
	/*
	select local_process_tips.tips, 
	lower(hex(package_name_md5.package_name_md5))
	from package_name_md5 join local_process_tips 
	on package_name_md5.package_name_md5 =x'8A90F9E4803F7E031840FAD073DE338A' 
	and local_process_tips.package_name_md5 = package_name_md5._id
	*/
//	private TipsData getTipsDataFromLocalProcessTipsDB(SQLiteDatabase db, String pkgNameMd5, String lang) {
//		if (null == db || TextUtils.isEmpty(pkgNameMd5))
//			return null;
//
//		StringBuilder builder = new StringBuilder();
//		builder.append("select local_process_tips.tips from package_name_md5 join local_process_tips on package_name_md5.package_name_md5=x'");
//		builder.append(pkgNameMd5);
//		builder.append("' and local_process_tips.package_name_md5=package_name_md5._id");
//		String sql = builder.toString();
//		String content = null;
//		Cursor cursor = null;
//		String[] langTextIds = null;
//		String[] langTexts = null;
//		try {
//			cursor = db.rawQuery(sql, null);
//			if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
//				content = cursor.getString(0);
//				langTextIds = parseLangTextIds(content, lang, 2);
//				langTexts = getLangTextFromIds(db, langTextIds);
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
//
//		TipsData result = null;
//		if (langTexts != null) {
//			result = new TipsData();
//			result.mPkgNameMd5 = pkgNameMd5;
//			result.mProcTips   = langTexts[0];
//			result.mProcTipsId = langTextIds[0];
//			result.mAppTips    = langTexts[1];
//			result.mAppTipsId  = langTextIds[1];
//		}
//		return result;
//	}
	/*
	select local_label.proc_label, 
	lower(hex(package_name_md5.package_name_md5))
	from package_name_md5 join local_label 
	on package_name_md5.package_name_md5 =x'EBE6C8D1940F0B32C6E48F556DFAE5FA' 
	and local_label.package_name_md5 = package_name_md5._id
	*/
/*	private LabelData getLabelDataFromLocalProcessTipsDB(SQLiteDatabase db, String pkgNameMd5, String lang) {
		if (null == db)
			return null;
	
		StringBuilder builder = new StringBuilder();
		builder.append("select local_label.proc_label from package_name_md5 join local_label on package_name_md5.package_name_md5=x'");
		builder.append(pkgNameMd5);
		builder.append("' and local_label.package_name_md5=package_name_md5._id");
		String sql = builder.toString();
		String content = null;
		Cursor cursor = null;
		String[] langTextIds = null;
		String[] langTexts = null;
		try {
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
				content = cursor.getString(0);
				langTextIds = parseLangTextIds(content, lang, 1);
				langTexts = getLangTextFromIds(db, langTextIds);
			}		
		} catch(SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		LabelData result = null;
		if (langTexts != null) {
			result = new LabelData();
			result.mPkgNameMd5 = pkgNameMd5;
			result.mProcLabel  = langTexts[0];
			result.mProcLabelId= langTextIds[1];
		}
		return result;
	}*/
	
//	private LabelData[] getLabelDataFromLocalProcessTipsDB(SQLiteDatabase db, String[] pkgNameMd5s, String lang) {
//		if (null == db || null == pkgNameMd5s || 0 == pkgNameMd5s.length)
//			return null;
//
//		///////////////////////////////////////////////////////////////////////
//		StringBuilder builder = new StringBuilder();
//		String tmp = "select local_label.proc_label, lower(hex(package_name_md5.package_name_md5)) "
//				+ "from package_name_md5 join local_label on package_name_md5.package_name_md5 in (";
//		builder.append(tmp);
//		int appendCnt = 0;
//		for (int i = 0; i < pkgNameMd5s.length; ++i) {
//			if (TextUtils.isEmpty(pkgNameMd5s[i]))
//				continue;
//
//			if (appendCnt > 0) {
//				builder.append(',');
//			}
//			builder.append("x'");
//			builder.append(pkgNameMd5s[i]);
//			builder.append("'");
//			++appendCnt;
//		}
//
//		builder.append(") and local_label.package_name_md5=package_name_md5._id");
//		String sql = builder.toString();
//		/////////////////////////////////////////////////////////////////////
//		TreeSet<String> textIds = new TreeSet<String>();
//		TreeMap<String, String> idToTextResult = new TreeMap<String, String>();
//		TreeMap<String, LabelData> queryResult = new TreeMap<String, LabelData>();
//		ArrayList<LabelData> resultDatas = new ArrayList<LabelData>();
//		LabelData[] result = null;
//		String content = null;
//		String packageMd5 = null;
//		Cursor cursor = null;
//		String[] langTextIds = null;
//		try {
//			cursor = db.rawQuery(sql, null);
//			if (cursor != null && cursor.getCount() != 0) {
//				while (cursor.moveToNext()) {
//					content = cursor.getString(0);
//					packageMd5 = cursor.getString(1);
//					langTextIds = parseLangTextIds(content, lang, 1);
//					if (langTextIds != null) {
//						LabelData data = new LabelData();
//						data.mPkgNameMd5 = packageMd5;
//						data.mProcLabelId = langTextIds[0];
//						textIds.add(langTextIds[0]);
//						queryResult.put(packageMd5, data);
//					}
//				}
//			}
//			String[] ids = null;
//			String[] texts = null;
//			if (!textIds.isEmpty()) {
//				ids = new String[textIds.size()];
//				textIds.toArray(ids);
//				texts = getLangTextFromIds(db, ids);
//				if (texts != null) {
//					for (int i = 0; i < texts.length; ++i) {
//						idToTextResult.put(ids[i], texts[i]);
//					}
//					for (int i = 0; i < pkgNameMd5s.length; ++i) {
//						LabelData data = queryResult.get(pkgNameMd5s[i]);
//						if (null == data) {
//							continue;
//						}
//						data.mProcLabel = idToTextResult.get(data.mProcLabelId);
//						resultDatas.add(data);
//					}
//				}
//			}
//			if (!resultDatas.isEmpty()) {
//				result = new LabelData[resultDatas.size()];
//				resultDatas.toArray(result);
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
//		return result;
//	}
	
	
//	/**
//	 * 获取本地默认的Label列表信息
//	 * @param db
//	 * @param pkgNameMd5 包名的MD5值
//	 * @param lang 语言
//	 * @return
//	 */
//	private final static int MAX_BATCH_QUERY_CNT = 64;
//	public synchronized ArrayMap<String, String> getDefaultLabelsFromData(SQLiteDatabase db, List<String> pkgList, String lang){
//		if (null == db || pkgList == null || pkgList.size() <= 0)
//			return null;
//
//		ArrayMap<String, String> labelMaps = new ArrayMap<String, String>();
//		int i = 0;
//		ArrayList<String> pkgs = new ArrayList<String>(MAX_BATCH_QUERY_CNT);
//		for (String pkg : pkgList) {
//			pkgs.add(pkg);
//			++i;
//			if (i >= MAX_BATCH_QUERY_CNT) {
//				fillProcLabelQueryResult(db, pkgs, lang, labelMaps);
//				i = 0;
//				pkgs.clear();
//			}
//		}
//		if (!pkgs.isEmpty()) {
//			fillProcLabelQueryResult(db, pkgs, lang, labelMaps);
//			pkgs.clear();
//		}
//		return labelMaps;
//	}
	
//	private void fillProcLabelQueryResult(SQLiteDatabase db, ArrayList<String> pkgs, String lang, ArrayMap<String, String> result) {
//		if (null == pkgs || pkgs.isEmpty())
//			return;
//
//		String[] pkgMd5s = null;
//		LabelData[] datas = null;
//		pkgMd5s = new String[pkgs.size()];
//		pkgs.toArray(pkgMd5s);
//		datas = getLabelDataFromLocalProcessTipsDB(db, pkgMd5s, lang);
//		if (datas != null) {
//			for (LabelData data : datas) {
//				if (data == null)
//					continue;
//
//				if (TextUtils.isEmpty(data.mPkgNameMd5) || TextUtils.isEmpty(data.mProcLabel)) {
//					continue;
//				}
//				result.put(data.mPkgNameMd5, data.mProcLabel);
//				 if(CloudTipsDAOHelper.isTestMode){
//					 NLog.e(TAG, "获取本地Label信息:"+data.mPkgNameMd5+"-----"+lang+"-----"+data.mProcLabel);
//				 }
//			}
//		}
//	}
}
