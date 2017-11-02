//=============================================================================
/**
 * @file KPreInstallPkgQueryDataEnDeCode.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud.core.cache;

import android.text.TextUtils;

import com.hawkclean.framework.log.NLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import space.network.cleancloud.KCacheCloudQuery.PkgQueryData;
import space.network.cleancloud.KCacheCloudQuery.PkgQueryPathItem;
import space.network.cleancloud.KCacheCloudQuery.PkgQueryResult;
import space.network.cleancloud.KCacheCloudQuery.PkgResultType;
import space.network.cleancloud.KCacheCloudQuery.ResultSourceType;
import space.network.cleancloud.core.base.BaseQueryDataEnDecode;
import space.network.commondata.KCleanCloudEnv;
import space.network.commondata.KQueryHeaderUtils;
import space.network.util.ConvertUtil;
import space.network.util.compress.EnDeCodeUtils;

/** 缓存查询新协议
 协议格式 http://beha.cloud.duba.net/cpsn
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
 packageMd51 byte[16]
 packageMd52 byte[16]
 ......
 }

 参数说明
 dataSize：PostHead+PostBody总大小
 crc：用来校验数据的crc信息，crc(channelId字节+xor(version字节+language字节+uuid字节+PostBody字节,channelKey)+channelKey)
 version：客户端的版本号
 uuid：客户端的32位字符串uuid值转换成16位
 languageCode：6字符语言代码，如zh-CN、syr-SY，少于6字符末尾填充空格
 count：查询目录数
 packageMd51：包1的Md5
 packageMd52：包2的Md5
 从version开始，后面所有的内容传上来的都是异或过的内容

 返回值
 xor(result, ResponseKey)
 返回值异或后是json格式，如果客户端获取到不是json格式，则可以认为服务端有问题
 错误的返回：{‘e’: ‘错误描述’}
 正确的返回：
 {"s":[
 {"i":123,"g":0,"l":[
 {“i”:666, “t”:1, “o”: 1, “c”:7, “f”:8, “n”:”视频目录”, “k”:1, “m”:0,"a":1,"g":0, p:\”aaaaaaaaaaaaaaaaaaa+bbbbbbbbbbbbbbbbb+abc/aaa\”, \”aaaaaaaaaaaaaaaaaaa+bbbbbbbbbbbbbbbbb+abc/bbb\”}
 ]},
 {"i":124,"l":[
 {“i”:668, “t”:1, “o”: 1, “c”:7, “f”:8, “n”:”视频文件”, “k”:0,“m”:7,"a":3,"g":1, p:\”aaaaaaaaaaaaaaaaaaa+bbbbbbbbbbbbbbbbb+abc/aaa\”, \”aaaaaaaaaaaaaaaaaaa+bbbbbbbbbbbbbbbbb+abc/bbb\”}
 ]}
 ]}
 服务器异常：解析json会失败

 备注
 服务端分配给不同产品不同的查询渠道信息，每个渠道号有不同的channelKey
 比如：
 channelId		1002
 channelKey	BcpjBhC*8kZ&=0Oo
 ResponseKey	%^ZHGrLSqV=ZLWv

 i: 包ID（查不到返回0）
 g: 清除系统缓存标识
 l: 特征列表，其数据为一个json数组，其每个信息都会包含下面几个属性：
 i: 目录ID
 t: cleanType（清理类型 - 建议、深度）
 o: cleanOperation（1-清理目录，2-不清理目录，只清理目录下文件）
 c: cleanTime（清理多久前的）
 f: fileType（文件类型）
 p: path（路径或正则）
 v: 隐私类型（0表示不是隐私类型，其它值表示对应的隐私类型）
 k: 是否需要用户选择，0表示不需要，1表示需要
 m: 媒体类型(第一个比特位表示图片,第二位表示音频,第三位表示视频,1表示清理, 0表示不清理. 如: 011表示图片和音频可以清理，100表示只有视频可以清理)
 g: 测试特征标记
 a: 特征类别 1：目录；2：目录正则；3：文件；4：文件正则；5：root目录；6：root目录正则
 */
public class KCachePkgQueryDataEnDeCode extends BaseQueryDataEnDecode{
    public static final int DATA_BODY_ITEM_SIZE = 16;// md5
    private static final String TAG = KCachePkgQueryDataEnDeCode.class.getSimpleName();

