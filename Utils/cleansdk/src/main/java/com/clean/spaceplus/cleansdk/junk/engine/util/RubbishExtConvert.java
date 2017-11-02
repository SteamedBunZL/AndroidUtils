package com.clean.spaceplus.cleansdk.junk.engine.util;


import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.junk.junkext.stub.JunkExtItem;

import java.io.File;

public class RubbishExtConvert {
	
	public static SDcardRubbishResult convert(final ScanTaskController ctrl, JunkExtItem item){
		SDcardRubbishResult sdcardResult = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER);
		
		if ( item.getType() == JunkExtItem.TYPE_CACHE_TEMP_DIR ){
			long fileCompute[] = new long[3];
			PathOperFunc.CalcSizeCallback calcCallback = new PathOperFunc.CalcSizeCallback(
					ctrl, 60L * 1000L, 32);
			PathOperFunc.computeFileSize(
					item.getPathDir(), fileCompute,
					calcCallback, null, null);
			
			sdcardResult.setFilesCount(fileCompute[2]);
			sdcardResult.setSize(fileCompute[0]);
			sdcardResult.setStrDirPath(item.getPathDir());
		}
		else if ( item.getType() == JunkExtItem.TYPE_CACHE_TEMP_FILE_LISTS ){
			sdcardResult.setFilesCount(item.getPathLists().size());
			File nowFile = null;
			long size = 0L;
			for (String sub : item.getPathLists()) {
				nowFile = new File(sub);
				size += nowFile.length();
				sdcardResult.addPathList(nowFile.getPath());
			}
			sdcardResult.setSize(size);
			
		}
		else{
			return null;
		}
		
		sdcardResult.setSignId(item.getSignId());
		sdcardResult.SetWhiteListKey(item.getSignId() + "");
		sdcardResult.setCheck(true);
		sdcardResult.setType(SDcardRubbishResult.RF_TEMPFILES);
		sdcardResult.setChineseName(item.getDesc());
		sdcardResult.setApkName(item.getDesc());
		sdcardResult.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
		if (!sdcardResult.isCheck()) {
			sdcardResult.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
			sdcardResult.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
		}
		
		return sdcardResult;
		
	}

}
