//=============================================================================
/**
 * @file KResidualDataEnDeCode.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud.core.residual.dir;

import android.text.TextUtils;

import com.hawkclean.framework.log.NLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.KResidualCloudQuery.DirQueryData;
import space.network.cleancloud.KResidualCloudQuery.DirQueryResult;
import space.network.cleancloud.core.base.BaseQueryDataEnDecode;
import space.network.cleancloud.core.residual.KResidualCommonData.DirQueryInnerData;
import space.network.commondata.KCleanCloudEnv;
import space.network.commondata.KQueryHeaderUtils;
import space.network.util.ConvertUtil;
import space.network.util.compress.EnDeCodeUtils;
import space.network.util.hash.KQueryMd5Util;
import space.network.util.net.KJsonUtils;


public class KDirQueryDataEnDeCode extends BaseQueryDataEnDecode {
	/*
	协议格式 http://beha.cloud.duba.net/adsn
	PostHead：请求的协议头信息，任何请求都需要有该信息
	PostBody：请求的内容，业务不同，请求内容不一样

	PostHead：type PostHead struct {
		dataSize  uint16
		crc      uint32
	    vt     uint8
		channelId  uint16
		version   uint32
	    languageCode  byte[6]
		uuid      byte[24]
	    mcc      uint16
	}

	PostBody：type PostBody struct {
	count byte
	dir1Md5 byte[16]
	dir2Md5 byte[16]
	......
	}

	参数说明
	dataSize：PostHead+PostBody总大小
	crc：用来校验数据的crc信息，crc(vt字节+channelId字节+xor(version字节+language字节+uuid字节+mcc字节+PostBody字节, channelKey)+channelKey)
	vt：version type, 接口的版本号, 决定具体接口的解析定义 (本接口定义为 1)
	version：客户端的版本号
	languageCode：6字符语言代码，如zh-CN、syr-SY，少于6字符末尾填充空格
	uuid：客户端的AndroidID, 预留24byte, 服务端解析时去除为0的补位
	mcc：移动国家码

	count：查询目录数
	dir2Md5：目录1的Md5
	dir2Md5：目录2的Md5
	从version开始，后面所有的内容传上来的都是异或过的内容

	返回值
	xor(result, ResponseKey)
	返回值异或后是json格式，如果客户端获取到不是json格式，则可以认为服务端有问题
	错误的返回：{‘e’: ‘错误描述’}
	正确的返回：{"s":[{"a":"Music","c":1,"n:"酷我音乐","p":"['cn.kuwo.player']","x":"[]","r":2, "i":1001,"u":0}, {"a":"Talk","c":1,"n:"QQ","p":"['qq.com']","x":"[]","r":1, "i":1002,"u":0}, {"r":1,"u":1}]}
	服务器异常：解析json会失败
	r：返回结果类型（1信息不存在、2包列表、3目录列表、4没有残留、5 要进行遍历查询的目录列表）
	c：clean_type 清理类型（1建议清理，2谨慎清理）
	n：name
	a：alert
	de：desc 可能没有
	p: packages包名列表，json列表格式字符串
	x: package_regex包名正则表达式列表
	d: dirs目录列表，json列表格式字符串
	s: subdirs子特征目录列表,json列表格式字符串,每个串的格式为"路径:特征id:清理类型(建议/慎重)"
	i: 特征ID
	f: 文件类型(0表示其他，1表示图片，2表示音频，3表示视频，4表示备份，5表示加密，6表示文档，7表示下载)
	m: 媒体类型(第一个比特位表示图片,第二位表示音频,第三位表示视频,1表示清理, 0表示不清理. 如: 011表示图片和音频可以清理，100表示只有视频可以清理)
	u: 是否需要进一步上报目录（0或者没有此字段表示不需上报，1表示需上报）
	g: 测试特征标志
	t: 目录清理的时间线
	s: 文件扩展名过滤信息的JSON原始字符串，如：{"f":"1|2","w":"ext1|ext2","b":"ext3|ext4"}
        f: 全局扩展名过滤类别id列表，竖线(|)分割
        w: 扩展名过滤白名单（不可删除），竖线(|)分割
        b: 扩展名过滤黑名单（可删除），竖线(|)分割


	备注
	服务端分配给不同产品不同的查询渠道信息，每个渠道号有不同的channelKey
	比如：
	channelId		1002
	channelKey	BcpjBhC*8kZ&=0Oo
	ResponseKey	%^ZHGrLSqV=ZLWv)

	*/

    public static final int DATA_BODY_ITEM_SIZE = 16;//md5
    private static final String TAG = KDirQueryDataEnDeCode.class.getSimpleName();

    public static byte[] getPostData(
            Collection<DirQueryData> datas,
            short channelId,
            int version,
            byte[] lang,
            byte[] xaid,
            short mcc,
            byte[] encodekey) {
        if (      null == datas || datas.isEmpty()
                || null == lang || lang.length < 6
                || null == encodekey || encodekey.length < 1)
            return null;

        int count =  datas.size();
        if (count > 255)
            return null;

        short datasize = (short)(KQueryHeaderUtils.QUERY_POST_DATA_HEAD_SIZE + 1 + DATA_BODY_ITEM_SIZE * count);
        byte[] data = new byte[datasize];
        KQueryHeaderUtils.fillQueryHeader(
                data,
                datasize,
                channelId,
                version,
                lang,
                xaid,
                mcc);
        data[KQueryHeaderUtils.QUERY_POST_DATA_HEAD_SIZE] = (byte)(count);
        int md5pos = KQueryHeaderUtils.QUERY_POST_DATA_HEAD_SIZE + 1;
        String dirNameMd5;
        for (DirQueryData item : datas) {
            dirNameMd5 = ((DirQueryInnerData)item.mInnerData).mDirNameMd5;
            EnDeCodeUtils.copyHexStringtoBytes(dirNameMd5, data, md5pos, 16);
            md5pos += 16;
        }
        KQueryHeaderUtils.encodeQueryHeader(data, datasize, encodekey);


        //在之前的加密基础上再做一层AES加密
        data = encrypt(KCleanCloudEnv.DEFAULT_CHANNEL_KEY_AES,data );
        return data;
    }

    static class DirQueryTempResult {
        public ArrayList<String> oriFilterSubDirs;
        public DirQueryResult queryResult;
        public String oriSuffixInfo;

        @Override
        public String toString() {
            return "DirQueryTempResult{" +
                    "oriFilterSubDirs=" + oriFilterSubDirs +
                    ", queryResult=" + queryResult +
                    ", oriSuffixInfo='" + oriSuffixInfo + '\'' +
                    '}';
        }
    }

    public static class DirBatchQuryResult {
        public int mErrorCode;       ///< 0成功， 其他失败
        public String mErrorMsg;     ///< 服务段返回的错误码，现在没用先不开放
        Collection<DirQueryTempResult> mResults;///< 结果列表
        private HashSet<Integer> mReportIds = new HashSet<>();

        @Override
        public String toString() {
            return "DirBatchQuryResult{" +
                    "mErrorCode=" + mErrorCode +
                    ", mErrorMsg='" + mErrorMsg + '\'' +
                    ", mResults=" + mResults +
                    ", mReportIds=" + mReportIds +
                    '}';
        }
    }


    public static String getResultString(byte[] respone, byte[] decodekey) {
        if (null == respone)
            return null;

        EnDeCodeUtils.xorEncodeBytes(
                respone,
                0,
                respone.length,
                decodekey);

        String strResult = null;

        try {
            strResult = new String(respone, "utf-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            strResult = null;
        }
        return strResult;
    }

    public static DirBatchQuryResult getResultDataFromJsonString(String strResult) {
        DirBatchQuryResult result = new DirBatchQuryResult();
        result.mErrorCode = -1;

        if (TextUtils.isEmpty(strResult))
            return result;

        JSONObject jsonObject = null;
        ArrayList<DirQueryTempResult> queryResults;

        try {
            jsonObject = new JSONObject(strResult);
            if (jsonObject.has("e")) {
                result.mErrorCode = -2;
                result.mErrorMsg = jsonObject.getString("e");
            } else if (jsonObject.has("s")) {
                JSONArray array = jsonObject.getJSONArray("s");
                int arraylen;
                if (array != null) {
                    arraylen = array.length();
                    queryResults = new ArrayList<>(arraylen);
                    for (int i = 0; i < arraylen; ++i) {

                        DirQueryTempResult tmpQueryResult = new DirQueryTempResult();
                        DirQueryResult queryResult = new DirQueryResult();
                        queryResult.mShowInfo = new KResidualCloudQuery.ShowInfo();

                        tmpQueryResult.queryResult = queryResult;

                        JSONObject jsonResult = array.getJSONObject(i);

                        if (jsonResult.has("r")) {
                            queryResult.mQueryResult = jsonResult.getInt("r");
                        }

                        if (jsonResult.has("c")) {
                            queryResult.mCleanType   = jsonResult.getInt("c");
                        }
                        if (jsonResult.has("m")) {
                            queryResult.mCleanMediaFlag   = jsonResult.getInt("m");
                        }
                        if (jsonResult.has("f")) {
                            queryResult.mContentType =  jsonResult.getInt("f");
                        }
                        if (jsonResult.has("i")) {
                            queryResult.mSignId      = jsonResult.getInt("i");
                        }
                        if (jsonResult.has("n")) {
                            queryResult.mShowInfo.mName = jsonResult.getString("n");
                        }
                        if (jsonResult.has("a")) {
                            queryResult.mShowInfo.mAlertInfo = jsonResult.getString("a");
                        }
                        if (jsonResult.has("de")) {
                            queryResult.mShowInfo.mName = jsonResult.getString("de");
                        }
                        if (jsonResult.has("p")) {
                            /**
                             * 将云端返回的Md5 16进制String，截断为64位的Long.
                             * */
                            queryResult.mPkgsMD5HexString = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("p"));
                            if(queryResult.mPkgsMD5HexString != null){
                                ArrayList<Long> pkgsArr = new ArrayList<>();
                                for(String md5hex : queryResult.mPkgsMD5HexString){
                                    byte[] md5 = EnDeCodeUtils.hexStringtoBytes(md5hex);
                                    long md5_half = KQueryMd5Util.getMD5High64BitFromMD5(md5);
                                    pkgsArr.add(md5_half);
                                }
                                queryResult.mPkgsMD5High64 = pkgsArr;
                            }
                        }
                        if (jsonResult.has("d")) {
                            queryResult.mDirs = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("d"));
                        }
                        if (jsonResult.has("s")) {
                            tmpQueryResult.oriFilterSubDirs = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("s"));
                        }

                        if (jsonResult.has("x")) {
                            queryResult.mPackageRegexs = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("x"));
                        }

                        queryResult.mTestFlag = jsonResult.optInt("g");

                        if (jsonResult.has("u")) {
                            boolean report = jsonResult.getInt("u") == 1;
                            if (report) {
                                result.mReportIds.add(i);
                            }
                        }

                        // 目录清理的时间线值
                        if (jsonResult.has("t")) {
                            queryResult.mCleanTime = jsonResult.getInt("t");
                        }

                        // 目录清理的过滤文件名后缀信息（json字符串）
                        if (jsonResult.has("sf")) {
                            String sf = jsonResult.getString("sf");
                            queryResult.mFileCheckerData
                                    = KResidualCloudQuery.FileCheckerData.parseFromJsonString(sf);
                            tmpQueryResult.oriSuffixInfo = sf;
                        }

                        queryResults.add(tmpQueryResult);
                    }
                    result.mResults = queryResults;
                    result.mErrorCode = 0;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return result;
        }
        return result;
    }
    public static DirBatchQuryResult getResultDataFromJsonString_v2(String strResult) {
        DirBatchQuryResult result = new DirBatchQuryResult();
        result.mErrorCode = -1;

        if (TextUtils.isEmpty(strResult))
            return result;

        JSONObject jsonObject = null;
        ArrayList<DirQueryTempResult> queryResults;

        try {
            JsonResult tempjsonResult = checkResult(strResult);
            result.mErrorCode = ConvertUtil.toInt(tempjsonResult.code, -1);
            result.mErrorMsg = tempjsonResult.msg;
            if (tempjsonResult != null && result.mErrorCode == 0){
                jsonObject = tempjsonResult.data;
                if (jsonObject.has("e")) {
                    result.mErrorCode = -2;
                    result.mErrorMsg = jsonObject.getString("e");
                } else if (jsonObject.has("s")) {
                    JSONArray array = jsonObject.getJSONArray("s");
                    int arraylen;
                    if (array != null) {
                        arraylen = array.length();
                        queryResults = new ArrayList<>(arraylen);
                        for (int i = 0; i < arraylen; ++i) {

                            DirQueryTempResult tmpQueryResult = new DirQueryTempResult();
                            DirQueryResult queryResult = new DirQueryResult();
                            queryResult.mShowInfo = new KResidualCloudQuery.ShowInfo();

                            tmpQueryResult.queryResult = queryResult;

                            JSONObject jsonResult = array.getJSONObject(i);

                            if (jsonResult.has("r")) {
                                queryResult.mQueryResult = jsonResult.getInt("r");
                            }

                            if (jsonResult.has("c")) {
                                queryResult.mCleanType   = jsonResult.getInt("c");
                            }
                            if (jsonResult.has("m")) {
                                queryResult.mCleanMediaFlag   = jsonResult.getInt("m");
                            }
                            if (jsonResult.has("f")) {
                                queryResult.mContentType =  jsonResult.getInt("f");
                            }
                            if (jsonResult.has("i")) {
                                queryResult.mSignId      = jsonResult.getInt("i");
                            }
                            if (jsonResult.has("n")) {
                                queryResult.mShowInfo.mName = jsonResult.getString("n");
                            }
                            if (jsonResult.has("a")) {
                                queryResult.mShowInfo.mAlertInfo = jsonResult.getString("a");
                            }
                            if (jsonResult.has("de")) {
                                queryResult.mShowInfo.mName = jsonResult.getString("de");
                            }
                            if (jsonResult.has("p")) {
                                /**
                                 * 将云端返回的Md5 16进制String，截断为64位的Long.
                                 * */
                                queryResult.mPkgsMD5HexString = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("p"));
                                if(queryResult.mPkgsMD5HexString != null){
                                    ArrayList<Long> pkgsArr = new ArrayList<>();
                                    for(String md5hex : queryResult.mPkgsMD5HexString){
                                        byte[] md5 = EnDeCodeUtils.hexStringtoBytes(md5hex);
                                        long md5_half = KQueryMd5Util.getMD5High64BitFromMD5(md5);
                                        pkgsArr.add(md5_half);
                                    }
                                    queryResult.mPkgsMD5High64 = pkgsArr;
                                }
                            }
                            if (jsonResult.has("d")) {
                                queryResult.mDirs = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("d"));
                            }
                            if (jsonResult.has("s")) {
                                tmpQueryResult.oriFilterSubDirs = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("s"));
                            }

                            if (jsonResult.has("x")) {
                                queryResult.mPackageRegexs = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("x"));
                            }

                            queryResult.mTestFlag = jsonResult.optInt("g");

                            if (jsonResult.has("u")) {
                                boolean report = jsonResult.getInt("u") == 1;
                                if (report) {
                                    result.mReportIds.add(i);
                                }
                            }

                            // 目录清理的时间线值
                            if (jsonResult.has("t")) {
                                queryResult.mCleanTime = jsonResult.getInt("t");
                            }

                            // 目录清理的过滤文件名后缀信息（json字符串）
                            if (jsonResult.has("sf")) {
                                String sf = jsonResult.getString("sf");
                                queryResult.mFileCheckerData
                                        = KResidualCloudQuery.FileCheckerData.parseFromJsonString(sf);
                                tmpQueryResult.oriSuffixInfo = sf;
                            }

                            queryResults.add(tmpQueryResult);
                        }
                        result.mResults = queryResults;
                        result.mErrorCode = 0;
                    }
            }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static KResidualCloudQuery.FilterDirData getFilterSubDirDataFromString(String str) {
        if (TextUtils.isEmpty(str))
            return null;

        String[] parseResult = str.split(":");
        if (null == parseResult || 0 == parseResult.length)
            return null;

        KResidualCloudQuery.FilterDirData result = new KResidualCloudQuery.FilterDirData();
        result.mPath = parseResult[0];

        if (parseResult.length > 1) {
            result.mSingId = Integer.valueOf(parseResult[1]);
        }
        if (parseResult.length > 2) {
            result.mCleanType = Integer.valueOf(parseResult[2]);
        }
        return result;
    }

    public static  ArrayList<KResidualCloudQuery.FilterDirData> getFilterSubDirDatasFromStrings(ArrayList<String> oriFilterSubDirs) {
        if (null == oriFilterSubDirs || oriFilterSubDirs.isEmpty())
            return null;

        ArrayList<KResidualCloudQuery.FilterDirData> result =
                new ArrayList<>(oriFilterSubDirs.size());
        for (String str : oriFilterSubDirs) {
            KResidualCloudQuery.FilterDirData data = getFilterSubDirDataFromString(str);
            if (data != null) {
                result.add(data);
            }
        }
        return result;
    }

    public static boolean setResultToQueryData(DirBatchQuryResult result, Collection<DirQueryData> datas) {
        if (null == datas)
            return false;

        Iterator<DirQueryTempResult> iter = null;
        if (result.mResults != null) {
            iter = result.mResults.iterator();
        }
        final LinkedList<DirData> reportData = new LinkedList<>();
        int i = 0;
        for (DirQueryData data : datas) {
            if (null == result) {
                data.mErrorCode = -100;
            }else if (null == result.mResults){
                if (result.mErrorCode != 0) {
                    data.mErrorCode = result.mErrorCode;
                }
                else {
                    data.mErrorCode = -101;
                }
            } else if (iter != null && iter.hasNext()) {
                DirQueryTempResult tmpQueryResult = iter.next();
                data.mErrorCode = 0;
                data.mResult    = tmpQueryResult.queryResult;
                data.mResultExpired = false;
                data.mResultSource  = KResidualCloudQuery.ResultSourceType.CLOUD;

                ((DirQueryInnerData)data.mInnerData).mOriFilterSubDirs
                        = tmpQueryResult.oriFilterSubDirs;

                ((DirQueryInnerData)data.mInnerData).mFilterSubDirDatas
                        = getFilterSubDirDatasFromStrings(tmpQueryResult.oriFilterSubDirs);

                ((DirQueryInnerData)data.mInnerData).mSuffixInfo = tmpQueryResult.oriSuffixInfo;

                if (result.mReportIds.contains(i)) {
                    reportData.add(new DirData(data.mDirName, ((DirQueryInnerData)data.mInnerData).mDirNameMd5));
                }
            } else {
                data.mErrorCode = -110;
            }
            i++;
        }
//		reportData.add(new DirData("tencent/micromsg", "ee3f402d8a6023502b2cf6325badfd1f"));
//		reportData.add(new DirData("android/data/com.tencent.news", "ace829368620a386a6cf92d2abf3c87f"));
//		reportData.add(new DirData("tencent/assistant/cache", "ff6e7fdf3c54c710584c9f18a2cc6b7a"));
//		reportData.add(new DirData("android/data/com.qzone", "bde1d1d723f58385bb272e3e21c5fca6"));
        if (!reportData.isEmpty()) {
			/*KSimpleGlobalTask.getInstance().post(new Runnable() {

				@Override
				public void run() {
					new KResidualUnknownReport(AppGlobalData.getApplicationContext(), KCleanCloudFactroy.getCleanCloudGlue()).report(reportData);
				}
			});*/
        }
        return true;
    }

    static public boolean decodeAndsetResultToQueryData(byte[] respone, byte[] decodekey, Collection<DirQueryData> datas) {
        //服务端返回的数据新作了一次AES加密 需要先做AES界面 再解析byte数组
        respone = decrypt(KCleanCloudEnv.DEFAULT_RESPONSE_KEY_AES, respone);
        if (respone == null || respone.length == 0){
            NLog.d(TAG, "AES解密数组为空 ");
            return false;
        }
        String resultString = getResultString(respone, decodekey);
        NLog.d(TAG, "KDirQueryDataEnDeCode resultString = "+resultString);
        DirBatchQuryResult result = getResultDataFromJsonString_v2(resultString);
        //DirBatchQuryResult result = getResultDataFromJsonString(resultString);
        return setResultToQueryData(result, datas);
    }


    public static final class DirData {
        String path;
        String pathMD5;
        public DirData(String path, String pathMD5) {
            super();
            this.path = path;
            this.pathMD5 = pathMD5;
        }
    }
}