    public static byte[] getPostData(
            Collection<PkgQueryData> datas,
            short channelId,
            int version,
            byte[] lang,
            byte[] xaid,
            short mcc,
            byte[] encodekey) {
        if (null == datas
                || datas.isEmpty()
                || null == lang
                || lang.length < 6
                || null == encodekey
                || encodekey.length < 1)
            return null;

        int count = datas.size();
        if (count > 255)
            return null;

        short datasize = (short) (KQueryHeaderUtils.QUERY_POST_DATA_HEAD_SIZE + 1 + DATA_BODY_ITEM_SIZE * count);
        byte[] data = new byte[datasize];
        KQueryHeaderUtils.fillQueryHeader(
                data,
                datasize,
                channelId,
                version,
                lang,
                xaid,
                mcc);

        data[KQueryHeaderUtils.QUERY_POST_DATA_HEAD_SIZE] = (byte) (count);
        int md5pos = KQueryHeaderUtils.QUERY_POST_DATA_HEAD_SIZE + 1;
        String dirNameMd5;
        for (PkgQueryData item : datas) {
            dirNameMd5 = ((KCacheCommonData.CachePkgQueryInnerData) item.mInnerData).mPkgNameMd5;
            EnDeCodeUtils.copyHexStringtoBytes(dirNameMd5, data, md5pos, 16);
            md5pos += 16;
        }
        KQueryHeaderUtils.encodeQueryHeader(data, datasize, encodekey);
        for (int i =0; i < data.length; i ++){
            //NLog.d(TAG, "send posta [ " +i +" ]  = " + data[i]);
        }
        //在之前的加密基础上再做一层AES加密
        data = encrypt(KCleanCloudEnv.DEFAULT_CHANNEL_KEY_AES,data );
        return data;
    }

    public static class CachePkgBatchQuryResult {
        public int mErrorCode; // /< 0成功， 其他失败
        public String mErrorMsg; // /< 服务段返回的错误码，现在没用先不开放
        Collection<CachePkgTempResult> mResults;// /< 结果列表


        @Override
        public String toString() {
            return "CachePkgBatchQuryResult{" +
                    "mErrorCode=" + mErrorCode +
                    ", mErrorMsg='" + mErrorMsg + '\'' +
                    ", mResults=" + mResults +
                    '}';
        }
    }
    static class CachePkgTempResult{
        public KCacheCommonData.CachePkgQueryInnerData innerData;
        public PkgQueryResult queryResult;

        @Override
        public String toString() {
            return "CachePkgTempResult{" +
                    "innerData=" + innerData +
                    ", queryResult=" + queryResult +
                    '}';
        }
    }


    public static CachePkgBatchQuryResult getResultDataFromJsonString(String strResult) {
        CachePkgBatchQuryResult result = new CachePkgBatchQuryResult();
        result.mErrorCode = -1;

        if (TextUtils.isEmpty(strResult))
            return result;

        JSONObject jsonObject = null;
        ArrayList<CachePkgTempResult> queryResults;

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
                        CachePkgTempResult tempResult = new CachePkgTempResult();
                        tempResult.queryResult = new PkgQueryResult();
                        tempResult.innerData = new KCacheCommonData.CachePkgQueryInnerData();
                        JSONObject jsonResult = array.getJSONObject(i);

                        if (jsonResult.has("i")) {
                            tempResult.queryResult.mPkgId = jsonResult.getInt("i");
                            if (tempResult.queryResult.mPkgId == 0) {
                                tempResult.queryResult.mQueryResult = PkgResultType.NOT_FOUND;
                                queryResults.add(tempResult);
                                continue;
                            }
                        }
                        if (jsonResult.has("g")) {
                            tempResult.queryResult.mSysFlag = jsonResult.getInt("g");
                        }
                        int count = 0;
                        if (jsonResult.has("l")) {
                            count += getResultPathData(tempResult.innerData, jsonResult);
                        }
                        tempResult.queryResult.mQueryResult = count > 0 ? PkgResultType.DIR_LIST : PkgResultType.NOT_FOUND;
                        queryResults.add(tempResult);
                    }
                    result.mResults = queryResults;
                    result.mErrorCode = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }


    public static CachePkgBatchQuryResult getResultDataFromJsonString_v2(String strResult) {
        CachePkgBatchQuryResult result = new CachePkgBatchQuryResult();
        result.mErrorCode = -1;

        if (TextUtils.isEmpty(strResult))
            return result;
        try {
            JsonResult tempjsonResult = checkResult(strResult);
            result.mErrorCode = ConvertUtil.toInt(tempjsonResult.code, -1);
            result.mErrorMsg = tempjsonResult.msg;
            ArrayList<CachePkgTempResult> queryResults;
            if (tempjsonResult != null && result.mErrorCode == 0){
                JSONObject jsonObject = tempjsonResult.data;

                if (jsonObject != null && jsonObject.has("s")){
                    JSONArray array = jsonObject.getJSONArray("s");
                    int arraylen;
                    if (array != null) {
                        arraylen = array.length();
                        queryResults = new ArrayList<>(arraylen);
                        for (int i = 0; i < arraylen; ++i) {
                            CachePkgTempResult tempResult = new CachePkgTempResult();
                            tempResult.queryResult = new PkgQueryResult();
                            tempResult.innerData = new KCacheCommonData.CachePkgQueryInnerData();
                            JSONObject jsonResult = array.getJSONObject(i);

                            if (jsonResult.has("i")) {
                                tempResult.queryResult.mPkgId = jsonResult.getInt("i");
                                if (tempResult.queryResult.mPkgId == 0) {
                                    tempResult.queryResult.mQueryResult = PkgResultType.NOT_FOUND;
                                    queryResults.add(tempResult);
                                    continue;
                                }
                            }
                            if (jsonResult.has("g")) {
                                tempResult.queryResult.mSysFlag = jsonResult.getInt("g");
                            }
                            int count = 0;
                            if (jsonResult.has("l")) {
                                count += getResultPathData(tempResult.innerData, jsonResult);
                            }
                            tempResult.queryResult.mQueryResult = count > 0 ? PkgResultType.DIR_LIST : PkgResultType.NOT_FOUND;
                            queryResults.add(tempResult);
                        }
                        result.mResults = queryResults;
                        result.mErrorCode = 0;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return result;
        }
        return result;
    }




    private static int getResultPathData(
            KCacheCommonData.CachePkgQueryInnerData innerData,
            JSONObject jsonResult) throws JSONException {
        JSONArray dirs = jsonResult.getJSONArray("l");
        if (dirs != null && dirs.length()!=0) {
            if (innerData.mPkgQueryPathItems != null
                    && !innerData.mPkgQueryPathItems.isEmpty()) {
                innerData.mPkgQueryPathItems.clear();
            } else {
                innerData.mPkgQueryPathItems = new ArrayList<>(dirs.length());
            }
            int dirLen = dirs.length();
            for (int i=0;i<dirLen; i++) {
                PkgQueryPathItem pkgQueryPathItem = new PkgQueryPathItem();
                try {
                    JSONObject o = dirs.optJSONObject(i);
                    pkgQueryPathItem.mPrivacyType = o.optInt("v");
                    pkgQueryPathItem.mCleanOperation = o.getInt("o");
                    pkgQueryPathItem.mCleanTime = o.optInt("c");
                    pkgQueryPathItem.mCleanType = o.getInt("t");
                    pkgQueryPathItem.mPathString = o.getString("p");
                    pkgQueryPathItem.mSignId = o.getString("i");
                    pkgQueryPathItem.mContentType = o.optInt("f");
                    pkgQueryPathItem.mCleanMediaFlag = o.optInt("m");
                    pkgQueryPathItem.mNeedCheck = o.optInt("k");
                    pkgQueryPathItem.mPathType = o.getInt("a");
                    pkgQueryPathItem.mTestFlag = o.optInt("g");
                    innerData.mPkgQueryPathItems.add(pkgQueryPathItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dirs == null ? 0 : dirs.length();
    }

    public static boolean setResultToQueryData(CachePkgBatchQuryResult result, Collection<PkgQueryData> datas) {
        if (null == datas)
            return false;

        Iterator<CachePkgTempResult> iter = null;
        if (result.mResults != null) {
            iter = result.mResults.iterator();
        }
        for (PkgQueryData data : datas) {
            if (null == result) {
                data.mErrorCode = -100;
            } else if (null == result.mResults) {
                if (result.mErrorCode != 0) {
                    data.mErrorCode = result.mErrorCode;
                } else {
                    data.mErrorCode = -101;
                }
            } else if (iter != null && iter.hasNext()) {
                data.mErrorCode = 0;
                CachePkgTempResult next = iter.next();
                data.mResult = next.queryResult;

                KCacheCommonData.CachePkgQueryInnerData mInnerData = (KCacheCommonData.CachePkgQueryInnerData) data.mInnerData;
                next.innerData.mPkgNameMd5 = mInnerData.mPkgNameMd5;

                data.mInnerData = next.innerData;
                data.mResultExpired = false;
                data.mResultIntegrity = true;
                data.mResultSource = ResultSourceType.CLOUD;
            } else {
                data.mErrorCode = -110;
            }
        }
        NLog.d(TAG,"setResultToQueryData result = " + datas);
        return true;
    }

    static public boolean decodeAndsetResultToQueryData(byte[] respone, byte[] decodekey, Collection<PkgQueryData> datas) {
        //服务端返回的数据新作了一次AES加密 需要先做AES界面 再解析byte数组
        if (respone == null || respone.length == 0){
            NLog.d(TAG, "AES解密数组为空 ");
            return false;
        }
        respone = decrypt(KCleanCloudEnv.DEFAULT_RESPONSE_KEY_AES, respone);

        String resultString = getResultString(respone, decodekey);
        NLog.d(TAG,"decodeAndsetResultToQueryData resultString = "+resultString);
        CachePkgBatchQuryResult result = getResultDataFromJsonString_v2(resultString);
        //CachePkgBatchQuryResult result = getResultDataFromJsonString(resultString);
        return setResultToQueryData(result, datas);
    }
}